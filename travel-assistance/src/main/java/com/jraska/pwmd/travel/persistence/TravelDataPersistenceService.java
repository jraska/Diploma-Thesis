package com.jraska.pwmd.travel.persistence;

import com.jraska.common.Disposable;
import com.jraska.common.events.Observable;
import com.jraska.core.BaseApp;
import com.jraska.core.services.AppService;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;

import java.util.List;
import java.util.UUID;

public interface TravelDataPersistenceService extends AppService, Disposable {
  //region Events

  Observable<RouteDescription> getOnNewRoute();

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
    public static TravelDataPersistenceService asInterface() {
      return BaseApp.getService(TravelDataPersistenceService.class);
    }
  }

  //endregion
}
