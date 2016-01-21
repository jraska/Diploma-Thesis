package com.jraska.pwmd.travel.navigation;

import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.RouteData;

/**
 * Class which hould determine teh closest point of the rout from current position
 * and which point is supposed to be next.
 */
public class RouteCursor {
  //region Fields

  private final RouteData _routeData;

  private Position _lastPosition;

  //endregion

  //region Constructor

  public RouteCursor(RouteData routeData, Position lastPosition) {
    _routeData = routeData;

    onNextPosition(lastPosition);
  }

  //endregion

  //region Methods

  protected void onNextPosition(Position position) {
    _lastPosition = position;
  }

  //endregion
}
