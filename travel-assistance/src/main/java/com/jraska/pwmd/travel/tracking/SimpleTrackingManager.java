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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleTrackingManager implements TrackingManager {
  //region Fields

  private final Context _context;
  private final LocationFilter _filter;

  private boolean _running;
  private TrackingService.TrackingServiceBinder _serviceBinder;
  private Date _start;

  private final TrackingServiceConnection _connection = new TrackingServiceConnection();

  //endregion

  //region Constructors

  public SimpleTrackingManager(Context context) {
    this(context, LocationFilter.Empty);
  }

  public SimpleTrackingManager(Context context, LocationFilter filter) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(filter);

    _context = context;
    _filter = filter;
  }

  //endregion

  //region Properties

  protected Context getContext() {
    return _context;
  }

  protected LocationFilter getFilter() {
    return _filter;
  }

  //endregion

  //region ITrackingManagementService impl

  @Override
  public boolean isTracking() {
    return _running;
  }

  @Override
  public void startTracking() {
    if (_running) {
      return;
    }

    _start = new Date();
    Intent intent = getServiceIntent();
    _context.startService(intent);
    _context.bindService(intent, _connection, 0);

    _running = true;
  }

  @Override
  public PathInfo getLastPath() {
    if (_serviceBinder == null) {
      return null;
    }

    List<Position> positions = _serviceBinder.getService().getPositions();

    if (positions.size() == 0) {
      return null;
    }

    positions = filterPositions(positions);

    return new PathInfo(_start, new Date(), new Path(positions));
  }

  @Override
  public void stopTracking() {
    if (!_running) {
      return;
    }

    _context.unbindService(_connection);
    _context.stopService(getServiceIntent());

    _serviceBinder = null;
    _running = false;
  }

  //endregion

  //region Methods

  protected List<Position> filterPositions(List<Position> positions) {
    List<Position> filtered = new ArrayList<>(positions.size());

    LocationFilter filter = getFilter();
    for (Position position : positions) {
      if (filter.accept(position)) {
        filtered.add(position);
      }
    }

    return filtered;
  }

  protected Intent getServiceIntent() {
    return new Intent(_context, TrackingService.class);
  }

  //endregion

  //region Nested classes

  protected class TrackingServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      _serviceBinder = (TrackingService.TrackingServiceBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  }

  public static class GpsProviderOnlyFilter implements LocationFilter {
    @Override
    public boolean accept(Position position) {
      return LocationManager.GPS_PROVIDER.equals(position.provider);
    }
  }

  //endregion
}
