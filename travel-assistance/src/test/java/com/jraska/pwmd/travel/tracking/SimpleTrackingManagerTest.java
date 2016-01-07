package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.TableRouteDataRepositoryTest;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SimpleTrackingManagerTest extends BaseTest {
  //region Fields

  private SimpleTrackingManager _simpleTrackingManager;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    LocationService locationService = mock(LocationService.class);
    doReturn(TableRouteDataRepositoryTest.generatePosition())
        .when(locationService).getLastPosition();

    _simpleTrackingManager = new SimpleTrackingManager(mock(Context.class), locationService,
        mock(LocationFilter.class));
  }

  //endregion

  //region Test Methods

  @Test
  public void testTransportChangesEmptyAfterStart() throws Exception {
    _simpleTrackingManager.addChange(TransportChangeSpec.TRANSPORT_TYPE_BUS, "s");


    assertThat(_simpleTrackingManager.getChanges(), hasSize(1));
    _simpleTrackingManager.startTracking();

    assertThat(_simpleTrackingManager.getChanges(), hasSize(0));
  }

  @Test
  public void testNotesEmptyAfterStart() throws Exception {
    _simpleTrackingManager.addNote(UUID.randomUUID(), "s", null);


    assertThat(_simpleTrackingManager.getNoteSpecs(), hasSize(1));
    _simpleTrackingManager.startTracking();

    assertThat(_simpleTrackingManager.getNoteSpecs(), hasSize(0));
  }

  @Test
  public void testTransportChangesEmptyAfterStop() throws Exception {
    _simpleTrackingManager.startTracking();

    _simpleTrackingManager.addChange(TransportChangeSpec.TRANSPORT_TYPE_BUS, "s");
    assertThat(_simpleTrackingManager.getChanges(), hasSize(1));

    _simpleTrackingManager.stopTracking();
    assertThat(_simpleTrackingManager.getChanges(), hasSize(0));
  }

  //endregion
}