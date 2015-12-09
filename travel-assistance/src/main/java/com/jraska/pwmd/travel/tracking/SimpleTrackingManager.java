package com.jraska.pwmd.travel.tracking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Collections;
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
  private final List<TransportChangeSpec> _changes = new ArrayList<>();
  private final LocationService _locationService;

  //endregion

  //region Constructors

  public SimpleTrackingManager(Context context, LocationService locationService, LocationFilter filter) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(locationService);
    ArgumentCheck.notNull(filter);

    _context = context;
    _filter = filter;
    _locationService = locationService;
  }

  //endregion

  //region Properties

  protected Context getContext() {
    return _context;
  }

  protected LocationFilter getFilter() {
    return _filter;
  }

  protected List<TransportChangeSpec> getChanges() {
    return Collections.unmodifiableList(_changes);
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

    _changes.clear();

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

    positions = filterPositions(positions);

    if (positions.size() == 0) {
      return null;
    }

    return new PathInfo(_start, new Date(), new Path(positions), new ArrayList<>(_changes));
  }

  @Override
  public void stopTracking() {
    if (!_running) {
      return;
    }

    _changes.clear();

    _context.unbindService(_connection);
    _context.stopService(getServiceIntent());

    _serviceBinder = null;
    _running = false;
  }

  @Override
  public boolean addChange(int type, @NonNull String title, String description) {
    ArgumentCheck.notNull(title);

    Position lastPosition = _locationService.getLastPosition();
    if (lastPosition == null) {
      Timber.w("Cannot add transportation change title=%s", title);

      return false;
    }

    _changes.add(new TransportChangeSpec(lastPosition.latLng, type, title, description));

    return true;
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
      // No handling here
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
