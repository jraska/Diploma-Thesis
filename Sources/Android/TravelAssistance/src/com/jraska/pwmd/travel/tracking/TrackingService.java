package com.jraska.pwmd.travel.tracking;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.jraska.common.events.IObserver;
import com.jraska.pwmd.core.gps.ILocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.RoutesListActivity;
import com.jraska.pwmd.travel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for system tracking
 */
public class TrackingService extends Service
{
	//region Constants

	protected final int ID = 26802; //random number

	//endregion

	//region Fields

	private final List<Position> mPositions = new ArrayList<Position>();
	private boolean mRunning;

	private final Object mLock = new Object();

	private final IObserver<Position> mPositionObserver = new IObserver<Position>()
	{
		@Override
		public void update(Object sender, Position args)
		{
			synchronized (mLock)
			{
				mPositions.add(args);
			}
		}
	};

	//endregion

	//region Properties


	public boolean isRunning()
	{
		return mRunning;
	}

	public List<Position> getPositions()
	{
		return new ArrayList<Position>(mPositions);
	}

	protected ILocationService getLocationService()
	{
		return ILocationService.Stub.asInterface();
	}

	//endregion

	//region Service impl


	@Override
	public void onCreate()
	{
		super.onCreate();

		mRunning = true;

		Notification notification = prepareForegroundNotification();
		startForeground(ID, notification);

		stopTracking();
		startTrackingNewPosition();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new TrackingServiceBinder(this);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		stopTracking();

		mRunning = false;
	}

	//endregion

	//region Methods

	protected Notification prepareForegroundNotification()
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		final String appName = getString(R.string.app_name);
		builder.setContentTitle(appName);
		builder.setContentText(getString(R.string.tap_to_return));
		builder.setTicker(appName);
		builder.setSmallIcon(R.drawable.ic_launcher);
//		builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(false);

		Intent runApplicationIntent = new Intent(this, RoutesListActivity.class);
		runApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, runApplicationIntent, 0);

		builder.setContentIntent(pendingIntent);

		return builder.build();
	}

	protected void stopTracking()
	{
		ILocationService locationService = getLocationService();
		locationService.stopTracking();
		locationService.getNewPosition().unregisterObserver(mPositionObserver);
	}

	protected void startTrackingNewPosition()
	{
		synchronized (mLock)
		{
			mPositions.clear();
		}

		ILocationService locationService = getLocationService();

		locationService.startTracking(new LocationSettings(5, 5));
		locationService.getNewPosition().registerObserver(mPositionObserver);
	}

	//endregion

	//region Nested classes

	public class TrackingServiceBinder extends Binder
	{
		private final TrackingService mService;

		public TrackingServiceBinder(TrackingService service)
		{
			mService = service;
		}

		public TrackingService getService()
		{
			return mService;
		}
	}

	//endregion
}
