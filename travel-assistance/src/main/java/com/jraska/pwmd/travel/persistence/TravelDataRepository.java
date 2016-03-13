package com.jraska.pwmd.travel.persistence;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import rx.Observable;

import java.util.List;

public interface TravelDataRepository {
  //region Methods

  Observable<List<RouteData>> selectAll();

  Observable<RouteData> select(long id);

  Observable<Boolean> routeExists(long id);

  Observable<Long> delete(RouteData routeData);

  Observable<Long> insertOrUpdate(RouteData routeData);

  //endregion

  //region Nested classes

  class RouteDeleteEvent {
    public final RouteData _deletedRoute;

    protected RouteDeleteEvent(RouteData deletedRoute) {
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

  class UpdatedRouteEvent {
    public final RouteData _routeData;

    protected UpdatedRouteEvent(RouteData routeData) {
      ArgumentCheck.notNull(routeData);

      _routeData = routeData;
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
