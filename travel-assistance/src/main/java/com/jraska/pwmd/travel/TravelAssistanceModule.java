package com.jraska.pwmd.travel;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.tracking.LocationFilter;
import com.jraska.pwmd.travel.tracking.SimpleTrackingManager;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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

  @Provides @PerApp LayoutInflater provideInflater(Context context) {
    return LayoutInflater.from(context);
  }

  @Provides @PerApp EventBus provideDefaultBus() {
    return EventBus.getDefault();
  }
}
