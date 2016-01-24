package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.travel.persistence.DataModule;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

import javax.inject.Named;

@Module
public class TrackingModule {
  @Provides @PerApp
  TrackingManager provideTrackingManager(Context context, LocationService locationService,
                                         LocationFilter locationFilter, EventBus systemBus) {
    return new SimpleTrackingManager(context, locationService, locationFilter, systemBus);
  }
}
