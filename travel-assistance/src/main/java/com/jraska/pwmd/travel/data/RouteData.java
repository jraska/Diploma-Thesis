package com.jraska.pwmd.travel.data;

import java.util.Date;
import java.util.UUID;

public class RouteData {
  //region Fields

  private final RouteDescription _description;
  private final Path _route;

  //endregion

  //region Constructors

  public RouteData(RouteDescription description, Path route) {
    //TODO: checks

    _description = description;
    _route = route;
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

  //endregion

  //region Object impl

  @Override
  public String toString() {
    return "RouteData{" +
        "_description=" + _description +
        ", _route=" + _route +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteData routeData = (RouteData) o;

    if (!_description.equals(routeData._description)) return false;
    if (!_route.equals(routeData._route)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = _description.hashCode();
    result = 31 * result + _route.hashCode();
    return result;
  }

  //endregion
}
