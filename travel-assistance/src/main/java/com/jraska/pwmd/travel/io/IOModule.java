package com.jraska.pwmd.travel.io;

import android.content.Context;
import android.os.Build;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.DIRECTORY_RINGTONES;

@Module
public class IOModule {
  @Provides @PerApp @CacheDir File cacheDir(Context context) {
    File externalCacheDir = context.getExternalCacheDir();
    if (externalCacheDir != null) {
      return externalCacheDir;
    }

    Timber.w("External cache dir returned null");

    // on some devices this returns null, but there are still some dirs in cache array
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      File[] externalCacheDirs = context.getExternalCacheDirs();
      if (externalCacheDirs != null) {
        for (File dir : externalCacheDirs) {
          if (dir != null) {
            return dir;
          }
        }
      }
    }

    // just default to something
    return resolveDir(context, "Cache");
  }

  @Provides @PerApp @PicturesDir
  public File providePicturesDir(Context context) {
    return resolveDir(context, DIRECTORY_PICTURES);
  }

  @Provides @PerApp @SoundsDir
  public File provideSoundDir(Context context) {
    return resolveDir(context, DIRECTORY_RINGTONES);
  }

  private File resolveDir(Context context, String directoryType) {
    File externalDir = context.getExternalFilesDir(directoryType);
    if (externalDir != null) {
      return externalDir;
    }

    Timber.w("Default expected file not found for type %s", directoryType);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      File[] dirs = context.getExternalFilesDirs(directoryType);
      if (dirs != null) {
        for (File dir : dirs) {
          if (dir != null) {
            Timber.d("Found in array dir %s for type %s", dir, directoryType);
            return dir;
          }
        }
      }
    }

    Timber.d("Defaulting to internal storage %s", directoryType);
    // default to internal storage
    return context.getDir(directoryType, Context.MODE_PRIVATE);
  }
}
