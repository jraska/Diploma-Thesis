package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import org.greenrobot.eventbus.Subscribe;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy;
import com.jraska.pwmd.travel.navigation.Navigator;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

import javax.inject.Inject;

public class NavigationActivity extends BaseActivity {

  //region Constants

  public static final String KEY_INTENT_ROUTE_ID = "NavigationRouteId";

  //endregion

  //region Fields

  @Bind(R.id.arrow_view) View _arrowView;

  @Inject Navigator _navigator;
  @Inject EventBus _systemBus;
  @Inject TrackingManager _trackingManager;
  @Inject @Nullable Position _lastPosition;
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

    // TODO: 18/01/16 Test code
    updateDesiredDirection(90);

    _trackingManager.startTracking();

    _routeDisplayFragment = (RouteDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    _routeId = getIntent().getLongExtra(KEY_INTENT_ROUTE_ID, 0);

    _navigator.startNavigation(_routeId);
  }

  @Override
  protected void onStart() {
    super.onStart();

    showRoute();
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
    updateDesiredDirection(changedEvent._directionDegrees);
  }

  @Subscribe
  public void onNewPosition(Position position) {
    _routeDisplayFragment.addPositionMarker(position);
    updateDesiredDirection(_navigator.getCompassDirection());
  }

  //endregion

  //region Methods

  protected void updateDesiredDirection(int degrees) {
    if (degrees == DirectionDecisionStrategy.UNKNOWN_DIRECTION) {
      _arrowView.setVisibility(View.GONE);
    } else {
      _arrowView.setVisibility(View.VISIBLE);
    }

    // Rotation must be counter clockwise
    _arrowView.setRotation(-degrees);
  }

  public void showRoute() {
    if (!_routeDisplayFragment.isRouteDisplayed()) {
      RouteData routeData = loadRoute();

      if (routeData != null) {
        _routeDisplayFragment.displayRoute(routeData);
      } else {
        onRouteNotFound();
      }
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
