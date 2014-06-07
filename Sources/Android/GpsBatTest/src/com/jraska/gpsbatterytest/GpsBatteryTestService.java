package com.jraska.gpsbatterytest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.jraska.common.events.IObserver;
import com.jraska.gpsbatterytest.logging.ILogger;
import com.jraska.gpsbatterytest.logging.TextFileLogger;
import com.jraska.pwdm.core.battery.BatteryStats;
import com.jraska.pwdm.core.battery.IBatteryStatsService;
import com.jraska.pwdm.core.gps.ILocationService;
import com.jraska.pwdm.core.gps.LocationSettings;
import com.jraska.pwdm.core.gps.Position;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GpsBatteryTestService extends Service
{
	//region Constants

	protected final int ID = 82791; //random number

	//endregion

	//region Fields

	private ILogger mLogger;
	private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
	private final LocationObserver mLocationObserver = new LocationObserver();

	//endregion

	//region Service impl

	@Override
	public void onCreate()
	{
		super.onCreate();

		Notification notification = prepareForegroundNotification();
		startForeground(ID, notification);

		mLogger = createLogger();

		startLocationLogging();
		startBatteryLogging();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		mLogger.dispose();

		stopLocationLogging();
		stopBatteryLogging();

		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		return new ServiceBinder(this);
	}

	//endregion

	//region Methods

	private void startBatteryLogging()
	{
		mExecutor.scheduleAtFixedRate(new CheckBatteryRunnable(), 1, 5 * 60, TimeUnit.SECONDS);
	}

	private void stopBatteryLogging()
	{
		mExecutor.shutdownNow();
	}

	private void startLocationLogging()
	{
		final ILocationService locationService = ILocationService.Stub.asInterface();

		locationService.getNewPosition().registerObserver(mLocationObserver);
		locationService.startTracking(new LocationSettings(5, 5));
	}

	private void stopLocationLogging()
	{
		final ILocationService locationService = ILocationService.Stub.asInterface();

		locationService.getNewPosition().unregisterObserver(mLocationObserver);
		locationService.stopTracking();
	}

	protected ILogger createLogger()
	{
		final DateFormat dateTimeInstance = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
		final String nowText = dateTimeInstance.format(new Date());
		String fileName = "TestLog" + nowText + ".txt";

		final File externalFilesDir = getExternalFilesDir(null);


		File textFile = new File(externalFilesDir, fileName);
		return new TextFileLogger(textFile);
	}

	protected void log(Object o)
	{
		if (mLogger != null)
		{
			mLogger.log(o);
		}
	}

	protected Notification prepareForegroundNotification()
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		final String appName = getString(R.string.app_name);
		builder.setContentTitle(appName);
		builder.setContentText(getString(R.string.tap_to_return));
		builder.setTicker(appName);
		builder.setSmallIcon(android.R.drawable.ic_menu_info_details);
//		builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(false);

		Intent runApplicationIntent = new Intent(this, GpsBatteryTestMainActivity.class);
		runApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, runApplicationIntent, 0);

		builder.setContentIntent(pendingIntent);

		return builder.build();
	}

	//endregion

	//region Nested classes

	class LocationObserver implements IObserver<Position>
	{
		@Override
		public void update(Object sender, Position args)
		{
			log(args);
		}
	}

	class CheckBatteryRunnable implements Runnable
	{
		@Override
		public void run()
		{
			final BatteryStats currentBatteryStats = IBatteryStatsService.Stub.asInterface().getCurrentBatteryStats();
			log(currentBatteryStats);
		}
	}

	static class ServiceBinder extends Binder
	{
		private final GpsBatteryTestService mGpsBatteryTestService;

		ServiceBinder(GpsBatteryTestService gpsBatteryTestService)
		{
			mGpsBatteryTestService = gpsBatteryTestService;
		}

		public GpsBatteryTestService getGpsBatteryTestService()
		{
			return mGpsBatteryTestService;
		}
	}

	//endregion
}
