package com.jraska.pwmd.travel.navigation;

import android.app.PendingIntent;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.jraska.BaseTest;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.DBFlowDataRepositoryTest;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RouteEventsManagerTest extends BaseTest {
  //region Fields

  EventBus _eventBus;
  GeofencingApi _geofencingApi;
  GoogleApiClient _googleApiClient;
  RouteEventsManager _eventsManager;

  RouteData _testRouteData;
  int _expectedEventsCount;

  //endregion

  //region Setup Mehtods

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    _testRouteData = DBFlowDataRepositoryTest.createRouteData();
    assertThat(_testRouteData.getTransportChangeSpecs()).isNotEmpty();
    assertThat(_testRouteData.getNoteSpecs()).isNotEmpty();

    _eventBus = new EventBus();
    _geofencingApi = mock(GeofencingApi.class);
    PendingResult pendingResultMock = mock(PendingResult.class);
    when(_geofencingApi.addGeofences(any(), any(GeofencingRequest.class), any()))
        .thenReturn(pendingResultMock);

    when(_geofencingApi.removeGeofences(any(), any(PendingIntent.class)))
        .thenReturn(pendingResultMock);

    _googleApiClient = mock(GoogleApiClient.class);
    when(_googleApiClient.blockingConnect()).thenReturn(new ConnectionResult(SUCCESS));

    _eventsManager = new RouteEventsManager(getApplication(), _eventBus,
        _googleApiClient, _geofencingApi);

    _expectedEventsCount = _testRouteData.getNoteSpecs().size() +
        _testRouteData.getTransportChangeSpecs().size();
  }

  //endregion

  //region Test methods

  @Test
  public void whenRouteDataWithNoExtras_thenNoGeofenceRequestsCreated() throws Exception {
    RouteEventsManager eventsManager = _eventsManager;

    RouteDescription routeDescription = mock(RouteDescription.class);
    RouteData routeData = new RouteData(routeDescription, Collections.emptyList());

    List<Geofence> geofencesList = eventsManager.createGeofencesList(routeData);
    assertThat(geofencesList).isEmpty();

    int eventsSync = eventsManager.setupEventsSync(routeData);
    assertThat(eventsSync).isZero();
    verifyZeroInteractions(_geofencingApi);
    verifyZeroInteractions(_googleApiClient);
  }

  @Test
  public void whenEventsCreated_thenCreatedWithCorrectCount() {
    RouteEventsManager eventsManager = _eventsManager;

    int eventsSync = eventsManager.setupEventsSync(_testRouteData);

    assertThat(eventsSync).isEqualTo(_expectedEventsCount);
    assertThat(eventsManager.getKeyCache().size()).isEqualTo(_expectedEventsCount);
  }

  @Test
  public void whenApiCalledWithGeofences_thenCorrectEventsAreFiredOnGeofenceFound() {
    RouteEventsManager eventsManager = _eventsManager;

    eventsManager.setupEventsSync(_testRouteData);

    ArgumentCaptor<GeofencingRequest> captor = ArgumentCaptor.forClass(GeofencingRequest.class);
    verify(_geofencingApi).addGeofences(any(), captor.capture(), any());
    GeofencingRequest geofencingRequest = captor.getValue();

    EventsCaptor eventsCaptor = new EventsCaptor();
    _eventBus.register(eventsCaptor);
    for (Geofence geofence : geofencingRequest.getGeofences()) {
      _eventsManager.onGeofence(geofence);
    }

    assertThat(_testRouteData.getNoteSpecs()).isEqualTo(eventsCaptor._noteSpecs);
    assertThat(_testRouteData.getTransportChangeSpecs()).isEqualTo(eventsCaptor._changeSpecs);
  }

  @Test
  public void whenEventsRemoved_thenRemoveOnApiCalled() throws Exception {
    _eventsManager.setupEventsSync(_testRouteData);
    _eventsManager.clearEventsSync();

    verify(_geofencingApi).removeGeofences(any(), any(PendingIntent.class));
  }

  @Test
  public void whenEventsRemoved_thenCacheAreEmpty() throws Exception {
    _eventsManager.setupEventsSync(_testRouteData);
    _eventsManager.clearEventsSync();

    assertThat(_eventsManager.getKeyCache().isEmpty()).isTrue();
  }

  @Test
  public void whenBroadcastIntentIsCreated_thenItMatchesGeofencingReceiverFilter() throws Exception {
    Intent broadcastIntent = _eventsManager.createBroadcastIntent();

    assertThat(RouteEventsManager.GeofencingReceiver.FILTER.matchAction(broadcastIntent.getAction()));
  }

  //endregion

  //region Nested classes

  public static class EventsCaptor {
    private List<NoteSpec> _noteSpecs = new ArrayList<>();
    private List<TransportChangeSpec> _changeSpecs = new ArrayList<>();

    @Subscribe
    public void onNote(NoteSpec noteSpec) {
      _noteSpecs.add(noteSpec);
    }

    @Subscribe
    public void onChange(TransportChangeSpec changeSpec) {
      _changeSpecs.add(changeSpec);
    }
  }

  //endregion
}