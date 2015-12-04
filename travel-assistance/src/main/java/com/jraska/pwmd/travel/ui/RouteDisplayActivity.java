package com.jraska.pwmd.travel.ui;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class RouteDisplayActivity extends BaseActivity {
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

  //region Properties

  protected GoogleMap getMap() {
    if (_mapView == null) {
      _mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    return _mapView;
  }

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.route_display);
    TravelAssistanceApp.getComponent(this).inject(this);

    loadAndShowRoute();
  }

  //endregion

  //region Methods

  protected void loadAndShowRoute() {
    UUID routeId = (UUID) getIntent().getSerializableExtra(ROUTE_ID);

    RouteData routeData = _travelDataRepository.selectRouteData(routeId);

    setTitle(routeData.getTitle());
    displayOnMap(routeData);
  }

  protected void displayOnMap(RouteData routeData) {
    GoogleMap map = getMap();
    PolylineOptions polylineOptions = new PolylineOptions().width(ROUTE_WIDTH).color(Color.BLUE).visible(true);

    List<Position> points = routeData.getPath().getPoints();

    if (points.size() == 0) {
      throw new IllegalStateException("No points to display");
    }

    for (Position position : points) {
      polylineOptions.add(toGoogleLatLng(position));
    }

    map.addPolyline(polylineOptions);

    map.setMyLocationEnabled(true);

    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(toGoogleLatLng(points.get(0)), ZOOM);
    getMap().animateCamera(cameraUpdate);
  }

  protected LatLng toGoogleLatLng(com.jraska.pwmd.core.gps.LatLng latLng) {
    return new LatLng(latLng._latitude, latLng._longitude);
  }

  //endregion
}
