package com.jraska.gpsbatterytest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.jraska.common.events.IObserver;
import com.jraska.gpsbatterytest.logging.CompositeLogger;
import com.jraska.gpsbatterytest.logging.ConsoleLogger;
import com.jraska.gpsbatterytest.logging.ILogger;
import com.jraska.gpsbatterytest.logging.TextFileLogger;
import com.jraska.pwmd.core.battery.BatteryStats;
import com.jraska.pwmd.core.battery.BatteryStatsReader;
import com.jraska.pwmd.core.gps.ILocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;

import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GpsBatteryTestService extends Service {
  //region Constants

  protected static final int ID = 82791; //random number

  //endregion

  //region Fields

  private ILogger _logger;
  private final ScheduledExecutorService _executor = Executors.newSingleThreadScheduledExecutor();
  private final LocationObserver _locationObserver = new LocationObserver();

  @Inject ILocationService _locationService;
  @Inject BatteryStatsReader _batteryStatsReader;

  //endregion

  //region Service impl

  @Override
  public void onCreate() {
    super.onCreate();

    GpsBatteryTestApp.getComponent(this).inject(this);

    Notification notification = prepareForegroundNotification();
    startForeground(ID, notification);

    _logger = createLogger();

    startLocationLogging();
    startBatteryLogging();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    _logger.dispose();

    stopLocationLogging();
    stopBatteryLogging();

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return new ServiceBinder(this);
  }

  //endregion

  //region Methods

  private void startBatteryLogging() {
    _executor.scheduleAtFixedRate(new CheckBatteryRunnable(), 1, 5 * 60, TimeUnit.SECONDS);
  }

  private void stopBatteryLogging() {
    _executor.shutdownNow();
  }

  private void startLocationLogging() {
    final ILocationService locationService = _locationService;

    locationService.getNewPosition().registerObserver(_locationObserver);
    locationService.startTracking(new LocationSettings(5, 5));
  }

  private void stopLocationLogging() {
    final ILocationService locationService = _locationService;

    locationService.getNewPosition().unregisterObserver(_locationObserver);
    locationService.stopTracking();
  }

  protected ILogger createLogger() {
    final DateFormat dateTimeInstance = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.US);
    final String nowText = dateTimeInstance.format(new Date());
    String fileName = "TestLog" + nowText + ".txt";

    final File externalFilesDir = getExternalFilesDir(null);


    File textFile = new File(externalFilesDir, fileName);
    TextFileLogger textFileLogger = new TextFileLogger(textFile);
    ILogger[] loggers = {textFileLogger, new ConsoleLogger()};

    return new CompositeLogger(loggers);
  }

  protected void log(Object o) {
    if (_logger != null) {
      _logger.log(o);
    }
  }

  protected Notification prepareForegroundNotification() {
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

  class LocationObserver implements IObserver<Position> {
    @Override
    public void update(Object sender, Position args) {
      log(args);
    }
  }

  class CheckBatteryRunnable implements Runnable {
    @Override
    public void run() {
      final BatteryStats currentBatteryStats = _batteryStatsReader.getCurrentBatteryStats();
      log(currentBatteryStats);
    }
  }

  static class ServiceBinder extends Binder {
    private final GpsBatteryTestService _gpsBatteryTestService;

    ServiceBinder(GpsBatteryTestService gpsBatteryTestService) {
      _gpsBatteryTestService = gpsBatteryTestService;
    }

    public GpsBatteryTestService getGpsBatteryTestService() {
      return _gpsBatteryTestService;
    }
  }

  //endregion
}
