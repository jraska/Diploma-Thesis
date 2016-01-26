package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy;
import com.jraska.pwmd.travel.navigation.Navigator;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;

public class NavigationActivity extends BaseActivity {

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
    _routeId = getIntent().getLongExtra(RouteDetailActivity.ROUTE_ID, 0);
  }

  @Override
  protected void onStart() {
    super.onStart();

    showRoute();
  }

  @Override
  protected void onDestroy() {
    _navigator.getEventBus().unregister(this);

    _trackingManager.stopTracking(); // TODO: Handle starting diferent way

    super.onDestroy();
  }

  //endregion

  //region Event consuming

  public void onEvent(Navigator.RequiredDirectionEvent changedEvent) {
    updateDesiredDirection(changedEvent._directionDegrees);
  }

  public void onEvent(Position position) {
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

  public static void startNavigationActivity(Activity fromActivity, long routeId) {
    Intent startNavigationIntent = new Intent(fromActivity, NavigationActivity.class);
    startNavigationIntent.putExtra(RouteDetailActivity.ROUTE_ID, routeId);

    fromActivity.startActivity(startNavigationIntent);
  }

  public void showRoute() {
    if (!_routeDisplayFragment.isRouteDisplayed()) {
      RouteData routeData = loadRoute();

      _routeDisplayFragment.displayRoute(routeData);
    }
  }

  protected RouteData loadRoute() {
    RouteData routeData = _travelDataRepository.select(_routeId);
    return routeData;
  }

  //endregion
}
