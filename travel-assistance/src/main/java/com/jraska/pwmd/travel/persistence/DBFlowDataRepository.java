package com.jraska.pwmd.travel.persistence;

import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteData_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import hugo.weaving.DebugLog;
import org.greenrobot.eventbus.EventBus;
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

  @DebugLog
  @Override public List<RouteData> selectAll() {
    return SQLite.select().from(RouteData.class).queryList();
  }

  @DebugLog
  @Override public RouteData select(long id) {
    return SQLite.select().from(RouteData.class).where(RouteData_Table._id.eq(id)).querySingle();
  }

  @DebugLog
  @Override public boolean routeExists(long id) {
    return SQLite.select(Method.count()).from(RouteData.class)
        .where(RouteData_Table._id.eq(id)).count() > 0;
  }

  @DebugLog
  @Override public long delete(RouteData routeData) {
    if (!routeData.exists()) {
      Timber.w("Trying to delete not existing route data title=%s", routeData.getTitle());
      return 0;
    }

    for (NoteSpec noteSpec : routeData.getNoteSpecs()) {
      Timber.d("Posting deleted note spec '%s'", noteSpec.getCaption());
      _eventBus.post(new NoteSpecDeletedEvent(noteSpec));
    }

    Timber.d("Posting deleted route event id=%d", routeData.getId());
    _eventBus.post(new RouteDeletedEvent(routeData));

    routeData.delete();
    return 1;
  }

  @DebugLog
  @Override public long insertOrUpdate(final RouteData routeData) {
    routeData.save();
    Timber.d("Posting new route event id=%d", routeData.getId());
    _eventBus.post(new NewRouteEvent(routeData));
    return 1;
  }

  //endregion
}
