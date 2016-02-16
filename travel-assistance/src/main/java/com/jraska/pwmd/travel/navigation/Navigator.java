package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.RouteData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
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
  private RouteData _currentRoute;
  private RouteCursor _routeCursor;

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

  public int getUserDirection() {
    return _compass.getDirection();
  }

  //endregion

  //region Methods

  protected int computeDesiredDirection() {
    int realDirection = _compass.getDirection();
    if (realDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    int routeDirection = _routeDirectionStrategy.getDirection();
    if (routeDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    return computeDesiredDirection(realDirection, routeDirection);
  }

  private int computeDesiredDirection(int realDirection, int routeDirection) {
    int userDirection = 90 + routeDirection - realDirection;
    if (userDirection < 0) {
      return userDirection + 360;
    }
    return userDirection;
  }

  protected int computeDirectionToRoute(Position currentPosition) {
    int realDirection = _compass.getDirection();
    if (realDirection == UNKNOWN_DIRECTION) {
      return UNKNOWN_DIRECTION;
    }

    com.jraska.pwmd.core.gps.LatLng current = _routeCursor.findClosestPosition(currentPosition.latLng);

    int requiredDirection = DirectionDecisionStrategy.getDirection(currentPosition.latLng, current);

    return computeDesiredDirection(realDirection, requiredDirection);
  }

  protected void onNewDirection(int degrees) {
    if (degrees != _lastDirectionDegrees) {
      _lastDirectionDegrees = degrees;
      _eventBus.post(new RequiredDirectionEvent(degrees));
    }
  }

  @Subscribe
  protected void onNewPosition(Position position) {
    int directionToRoute = computeDirectionToRoute(position);

    onNewDirection(directionToRoute);
  }

  //endregion

  //region Static Methods

  public static LatLng toGoogleLatLng(@NonNull com.jraska.pwmd.core.gps.LatLng latLng) {
    return new LatLng(latLng._latitude, latLng._longitude);
  }

  public void startNavigation(RouteData routeData) {
    ArgumentCheck.notNull(routeData);
    if (routeData == _currentRoute) {
      return;
    }

    Timber.i("Navigation route id=%s, title=%s started.", routeData.getId(), routeData.getTitle());
    _currentRoute = routeData;

    if (!_eventBus.isRegistered(this)) {
      _eventBus.register(this);
    }

    _routeCursor = new RouteCursor(_currentRoute.getPath());
  }

  public void stopNavigation() {
    if (_currentRoute == null) {
      return;
    }

    if (_eventBus.isRegistered(this)) {
      _eventBus.unregister(this);
    }

    Timber.i("Navigation for route %d ended", _currentRoute.getId());
    _currentRoute = null;
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
