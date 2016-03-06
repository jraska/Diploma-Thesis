package com.jraska.pwmd.travel;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.gms.GoogleLocationApiClientProvider;
import com.jraska.pwmd.travel.tracking.LocationFilter;
import com.jraska.pwmd.travel.tracking.SimpleTrackingManager;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

@Module
public class AppModule {
  private final TravelAssistanceApp _app;

  public AppModule(@NonNull TravelAssistanceApp app) {
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

  @Provides
  GoogleApiClient provideGoogleApiClient(GoogleLocationApiClientProvider apiClientProvider) {
    return apiClientProvider.get();
  }

  @Provides @PerApp EventBus provideDefaultBus() {
    return EventBus.getDefault();
  }

  @Provides @PerApp Vibrator provideVibrator(Context context) {
    return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  }
}
