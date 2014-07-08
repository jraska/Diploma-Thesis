package com.jraska.pwdm.travel.tracking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;
import com.jraska.core.JRApplication;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.travel.data.Path;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingManagementService implements ITrackingManagementService
{
	//region Fields

	private boolean mRunning;
	private TrackingService.TrackingServiceBinder mServiceBinder;
	private Date mStart;

	private final TrackingServiceConnection mConnection = new TrackingServiceConnection();

	private ILocationFilter mFilter;

	//endregion

	//region Properties

	protected Context getContext()
	{
		return JRApplication.getCurrent();
	}

	protected ILocationFilter getFilter()
	{
		if (mFilter == null)
		{
			return ILocationFilter.Empty.Instance;
		}

		return mFilter;
	}

	//endregion

	//region ITrackingManagementService impl

	@Override
	public boolean isTracking()
	{
		return mRunning;
	}

	@Override
	public void startTracking()
	{
		if (mRunning)
		{
			return;
		}

		mStart = new Date();
		Intent intent = getServiceIntent();
		getContext().startService(intent);
		getContext().bindService(intent, mConnection, 0);

		mRunning = true;
	}

	@Override
	public PathInfo getLastPath()
	{
		if (mServiceBinder == null)
		{
			return null;
		}

		List<Position> positions = mServiceBinder.getService().getPositions();

		if (positions.size() == 0)
		{
			return null;
		}

		positions = filterPositions(positions);

		return new PathInfo(mStart, new Date(), new Path(positions));
	}

	@Override
	public void stopTracking()
	{
		if (!mRunning)
		{
			return;
		}

		getContext().unbindService(mConnection);
		getContext().stopService(getServiceIntent());

		mServiceBinder = null;
		mRunning = false;
	}

	//endregion

	//region Methods

	protected List<Position> filterPositions(List<Position> positions)
	{
		List<Position> filtered = new ArrayList<Position>(positions.size());

		ILocationFilter filter = getFilter();
		for (Position position : positions)
		{
			if (filter.accept(position))
			{
				filtered.add(position);
			}
		}

		return filtered;
	}

	protected Intent getServiceIntent()
	{
		return new Intent(getContext(), TrackingService.class);
	}

	//endregion

	//region Nested classes

	protected class TrackingServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mServiceBinder = (TrackingService.TrackingServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
		}
	}

	protected class GpsProviderOnlyFilter implements ILocationFilter
	{
		@Override
		public boolean accept(Position position)
		{
			return position.provider.equals(LocationManager.GPS_PROVIDER);
		}
	}

	//endregion
}
