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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

//@Ignore // Not working with async saving and deleting
public class DBFlowDataRepositoryTest extends BaseTest {
  //region Fields

  private EventBus _dataBus;
  protected TravelDataRepository _repository;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    _dataBus = new EventBus();
    _repository = new DBFlowDataRepository(_dataBus);
  }

  @After
  public void tearDown() throws Exception {
    FlowManager.destroy();
  }

  //endregion

  //region Test Methods

  @Test
  public void testInsert() throws Exception {
    RouteData insertedData = createRouteData();

    _repository.insertOrUpdate(insertedData);

    List<RouteData> routeDescriptions = _repository.selectAll();
    assertThat(routeDescriptions).hasSize(1);

    RouteData loadedData = _repository.select(routeDescriptions.get(0).getId());

    assertThat(loadedData.getNoteSpecs()).isEqualTo(insertedData.getNoteSpecs());
    assertThat(loadedData.getLocations()).isEqualTo(insertedData.getLocations());
    assertThat(loadedData.getTransportChangeSpecs()).isEqualTo(insertedData.getTransportChangeSpecs());
  }

  @Test
  public void testUpdate() {
    RouteData routeData = createRouteData();
    _repository.insertOrUpdate(routeData);

    ArrayList<LatLng> locations = new ArrayList<>(routeData.getPath());
    locations.add(generatePosition());
    RouteData routeData2 = new RouteData(routeData.getDescription(), locations);

    _repository.insertOrUpdate(routeData2);

    //try get all
    RouteData updatedData = _repository.select(routeData2.getId());
    assertThat(updatedData.getPath()).isEqualTo(routeData2.getPath());
  }

  @Test
  public void testDelete() throws Exception {
    RouteData routeData = createRouteData();

    _repository.insertOrUpdate(routeData);
    assertThat(_repository.selectAll()).hasSize(1);

    _repository.delete(routeData);

    assertThat(_repository.selectAll()).isEmpty();
  }

  //endregion

  //region Methods

  @NonNull
  public static RouteData createRouteData() {
    List<LatLng> latLngs = generatePositions(3);

    return createRouteData(latLngs);
  }

  public static RouteData createRouteData(List<LatLng> latLngs) {
    //build test route
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
}
