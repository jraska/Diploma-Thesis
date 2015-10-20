package com.jraska.pwmd.travel;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.tracking.LocationFilter;
import com.jraska.pwmd.travel.tracking.SimpleTrackingManager;
import dagger.Module;
import dagger.Provides;

@Module
public class TravelAssistanceModule {
  private final TravelAssistanceApp _app;

  public TravelAssistanceModule(@NonNull TravelAssistanceApp app) {
    _app = app;
  }

  @Provides @PerApp TravelAssistanceApp provideTravelAssistanceApp() {
    return _app;
  }

  @Provides @PerApp Application provideApp(TravelAssistanceApp app) {
    return app;
  }

  @Provides @PerApp Context provideContext(Application app) {
    return app;
  }

  @Provides @PerApp LocationFilter provideFilter() {
    return new SimpleTrackingManager.GpsProviderOnlyFilter();
  }
}
