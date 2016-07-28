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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteIcon;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.dialog.LambdaDialogFragment;
import com.jraska.pwmd.travel.feedback.Feedback;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.jraska.pwmd.travel.media.SoundsManager;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.rx.IOThreadTransformer;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import com.jraska.pwmd.travel.transport.SimpleTransportManager;
import com.jraska.pwmd.travel.util.ShowContentDescriptionLongClickListener;
import com.jraska.pwmd.travel.util.TimeProvider;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Date;
import java.util.UUID;

public class RouteRecordActivity extends BaseActivity {

  //region Constants

  public static final int REQUEST_IMAGE_CAPTURE = 1;
  public static final int REQUEST_VOICE_RECORD = 2;

  // We need to explicitly save the input to handle the case
  // when user currently changed the title, did not saved so far and activity rotated.
  private static final String STATE_KEY_TITLE = "titleInput";

  private static final String STATE_KEY_LAST_SAVED_TIME = "lastSavedTime";

  private static final long SECONDS_TO_PROMPT_SAVE = 60;

  //endregion

  //region Fields

  @BindView(R.id.record_btnStartRecording) View _startTrackingButton;
  @BindView(R.id.record_btnStopRecording) View _stopTrackingButton;
  @BindView(R.id.record_btnSaveRoute) View _saveRouteButton;
  @BindView(R.id.record_btnChangeTransportType) ImageView _changeTransportButton;
  @BindView(R.id.record_btnAddPhoto) View _takePhotoButton;
  @BindView(R.id.record_btnAddTextNote) View _addNoteButton;
  @BindView(R.id.record_btnAddVoice) View _addSoundButton;
  @BindView(R.id.record_route_title_input) EditText _titleInput;
  @BindView(R.id.record_set_icon_view) ImageView _pickIconView;

  @Inject SimpleTransportManager _transportManager;
  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;
  @Inject PicturesManager _picturesManager;
  @Inject LayoutInflater _inflater;
  @Inject SoundsManager _soundsManager;
  @Inject EventBus _eventBus;
  @Inject Provider<Location> _lastLocationProvider;
  @Inject TimeProvider _timeProvider;

  private UUID _lastPhotoRequestId;
  private RouteDisplayFragment _routeDisplayFragment;
  private ShowcaseView _showcaseView;
  private long _lastSavedRouteTime;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.v("onCreate()");
    setContentView(R.layout.activity_route_record);

    TravelAssistanceApp.getComponent(this).inject(this);

    _routeDisplayFragment = (RouteDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    updateUIState();
    showRecordShowcase();
    updateTransportIcon();
    updateRouteIcon();
    centerMapToLastPosition();

    TrackingManager.UserInput lastUserInput = _trackingManager.getLastUserInput();
    if (lastUserInput != null) {
      setFromUserInput(lastUserInput);
    }

    _eventBus.register(this);
  }

  private void showRecordShowcase() {
    _showcaseView = new ShowcaseView.Builder(this)
        .setTarget(new ViewTarget(_startTrackingButton))
        .setContentTitle(R.string.record_button_showcase_title)
        .setContentTitlePaint(paintOf(R.color.colorAccent))
        .setContentText(R.string.record_button_showcase_subtitle)
        .setContentTextPaint(paintOf(R.color.textMain))
        .hideOnTouchOutside()
        .singleShot(12345)
        .build();
  }

  private TextPaint paintOf(int colorRes) {
    int color = ContextCompat.getColor(this, colorRes);
    TextPaint paint = new TextPaint();
    paint.setColor(color);
    paint.setAntiAlias(true);
    paint.setTextSize(getResources().getDimension(R.dimen.textTitle));

    return paint;
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

      _lastSavedRouteTime = savedInstanceState.getLong(STATE_KEY_LAST_SAVED_TIME);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_KEY_TITLE, getInputTitle());
    outState.putLong(STATE_KEY_LAST_SAVED_TIME, _lastSavedRouteTime);
  }

  @Override
  protected void onDestroy() {
    _eventBus.unregister(this);

    if (isFinishing()) {
      stopTracking();
    }

    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if (isUserRecordingLongWithoutSaving()) {
      promptFinish();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected boolean onNavigationIconClicked() {
    if (isUserRecordingLongWithoutSaving()) {
      promptFinish();
    } else {
      finish();
    }

    return true;
  }

  //endregion

  //region Methods

  void promptFinish() {
    if (!_trackingManager.isTracking()) {
      finish();
      return;
    }

    LambdaDialogFragment.builder()
        .validateEagerly(BuildConfig.DEBUG)
        .iconRes(android.R.drawable.ic_dialog_alert)
        .title(getString(R.string.record_prompt_title))
        .message(getString(R.string.record_prompt_message))
        .cancelable(true)
        .negativeText(getString(R.string.record_prompt_button_finish))
        .negativeMethod(RouteRecordActivity::finish)
        .positiveText(getString(R.string.record_prompt_button_continue))
        .show(getSupportFragmentManager());

    Timber.i("Prompt dialog is showing to the user");
  }

  @Subscribe
  public void onNewLocation(Location location) {
    updateDisplayedRoute();
    centerMapToLastPosition();
  }

  public void centerMapToLastPosition() {
    Location location = _lastLocationProvider.get();
    if (location != null) {
      _routeDisplayFragment.centerMapTo(location);
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

    Timber.i("User started recording");

    // Nothing is saved now
    _lastSavedRouteTime = currentMillis();

    Snackbar.make(_titleInput, R.string.record_tracking_now, Snackbar.LENGTH_LONG).show();

    updateUIState();
  }

  @OnClick(R.id.record_btnStopRecording) void stopTrackingClicked() {
    if (isUserRecordingLongWithoutSaving()) {
      LambdaDialogFragment.builder()
          .validateEagerly(BuildConfig.DEBUG)
          .title(getString(R.string.record_prompt_title))
          .message(getString(R.string.record_unsaved_prompt_message))
          .cancelable(true)
          .negativeText(getString(R.string.record_prompt_button_finish))
          .negativeMethod(RouteRecordActivity::stopTracking)
          .positiveText(getString(R.string.record_prompt_button_continue))
          .show(getSupportFragmentManager());
    } else {
      stopTracking();
    }
  }

  private boolean isUserRecordingLongWithoutSaving() {
    RouteData routeData = _trackingManager.getRouteData(getUserInput());
    if (routeData == null) {
      return false;
    }

    long now = currentMillis();

    long secondsAfterLastSave = (now - _lastSavedRouteTime) / 1000;
    if (secondsAfterLastSave > SECONDS_TO_PROMPT_SAVE) {
      return true;
    } else {
      return false;
    }
  }

  void stopTracking() {
    _trackingManager.stopTracking();
    _routeDisplayFragment.displayRoute(null);

    Timber.i("User stopped recording");

    updateUIState();
  }

  private void updateUIState() {
    if (_showcaseView != null) {
      _showcaseView.hide();
    }

    boolean tracking = _trackingManager.isTracking();

    if (tracking) {
      setTitle(R.string.record_tracking_now);
    } else {
      setTitle(R.string.title_activity_route_record);
    }

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
    _pickIconView.setEnabled(tracking);
  }

  @OnClick(R.id.record_btnSaveRoute) void saveRoute() {
    TrackingManager.UserInput userInput = getUserInput();
    RouteData routeData = _trackingManager.getRouteData(userInput);
    if (routeData == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    Timber.i("Saving route title=%s", routeData.getTitle());

    _travelDataRepository.insertOrUpdate(routeData)
        .compose(IOThreadTransformer.get())
        .subscribe(this::onSaved);
  }

  void onSaved(long savedCount) {
    _lastSavedRouteTime = currentMillis();
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
    boolean result = _trackingManager.addChange(type, title);
    if (result) {
      updateDisplayedRoute();
    }

    return result;
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

    if (addNote(null, title, id)) {
      Toast.makeText(this, R.string.record_voice_saved, Toast.LENGTH_SHORT).show();
    }
  }

  boolean addNote(UUID imageId, String title, UUID soundId) {
    boolean result = _trackingManager.addNote(imageId, title, soundId);
    if (result) {
      updateDisplayedRoute();
    }

    return result;
  }

  protected void handlePhotoTakenIntent(@Nullable Intent data) {
    try {
      handlePhotoTakeUnchecked(data);
    } catch (Exception ex) {
      handlePhotoException(ex);
    }
  }

  protected void handlePhotoException(Exception ex) {
    Timber.e(ex, "Unexpected error on photo received.");
    String message = Log.getStackTraceString(ex);
    Snackbar snackbar = Snackbar.make(_addNoteButton, R.string.route_record_picture_error_message,
        Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction(R.string.route_record_error_send, v -> Feedback.startFeedback(this, message));
    snackbar.show();
  }

  protected void handlePhotoTakeUnchecked(@Nullable Intent data) {
    final UUID imageId = _lastPhotoRequestId;
    if (!_picturesManager.imageExists(imageId)) {
      Timber.e("Image does not exists on successful picture taken. Id= %s", imageId);
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
    builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> _picturesManager.deleteImage(imageId));
    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
      String caption = photoDialogHolder._captionInput.getText().toString();
      addNote(imageId, caption, null);
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
      addNote(null, caption, null);
    });

    builder.show();
  }

  @OnClick(R.id.record_btnAddVoice) void addVoice() {
    Intent voiceRecordIntent = new Intent(this, VoiceRecordActivity.class);
    startActivityForResult(voiceRecordIntent, REQUEST_VOICE_RECORD);
  }

  @OnLongClick({R.id.record_btnAddTextNote, R.id.record_btnAddVoice, R.id.record_btnAddPhoto,
      R.id.record_btnChangeTransportType, R.id.record_btnStartRecording,
      R.id.record_btnStopRecording, R.id.record_btnSaveRoute, R.id.record_set_icon_view})
  boolean showContentDescription(View view) {
    return ShowContentDescriptionLongClickListener.showContentDescription(view);
  }

  @OnClick(R.id.record_set_icon_view) void showIconPickDialog() {
    PickRouteIconDialog.show(this, _trackingManager.getRouteIcon());
  }

  void onRouteIconPicked(RouteIcon routeIcon) {
    _trackingManager.setRouteIcon(routeIcon);
    updateRouteIcon();
  }

  void updateRouteIcon() {
    _pickIconView.setImageResource(_trackingManager.getRouteIcon().iconResId);
  }

  void updateDisplayedRoute() {
    RouteData routeData = _trackingManager.getRouteData(getUserInput());
    _routeDisplayFragment.displayRoute(routeData);
  }

  long currentMillis() {
    return _timeProvider.currentTime();
  }

  //endregion

  //region Nested classes

  static class PhotoDialogHolder {
    @BindView(R.id.record_photo_preview) ImageView _imagePreview;
    @BindView(R.id.record_photo_caption) EditText _captionInput;

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

    @BindView(R.id.transport_change_title_input) EditText _titleInput;

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
