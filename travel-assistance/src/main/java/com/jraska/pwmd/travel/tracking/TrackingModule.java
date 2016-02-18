package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import android.location.Location;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Provider;

@Module
public class TrackingModule {
  @Provides @PerApp
  TrackingManager provideTrackingManager(Context context, Provider<Location> locationProvider,
                                         LocationFilter locationFilter, EventBus systemBus) {
    return new SimpleTrackingManager(context, locationProvider, locationFilter, systemBus);
  }
}
