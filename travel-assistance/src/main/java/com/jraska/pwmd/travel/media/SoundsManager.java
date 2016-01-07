package com.jraska.pwmd.travel.media;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SoundsManager implements MediaPlayer.OnCompletionListener {
  //region Constants

  public static final String SOUND_DIR = "Sounds";
  public static final String GPP_EXTENSION = ".3gp";

  //endregion

  //region Fields

  private final File _soundsDir;

  private MediaRecorder _recorder;
  private MediaPlayer _mediaPlayer;
  private UUID _currentFileId;

  //endregion

  //region Constructors

  @Inject @PerApp
  public SoundsManager(@Named(SOUND_DIR) @NonNull File soundsDir) {
    ArgumentCheck.notNull(soundsDir);

    _soundsDir = soundsDir;
  }

  //endregion

  //region Properties

  public boolean isRecording() {
    return _recorder != null;
  }

  public boolean isPlaying() {
    return _mediaPlayer != null;
  }

  //endregion

  //region OnCompletionListener impl

  @Override public void onCompletion(MediaPlayer mp) {
    stopPlaying();
  }

  //endregion

  //region Methods

  public void startRecording() {
    if (isRecording()) {
      return;
    }

    _currentFileId = UUID.randomUUID();
    File outputFile = getFileForId(_currentFileId);

    _recorder = new MediaRecorder();
    _recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    _recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    _recorder.setOutputFile(outputFile.getAbsolutePath());
    _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    try {
      _recorder.prepare();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    _recorder.start();
  }

  @NonNull
  protected File getFileForId(@NonNull UUID id) {
    return new File(_soundsDir, id.toString() + GPP_EXTENSION);
  }

  /**
   * Stops current recording and gets id of existing record if any
   *
   * @return Id of saved file, null if there is no record.
   */
  @Nullable
  public UUID stopRecording() {
    if (!isRecording()) {
      return null;
    }

    _recorder.stop();
    _recorder.release();
    _recorder = null;

    UUID fileId = _currentFileId;
    _currentFileId = null;

    return fileId;
  }

  public void play(@NonNull UUID soundId) {
    File file = getFileForId(soundId);
    if (file.exists()) {
      play(file);
    } else {
      throw new IllegalStateException("Sound file not found");
    }
  }

  protected void play(File file) {
    if (isPlaying()) {
      return;
    }

    _mediaPlayer = new MediaPlayer();
    try {
      _mediaPlayer.setDataSource(file.getAbsolutePath());
      _mediaPlayer.prepare();
      _mediaPlayer.start();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    _mediaPlayer.setOnCompletionListener(this);
  }

  private void stopPlaying() {
    if (!isPlaying()) {
      return;
    }

    _mediaPlayer.release();
    _mediaPlayer = null;
  }

  //endregion
}
