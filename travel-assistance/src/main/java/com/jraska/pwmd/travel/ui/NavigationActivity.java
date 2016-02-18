package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.navigation.Compass;
import com.jraska.pwmd.travel.navigation.Navigator;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;

public class NavigationActivity extends BaseActivity {

  //region Constants

  public static final String KEY_INTENT_ROUTE_ID = "NavigationRouteId";

  //endregion

  //region Fields

  @Bind(R.id.navigate_arrow_view) View _desiredDirectionView;
  @Bind(R.id.navigate_user_direction_arrow_view) View _userDirectionView;

  @Inject Navigator _navigator;
  @Inject EventBus _systemBus;
  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;

  private RouteDisplayFragment _routeDisplayFragment;
  private long _routeId;

  //endregion

  //region Properties

  public long getRouteId() {
    return _routeId;
  }

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    TravelAssistanceApp.getComponent(this).inject(this);

    updateDesiredDirection(_navigator.getLastRequiredDirection());
    _navigator.getEventBus().register(this);

    _trackingManager.startTracking();

    _routeDisplayFragment = (RouteDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    _routeId = getIntent().getLongExtra(KEY_INTENT_ROUTE_ID, 0);

    startNavigation();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onDestroy() {
    _navigator.getEventBus().unregister(this);

    if (!isChangingConfigurations()) {
      _trackingManager.stopTracking();
      _navigator.stopNavigation();
    }

    super.onDestroy();
  }

  //endregion

  //region Event consuming

  @Subscribe
  public void onDirectionChanged(Navigator.RequiredDirectionEvent changedEvent) {
    updateDesiredDirection(changedEvent._bearing);
  }

  @Subscribe
  public void onNewPosition(Location location) {
    _routeDisplayFragment.addLocationMarker(location);
    updateUserDirection(_navigator.getUserDirection());
  }

  //endregion

  //region Methods

  protected void updateDesiredDirection(float bearing) {
    updateDirection(bearing, _desiredDirectionView);
  }

  protected void updateUserDirection(float bearing) {
    updateDirection(bearing, _userDirectionView);
  }

  protected void updateDirection(float bearing, View view) {
    if (bearing == Compass.UNKNOWN_BEARING) {
      view.setVisibility(View.GONE);
    } else {
      view.setVisibility(View.VISIBLE);
    }

    // Rotation must be counter clockwise
    view.setRotation(bearing);
  }

  public void startNavigation() {
    RouteData routeData = _routeDisplayFragment.getRouteData();

    if (routeData == null) {
      routeData = loadRoute();
    }

    if (routeData == null) {
      onRouteNotFound();
    } else {
      _routeDisplayFragment.displayRoute(routeData);
      _navigator.startNavigation(routeData);
    }
  }

  private void onRouteNotFound() {
    Timber.w("Route with id %s not found.", _routeId);

    finish();
  }

  protected RouteData loadRoute() {
    RouteData routeData = _travelDataRepository.select(_routeId);
    return routeData;
  }

  public static void startNew(Activity fromActivity, long routeId) {
    Intent startNavigationIntent = createIntent(fromActivity, routeId);

    fromActivity.startActivity(startNavigationIntent);
  }

  public static Intent createIntent(Activity fromActivity, long routeId) {
    Intent startNavigationIntent = new Intent(fromActivity, NavigationActivity.class);
    startNavigationIntent.putExtra(KEY_INTENT_ROUTE_ID, routeId);
    return startNavigationIntent;
  }

  //endregion
}
