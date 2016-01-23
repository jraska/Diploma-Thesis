package com.jraska.pwmd.travel.data;

import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Table(database = TravelDatabase.class)
public class RouteData extends BaseModel {
  //region Fields

  @PrimaryKey(autoincrement = true) long _id;
  @Column Date _start;
  @Column Date _end;
  @Column String _title;

  List<TransportChangeSpec> _transportChangeSpecs;
  List<NoteSpec> _noteSpecs;
  List<DbPosition> _positions;

  //endregion

  //region Constructors

  RouteData() {
  }

  public RouteData(RouteDescription description, List<LatLng> route) {
    this(description, route, Collections.<TransportChangeSpec>emptyList(),
        Collections.<NoteSpec>emptyList());
  }

  public RouteData(RouteDescription description, List<LatLng> route,
                   List<TransportChangeSpec> changeSpecs, List<NoteSpec> noteSpecs) {
    _title = description.getTitle();
    _start = description.getStart();
    _end = description.getEnd();

    _positions = new ArrayList<>(route.size());
    for (LatLng latLng : route) {
      _positions.add(new DbPosition(latLng));
    }

    _transportChangeSpecs = Collections.unmodifiableList(changeSpecs);
    _noteSpecs = noteSpecs;
  }

  //endregion

  //region Properties

  public long getId() {
    return _id;
  }

  public Date getStart() {
    return _start;
  }

  public Date getEnd() {
    return _end;
  }

  public String getTitle() {
    return _title;
  }

  public List<LatLng> getPath() {
    List<DbPosition> positions = getPositions();
    ArrayList<LatLng> list = new ArrayList<>(positions.size());
    for (DbPosition pos : positions) {
      list.add(pos.latLng);
    }

    return list;
  }

  @OneToMany(methods = {OneToMany.Method.DELETE}, variableName = "_positions")
  public List<DbPosition> getPositions() {
    if (_positions == null) {
      if (exists()) {
        _positions = SQLite.select().from(DbPosition.class)
            .where(DbPosition_Table._routeId.eq(_id)).queryList();
      } else {
        throw new IllegalStateException();
      }
    }

    return _positions;
  }

  @OneToMany(methods = {OneToMany.Method.DELETE}, variableName = "_transportChangeSpecs")
  public List<TransportChangeSpec> getTransportChangeSpecs() {
    if (_transportChangeSpecs == null) {
      if (exists()) {
        _transportChangeSpecs = SQLite.select().from(TransportChangeSpec.class)
            .where(TransportChangeSpec_Table._routeId.eq(_id)).queryList();
      } else {
        throw new IllegalStateException();
      }
    }

    return _transportChangeSpecs;
  }

  @OneToMany(methods = {OneToMany.Method.DELETE}, variableName = "_noteSpecs")
  public List<NoteSpec> getNoteSpecs() {
    if (_noteSpecs == null) {
      if (exists()) {
        _noteSpecs = SQLite.select().from(NoteSpec.class)
            .where(NoteSpec_Table._routeId.eq(_id)).queryList();
      } else {
        throw new IllegalStateException();
      }
    }

    return _noteSpecs;
  }

  //endregion

  //region Model overrides

  @Override public void save() {
    List<DbPosition> positions = getPositions();

    super.save();

    for (NoteSpec noteSpec : getNoteSpecs()) {
      noteSpec._routeId = _id;
      noteSpec.save();
    }

    for (DbPosition position : positions) {
      position._routeId = _id;
      position.save();
    }

    for (TransportChangeSpec pec : getTransportChangeSpecs()) {
      pec._routeId = _id;
      pec.save();
    }
  }

  public RouteDescription getDescription() {
    return new RouteDescription(_start, _end, _title);
  }

  //endregion

  //region Object impl

  @Override public String toString() {
    return "RouteData{" +
        "_id=" + _id +
        ", _start=" + _start +
        ", _end=" + _end +
        ", _title='" + _title + '\'' +
        ", _transportChangeSpecs=" + _transportChangeSpecs +
        ", _noteSpecs=" + _noteSpecs +
        ", _positions=" + _positions +
        '}';
  }

  // TODO: 23/01/16 Equals etc

  //endregion
}
