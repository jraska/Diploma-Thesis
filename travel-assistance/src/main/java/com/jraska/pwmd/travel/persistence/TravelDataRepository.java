package com.jraska.pwmd.travel.persistence;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.Disposable;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;

import java.util.List;
import java.util.UUID;

public interface TravelDataRepository extends Disposable {
  //region Methods

  List<RouteDescription> selectAllRouteDescriptions();

  RouteData selectRouteData(UUID id);

  long deleteRoute(UUID id);

  long updateRoute(RouteData routeData);

  long insertRoute(RouteData routeData);

  //endregion

  //region Nested classes

  class NewRouteEvent {
    public final RouteDescription _newRoute;

    public NewRouteEvent(@NonNull RouteDescription newRoute) {
      ArgumentCheck.notNull(newRoute);

      _newRoute = newRoute;
    }
  }

  //endregion
}
