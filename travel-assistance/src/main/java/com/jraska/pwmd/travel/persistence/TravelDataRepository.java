package com.jraska.pwmd.travel.persistence;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;

import java.util.List;

public interface TravelDataRepository {
  //region Methods

  List<RouteData> selectAll();

  RouteData select(long id);

  boolean routeExists(long id);

  long delete(RouteData routeData);

  long insertOrUpdate(RouteData routeData);

  //endregion

  //region Nested classes

  class RouteDeletedEvent {
    public final RouteData _deletedRoute;

    public RouteDeletedEvent(RouteData deletedRoute) {
      ArgumentCheck.notNull(deletedRoute);

      _deletedRoute = deletedRoute;
    }
  }

  class NewRouteEvent {
    public final RouteData _newRoute;

    public NewRouteEvent(@NonNull RouteData newRoute) {
      ArgumentCheck.notNull(newRoute);

      _newRoute = newRoute;
    }
  }

  class NoteSpecDeletedEvent {
    public final NoteSpec _noteSpec;

    public NoteSpecDeletedEvent(@NonNull NoteSpec noteSpec) {
      ArgumentCheck.notNull(noteSpec);

      _noteSpec = noteSpec;
    }
  }

  //endregion
}
