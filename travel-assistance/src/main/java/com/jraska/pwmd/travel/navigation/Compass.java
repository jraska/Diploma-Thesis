package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.dagger.PerApp;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Class determining current direction of the user and its device.
 * Currently it uses just last GPS coordinates to determine direction
 */
@PerApp
public class Compass {
  //region Constants

  public static final float UNKNOWN_BEARING = Float.MIN_VALUE;

  //endregion

  //region Fields

  private Location _previousLocation;
  private Location _lastLocation;

  //endregion

  //region Constructors

  @Inject
  public Compass(EventBus systemBus) {
    systemBus.register(this);
  }


  //endregion

  //region Methods

  @Subscribe
  public void onNewLocation(Location location) {
    _previousLocation = _lastLocation;
    _lastLocation = location;
  }

  public float getBearing() {
    if (_previousLocation == null || _lastLocation == null) {
      return UNKNOWN_BEARING;
    }

    return _previousLocation.bearingTo(_lastLocation);
  }

  //endregion
}
