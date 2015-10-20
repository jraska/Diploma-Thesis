package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

@Module
public class TrackingModule {
  @Provides @PerApp
  TrackingManager provideTrackingManager(Context context, LocationFilter locationFilter) {
    return new SimpleTrackingManager(context, locationFilter);
  }
}
