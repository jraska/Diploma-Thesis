package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.pwmd.core.gps.LatLng;
import hugo.weaving.DebugLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public class ClosestLocationFinder {
  //region Fields

  private final List<LatLng> _pathReadOnly;

  //endregion

  //region Constructor

  public ClosestLocationFinder(List<LatLng> path) {
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Route data with empty positions");
    }

    _pathReadOnly = fastRandomAccessReadOnly(path);
  }

  private static List<LatLng> fastRandomAccessReadOnly(List<LatLng> route) {
    if (route instanceof RandomAccess) {
      return Collections.unmodifiableList(route);
    } else {
      return Collections.unmodifiableList(new ArrayList<>(route));
    }
  }

  //endregion

  //region Properties

  List<LatLng> getPath() {
    return _pathReadOnly;
  }

  //endregion

  //region Methods

  public Location findClosestLocation(Location location) {
    LatLng closestPosition = findClosestLocation(LatLng.fromLocation(location));
    return closestPosition.toLocation();
  }

  @DebugLog
  public LatLng findClosestLocation(LatLng location) {
    int closestIndex = findClosestLocationIndex(location);
    return _pathReadOnly.get(closestIndex);
  }

  public int findClosestLocationIndex(LatLng location) {
    int closestIndex = 0;

    double closestDistanceSquare = distanceSquare(_pathReadOnly.get(0), location);
    for (int i = 1, size = _pathReadOnly.size(); i < size; i++) {
      LatLng next = _pathReadOnly.get(i);
      double nextDistance = distanceSquare(next, location);
      if (nextDistance < closestDistanceSquare) {
        closestDistanceSquare = nextDistance;
        closestIndex = i;
      }
    }

    return closestIndex;
  }

  private double distanceSquare(LatLng first, LatLng second) {
    double latDiff = first._latitude - second._latitude;
    double lonDiff = first._longitude - second._longitude;

    return latDiff * latDiff + lonDiff * lonDiff;
  }

  //endregion
}
