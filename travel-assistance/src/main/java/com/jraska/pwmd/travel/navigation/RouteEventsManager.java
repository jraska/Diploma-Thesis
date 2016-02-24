package com.jraska.pwmd.travel.navigation;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.raizlabs.android.dbflow.structure.BaseModel;
import hugo.weaving.DebugLog;
import org.greenrobot.eventbus.EventBus;
import rx.Observable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

@PerApp
public class RouteEventsManager {
  //region Constants

  public static final float THRESHOLD_METERS = 30;

  //endregion

  //region Fields

  private final Context _context;
  private final EventBus _eventBus;
  private final GoogleApiClient _googleApiClient;
  private final GeofencingApi _geofencingApi;

  private final GeofencingReceiver _geofencingReceiver = new GeofencingReceiver(this);
  private final RecordToKeyCache _keyCache = new RecordToKeyCache();

  private PendingIntent _geoFencePendingIntent;

  //endregion

  //region Constructors

  @Inject
  public RouteEventsManager(Context context, EventBus eventBus, GoogleApiClient googleApiClient,
                            GeofencingApi geofencingApi) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(eventBus);
    ArgumentCheck.notNull(googleApiClient);
    ArgumentCheck.notNull(geofencingApi);

    _context = context.getApplicationContext();
    _eventBus = eventBus;
    _googleApiClient = googleApiClient;
    _geofencingApi = geofencingApi;
  }

  //endregion

  //region Properties

  RecordToKeyCache getKeyCache() {
    return _keyCache;
  }

  //endregion

  //region Methods

  public Observable<Integer> setupEvents(RouteData routeData) {
    return Observable.fromCallable(() -> setupEventsSync(routeData));
  }

  public Observable<Integer> clearEvents() {
    return Observable.fromCallable(this::clearEventsSync);
  }

  @DebugLog
  protected int setupEventsSync(RouteData routeData) {
    List<Geofence> geofencesList = createGeofencesList(routeData);
    if (geofencesList.isEmpty()) {
      Timber.d("No events for route is=%d, title='%s'", routeData.getId(), routeData.getTitle());
      return 0;
    }

    connectSync();
    _context.registerReceiver(_geofencingReceiver, GeofencingReceiver.FILTER);

    GeofencingRequest geofencingRequest = createGeofencingRequest(geofencesList);
    _geofencingApi.addGeofences(_googleApiClient, geofencingRequest, getGeofencePendingIntent())
        .setResultCallback(new LoggingCallbacks("Adding Geofences"));

    disconnectSync();
    return _keyCache.size();
  }

  @DebugLog
  protected int clearEventsSync() {
    if (_keyCache.isEmpty()) {
      return 0;
    }

    _context.unregisterReceiver(_geofencingReceiver);

    connectSync();

    _geofencingApi.removeGeofences(_googleApiClient, getGeofencePendingIntent())
        .setResultCallback(new LoggingCallbacks("Removing Geofences"));

    disconnectSync();
    _keyCache.clear();
    return 0;
  }

  private GeofencingRequest createGeofencingRequest(List<Geofence> geofences) {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofences(geofences);
    return builder.build();
  }

  protected List<Geofence> createGeofencesList(RouteData routeData) {
    ArrayList<Geofence> geofences = new ArrayList<>();

    for (NoteSpec noteSpec : routeData.getNoteSpecs()) {
      Geofence geofence = createGeofence(_keyCache.newKey(noteSpec), noteSpec.getLatLng());
      geofences.add(geofence);
    }

    for (TransportChangeSpec changeSpec : routeData.getTransportChangeSpecs()) {
      Geofence geofence = createGeofence(_keyCache.newKey(changeSpec), changeSpec.getLatLng());
      geofences.add(geofence);
    }

    return geofences;
  }

  private Geofence createGeofence(String key, LatLng latLng) {
    return new Geofence.Builder()
        .setRequestId(key)
        .setCircularRegion(latLng._latitude, latLng._longitude, THRESHOLD_METERS)
        .setExpirationDuration(NEVER_EXPIRE)
        .setTransitionTypes(GEOFENCE_TRANSITION_ENTER)
        .build();
  }

  private void connectSync() {
    ConnectionResult connectionResult = _googleApiClient.blockingConnect();
    if (!connectionResult.isSuccess()) {
      Timber.w("Failed to connect result=%s", connectionResult);
    }
  }

  private void disconnectSync() {
    _googleApiClient.disconnect();
  }

  private PendingIntent getGeofencePendingIntent() {
    if (_geoFencePendingIntent == null) {
      Intent intent = createBroadcastIntent();
      _geoFencePendingIntent = PendingIntent.getBroadcast(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    return _geoFencePendingIntent;
  }

  protected Intent createBroadcastIntent() {
    return new Intent(GeofencingReceiver.ACTION_GEO_FENCE);
  }

  protected void onGeofencingEvent(@NonNull GeofencingEvent event) {
    if (event.hasError()) {
      Timber.w("Received event with error code = %d event= %s", event.getErrorCode(), event);
      return;
    }
    Timber.d("Geofencing event received %s", event);

    if (event.getGeofenceTransition() == GEOFENCE_TRANSITION_ENTER) {
      for (Geofence geofence : event.getTriggeringGeofences()) {
        onGeofence(geofence);
      }
    }
  }

  protected void onGeofence(Geofence geofence) {
    BaseModel baseModel = _keyCache.get(geofence.getRequestId());
    if (baseModel == null) {
      Timber.w("Nothing was found for geofence %s", geofence);
      return;
    }

    // correct subscribers will be determined by type of model
    Timber.d("Posting %s event", baseModel);
    _eventBus.post(baseModel);
  }

  //endregion

  //region Nested classes

  static class GeofencingReceiver extends BroadcastReceiver {
    public static final String ACTION_GEO_FENCE = "com.jraska.pwmd.travel.Geofence";

    public static final IntentFilter FILTER = new IntentFilter(ACTION_GEO_FENCE);

    private final RouteEventsManager _routeEventsManager;

    GeofencingReceiver(RouteEventsManager routeEventsManager) {
      _routeEventsManager = routeEventsManager;
    }

    @Override public void onReceive(Context context, Intent intent) {
      Timber.v("Received intent: %s", intent);

      GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
      if (geofencingEvent == null) {
        Timber.w("Received geofence is null");
        return;
      }

      _routeEventsManager.onGeofencingEvent(geofencingEvent);
    }
  }

  static class LoggingCallbacks implements ResultCallback<Status> {
    private final String _action;

    LoggingCallbacks(String action) {
      _action = action;
    }

    @Override
    public void onResult(@NonNull Status status) {
      if (status.isSuccess()) {
        Timber.v("%s successful.", _action);
      } else {
        Timber.w("%s unsuccessful! Status: %s", _action, status);
      }
    }
  }

  //endregion
}
