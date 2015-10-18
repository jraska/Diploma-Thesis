package com.jraska.pwmd.core.gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.Observable;

public class SimpleSystemLocationService implements ILocationService, ILocationStatusService {
  //region Fields

  private final LocationManager mLocationManager;

  private Position mLastPosition;

  private Observable<Position> mNewPosition;
  private boolean mTracking;

  private final LocationListener mLocationListener = new InnerLocationListener();

  //endregion

  //region Constructors

  public SimpleSystemLocationService(LocationManager locationManager) {
    ArgumentCheck.notNull(locationManager);

    mLocationManager = locationManager;
  }

  //endregion

  //region Properties

  public LocationManager getLocationManager() {
    return mLocationManager;
  }


  //endregion

  //region ILocationStatusService impl

  @Override
  public boolean isGpsLocationOn() {
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  @Override
  public boolean isNetworkLocationOn() {
    return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  }

  //endregion


  //region ILocationService impl

  @Override
  public Observable<Position> getNewPosition() {
    if (mNewPosition == null) {
      mNewPosition = new Observable<Position>();
    }

    return mNewPosition;
  }

  @Override
  public Position getLastPosition() {
    if (mLastPosition == null || !mTracking) {
      return getLastKnownPosition();
    }

    return mLastPosition;
  }

  @Override
  public boolean isTracking() {
    return mTracking;
  }

  @Override
  public boolean isTrackingAvailable() {
    return isGpsLocationOn() || isNetworkLocationOn();
  }

  @Override
  public void startTracking(LocationSettings settings) {
    if (mTracking) {
      return;
    }

    ArgumentCheck.notNull(settings);

    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, settings.minTime, settings.minDistance, mLocationListener);
    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, settings.minTime, settings.minDistance, mLocationListener);

    mTracking = true;
  }

  @Override
  public void stopTracking() {
    if (!mTracking) {
      return;
    }

    mLocationManager.removeUpdates(mLocationListener);

    mTracking = false;
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
    mLastPosition = position;

    if (mNewPosition != null) {
      mNewPosition.notify(this, position);
    }
  }

  public Position getLastKnownPosition() {
    Location lastGps = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Location lastNetwork = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
  }

  //endregion

  //region Nested classes


  //endregion
}
