package com.jraska.pwmd.travel.navigation;

import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationServices;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {
  //region  Methods

  @PerApp @Provides GeofencingApi proviceGeofencingApi() {
    return LocationServices.GeofencingApi;
  }

  //endregion
}
