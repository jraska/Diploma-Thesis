package com.jraska.pwmd.core.gps;

import android.location.Location;

public interface LocationService {
  //region Properties

  Location getLastLocation();

  boolean isTracking();

  boolean isTrackingAvailable();

  //endregion

  //region Methods

  void startTracking(LocationSettings settings);

  void stopTracking();

  //endregion
}
