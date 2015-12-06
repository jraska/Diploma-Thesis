package com.jraska.pwmd.travel.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {
  @PerApp @Provides
  public SettingsManager provideSettingsManager(SharedPreferences sharedPreferences) {
    return new SettingsManager(sharedPreferences);
  }

  @PerApp @Provides
  public SharedPreferences provideSharedPreferences(Application app) {
    return app.getSharedPreferences("default", Context.MODE_PRIVATE);
  }
}
