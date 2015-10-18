package com.jraska.pwmd.travel.tracking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingManagementService implements ITrackingManagementService
{
	//region Fields

	private final Context mContext;
	private final ILocationFilter mFilter;

	private boolean mRunning;
	private TrackingService.TrackingServiceBinder mServiceBinder;
	private Date mStart;

	private final TrackingServiceConnection mConnection = new TrackingServiceConnection();

	//endregion

	//region Constructors

	@Inject
	public TrackingManagementService(Context context)
	{
		this(context, ILocationFilter.Empty.Instance);
	}

	public TrackingManagementService(Context context, ILocationFilter filter)
	{
		ArgumentCheck.notNull(context);
		ArgumentCheck.notNull(filter);

		mContext = context;
		mFilter = filter;
	}

	//endregion

	//region Properties

	protected Context getContext()
	{
		return mContext;
	}

	protected ILocationFilter getFilter()
	{
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
		mContext.startService(intent);
		mContext.bindService(intent, mConnection, 0);

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

		mContext.unbindService(mConnection);
		mContext.stopService(getServiceIntent());

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
		return new Intent(mContext, TrackingService.class);
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
			return LocationManager.GPS_PROVIDER.equals(position.provider);
		}
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = ITrackingManagementService.class, complete = false)
	public static class Module
	{
		@Provides
		@Singleton
		public ITrackingManagementService provideSvc(Context context)
		{
			return new TrackingManagementService(context);
		}
	}

	//endregion
}
