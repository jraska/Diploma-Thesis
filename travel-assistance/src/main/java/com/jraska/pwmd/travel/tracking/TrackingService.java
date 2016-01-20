package com.jraska.pwmd.travel.tracking;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.ui.RoutesListActivity;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for system location tracking
 */
public class TrackingService extends Service {
  //region Constants

  protected final int ID = 26802; //random number

  //endregion

  //region Fields

  private final List<Position> _positions = new ArrayList<>();
  private boolean _running;

  private final Object _lock = new Object();

  @Inject LocationService _locationService;
  @Inject EventBus _systemBus;

  //endregion

  //region Properties


  public boolean isRunning() {
    return _running;
  }

  public List<Position> getPositions() {
    return new ArrayList<>(_positions);
  }

  //endregion

  //region Service impl


  @Override
  public void onCreate() {
    super.onCreate();

    _running = true;

    TravelAssistanceApp.getComponent(this).inject(this);

    Notification notification = prepareForegroundNotification();
    startForeground(ID, notification);

    stopTracking();

    _systemBus.register(this);

    startTrackingNewPosition();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return new TrackingServiceBinder(this);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }

  @Override
  public void onDestroy() {
    _systemBus.unregister(this);

    stopTracking();

    _running = false;

    super.onDestroy();
  }

  //endregion

  //region Methods

  public void onEvent(Position position) {
    _positions.add(position);
  }

  protected Notification prepareForegroundNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    final String appName = getString(R.string.app_name);
    builder.setContentTitle(appName);
    builder.setContentText(getString(R.string.tap_to_return));
    builder.setTicker(appName);
    builder.setSmallIcon(android.R.drawable.ic_dialog_info); // TODO: 04/12/15 Icon
//		builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    builder.setWhen(System.currentTimeMillis());
    builder.setAutoCancel(false);

    Intent runApplicationIntent = new Intent(this, RoutesListActivity.class);
    runApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, runApplicationIntent, 0);

    builder.setContentIntent(pendingIntent);

    return builder.build();
  }

  protected void stopTracking() {
    LocationService locationService = _locationService;
    locationService.stopTracking();
  }

  protected void startTrackingNewPosition() {
    synchronized (_lock) {
      _positions.clear();
    }

    LocationService locationService = _locationService;

    locationService.startTracking(new LocationSettings(5, 5));
  }

  //endregion

  //region Nested classes

  public class TrackingServiceBinder extends Binder {
    private final TrackingService _service;

    public TrackingServiceBinder(TrackingService service) {
      _service = service;
    }

    public TrackingService getService() {
      return _service;
    }
  }

  //endregion
}
