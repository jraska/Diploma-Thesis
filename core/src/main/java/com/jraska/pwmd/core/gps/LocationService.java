package com.jraska.pwmd.core.gps;

public interface LocationService {
  //region Properties

  Position getLastPosition();

  boolean isTracking();

  boolean isTrackingAvailable();

  //endregion

  //region Methods

  void startTracking(LocationSettings settings);

  void stopTracking();

  //endregion
}
