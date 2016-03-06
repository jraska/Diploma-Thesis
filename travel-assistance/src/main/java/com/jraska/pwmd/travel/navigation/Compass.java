package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.collection.CircularFifoQueue;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

@PerApp
public class Compass {
  //region Constants

  public static final float UNKNOWN_BEARING = Float.MIN_VALUE;

  //endregion

  //region Fields

  private final CircularFifoQueue<Location> _locations = new CircularFifoQueue<>(4);

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
    _locations.add(location);
  }

  public float getBearing() {
    int size = _locations.size();
    if (size == 0) {
      return UNKNOWN_BEARING;
    }

    for (int i = size - 1; i >= 0; i--) {
      Location location = _locations.get(i);
      if (location.hasBearing()) {
        return location.getBearing();
      }
    }

    return UNKNOWN_BEARING;
  }

  //endregion
}
