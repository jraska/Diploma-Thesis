package com.jraska.pwmd.travel.media;

import android.content.Context;
import android.os.Environment;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import java.io.File;

@Module
public class MediaModule {
  //region Provide Methods

  @PerApp @Provides @Named(PicturesManager.PICTURES_DIR)
  public File providePicturesDir(Context context) {
    return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
  }

  @PerApp @Provides @Named(SoundsManager.SOUND_DIR)
  public File provideSoundDir(Context context) {
    return context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES);
  }

  //endregion
}
