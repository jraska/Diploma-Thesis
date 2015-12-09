package com.jraska.pwmd.travel.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class RouteDetailActivity extends BaseActivity implements OnMapReadyCallback {
  //region Constants

  public static final String ROUTE_ID = "RouteId";
  protected static final int ROUTE_WIDTH = 5;
  public static final int ZOOM = 18;

  //endregion

  //region Fields

  private GoogleMap _mapView;

  @Inject
  TravelDataRepository _travelDataRepository;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route_detail);
    TravelAssistanceApp.getComponent(this).inject(this);

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    mapFragment.getMapAsync(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.route_detail, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_route_navigate:
        startNavigation();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  //endregion

  //region OnMapReadyCallback impl

  @Override
  public void onMapReady(GoogleMap googleMap) {
    _mapView = googleMap;

    RouteData routeData = loadRoute();
    setTitle(routeData.getTitle());

    displayOnMap(routeData);
    displayRouteChanges(routeData);
  }

  //endregion

  //region Methods

  protected RouteData loadRoute() {
    UUID routeId = (UUID) getIntent().getSerializableExtra(ROUTE_ID);
    RouteData routeData = _travelDataRepository.selectRouteData(routeId);
    return routeData;
  }

  protected void displayOnMap(RouteData routeData) {
    PolylineOptions polylineOptions = new PolylineOptions().width(ROUTE_WIDTH).color(Color.BLUE).visible(true);

    List<Position> points = routeData.getPath().getPoints();

    if (points.size() == 0) {
      throw new IllegalStateException("No points to display");
    }

    for (Position position : points) {
      polylineOptions.add(toGoogleLatLng(position.latLng));
    }

    GoogleMap map = _mapView;
    map.addPolyline(polylineOptions);

    map.setMyLocationEnabled(true);

    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(toGoogleLatLng(points.get(0).latLng), ZOOM);
    map.animateCamera(cameraUpdate);
  }

  protected LatLng toGoogleLatLng(com.jraska.pwmd.core.gps.LatLng latLng) {
    return new LatLng(latLng._latitude, latLng._longitude);
  }

  protected void startNavigation() {
    Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
  }

  protected void displayRouteChanges(RouteData routeData) {
    for (TransportChangeSpec spec : routeData.getTransportChangeSpecs()) {
      BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(spec.getLightIconRes());

      LatLng markerLocation = toGoogleLatLng(spec.latLng);

      MarkerOptions routeChangeMarker = new MarkerOptions().position(markerLocation)
          .title(spec.title)
          .snippet(spec.description)
          .icon(icon);

      _mapView.addMarker(routeChangeMarker);
    }
  }

  //endregion
}
