package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.data.RouteData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;

import static com.jraska.pwmd.travel.navigation.Compass.UNKNOWN_BEARING;

@PerApp
public class Navigator {
  //region Constants

  private static final int RANGE_ON_ROUTE = 30;

  //endregion

  //region Fields

  private final EventBus _eventBus;
  private final Compass _compass;

  private float _lastBearing = UNKNOWN_BEARING;

  @NonNull
  private State _state = State.EMPTY;

  private RouteData _currentRoute;
  private ClosestLocationFinder _closestLocationFinder;
  private RouteCursor _routeCursor;

  //endregion

  //region Constructors

  @Inject
  public Navigator(@NonNull EventBus eventBus, @NonNull Compass compass) {
    ArgumentCheck.notNull(eventBus);
    ArgumentCheck.notNull(compass);

    _eventBus = eventBus;
    _compass = compass;
  }

  //endregion

  //region Properties

  public EventBus getEventBus() {
    return _eventBus;
  }

  public float getLastRequiredDirection() {
    return _lastBearing;
  }

  public float getUserDirection() {
    return _compass.getBearing();
  }

  public boolean isNavigating() {
    return _currentRoute != null;
  }

  @NonNull State getState() {
    return _state;
  }

  //endregion

  //region Methods

  protected static float computeDesiredBearing(float realBearing, float routeBearing) {
    float userDirection = routeBearing - realBearing;
    if (userDirection < -180) {
      return userDirection + 360;
    }
    return userDirection;
  }

  protected void onNewDirection(float bearing) {
    if (bearing != _lastBearing) {
      _lastBearing = bearing;
      _eventBus.post(new RequiredDirectionEvent(bearing));
    }
  }

  @Subscribe
  public void onNewLocation(Location location) {
    _state.onNewLocation(location);
  }

  public void startNavigation(RouteData routeData) {
    ArgumentCheck.notNull(routeData);
    if (routeData == _currentRoute) {
      return;
    }

    Timber.i("Navigation route id=%d, title='%s' started.", routeData.getId(), routeData.getTitle());
    _currentRoute = routeData;

    if (!_eventBus.isRegistered(this)) {
      _eventBus.register(this);
    }

    _closestLocationFinder = new ClosestLocationFinder(_currentRoute.getPath());
    _routeCursor = new RouteCursor(_closestLocationFinder);
    _state = new ApproachingToRouteState();
  }

  public void stopNavigation() {
    if (_currentRoute == null) {

      return;
    }

    if (_eventBus.isRegistered(this)) {
      _eventBus.unregister(this);
    }

    Timber.i("Navigation for route id=%d title='%s' ended",
        _currentRoute.getId(), _currentRoute.getTitle());
    _currentRoute = null;
    _state = State.EMPTY;
  }

  protected void onNewBearing(float bearing) {
    float desiredBearing;
    float realBearing = _compass.getBearing();
    if (realBearing != UNKNOWN_BEARING) {
      desiredBearing = computeDesiredBearing(realBearing, bearing);
    } else {
      desiredBearing = UNKNOWN_BEARING;
    }

    onNewDirection(desiredBearing);
  }

  //endregion

  //region Nested classes

  interface State {
    void onNewLocation(Location location);

    State EMPTY = new State() {
      @Override public void onNewLocation(Location location) {
      }
    };
  }

  class ApproachingToRouteState implements State {

    @Override
    public void onNewLocation(Location location) {
      Location closestLocation = _closestLocationFinder.findClosestLocation(location);
      float distanceTo = location.distanceTo(closestLocation);
      Timber.v("Computed distance to route: %s", distanceTo);
      if (distanceTo < RANGE_ON_ROUTE) {
        Timber.i("Location is on the route, switching to OnRouteNavigationState.");
        _state = new OnRouteState();
        _state.onNewLocation(location);
        return;
      }

      float bearingToRoute = location.bearingTo(closestLocation);
      onNewBearing(bearingToRoute);
    }
  }

  class OnRouteState implements State {
    @Override
    public void onNewLocation(Location location) {
      RouteCursor.Direction routeDirection = _routeCursor.getRouteDirection(location);
      Timber.v("Distance to closest computed: %s", routeDirection._distanceToRoute);

      if (routeDirection._distanceToRoute > RANGE_ON_ROUTE) {
        Timber.i("Location is too far away from route, switching to Approaching state.");
        _state = new ApproachingToRouteState();
        _state.onNewLocation(location);
      } else {
        onNewBearing(routeDirection._routeBearing);
      }
    }
  }

  public static final class RequiredDirectionEvent {
    public final float _bearing;

    public RequiredDirectionEvent(float bearing) {
      _bearing = bearing;
    }
  }

  //endregion
}
