package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.Position;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import static com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy.UNKNOWN_DIRECTION;

public class Navigator {
  //region Constants

  public static final String NAVIGATOR_BUS_NAME = "navigatorBus";

  //endregion

  //region Fields

  private final EventBus _eventBus;
  private final EventBus _systemBus;

  private final DirectionDecisionStrategy _realDirectionStrategy;
  private final DirectionDecisionStrategy _followingRouteDirectionStrategy;

  private int _lastDirectionDegrees = UNKNOWN_DIRECTION;

  //endregion

  //region Constructors

  @Inject @PerApp
  public Navigator(@NonNull @Named(NAVIGATOR_BUS_NAME) EventBus eventBus, EventBus systemBus,
                   DirectionDecisionStrategy realDirectionStrategy,
                   DirectionDecisionStrategy routeDirectionStrategy) {
    ArgumentCheck.notNull(eventBus);
    ArgumentCheck.notNull(systemBus);

    _eventBus = eventBus;
    _systemBus = systemBus;

    _realDirectionStrategy = realDirectionStrategy;
    _followingRouteDirectionStrategy = routeDirectionStrategy;

    _systemBus.register(this);
  }

  //endregion

  //region Properties

  public EventBus getEventBus() {
    return _eventBus;
  }

  public int getLastRequiredDirection() {
    return _lastDirectionDegrees;
  }

  //endregion

  //region Methods

  public void onEvent(Position position) {
    _realDirectionStrategy.addPoint(position.latLng);
    int desiredDirection = computeDesiredDirection();
    onNewDirection(desiredDirection);
  }

  protected int computeDesiredDirection() {
    int realDirection = _realDirectionStrategy.getDirection();
    if (realDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    int routeDirection = _followingRouteDirectionStrategy.getDirection();
    if (routeDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    int userDirection = 90 + routeDirection - realDirection;
    if (userDirection < 0) {
      return userDirection + 360;
    }
    return userDirection;
  }

  protected void onNewDirection(int degrees) {
    if (degrees != _lastDirectionDegrees) {
      _lastDirectionDegrees = degrees;
      _eventBus.post(new RequiredDirectionEvent(degrees));
    }
  }

  //endregion

  //region Nested classes

  public static final class RequiredDirectionEvent {
    public final int _directionDegrees;

    public RequiredDirectionEvent(int directionDegrees) {
      _directionDegrees = directionDegrees;
    }
  }

  //endregion
}
