package com.jraska.pwmd.core.gps;

import com.jraska.common.events.Observable;
import com.jraska.core.BaseApp;
import com.jraska.core.services.AppService;

public interface LocationService extends AppService {
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

  //region Nested classes

  class Stub {
    public static LocationService asInterface() {
      return BaseApp.getService(LocationService.class);
    }
  }

  //endregion
}
