package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LocationService;
import dagger.Module;
import dagger.Provides;

@Module
public class TrackingModule {
  @Provides @PerApp
  TrackingManager provideTrackingManager(Context context, LocationService locationService,
                                         LocationFilter locationFilter) {
    return new SimpleTrackingManager(context, locationService, locationFilter);
  }
}
