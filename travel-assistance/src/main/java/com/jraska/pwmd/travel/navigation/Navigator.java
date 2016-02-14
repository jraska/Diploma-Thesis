package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

import javax.inject.Inject;

import static com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy.UNKNOWN_DIRECTION;

@PerApp
public class Navigator {
  //region Fields

  private final EventBus _eventBus;

  private final Compass _compass;
  private final DirectionDecisionStrategy _routeDirectionStrategy;

  private int _lastDirectionDegrees = UNKNOWN_DIRECTION;
  private long _currentRouteId;

  //endregion

  //region Constructors

  @Inject
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

  public int getCompassDirection() {
    return _compass.getDirection();
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

  //region Static Methods

  public static LatLng toGoogleLatLng(@NonNull com.jraska.pwmd.core.gps.LatLng latLng) {
    return new LatLng(latLng._latitude, latLng._longitude);
  }

  public void startNavigation(long routeId) {
    // TODO: 13/02/16
    _currentRouteId = routeId;
    Timber.i("Navigation for route %d started.", _currentRouteId);
  }

  public void stopNavigation() {
    Timber.i("Navigation for route %d ended", _currentRouteId);
    _currentRouteId = 0;
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
