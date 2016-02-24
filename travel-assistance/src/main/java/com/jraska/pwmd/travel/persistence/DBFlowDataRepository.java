package com.jraska.pwmd.travel.persistence;

import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteData_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import hugo.weaving.DebugLog;
import org.greenrobot.eventbus.EventBus;
import rx.Observable;
import timber.log.Timber;

import java.util.List;

public class DBFlowDataRepository implements TravelDataRepository {
  //region Fields

  private final EventBus _eventBus;

  //endregion

  //region Constructors

  public DBFlowDataRepository(EventBus eventBus) {
    _eventBus = eventBus;
  }

  //endregion

  //region TravelDataRepository impl

  @Override
  public Observable<List<RouteData>> selectAll() {
    return Observable.fromCallable(this::selectAllSync);
  }

  @Override public Observable<RouteData> select(long id) {
    return Observable.fromCallable(() -> selectSync(id));
  }

  @DebugLog
  @Override public boolean routeExists(long id) {
    if (id <= 0) {
      return false;
    }

    return SQLite.select(Method.count()).from(RouteData.class)
        .where(RouteData_Table._id.eq(id)).count() > 0;
  }

  @Override
  public Observable<Long> delete(RouteData routeData) {
    return Observable.fromCallable(() -> deleteSync(routeData));
  }

  @Override
  public Observable<Long> insertOrUpdate(RouteData routeData) {
    return Observable.fromCallable(() -> insertOrUpdateSync(routeData));
  }

  //endregion

  //region Methods

  @DebugLog
  private List<RouteData> selectAllSync() {
    return SQLite.select().from(RouteData.class).queryList();
  }

  @DebugLog
  private RouteData selectSync(long id) {
    RouteData routeData = SQLite.select().from(RouteData.class)
        .where(RouteData_Table._id.eq(id)).querySingle();

    if (routeData != null) {
      routeData.loadFull();
    }

    return routeData;
  }

  @DebugLog
  private long deleteSync(RouteData routeData) {
    if (!routeData.exists()) {
      Timber.w("Trying to delete not existing route data title=%s", routeData.getTitle());
      return 0;
    }

    for (NoteSpec noteSpec : routeData.getNoteSpecs()) {
      Timber.d("Posting deleted note spec '%s'", noteSpec.getCaption());
      _eventBus.post(new NoteSpecDeletedEvent(noteSpec));
    }

    long routeId = routeData.getId();
    routeData.delete();

    Timber.d("Posting delete route event id=%d", routeId);
    _eventBus.post(new RouteDeleteEvent(routeData));
    return 1;
  }

  private long insertOrUpdateSync(final RouteData routeData) {
    Object event;
    if (routeExists(routeData.getId())) {
      event = new UpdatedRouteEvent(routeData);
    } else {
      event = new NewRouteEvent(routeData);
    }

    routeData.save();
    Timber.d("Posting %s id=%d", event.getClass().getSimpleName(), routeData.getId());
    _eventBus.post(event);
    return 1;
  }

  //endregion
}
