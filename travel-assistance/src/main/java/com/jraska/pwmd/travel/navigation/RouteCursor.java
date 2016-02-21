package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.pwmd.core.gps.LatLng;
import hugo.weaving.DebugLog;

import java.util.Iterator;
import java.util.List;

/**
 * Class which hould determine teh closest point of the rout from current latLng
 * and which point is supposed to be next.
 */
public class RouteCursor {
  //region Fields

  private final List<LatLng> _route;

  private Location _lastLocation;

  //endregion

  //region Constructor

  public RouteCursor(List<LatLng> route) {
    _route = route;
    if (route.isEmpty()) {
      throw new IllegalArgumentException("Route data with empty positions");
    }
  }

  //endregion

  //region Methods

  public Location findClosestLocation(Location location) {
    LatLng closestPosition = findClosestLocation(LatLng.fromLocation(location));
    return closestPosition.toLocation();
  }

  @DebugLog
  public LatLng findClosestLocation(LatLng position) {
    Iterator<LatLng> iterator = _route.iterator();
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

  private double distanceSquare(LatLng first, LatLng second) {
    double latDiff = first._latitude - second._latitude;
    double lonDiff = first._longitude - second._longitude;

    return latDiff * latDiff + lonDiff * lonDiff;
  }

  public float getCurrentDirection() {
    if (_lastLocation != null) {
      Location closestLocation = findClosestLocation(_lastLocation);

    }

    return Compass.UNKNOWN_BEARING; // TODO: 18/02/16 now just c.oses position of route - NOT CORRECT
  }

  //endregion
}
