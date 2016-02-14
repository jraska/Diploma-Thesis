package com.jraska.pwmd.travel.persistence;

import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteData_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.AsyncModel;
import com.raizlabs.android.dbflow.structure.Model;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import java.util.List;

public class DBFlowDataRepository implements TravelDataRepository {
  //region Fields

  private final EventBus _eventBus;

  private final AsyncModel.OnModelChangedListener _insertListener = new AsyncModel.OnModelChangedListener() {
    @Override public void onModelChanged(Model model) {
      RouteData routeData = (RouteData) model;
      Timber.d("Posting new route event id=" + routeData.getId());

      _eventBus.post(new NewRouteEvent((RouteData) model));
    }
  };

  //endregion

  //region Constructors

  public DBFlowDataRepository(EventBus eventBus) {
    _eventBus = eventBus;
  }

  //endregion

  //region TravelDataRepository impl

  @Override public List<RouteData> selectAll() {
    return SQLite.select().from(RouteData.class).queryList();
  }

  @Override public RouteData select(long id) {
    return SQLite.select().from(RouteData.class).where(RouteData_Table._id.eq(id)).querySingle();
  }

  @Override public boolean routeExists(long id) {
    return SQLite.select(Method.count()).from(RouteData.class)
        .where(RouteData_Table._id.eq(id)).count() > 0;
  }

  @Override public long delete(RouteData routeData) {
    routeData.async().withListener(new DeletedListener(routeData.getNoteSpecs())).delete();

    // COde to test delete and insert
//    routeData.delete();
//    _eventBus.post(new RouteDeletedEvent(routeData));
    return 1;
  }

  @Override public long insertOrUpdate(final RouteData routeData) {
    routeData.async().withListener(_insertListener).save();

    // code to test deleet and insert
//    routeData.save();
//    _eventBus.post(new NewRouteEvent(routeData));
    return 1;
  }

  //endregion

  //region Nested classes

  class DeletedListener implements AsyncModel.OnModelChangedListener {
    private final List<NoteSpec> _noteSpecs;

    public DeletedListener(List<NoteSpec> noteSpecs) {
      _noteSpecs = noteSpecs;
    }

    @Override
    public void onModelChanged(Model model) {
      for (NoteSpec noteSpec : _noteSpecs) {
        _eventBus.post(new NoteSpecDeletedEvent(noteSpec));
      }

      RouteData routeData = (RouteData) model;
      Timber.d("Posting deleted route event id=" + routeData.getId());
      _eventBus.post(new RouteDeletedEvent(routeData));
    }
  }

  //endregion
}
