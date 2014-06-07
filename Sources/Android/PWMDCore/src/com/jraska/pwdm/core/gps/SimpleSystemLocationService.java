package com.jraska.pwdm.core.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.Observable;
import com.jraska.core.JRApplication;

public class SimpleSystemLocationService implements ILocationService, ILocationStatusService
{
	//region Fields

	private LocationManager mLocationManager;
	private Position mLastPosition;

	private Observable<Position> mNewPosition;
	private boolean mTracking;

	private final LocationListener mLocationListener = new InnerLocationListener();

	//endregion

	//region Properties

	public LocationManager getLocationManager()
	{
		if (mLocationManager == null)
		{
			mLocationManager = (LocationManager) JRApplication.getCurrent().getSystemService(Context.LOCATION_SERVICE);
		}

		return mLocationManager;
	}


	//endregion

	//region ILocationStatusService impl

	@Override
	public boolean isGpsLocationOn()
	{
		return getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public boolean isNetworkLocationOn()
	{
		return getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	//endregion


	//region ILocationService impl

	@Override
	public Observable<Position> getNewPosition()
	{
		if (mNewPosition == null)
		{
			mNewPosition = new Observable<Position>();
		}

		return mNewPosition;
	}

	@Override
	public Position getLastPosition()
	{
		return mLastPosition;
	}

	@Override
	public boolean isTracking()
	{
		return mTracking;
	}

	@Override
	public boolean isTrackingAvailable()
	{
		return isGpsLocationOn() || isNetworkLocationOn();
	}

	@Override
	public void startTracking(LocationSettings settings)
	{
		ArgumentCheck.notNull(settings);

		getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, settings.minTime, settings.minDistance, mLocationListener);
		getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, settings.minTime, settings.minDistance, mLocationListener);

		mTracking = true;
	}

	@Override
	public void stopTracking()
	{
		getLocationManager().removeUpdates(mLocationListener);

		mTracking = false;
	}

	//endregion

	//region Methods

	protected Position toPosition(Location l)
	{
		return new Position(l.getLatitude(), l.getLongitude(), System.currentTimeMillis(), l.getAccuracy(), l.getProvider());
	}

	protected void onNewLocation(Location l)
	{
		onNewPosition(toPosition(l));
	}

	protected final void onNewPosition(Position position)
	{
		mLastPosition = position;

		if (mNewPosition != null)
		{
			mNewPosition.notify(this, position);
		}
	}

	//endregion

	//region Nested classes

	class InnerLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location l)
		{
			onNewLocation(l);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}
	}

	//endregion
}
