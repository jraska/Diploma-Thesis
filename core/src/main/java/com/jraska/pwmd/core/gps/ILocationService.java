package com.jraska.pwmd.core.gps;

import com.jraska.common.events.IObservable;
import com.jraska.core.BaseApplication;
import com.jraska.core.services.IAppService;

public interface ILocationService extends IAppService {
  //region Events

  IObservable<Position> getNewPosition();

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
    public static ILocationService asInterface() {
      return BaseApplication.getService(ILocationService.class);
    }
  }

  //endregion
}
