package com.jraska.pwmd.core.gps;

import com.jraska.common.events.Observable;
import com.jraska.core.BaseApp;

public interface LocationService {
  //region Events

  Observable<Position> getNewPosition();

  //endregion

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
