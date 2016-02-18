package com.jraska.pwmd.travel.ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Common util class for common map functions
 */
public final class MapHelper {
  //region Constants

  public static final int ZOOM = 17;

  //endregion

  //region Methods

  public static void configureMap(GoogleMap map) {
    map.setMyLocationEnabled(true);
    map.getUiSettings().setCompassEnabled(true);
    map.getUiSettings().setZoomControlsEnabled(true);
  }

  public static LatLng toLatLng(Location location) {
    return new LatLng(location.getLatitude(), location.getLongitude());
  }

  //endregion

  //region Constructors

  private MapHelper() {
    throw new AssertionError("No instances");
  }

  //endregion
}
