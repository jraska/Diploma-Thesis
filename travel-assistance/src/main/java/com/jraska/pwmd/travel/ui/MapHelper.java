package com.jraska.pwmd.travel.ui;

import com.google.android.gms.maps.GoogleMap;

/**
 * Common util class for common map functions
 */
public final class MapHelper {
  //region Constants

  public static final int ZOOM = 17;

  //endregion

  //region Methods

  public static void configureMap(GoogleMap map){
    map.setMyLocationEnabled(true);
    map.getUiSettings().setCompassEnabled(true);
    map.getUiSettings().setZoomControlsEnabled(true);
  }

  //endregion

  //region Constructors

  private MapHelper() {
    throw new AssertionError("No instances");
  }

  //endregion
}
