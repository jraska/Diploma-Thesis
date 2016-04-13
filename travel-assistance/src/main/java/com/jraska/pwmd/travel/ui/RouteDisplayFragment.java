package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.jraska.pwmd.travel.util.CircleImageProcessor;
import com.jraska.pwmd.travel.util.PathSmoother;
import com.jraska.pwmd.travel.util.Stopwatch;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jraska.pwmd.travel.ui.MapHelper.*;


public class RouteDisplayFragment extends SupportMapFragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
  //region Constants

  protected static final int ROUTE_WIDTH = 7;

  //endregion

  //region Fields

  @Inject PicturesManager _picturesManager;
  @Inject PathSmoother _pathSmoother;

  private RouteData _routeData;
  private GoogleMap _mapView;

  private Polyline _lastPolyline;
  private final Map<Marker, TransportChangeSpec> _transportChangeMarkers = new HashMap<>();
  private final Map<Marker, NoteSpec> _noteSpecMap = new HashMap<>();

  private DisplayImageOptions _imageOptions;
  private ImageSize _photoIconSize;

  private EventListener _eventListener;
  private final List<CameraUpdate> _pendingUpdates = new ArrayList<>();

  //endregion

  //region Constructors

  public RouteDisplayFragment() {
    setRetainInstance(true);
  }

  //endregion

  //region Properties

  public boolean isRouteDisplayed() {
    return _routeData != null;
  }

  public RouteData getRouteData() {
    return _routeData;
  }

  public ImageSize getPhotoIconSize() {
    if (_photoIconSize == null) {
      int imageSize = getResources().getDimensionPixelSize(R.dimen.map_image_size_default);
      _photoIconSize = new ImageSize(imageSize, imageSize);
    }

    return _photoIconSize;
  }

  //endregion

  //region SupportMapFragment overrides

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (_routeData != null) {
      setupActivity(activity);
    }

    // Activities should implement the event listener interface
    if (activity instanceof EventListener) {
      _eventListener = (EventListener) activity;
    }
  }

  @Override
  public void onDetach() {
    _eventListener = null;

    super.onDetach();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TravelAssistanceApp.getComponent(getActivity()).inject(this);


    int imageSize = getResources().getDimensionPixelSize(R.dimen.map_image_size_default);
    _photoIconSize = new ImageSize(imageSize, imageSize);
    _imageOptions = new DisplayImageOptions.Builder()
        .preProcessor(new CircleImageProcessor(imageSize))
        .considerExifParams(true)
        .build();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getMapAsync(this);

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  //endregion

  //region OnMarkerClickListener impl

  @Override
  public boolean onMarkerClick(Marker marker) {
    NoteSpec noteSpec = _noteSpecMap.get(marker);

    if (noteSpec == null) {
      return false;
    }

    if (_eventListener != null) {
      return _eventListener.onNoteSpecClicked(noteSpec);
    }
    return false;
  }

  //endregion

  //region OnMapReadCallback impl

  @Override
  public void onMapReady(GoogleMap googleMap) {
    _mapView = googleMap;
    configureMap(googleMap);
    if (_routeData != null) {
      displayRoute(_routeData);
    }

    for (CameraUpdate update : _pendingUpdates) {
      _mapView.moveCamera(update);
    }

    _pendingUpdates.clear();

    CameraUpdate zoomUpdate = CameraUpdateFactory.newLatLngZoom(_mapView.getCameraPosition().target, ZOOM);
    _mapView.animateCamera(zoomUpdate);
  }

  //endregion

  //region Methods

  public void centerMapTo(Location location) {
    CameraUpdate center = CameraUpdateFactory.newLatLng(toLatLng(location));

    updateCamera(center, true);
  }

  private void updateCamera(CameraUpdate cameraUpdate, boolean animate) {
    if (_mapView != null) {
      if (animate) {
        _mapView.animateCamera(cameraUpdate);
      } else {
        _mapView.moveCamera(cameraUpdate);
      }
    } else {
      _pendingUpdates.add(cameraUpdate);
    }
  }

  public void displayRoute(@Nullable RouteData routeData) {
    _routeData = routeData;

    if (routeData == null) {
      removeAll();
      return;
    }

    setupActivity(getActivity());
    if (_mapView == null) {
      // route will be displayed on map ready
      return;
    }

    displayOnMap(routeData);
    displayRouteChanges(routeData);
    displayNotes(routeData);
  }

  private void setupActivity(Activity activity) {
    activity.setTitle(_routeData.getTitle());

    if (activity instanceof BaseActivity) {
      BaseActivity baseActivity = (BaseActivity) activity;
      if (baseActivity._toolbar != null) {
        baseActivity._toolbar.setLogo(RoutesAdapter.getRouteIcon(_routeData));
      }
    }
  }

  protected void displayOnMap(RouteData routeData) {
    List<com.jraska.pwmd.core.gps.LatLng> points = routeData.getPath();

    if (points.isEmpty()) {
      throw new IllegalStateException("No points to display");
    }

    GoogleMap map = _mapView;

    removeLastPolyline();

    PolylineOptions polylineOptions = getPolylineOptions(points);
    _lastPolyline = map.addPolyline(polylineOptions);

    map.setOnMarkerClickListener(this);
  }

  private void removeLastPolyline() {
    if (_lastPolyline != null) {
      _lastPolyline.remove();
    }
  }

  private PolylineOptions getPolylineOptions(List<com.jraska.pwmd.core.gps.LatLng> points) {
    PolylineOptions polylineOptions = new PolylineOptions().width(ROUTE_WIDTH)
        .color(Color.BLUE).visible(true);

    List<com.jraska.pwmd.core.gps.LatLng> spLinePoints = _pathSmoother.smoothPath(points);

    for (com.jraska.pwmd.core.gps.LatLng latLng : spLinePoints) {
      polylineOptions.add(toGoogleLatLng(latLng));
    }
    return polylineOptions;
  }

  protected void displayRouteChanges(RouteData routeData) {
    for (TransportChangeSpec spec : routeData.getTransportChangeSpecs()) {
      if (_transportChangeMarkers.containsValue(spec)) {
        continue;
      }

      BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(spec.getHardIconRes());

      LatLng markerLocation = toGoogleLatLng(spec.getLatLng());

      MarkerOptions routeChangeMarkerOptions = new MarkerOptions().position(markerLocation)
          .title(spec.getTitle())
          .icon(icon);

      Marker routeChangeMarker = _mapView.addMarker(routeChangeMarkerOptions);
      _transportChangeMarkers.put(routeChangeMarker, spec);
    }
  }

  protected void displayNotes(RouteData routeData) {
    List<NoteSpec> noteSpecs = routeData.getNoteSpecs();

    Stopwatch stopwatch = Stopwatch.started();
    for (NoteSpec spec : noteSpecs) {
      if (_noteSpecMap.containsValue(spec)) {
        continue;
      }

      LatLng markerLocation = toGoogleLatLng(spec.getLatLng());

      MarkerOptions routeChangeMarker = new MarkerOptions().position(markerLocation)
          .title(spec.getCaption());


      Marker marker = _mapView.addMarker(routeChangeMarker);
      _noteSpecMap.put(marker, spec);

      loadImage(spec, marker);
    }

    stopwatch.stop();
    Timber.d("Displaying images took " + stopwatch.getElapsedMs() + "ms");
  }

  private void loadImage(NoteSpec spec, Marker marker) {
    if (spec.getImageId() != null) {
      Uri pictureUri = _picturesManager.createPictureUri(spec.getImageId());
      ImageLoader.getInstance().loadImage(pictureUri.toString(),
          getPhotoIconSize(), _imageOptions, new ImageLoadListener(marker));
    }
  }

  void removeAll() {
    removeLastPolyline();
    removeNoteSpecMarkers();
    removeTransporChangeMarkers();
  }

  private void removeTransporChangeMarkers() {
    for (Marker marker : _transportChangeMarkers.keySet()) {
      marker.remove();
    }

    _transportChangeMarkers.clear();
  }

  private void removeNoteSpecMarkers() {
    for (Marker marker : _noteSpecMap.keySet()) {
      marker.remove();
    }

    _noteSpecMap.clear();
  }

  public void centerMapToRouteStart() {
    if (_routeData == null) {
      return;
    }

    LatLng latLng = toGoogleLatLng(_routeData.getLocations().get(0).latLng);
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
    updateCamera(cameraUpdate, false);
  }

  //endregion

  //region Nested classes

  static class ImageLoadListener implements ImageLoadingListener {
    private final Marker _marker;

    public ImageLoadListener(Marker marker) {
      _marker = marker;
    }

    @Override public void onLoadingStarted(String imageUri, View view) {
      // do nothing
    }

    @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
      Timber.e(failReason.getCause(), "Loading for image " + imageUri + " failed.");
    }

    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
      if (_marker.isVisible()) {
        _marker.setIcon(BitmapDescriptorFactory.fromBitmap(loadedImage));
      }
    }

    @Override public void onLoadingCancelled(String imageUri, View view) {
      // do nothing
    }
  }

  interface EventListener {
    boolean onNoteSpecClicked(NoteSpec noteSpec);
  }

  //endregion
}
