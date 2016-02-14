package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.media.SoundsManager;

import javax.inject.Inject;
import java.util.UUID;

public class VoiceRecordActivity extends BaseActivity {
  //region Constants

  public static final String RECORDED_ID_KEY = "recordedId";
  public static final String RECORDED_TITLE_KEY = "recordedTitle";

  //endregion

  //region Fields

  @Bind(R.id.voice_record_stop_recording) View _stopRecordingButton;
  @Bind(R.id.voice_record_save_recording) View _saveRecordingButton;
  @Bind(R.id.voice_record_title_input) EditText _recordTitleInput;

  @Inject SoundsManager _soundsManager;

  private UUID _currentVoiceId;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_voice_record);

    TravelAssistanceApp.getComponent(this).inject(this);

    startRecording();
  }

  @Override
  protected void onDestroy() {
    if (!isChangingConfigurations()) {
      _soundsManager.stopRecording();
    }

    super.onDestroy();
  }

  //endregion

  //region Methods

  void startRecording() {
    setTitle(R.string.sound_record_listening);
    _soundsManager.startRecording();
  }

  @OnClick(R.id.voice_record_stop_recording) void stopRecording() {
    setTitle(R.string.sound_record_title);

    _saveRecordingButton.setVisibility(View.GONE);
    _saveRecordingButton.setVisibility(View.VISIBLE);

    _currentVoiceId = _soundsManager.stopRecording();
  }

  @OnClick(R.id.voice_record_save_recording) void saveRecording() {
    // ensure it is not recording anymore
    UUID uuid = _currentVoiceId;
    if (uuid != null) {
      Intent data = new Intent();
      data.putExtra(RECORDED_ID_KEY, uuid);
      data.putExtra(RECORDED_TITLE_KEY, _recordTitleInput.getText().toString());

      setResult(RESULT_OK, data);
    }

    finish();
  }

  //endregion
}
