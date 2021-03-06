package com.jraska.pwmd.travel.media;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.io.SoundsDir;
import lombok.SneakyThrows;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@PerApp
public class SoundsManager implements MediaPlayer.OnCompletionListener {
  //region Constants

  public static final String GPP_EXTENSION = ".3gp";

  //endregion

  //region Fields

  private final File _soundsDir;

  private MediaRecorder _recorder;
  private MediaPlayer _mediaPlayer;
  private UUID _currentFileId;

  //endregion

  //region Constructors

  @Inject
  public SoundsManager(@SoundsDir @NonNull File soundsDir) {
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

  @Override public void onCompletion(MediaPlayer mediaPlayer) {
    stopPlaying();
  }

  //endregion

  //region Events

  public void deleteSound(UUID soundId) {
    //region Methods
    File soundFile = getFileForId(soundId);
    if (soundFile.exists() && soundFile.delete()) {
      Timber.i("Sound " + soundId + " successfully deleted");
    } else {
      Timber.w("Sound " + soundId + " was not deleted ");

    }
  }

  //endregion


  @SneakyThrows(IOException.class)
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

    _recorder.prepare();

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
      Timber.e("Sound %s file not found.", soundId);
    }
  }

  @SneakyThrows(IOException.class)
  protected void play(File file) {
    if (isPlaying()) {
      return;
    }

    _mediaPlayer = new MediaPlayer();

    _mediaPlayer.setDataSource(file.getAbsolutePath());
    _mediaPlayer.prepare();
    _mediaPlayer.start();

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
