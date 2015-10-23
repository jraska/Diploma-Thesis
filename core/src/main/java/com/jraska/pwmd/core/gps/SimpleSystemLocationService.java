package com.jraska.pwmd.core.gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.ObservableImpl;

public class SimpleSystemLocationService implements LocationService, LocationStatusService {
  //region Fields

  private final LocationManager _locationManager;

  private Position _lastPosition;

  private ObservableImpl<Position> _newPosition;
  private boolean _tracking;

  private final LocationListener _locationListener = new InnerLocationListener();

  //endregion

  //region Constructors

  public SimpleSystemLocationService(LocationManager locationManager) {
    ArgumentCheck.notNull(locationManager);

    _locationManager = locationManager;
  }

  //endregion

  //region Properties

  public LocationManager getLocationManager() {
    return _locationManager;
  }


  //endregion

  //region ILocationStatusService impl

  @Override
  public boolean isGpsLocationOn() {
    return _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  @Override
  public boolean isNetworkLocationOn() {
    return _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  }

  //endregion


  //region ILocationService impl

  @Override
  public ObservableImpl<Position> getNewPosition() {
    if (_newPosition == null) {
      _newPosition = new ObservableImpl<>();
    }

    return _newPosition;
  }

  @Override
  public Position getLastPosition() {
    if (_lastPosition == null || !_tracking) {
      return getLastKnownPosition();
    }

    return _lastPosition;
  }

  @Override
  public boolean isTracking() {
    return _tracking;
  }

  @Override
  public boolean isTrackingAvailable() {
    return isGpsLocationOn() || isNetworkLocationOn();
  }

  @Override
  public void startTracking(LocationSettings settings) throws SecurityException {
    if (_tracking) {
      return;
    }

    ArgumentCheck.notNull(settings);

    _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, settings.minTime, settings.minDistance, _locationListener);
    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, settings.minTime, settings.minDistance, _locationListener);

    _tracking = true;
  }

  @Override
  public void stopTracking() throws SecurityException {
    if (!_tracking) {
      return;
    }

    _locationManager.removeUpdates(_locationListener);

    _tracking = false;
  }

  //endregion

  //region Methods

  protected Position toPosition(Location l) {
    return new Position(l.getLatitude(), l.getLongitude(), System.currentTimeMillis(), l.getAccuracy(), l.getProvider());
  }

  protected void onNewLocation(Location l) {
    onNewPosition(toPosition(l));
  }

  protected final void onNewPosition(Position position) {
    _lastPosition = position;

    if (_newPosition != null) {
      _newPosition.notify(this, position);
    }
  }

  public Position getLastKnownPosition() throws SecurityException {
    Location lastGps = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Location lastNetwork = _locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    Location better = chooseBetterLocation(lastGps, lastNetwork);

    if (better == null) {
      return null;
    }

    return toPosition(better);
  }

  private Location chooseBetterLocation(Location lastGps, Location lastNetwork) {
    if (lastGps == null) {
      return lastNetwork;
    }

    if (lastNetwork == null) {
      return lastGps;
    }

    long timeDiff = lastGps.getTime() - lastNetwork.getTime();

    if (timeDiff < -30 * 1000) // network is newer
    {
      return lastGps;
    }

    return lastNetwork;
  }

  //endregion

  //region Nested classes

  class InnerLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location l) {
      onNewLocation(l);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // Nothing here
    }

    @Override
    public void onProviderEnabled(String provider) {
      // Nothing here
    }

    @Override
    public void onProviderDisabled(String provider) {
      // Nothing here
    }
  }

  //endregion

  //region Nested classes


  //endregion
}
