package com.jraska.pwmd.travel.persistence;

import com.jraska.common.IDisposable;
import com.jraska.common.events.IObservable;
import com.jraska.core.BaseApplication;
import com.jraska.core.services.IAppService;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;

import java.util.List;
import java.util.UUID;

public interface ITravelDataPersistenceService extends IAppService, IDisposable {
  //region Events

  IObservable<RouteDescription> getOnNewRoute();

  //endregion

  //region Methods

  List<RouteDescription> selectAllRouteDescriptions();

  RouteData selectRouteData(UUID id);

  long deleteRoute(UUID id);

  long updateRoute(RouteData routeData);

  long insertRoute(RouteData routeData);

  //endregion

  //region Nested class

  class Stub {
    public static ITravelDataPersistenceService asInterface() {
      return BaseApplication.getService(ITravelDataPersistenceService.class);
    }
  }

  //endregion
}
