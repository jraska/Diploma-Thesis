package com.jraska.pwmd.travel.navigation;

import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.RouteData;

import java.util.Iterator;

/**
 * Class which hould determine teh closest point of the rout from current latLng
 * and which point is supposed to be next.
 */
public class RouteCursor {
  //region Fields

  private final RouteData _routeData;

  private Position _lastPosition;

  //endregion

  //region Constructor

  public RouteCursor(RouteData routeData) {
    _routeData = routeData;
    if (routeData.getPositions().isEmpty()) {
      throw new IllegalArgumentException("Route data with empty positions");
    }
  }

  //endregion

  //region Methods

  protected LatLng findClosestPosition(LatLng position) {
    Iterator<LatLng> iterator = _routeData.getPath().iterator();
    LatLng closest = iterator.next();

    double closestDistanceSquare = distanceSquare(closest, position);

    while (iterator.hasNext()) {
      LatLng next = iterator.next();
      double nextDistance = distanceSquare(next, position);
      if (nextDistance < closestDistanceSquare) {
        closestDistanceSquare = nextDistance;
        closest = next;
      }
    }

    return closest;
  }

  private double distanceSquare(LatLng closest, LatLng position) {
    double latDiff = closest._latitude - position._latitude;
    double lonDiff = closest._longitude - position._longitude;

    return latDiff * latDiff + lonDiff * lonDiff;
  }

  protected void onNextPosition(Position position) {
    _lastPosition = position;
  }

  //endregion
}
