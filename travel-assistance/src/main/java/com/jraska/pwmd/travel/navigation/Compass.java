package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.dagger.PerApp;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

@PerApp
public class Compass {
  //region Constants

  public static final float UNKNOWN_BEARING = Float.MIN_VALUE;

  //endregion

  //region Fields

  private Location _lastLocation;

  //endregion

  //region Constructors

  @Inject
  public Compass(EventBus eventBus) {
    eventBus.register(this);
  }

  //endregion

  //region Methods

  @Subscribe
  public void onNewLocation(Location location) {
    _lastLocation = location;
  }

  public float getBearing() {
    if (_lastLocation != null && _lastLocation.hasBearing()) {
      return _lastLocation.getBearing();
    }

    return UNKNOWN_BEARING;
  }

  //endregion
}
