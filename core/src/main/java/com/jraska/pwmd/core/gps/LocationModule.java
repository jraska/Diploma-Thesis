package com.jraska.pwmd.core.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

@Module
public class LocationModule {
  @Provides LocationManager provideLocationManager(Context context) {
    return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  @Provides @PerApp
  SimpleSystemLocationService provideLocationServiceImpl(LocationManager locationManager,
                                                         EventBus eventBus) {
    return new SimpleSystemLocationService(locationManager, eventBus);
  }

  @Provides @PerApp LocationService provideLocationService(SimpleSystemLocationService svc) {
    return svc;
  }

  @Provides @PerApp
  LocationStatusService provideLocationStatusService(SimpleSystemLocationService svc) {
    return svc;
  }

  @Provides @Nullable Location provideLastLocation(LocationService locationService) {
    return locationService.getLastLocation();
  }
}
