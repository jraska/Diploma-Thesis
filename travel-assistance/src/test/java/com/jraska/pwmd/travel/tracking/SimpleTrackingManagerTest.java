package com.jraska.pwmd.travel.tracking;

import android.content.Context;
import android.location.Location;
import com.jraska.BaseTest;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.DBFlowDataRepositoryTest;
import dagger.internal.InstanceFactory;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class SimpleTrackingManagerTest extends BaseTest {
  //region Fields

  private SimpleTrackingManager _simpleTrackingManager;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    Provider<Location> locationProvider = InstanceFactory.create(generateLocation());

    _simpleTrackingManager = new SimpleTrackingManager(mock(Context.class), locationProvider,
        mock(LocationFilter.class), new EventBus());
  }

  //endregion

  //region Test Methods

  @Test
  public void testTransportChangesEmptyAfterStart() throws Exception {
    _simpleTrackingManager.addChange(TransportChangeSpec.TRANSPORT_TYPE_BUS, "s");


    assertThat(_simpleTrackingManager.getPendingChanges(), hasSize(1));
    _simpleTrackingManager.startTracking();

    assertThat(_simpleTrackingManager.getPendingChanges(), hasSize(0));
  }

  @Test
  public void testNotesEmptyAfterStart() throws Exception {
    _simpleTrackingManager.addNote(UUID.randomUUID(), "s", null);


    assertThat(_simpleTrackingManager.getPendingNoteSpecs(), hasSize(1));
    _simpleTrackingManager.startTracking();

    assertThat(_simpleTrackingManager.getPendingNoteSpecs(), hasSize(0));
  }

  @Test
  public void testTransportChangesEmptyAfterStop() throws Exception {
    _simpleTrackingManager.startTracking();

    _simpleTrackingManager.addChange(TransportChangeSpec.TRANSPORT_TYPE_BUS, "s");
    assertThat(_simpleTrackingManager.getPendingChanges(), hasSize(1));

    _simpleTrackingManager.stopTracking();
    assertThat(_simpleTrackingManager.getPendingChanges(), hasSize(0));
  }

  //endregion

  //region Methods

  private Location generateLocation() {
    return DBFlowDataRepositoryTest.generatePosition().toLocation();
  }

  //endregion
}