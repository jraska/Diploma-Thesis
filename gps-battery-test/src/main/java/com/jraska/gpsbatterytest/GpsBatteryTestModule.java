package com.jraska.gpsbatterytest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module
public class GpsBatteryTestModule {
  private final GpsBatteryTestApp _app;

  public GpsBatteryTestModule(@NonNull GpsBatteryTestApp app) {
    _app = app;
  }

  @Provides @PerApp GpsBatteryTestApp provideGpsBatteryTestApp() {
    return _app;
  }

  @Provides @PerApp Application provideApp(GpsBatteryTestApp app) {
    return app;
  }

  @Provides @PerApp Context provideContext(Application app) {
    return app;
  }

  @Provides @PerApp EventBus provideDefaultBus() {
    return EventBus.getDefault();
  }
}
