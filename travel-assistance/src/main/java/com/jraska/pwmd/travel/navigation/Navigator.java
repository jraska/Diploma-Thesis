package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import static com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy.UNKNOWN_DIRECTION;

public class Navigator {
  //region Fields

  private final EventBus _eventBus;

  private final Compass _compass;
  private final DirectionDecisionStrategy _routeDirectionStrategy;

  private int _lastDirectionDegrees = UNKNOWN_DIRECTION;

  //endregion

  //region Constructors

  @Inject @PerApp
  public Navigator(EventBus eventBus, @NonNull Compass compass,
                   @NonNull DirectionDecisionStrategy routeDirectionStrategy) {
    ArgumentCheck.notNull(eventBus);
    ArgumentCheck.notNull(compass);
    ArgumentCheck.notNull(routeDirectionStrategy);

    _eventBus = eventBus;
    _compass = compass;
    _routeDirectionStrategy = routeDirectionStrategy;
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

  // TODO: 20/01/16 Figure out when the direction is recomputed

  protected int computeDesiredDirection() {
    int realDirection = _compass.getDirection();
    if (realDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    int routeDirection = _routeDirectionStrategy.getDirection();
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
