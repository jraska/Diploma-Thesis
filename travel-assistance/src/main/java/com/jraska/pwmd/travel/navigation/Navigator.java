package com.jraska.pwmd.travel.navigation;

import android.location.Location;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
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
  private RouteData _currentRoute;
  private RouteCursor _routeCursor;

  //endregion

  //region Constructors

  @Inject
  public Navigator(EventBus eventBus, @NonNull Compass compass) {
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

  protected float computeDesiredDirection() {
    if (!isNavigating()) {
      return UNKNOWN_BEARING;
    }

    float userBearing = (int) _compass.getBearing();
    if (userBearing == UNKNOWN_BEARING) {
      return UNKNOWN_BEARING;
    }

    float routeDirection = _routeCursor.getCurrentDirection();

    return computeDesiredDirection(userBearing, routeDirection);
  }

  protected static float computeDesiredDirection(float realDirection, float routeDirection) {
    float userDirection = 90 + routeDirection - realDirection;
    if (userDirection < 0) {
      return userDirection + 360;
    }
    return userDirection;
  }

  protected int computeDirectionToRoute(Location currentPosition) {
    int realDirection = (int) _compass.getBearing();
    if (realDirection == Compass.UNKNOWN_BEARING) {
      return realDirection;
    }

    Location closesLocation = _routeCursor.findClosestLocation(currentPosition);

//    int requiredDirection = DirectionDecisionStrategy.getBearing(currentPosition.latLng, current);

    // TODO: 17/02/16
    return 0;
//    return computeDesiredDirection(realDirection, requiredDirection);
  }

  protected void onNewDirection(float bearing) {
    if (bearing != _lastBearing) {
      _lastBearing = bearing;
      _eventBus.post(new RequiredDirectionEvent(bearing));
    }
  }

  @Subscribe
  protected void onNewLocation(Location location) {
    int directionToRoute = computeDirectionToRoute(location);

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
    public final float _bearing;

    public RequiredDirectionEvent(float bearing) {
      _bearing = bearing;
    }
  }

  //endregion
}
