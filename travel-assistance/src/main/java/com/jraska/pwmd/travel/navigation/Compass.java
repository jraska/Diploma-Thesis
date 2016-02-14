package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.Position;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Class determining current direction of the user and its device.
 * <p/>
 * Currently it uses jsut last GPS coordinates to determine direction
 */
@PerApp
public class Compass {
  //region Fields

  private final DirectionDecisionStrategy _decisionStrategy;
  private final EventBus _systemBus;

  //endregion

  //region Constructors

  @Inject
  public Compass(@NonNull DirectionDecisionStrategy decisionStrategy, EventBus systemBus) {
    _decisionStrategy = decisionStrategy;
    _systemBus = systemBus;

    _systemBus.register(this);
  }


  //endregion

  //region Methods

  @Subscribe
  public void onNewPosition(Position position) {
    _decisionStrategy.addPoint(position.latLng);
  }

  public int getDirection() {
    return _decisionStrategy.getDirection();
  }

  //endregion
}
