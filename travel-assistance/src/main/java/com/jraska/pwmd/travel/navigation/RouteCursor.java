package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;

import java.util.List;

public class RouteCursor {
  //region Fields

  private final ClosestLocationFinder _closestLocationFinder;

  //endregion

  //region Constructors

  public RouteCursor(ClosestLocationFinder closestLocationFinder) {
    ArgumentCheck.notNull(closestLocationFinder);

    if (closestLocationFinder.getPath().size() <= 1) {
      throw new IllegalArgumentException("Need to have at least two points to compute direction.");
    }

    _closestLocationFinder = closestLocationFinder;
  }

  //endregion

  //region Methods

  public Direction getRouteDirection(Location location) {
    int closestLocationIndex = _closestLocationFinder.findClosestLocationIndex(LatLng.fromLocation(location));

    List<LatLng> path = _closestLocationFinder.getPath();
    LatLng closest = path.get(closestLocationIndex);

    LatLng firstLocation;
    LatLng secondLocation;

    // In case we are at the end
    if (closestLocationIndex == path.size() - 1) {
      firstLocation = path.get(closestLocationIndex - 1);
      secondLocation = path.get(closestLocationIndex);
    } else {
      firstLocation = path.get(closestLocationIndex);
      secondLocation = path.get(closestLocationIndex + 1);
    }


    float direction = firstLocation.bearingTo(secondLocation);
    float distance = closest.distanceTo(location);

    return new Direction(direction, distance);
  }

  //endregion

  //region Nested classes

  /**
   * Modifiable holder for more then one result
   */
  static class Direction {
    final float _routeBearing;
    final float _distanceToRoute;

    private Direction(float routeBearing, float distanceToRoute) {
      _routeBearing = routeBearing;
      _distanceToRoute = distanceToRoute;
    }
  }

  //endregion
}
