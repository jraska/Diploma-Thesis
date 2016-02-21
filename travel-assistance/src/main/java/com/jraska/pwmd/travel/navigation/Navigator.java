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
  //region Fields

  private final EventBus _eventBus;
  private final Compass _compass;

  private float _lastBearing = Compass.UNKNOWN_BEARING;

  @NonNull
  private State _state = State.EMPTY;

  private RouteData _currentRoute;
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

  //endregion

  //region Methods

  protected static float computeDesiredDirection(float realDirection, float routeDirection) {
    float userDirection = routeDirection - realDirection;
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

    Timber.i("Navigation route id=%s, title=%s started.", routeData.getId(), routeData.getTitle());
    _currentRoute = routeData;

    if (!_eventBus.isRegistered(this)) {
      _eventBus.register(this);
    }

    _routeCursor = new RouteCursor(_currentRoute.getPath());
    _state = new ApproachingToRouteState();
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
    _state = State.EMPTY;
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
      float directionToRoute = computeDesiredDirectionToRoute(location);

      onNewDirection(directionToRoute);
    }

    protected float computeDesiredDirectionToRoute(Location location) {
      int realBearing = (int) _compass.getBearing();
      if (realBearing == Compass.UNKNOWN_BEARING) {
        return realBearing;
      }

      Location closestLocation = _routeCursor.findClosestLocation(location);
      float bearingToRoute = location.bearingTo(closestLocation);

      return computeDesiredDirection(realBearing, bearingToRoute);
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
