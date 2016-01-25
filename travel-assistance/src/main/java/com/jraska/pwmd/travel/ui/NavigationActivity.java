package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.collection.CircularFifoQueue;
import com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy;
import com.jraska.pwmd.travel.navigation.Navigator;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import java.text.DecimalFormat;

import static com.jraska.pwmd.travel.navigation.Navigator.toGoogleLatLng;

public class NavigationActivity extends BaseActivity implements OnMapReadyCallback {

  //region Fields

  @Bind(R.id.arrow_view) View _arrowView;

  @Inject Navigator _navigator;
  @Inject EventBus _systemBus;
  @Inject TrackingManager _trackingManager;
  @Inject @Nullable Position _lastPosition;

  private GoogleMap _map;
  private int _markerCounter;

  private final CircularFifoQueue<Marker> _markers = new CircularFifoQueue<>(2);
  private final DecimalFormat _latLngFormat = new DecimalFormat("#.#######");

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

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    mapFragment.getMapAsync(this);
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
    addPositionMarker(position);
    updateDesiredDirection(_navigator.getCompassDirection());
  }

  protected void addPositionMarker(Position position) {
    if (_map == null) {
      return;
    }

    com.jraska.pwmd.core.gps.LatLng latLng = position.latLng;

    String title = "#" + ++_markerCounter + " " + _latLngFormat.format(latLng._latitude)
        + ", " + _latLngFormat.format(latLng._longitude);
    MarkerOptions markerOptions = new MarkerOptions().position(toGoogleLatLng(latLng))
        .alpha(0.5f).title(title);
    Marker marker = _map.addMarker(markerOptions);

    if (_markers.isAtFullCapacity()) {
      Marker toRemove = _markers.remove();
      toRemove.remove();
    }

    _markers.add(marker);
  }

  //endregion

  //region OnMapReadyCallback impl

  @Override
  public void onMapReady(GoogleMap map) {
    _map = map;

    MapHelper.configureMap(map);

    LatLng center;
    if (_lastPosition != null) {
      center = toGoogleLatLng(_lastPosition.latLng);
    } else {
      center = new LatLng(49, 19);
    }

    CameraUpdate update = CameraUpdateFactory.newLatLng(center);
    map.moveCamera(update);

    CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(MapHelper.ZOOM);
    map.animateCamera(zoomTo);
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

  //endregion
}
