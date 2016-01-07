package com.jraska.pwmd.travel.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RouteData {
  //region Fields

  private final RouteDescription _description;
  private final Path _route;
  private final List<TransportChangeSpec> _transportChangeSpecs;
  private final List<NoteSpec> _noteSpecs;


  //endregion

  //region Constructors


  public RouteData(RouteDescription description, Path route) {
    this(description, route, Collections.<TransportChangeSpec>emptyList(),
        Collections.<NoteSpec>emptyList());
  }

  public RouteData(RouteDescription description, Path route,
                   List<TransportChangeSpec> changeSpecs, List<NoteSpec> noteSpecs) {
    //TODO: checks

    _description = description;
    _route = route;
    _transportChangeSpecs = Collections.unmodifiableList(changeSpecs);
    _noteSpecs = noteSpecs;
  }

  //endregion

  //region Properties

  public RouteDescription getDescription() {
    return _description;
  }

  public UUID getId() {
    return _description.getId();
  }

  public Path getPath() {
    return _route;
  }

  public Date getStart() {
    return _description.getStart();
  }

  public Date getEnd() {
    return _description.getEnd();
  }

  public String getTitle() {
    return _description.getTitle();
  }

  public List<TransportChangeSpec> getTransportChangeSpecs() {
    return _transportChangeSpecs;
  }

  public List<NoteSpec> getNoteSpecs() {
    return _noteSpecs;
  }

  //endregion

  //region Object impl

  @Override
  public String toString() {
    return "RouteData{" +
        "_description=" + _description +
        ", _route=" + _route +
        ", _transportChangeSpecs=" + _transportChangeSpecs +
        ", _pictureSpecs=" + _noteSpecs +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteData routeData = (RouteData) o;

    if (_description != null ? !_description.equals(routeData._description) : routeData._description != null) {
      return false;
    }
    if (_route != null ? !_route.equals(routeData._route) : routeData._route != null) return false;
    if (_transportChangeSpecs != null ? !_transportChangeSpecs.equals(routeData._transportChangeSpecs) : routeData._transportChangeSpecs != null) {
      return false;
    }
    return _noteSpecs != null ? _noteSpecs.equals(routeData._noteSpecs) : routeData._noteSpecs == null;

  }

  @Override
  public int hashCode() {
    int result = _description != null ? _description.hashCode() : 0;
    result = 31 * result + (_route != null ? _route.hashCode() : 0);
    result = 31 * result + (_transportChangeSpecs != null ? _transportChangeSpecs.hashCode() : 0);
    result = 31 * result + (_noteSpecs != null ? _noteSpecs.hashCode() : 0);
    return result;
  }

  //endregion
}
