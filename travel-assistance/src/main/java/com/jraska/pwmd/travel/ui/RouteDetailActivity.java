package com.jraska.pwmd.travel.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.jraska.pwmd.travel.media.SoundsManager;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jraska.pwmd.travel.navigation.Navigator.toGoogleLatLng;
import static com.jraska.pwmd.travel.ui.MapHelper.ZOOM;

public class RouteDetailActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
  //region Constants

  public static final String ROUTE_ID = "RouteId";
  protected static final int ROUTE_WIDTH = 5;

  //endregion

  //region Fields

  private GoogleMap _mapView;

  @Inject TravelDataRepository _travelDataRepository;
  @Inject PicturesManager _picturesManager;
  @Inject SoundsManager _soundsManager;

  private long _routeId;

  private Map<Marker, NoteSpec> _noteSpecMap = new HashMap<>();

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

    _routeId = getIntent().getLongExtra(ROUTE_ID, 0);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_route_detail, menu);

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

    MapHelper.configureMap(googleMap);

    RouteData routeData = loadRoute();
    setTitle(routeData.getTitle());

    displayOnMap(routeData);
    displayRouteChanges(routeData);
    displayNotes(routeData);
  }

  //endregion

  //region OnMarkerClickListener impl

  @Override
  public boolean onMarkerClick(Marker marker) {
    NoteSpec noteSpec = _noteSpecMap.get(marker);
    if (noteSpec != null) {
      if (noteSpec.getSoundId() != null) {
        _soundsManager.play(noteSpec.getSoundId());
        return false;
      }
    }

    return false;
  }

  //endregion

  //region Methods

  protected RouteData loadRoute() {
    long routeId = getIntent().getLongExtra(ROUTE_ID, 0);
    RouteData routeData = _travelDataRepository.selectRouteData(routeId);
    return routeData;
  }

  protected void displayOnMap(RouteData routeData) {
    PolylineOptions polylineOptions = new PolylineOptions().width(ROUTE_WIDTH)
        .color(Color.BLUE).visible(true);

    List<com.jraska.pwmd.core.gps.LatLng> points = routeData.getPath();

    if (points.size() == 0) {
      throw new IllegalStateException("No points to display");
    }

    for (com.jraska.pwmd.core.gps.LatLng position : points) {
      polylineOptions.add(toGoogleLatLng(position));
    }

    GoogleMap map = _mapView;
    map.addPolyline(polylineOptions);

    LatLng latLng = toGoogleLatLng(points.get(0));
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
    map.animateCamera(cameraUpdate);

    map.setOnMarkerClickListener(this);
  }

  protected void startNavigation() {
    NavigationActivity.startNavigationActivity(this, _routeId);
  }

  protected void displayRouteChanges(RouteData routeData) {
    for (TransportChangeSpec spec : routeData.getTransportChangeSpecs()) {
      BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(spec.getLightIconRes());

      LatLng markerLocation = toGoogleLatLng(spec.getLatLng());

      MarkerOptions routeChangeMarker = new MarkerOptions().position(markerLocation)
          .title(spec.getTitle())
          .icon(icon);

      _mapView.addMarker(routeChangeMarker);
    }
  }

  protected void displayNotes(RouteData routeData) {
    for (NoteSpec spec : routeData.getNoteSpecs()) {
      LatLng markerLocation = toGoogleLatLng(spec.getLatLng());

      MarkerOptions routeChangeMarker = new MarkerOptions().position(markerLocation)
          .title(spec.getCaption());

      if (spec.getImageId() != null) {
        Uri pictureUri = _picturesManager.createPictureUri(spec.getImageId());
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(pictureUri.toString(), new ImageSize(32, 32));
        routeChangeMarker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
      }

      Marker marker = _mapView.addMarker(routeChangeMarker);
      _noteSpecMap.put(marker, spec);
    }
  }

  //endregion
}
