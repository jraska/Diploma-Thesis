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


  //endregion

  //region Constructors


  public RouteData(RouteDescription description, Path route) {
    this(description, route, Collections.<TransportChangeSpec>emptyList());
  }

  public RouteData(RouteDescription description, Path route, List<TransportChangeSpec> specs) {
    //TODO: checks

    _description = description;
    _route = route;
    _transportChangeSpecs = Collections.unmodifiableList(specs);
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

  //endregion

  //region Object impl

  @Override
  public String toString() {
    return "RouteData{" +
        "_description=" + _description +
        ", _route=" + _route +
        ", _transportChangeSpecs=" + _transportChangeSpecs +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteData routeData = (RouteData) o;

    if (!_description.equals(routeData._description)) return false;
    if (!_route.equals(routeData._route)) return false;
    return _transportChangeSpecs.equals(routeData._transportChangeSpecs);

  }

  @Override
  public int hashCode() {
    int result = _description.hashCode();
    result = 31 * result + _route.hashCode();
    result = 31 * result + _transportChangeSpecs.hashCode();
    return result;
  }


  //endregion
}
