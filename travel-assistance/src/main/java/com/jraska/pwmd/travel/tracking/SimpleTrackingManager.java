package com.jraska.pwmd.travel.tracking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import javax.inject.Provider;
import java.util.*;

public class SimpleTrackingManager implements TrackingManager {
  //region Fields

  private final Context _context;
  private final LocationFilter _filter;

  private boolean _running;

  @Nullable
  private TrackingService.TrackingServiceBinder _serviceBinder;
  private Date _start;

  private final TrackingServiceConnection _connection = new TrackingServiceConnection();
  private final List<TransportChangeSpec> _pendingChanges = new ArrayList<>();
  private final List<NoteSpec> _pendingNoteSpecs = new ArrayList<>();
  private final List<Location> _pendingLocations = new ArrayList<>();
  private final Provider<Location> _lastLocationProvider;
  private final EventBus _eventBus;

  private RouteData _routeData;
  private UserInput _lastUserInput;

  //endregion

  //region Constructors

  public SimpleTrackingManager(Context context, Provider<Location> lastLocationProvider,
                               LocationFilter filter, EventBus eventBus) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(lastLocationProvider);
    ArgumentCheck.notNull(filter);
    ArgumentCheck.notNull(eventBus);

    _context = context;
    _filter = filter;
    _lastLocationProvider = lastLocationProvider;
    _eventBus = eventBus;

    eventBus.register(this);
  }

  //endregion

  //region Properties

  protected Context getContext() {
    return _context;
  }

  protected LocationFilter getFilter() {
    return _filter;
  }

  protected List<TransportChangeSpec> getPendingChanges() {
    return Collections.unmodifiableList(_pendingChanges);
  }

  protected List<NoteSpec> getPendingNoteSpecs() {
    return Collections.unmodifiableList(_pendingNoteSpecs);
  }

  //endregion

  //region ITrackingManagementService impl


  @Nullable @Override public UserInput getLastUserInput() {
    return _lastUserInput;
  }

  @Override
  public boolean isTracking() {
    return _running;
  }

  @Override
  public void startTracking() {
    if (_running) {
      return;
    }

    Timber.i("Starting tracking");

    _pendingChanges.clear();
    _pendingNoteSpecs.clear();

    _start = new Date();
    Intent intent = getServiceIntent();
    _context.startService(intent);
    _context.bindService(intent, _connection, 0);

    _running = true;
  }

  @Override @Nullable
  public RouteData getRouteData(@NonNull UserInput userInput) {
    ArgumentCheck.notNull(userInput);

    _lastUserInput = userInput;

    if (!isTracking() || _serviceBinder == null) {
      return null;
    }

    List<LatLng> locations = filterPositions(_pendingLocations);
    if (isNotEnoughLocationsToSaveRoute(locations)) {
      return null;
    }

    if (_routeData == null) {
      RouteDescription routeDescription = new RouteDescription(_start, new Date(), userInput.getTitle());

      _routeData = new RouteData(routeDescription, locations, _pendingChanges, _pendingNoteSpecs);
    } else {
      _routeData.setTitle(userInput.getTitle());
      _routeData.setEnd(new Date());

      for (NoteSpec spec : _pendingNoteSpecs) {
        _routeData.addNote(spec);
      }

      for (TransportChangeSpec spec : _pendingChanges) {
        _routeData.addChange(spec);
      }

      for (LatLng latLng : locations) {
        _routeData.addLatLng(latLng);
      }
    }

    _pendingChanges.clear();
    _pendingNoteSpecs.clear();
    _pendingLocations.clear();

    return _routeData;
  }

  private boolean isNotEnoughLocationsToSaveRoute(List<LatLng> locations) {
    return locations.size() <= 1 && _routeData == null;
  }

  @Override
  public void stopTracking() {
    if (!_running) {
      return;
    }

    Timber.i("Stopping tracking");

    _pendingChanges.clear();

    for (NoteSpec spec : _pendingNoteSpecs) {
      if (!spec.exists()) {
        _eventBus.post(new TravelDataRepository.NoteSpecDeletedEvent(spec));
      }
    }

    _pendingNoteSpecs.clear();
    _routeData = null;
    _lastUserInput = null;

    _context.unbindService(_connection);
    _context.stopService(getServiceIntent());

    _serviceBinder = null;
    _running = false;
  }

  @Override
  public boolean addChange(int type, @NonNull String title) {
    ArgumentCheck.notNull(title);

    Location lastLocation = _lastLocationProvider.get();
    if (lastLocation == null) {
      Timber.w("Cannot add transportation change title=%s", title);

      return false;
    }

    _pendingChanges.add(new TransportChangeSpec(new LatLng(lastLocation), type, title));

    return true;
  }

  @Override
  public boolean addNote(@Nullable UUID imageId, @NonNull String caption,
                         @Nullable UUID soundId) {
    Location lastLocation = _lastLocationProvider.get();
    if (lastLocation == null) {
      Timber.w("Cannot add picture without location caption=%s", caption);

      return false;
    }

    _pendingNoteSpecs.add(new NoteSpec(new LatLng(lastLocation), imageId, caption, soundId));

    return true;
  }

  //endregion

  //region Methods

  @Subscribe
  public void onNewLocation(Location location) {
    if (isTracking()) {
      _pendingLocations.add(location);
    }
  }

  @Subscribe
  public void onRouteDeleted(TravelDataRepository.RouteDeleteEvent event) {
    // This happen in the case when route is recording and saved but the user
    // press delete in routes list
    if (_routeData != null && event._deletedRoute.getDeletedId() == _routeData.getId()) {
      Timber.d("Current route data were deleted. Invalidating...");
      _routeData = null;
    }
  }

  protected List<LatLng> filterPositions(List<Location> locations) {
    List<LatLng> filtered = new ArrayList<>(locations.size());

    LocationFilter filter = getFilter();
    for (Location location : locations) {
      if (filter.accept(location)) {
        filtered.add(new LatLng(location));
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
    public boolean accept(Location location) {
      if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
        return location.getAccuracy() < 50f;
      } else {
        return location.getAccuracy() < 30f;
      }
    }
  }

  //endregion
}
