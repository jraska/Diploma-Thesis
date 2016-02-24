package com.jraska.pwmd.travel.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.google.android.gms.maps.*;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.jraska.pwmd.travel.media.SoundsManager;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import com.jraska.pwmd.travel.transport.SimpleTransportManager;
import com.jraska.pwmd.travel.util.ShowContentDescriptionLongClickListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Date;
import java.util.UUID;

import static com.jraska.pwmd.travel.ui.MapHelper.toLatLng;

public class RouteRecordActivity extends BaseActivity implements OnMapReadyCallback {

  //region Constants

  public static final int REQUEST_IMAGE_CAPTURE = 1;
  public static final int REQUEST_VOICE_RECORD = 2;

  // We need to explicitly save the input to handle the case
  // when user currently changed the title, did not saved so far and activity rotated.
  private static final String STATE_KEY_TITLE = "titleInput";

  //endregion

  //region Fields

  @Bind(R.id.record_btnStartRecording) View _startTrackingButton;
  @Bind(R.id.record_btnStopRecording) View _stopTrackingButton;
  @Bind(R.id.record_btnSaveRoute) View _saveRouteButton;
  @Bind(R.id.record_btnChangeTransportType) ImageView _changeTransportButton;
  @Bind(R.id.record_btnAddPhoto) View _takePhotoButton;
  @Bind(R.id.record_btnAddTextNote) View _addNoteButton;
  @Bind(R.id.record_btnAddVoice) View _addSoundButton;
  @Bind(R.id.record_route_title_input) EditText _titleInput;

  @Inject SimpleTransportManager _transportManager;
  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;
  @Inject PicturesManager _picturesManager;
  @Inject LayoutInflater _inflater;
  @Inject SoundsManager _soundsManager;
  @Inject EventBus _eventBus;
  @Inject Provider<Location> _lastLocationProvider;

  private UUID _lastPhotoRequestId;
  private GoogleMap _map;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.v("onCreate()");
    setContentView(R.layout.activity_route_record);

    TravelAssistanceApp.getComponent(this).inject(this);

    updateStartStopButtons();
    updateTransportIcon();

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    TrackingManager.UserInput lastUserInput = _trackingManager.getLastUserInput();
    if (lastUserInput != null) {
      setFromUserInput(lastUserInput);
    }

    _eventBus.register(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    handleActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    if (savedInstanceState != null) {
      String title = savedInstanceState.getString(STATE_KEY_TITLE);
      if (title != null) {
        setInputTitle(title);
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_KEY_TITLE, getInputTitle());
  }

  @Override
  protected void onDestroy() {
    _eventBus.unregister(this);

    super.onDestroy();
  }

  //endregion

  //region OnMapReadyCallback

  @Override
  public void onMapReady(GoogleMap map) {
    MapHelper.configureMap(map);
    _map = map;
    CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(MapHelper.ZOOM);
    _map.moveCamera(cameraUpdate);

    centerMapToLastPosition(false);
  }

  //endregion

  //region Methods

  @Subscribe
  public void onNewLocation(Location location) {
    centerMapToLastPosition(true);
  }

  public void centerMapToLastPosition(boolean animate) {
    Location location = _lastLocationProvider.get();
    if (location != null && _map != null) {
      CameraUpdate center = CameraUpdateFactory.newLatLng(toLatLng(location));
      if (animate) {
        _map.animateCamera(center);
      } else {
        _map.moveCamera(center);
      }
    }
  }

  private void setFromUserInput(TrackingManager.UserInput lastUserInput) {
    setInputTitle(lastUserInput.getTitle());
  }

  private TrackingManager.UserInput getUserInput() {
    return new TrackingManager.UserInput(getUserInputTitle());
  }

  protected void setInputTitle(String text) {
    if (text == null) {
      return;
    }

    _titleInput.setText(text);
    _titleInput.setSelection(text.length());
  }

  protected String getUserInputTitle() {
    String titleText = getInputTitle();

    if (TextUtils.isEmpty(titleText)) {
      return getString(R.string.route_default_title, getCurrentTimeText());
    }

    return titleText;
  }

  @NonNull private String getInputTitle() {
    return _titleInput.getText().toString();
  }

  @OnClick(R.id.record_btnStartRecording) void startTracking() {
    _trackingManager.startTracking();

    updateStartStopButtons();
  }

  @OnClick(R.id.record_btnStopRecording) void stopTracking() {
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
    TrackingManager.UserInput userInput = getUserInput();
    RouteData routeData = _trackingManager.getRouteData(userInput);
    if (routeData == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    _travelDataRepository.insertOrUpdate(routeData)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onSaved);
  }

  void onSaved(long savedCount) {
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

  protected String getCurrentTimeText() {
    return TravelAssistanceApp.USER_TIME_FORMAT.format(new Date());
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

  @OnClick(R.id.record_btnAddPhoto) void takePhoto() {
    dispatchTakePictureIntent();
  }

  protected void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    _lastPhotoRequestId = _picturesManager.createPictureId();
    Uri pictureUri = _picturesManager.createPictureUri(_lastPhotoRequestId);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      Timber.d("Starting Camera");
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

  protected void handlePhotoTakenIntent(@Nullable Intent data) {
    final UUID imageId = _lastPhotoRequestId;
    if (!_picturesManager.imageExists(imageId)) {
      Timber.e("Image does not exists on successful picture taken. Id= " + imageId);
      return;
    }


    Bitmap imageBitmap = null;
    if (data != null) {
      Bundle extras = data.getExtras();
      imageBitmap = (Bitmap) extras.get("data");
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.record_add_photo));

    @SuppressLint("InflateParams") // cannot get parent
        View dialogView = _inflater.inflate(R.layout.dialog_record_photo_preview, null);

    builder.setView(dialogView);
    final PhotoDialogHolder photoDialogHolder = new PhotoDialogHolder(dialogView);
    if (imageBitmap != null) {
      photoDialogHolder._imagePreview.setImageBitmap(imageBitmap);
    }

    builder.setOnCancelListener(dialog -> _picturesManager.deleteImage(imageId));
    builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
      _picturesManager.deleteImage(imageId);
    });
    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
      String caption = photoDialogHolder._captionInput.getText().toString();
      _trackingManager.addNote(imageId, caption, null);
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
    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
      String caption = noteInput.getText().toString();
      _trackingManager.addNote(null, caption, null);
    });

    builder.show();
  }

  @OnClick(R.id.record_btnAddVoice) void addVoice() {
    Intent voiceRecordIntent = new Intent(this, VoiceRecordActivity.class);
    startActivityForResult(voiceRecordIntent, REQUEST_VOICE_RECORD);
  }

  @OnLongClick({R.id.record_btnAddTextNote, R.id.record_btnAddVoice, R.id.record_btnAddPhoto,
      R.id.record_btnChangeTransportType, R.id.record_btnStartRecording,
      R.id.record_btnStopRecording, R.id.record_btnSaveRoute})
  boolean showContentDescription(View view) {
    return ShowContentDescriptionLongClickListener.showContentDescription(view);
  }

  //endregion

  //region Nested classes

  static class PhotoDialogHolder {
    @Bind(R.id.record_photo_preview) ImageView _imagePreview;
    @Bind(R.id.record_photo_caption) EditText _captionInput;

    public PhotoDialogHolder(View rootView) {
      ButterKnife.bind(this, rootView);
    }

    @OnLongClick(R.id.record_photo_preview) boolean showContentDescription(View v) {
      return ShowContentDescriptionLongClickListener.showContentDescription(v);
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

    @OnLongClick({R.id.btn_transport_change_select_walking,
        R.id.btn_transport_change_select_train, R.id.btn_transport_change_select_bus})
    boolean showContentDescription(View view) {
      return ShowContentDescriptionLongClickListener.showContentDescription(view);
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
