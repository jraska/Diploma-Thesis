package com.jraska.pwmd.travel.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.jraska.pwmd.travel.media.SoundsManager;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import com.jraska.pwmd.travel.transport.SimpleTransportManager;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.UUID;

public class RouteRecordActivity extends BaseActivity {

  //region Constants

  public static final int REQUEST_IMAGE_CAPTURE = 1;
  public static final int REQUEST_VOICE_RECORD = 2;

  //endregion

  //region Fields

  @Bind(R.id.record_btnStartTracking) View _startTrackingButton;
  @Bind(R.id.record_btnStopTracking) View _stopTrackingButton;
  @Bind(R.id.record_btnSaveRoute) View _saveRouteButton;
  @Bind(R.id.record_btnChangeTransportType) ImageView _changeTransportButton;
  @Bind(R.id.record_btnTakePhoto) View _takePhotoButton;
  @Bind(R.id.record_btnAddTextNote) View _addNoteButton;
  @Bind(R.id.record_btnAddVoice) View _addSoundButton;

  @Inject SimpleTransportManager _transportManager;
  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;
  @Inject PicturesManager _picturesManager;
  @Inject LayoutInflater _inflater;
  @Inject SoundsManager _soundsManager;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route_record);

    TravelAssistanceApp.getComponent(this).inject(this);

    updateStartStopButtons();
    updateTransportIcon();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    handleActivityResult(requestCode, resultCode, data);
  }

  //endregion

  //region Methods

  @OnClick(R.id.record_btnStartTracking) void startTracking() {
    _trackingManager.startTracking();

    updateStartStopButtons();
  }

  @OnClick(R.id.record_btnStopTracking) void stopTracking() {
    _trackingManager.stopTracking();

    updateStartStopButtons();
  }

  private void updateStartStopButtons() {
    boolean tracking = _trackingManager.isTracking();

    if (tracking) {
      _startTrackingButton.setVisibility(View.GONE);
      _stopTrackingButton.setVisibility(View.VISIBLE);
    } else {
      _startTrackingButton.setVisibility(View.VISIBLE);
      _stopTrackingButton.setVisibility(View.GONE);
    }

    _takePhotoButton.setEnabled(tracking);
    _addNoteButton.setEnabled(tracking);
    _saveRouteButton.setEnabled(tracking);
    _changeTransportButton.setEnabled(tracking);
    _addSoundButton.setEnabled(tracking);
  }

  @OnClick(R.id.record_btnSaveRoute) void saveRoute() {
    TrackingManager.PathInfo lastPath = _trackingManager.getLastPath();
    if (lastPath == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    RouteData routeData = new RouteData(new RouteDescription(UUID.randomUUID(),
        lastPath.getStart(), lastPath.getEnd(), "Test"), lastPath.getPath(),
        lastPath.getTransportChangeSpecs(), lastPath.getNoteSpecs());

    _travelDataRepository.insertRoute(routeData);

    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.record_btnChangeTransportType) void changeTransportType() {
    Dialog dialog = new Dialog(this);
    FrameLayout dialogView = new FrameLayout(this);

    LayoutInflater.from(this).inflate(R.layout.dialog_choose_new_transport, dialogView);
    dialog.setContentView(dialogView);
    dialog.setTitle(R.string.transport_change_select);
    TransportDialogHolder transportDialogHolder = new TransportDialogHolder(dialog, dialogView);
    transportDialogHolder.show();
  }

  protected boolean addTransportationChange(int type, @NonNull String title) {
    return _trackingManager.addChange(type, title);
  }

  protected void updateTransportIcon() {
    int iconRes = TransportChangeSpec.getDarkIconRes(_transportManager.getCurrentTransportType());

    _changeTransportButton.setImageResource(iconRes);
  }

  protected void onNewTransportationChange(int type, @NonNull String title) {
    _transportManager.setCurrentTransportType(type);
    addTransportationChange(type, title);
    updateTransportIcon();
  }

  @OnClick(R.id.record_btnTakePhoto) void takePhoto() {
    dispatchTakePictureIntent();
  }

  protected void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, _picturesManager.createPictureUri());

    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    } else {
      Timber.e("Cannot get image to capture.");
      Toast.makeText(this, R.string.cannot_get_pictures, Toast.LENGTH_SHORT).show();
    }
  }

  protected void handleActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      handlePhotoTakenIntent(data);
    }
    if (requestCode == REQUEST_VOICE_RECORD && resultCode == RESULT_OK) {
      handleVoiceRecordTaken(data);
    }
  }

  protected void handleVoiceRecordTaken(Intent data) {
    UUID id = (UUID) data.getSerializableExtra(VoiceRecordActivity.RECORDED_ID_KEY);
    String title = data.getStringExtra(VoiceRecordActivity.RECORDED_TITLE_KEY);

    if (_trackingManager.addNote(null, title, id)) {
      Toast.makeText(this, R.string.record_voice_saved, Toast.LENGTH_SHORT).show();
    }
  }

  protected void handlePhotoTakenIntent(Intent data) {
    Bundle extras = data.getExtras();
    Bitmap imageBitmap = (Bitmap) extras.get("data");

    final UUID imageId = _picturesManager.getIdForUri(data.getData());

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.record_save_photo));

    @SuppressLint("InflateParams") // cannot get parent
        View dialogView = _inflater.inflate(R.layout.dialog_record_photo_preview, null);

    builder.setView(dialogView);
    final PhotoDialogHolder photoDialogHolder = new PhotoDialogHolder(dialogView);
    photoDialogHolder._imagePreview.setImageBitmap(imageBitmap);

    builder.setNegativeButton(android.R.string.cancel, null);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String caption = photoDialogHolder._captionInput.getText().toString();
        _trackingManager.addNote(imageId, caption, null);
      }
    });

    builder.show();
  }

  @OnClick(R.id.record_btnAddTextNote) void addNote() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.record_add_note);

    @SuppressLint("InflateParams")
    View dialogView = _inflater.inflate(R.layout.dialog_record_add_note, null);
    builder.setView(dialogView);

    final EditText noteInput = ButterKnife.findById(dialogView, R.id.record_note_caption_input);

    builder.setNegativeButton(android.R.string.cancel, null);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String caption = noteInput.getText().toString();
        _trackingManager.addNote(null, caption, null);
      }
    });

    builder.show();
  }

  @OnClick(R.id.record_btnAddVoice) void addVoice() {
    Intent voiceRecordIntent = new Intent(this, VoiceRecordActivity.class);
    startActivityForResult(voiceRecordIntent, REQUEST_VOICE_RECORD);
  }

  //endregion

  //region Nested classes

  static class PhotoDialogHolder {
    @Bind(R.id.record_photo_preview) ImageView _imagePreview;
    @Bind(R.id.record_photo_caption) EditText _captionInput;

    public PhotoDialogHolder(View rootView) {
      ButterKnife.bind(this, rootView);
    }
  }

  static class TransportDialogHolder {
    private final Dialog _dialog;
    private final View _rootView;

    @Bind(R.id.transport_change_title_input) EditText _titleInput;

    public TransportDialogHolder(Dialog dialog, View rootView) {
      _dialog = dialog;
      _rootView = rootView;

      ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.btn_transport_change_select_bus) void selectBus() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_BUS);
    }

    @OnClick(R.id.btn_transport_change_select_train) void selectTrain() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_TRAIN);
    }

    @OnClick(R.id.btn_transport_change_select_walking) void selectWalking() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_WALK);
    }

    @OnClick({R.id.btn_transport_change_select_walking,
        R.id.btn_transport_change_select_train, R.id.btn_transport_change_select_bus})
    void dismissDialog() {
      _dialog.dismiss();
    }

    void addTransportInfo(int type) {
      RouteRecordActivity recordActivity = (RouteRecordActivity) _rootView.getContext();

      recordActivity.onNewTransportationChange(type, _titleInput.getText().toString());
    }

    public void show() {
      _dialog.show();
    }
  }

  //endregion
}
