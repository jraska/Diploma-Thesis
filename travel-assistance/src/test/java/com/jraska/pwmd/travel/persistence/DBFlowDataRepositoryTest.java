package com.jraska.pwmd.travel.persistence;

import android.support.annotation.NonNull;
import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.raizlabs.android.dbflow.config.FlowManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DBFlowDataRepositoryTest extends BaseTest {
  //region Fields

  private EventBus _eventBus;
  protected DBFlowDataRepository _repository;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() {
    _eventBus = new EventBus();
    _repository = new DBFlowDataRepository(_eventBus);
  }

  @After
  public void tearDown() {
    FlowManager.destroy();
  }

  //endregion

  //region Test Methods

  @Test
  public void testInsert() {
    RouteData insertedData = createRouteData();

    _repository.insertOrUpdateSync(insertedData);

    List<RouteData> routeDescriptions = _repository.selectAllSync();
    assertThat(routeDescriptions).hasSize(1);

    RouteData loadedData = _repository.selectSync(routeDescriptions.get(0).getId());

    assertThat(loadedData.getNoteSpecs()).isEqualTo(insertedData.getNoteSpecs());
    assertThat(loadedData.getLocations()).isEqualTo(insertedData.getLocations());
    assertThat(loadedData.getTransportChangeSpecs()).isEqualTo(insertedData.getTransportChangeSpecs());
  }

  @Test
  public void testUpdate() {
    RouteData routeData = createRouteData();
    _repository.insertOrUpdateSync(routeData);

    ArrayList<LatLng> locations = new ArrayList<>(routeData.getPath());
    locations.add(generatePosition());
    RouteData routeData2 = new RouteData(routeData.getDescription(), locations);

    _repository.insertOrUpdateSync(routeData2);

    //try get all
    RouteData updatedData = _repository.selectSync(routeData2.getId());
    assertThat(updatedData.getPath()).isEqualTo(routeData2.getPath());
  }

  @Test
  public void testDelete() {
    RouteData routeData = createRouteData();

    _repository.insertOrUpdateSync(routeData);
    assertThat(_repository.selectAllSync()).hasSize(1);

    _repository.deleteSync(routeData);

    assertThat(_repository.selectAllSync()).isEmpty();
  }

  @Test
  public void whenRouteInserted_inserteRouteDataEventFired() {
    RouteData routeData = createRouteData();

    NewRouteEventSubscriber newRouteEventSubscriber = new NewRouteEventSubscriber();
    _eventBus.register(newRouteEventSubscriber);

    UpdateRouteEventSubscriber updateRouteEventSubscriber = new UpdateRouteEventSubscriber();
    _eventBus.register(updateRouteEventSubscriber);

    _repository.insertOrUpdateSync(routeData);

    assertThat(newRouteEventSubscriber._events).hasSize(1);
    assertThat(updateRouteEventSubscriber._events).isEmpty();
  }

  @Test
  public void whenRouteUpdated_updateRouteDataEventFired() {
    RouteData routeData = createRouteData();

    UpdateRouteEventSubscriber updateRouteEventSubscriber = new UpdateRouteEventSubscriber();
    _eventBus.register(updateRouteEventSubscriber);

    _repository.insertOrUpdateSync(routeData);
    _repository.insertOrUpdateSync(routeData);

    assertThat(updateRouteEventSubscriber._events).hasSize(1);
  }

  //endregion

  //region Methods

  @NonNull
  public static RouteData createRouteData() {
    List<LatLng> latLngs = generatePositions(3);

    return createRouteData(latLngs);
  }

  public static RouteData createRouteData(List<LatLng> latLngs) {
    RouteDescription routeDescription = new RouteDescription(new Date(), new Date(), "Test");
    ArrayList<TransportChangeSpec> specs = new ArrayList<>();
    specs.add(new TransportChangeSpec(generatePosition(), TransportChangeSpec.TRANSPORT_TYPE_TRAIN, "ds"));
    specs.add(new TransportChangeSpec(generatePosition(), TransportChangeSpec.TRANSPORT_TYPE_BUS, "uii"));

    ArrayList<NoteSpec> noteSpecs = new ArrayList<>();
    noteSpecs.add(new NoteSpec(generatePosition(), UUID.randomUUID(), "das"));

    return new RouteData(routeDescription, latLngs, specs, noteSpecs);
  }

  @NonNull
  protected static List<LatLng> generatePositions(int pointsCount) {
    List<LatLng> locations = new ArrayList<>(pointsCount);
    for (int i = 0; i < pointsCount; i++) {
      locations.add(generatePosition());
    }
    return locations;
  }

  public static LatLng generatePosition() {
    Random random = new Random();
    return new LatLng(random.nextDouble() * 50, random.nextDouble() * 50);
  }

  //endregion

  //region Nested classes

  public static class NewRouteEventSubscriber {
    private final List<TravelDataRepository.NewRouteEvent> _events = new ArrayList<>();

    @Subscribe
    public void onNewRoute(TravelDataRepository.NewRouteEvent e) {
      _events.add(e);
    }
  }

  public static class UpdateRouteEventSubscriber {
    private final List<TravelDataRepository.UpdatedRouteEvent> _events = new ArrayList<>();

    @Subscribe
    public void onNewRoute(TravelDataRepository.UpdatedRouteEvent e) {
      _events.add(e);
    }
  }

  //endregion
}
