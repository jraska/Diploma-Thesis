package com.jraska.pwmd.travel.persistence;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.*;
import de.greenrobot.event.EventBus;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;


public class TableRouteDataRepositoryTest extends BaseTest {
  //region Fields

  private EventBus _dataBus;
  private SQLiteOpenHelper _openHelper;
  protected TravelDataRepository _repository;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    _dataBus = new EventBus();
    _openHelper = new TravelAssistanceDbHelper(getApplication(), "testDataDb.sqlite");
    _repository = new TableRouteDataRepository(_openHelper, _dataBus);
  }

  @After
  public void tearDown() throws Exception {
    _openHelper.close();
  }

  //endregion

  //region Test Methods

  @Test
  public void testInsert() throws Exception {
    RouteData routeData = createRouteData();

    _repository.insertRoute(routeData);

    List<RouteDescription> routeDescriptions = _repository.selectAllRouteDescriptions();
    assertThat(routeDescriptions, hasSize(1));

    RouteData loadedData = _repository.selectRouteData(routeDescriptions.get(0).getId());
    assertThat(loadedData, equalTo(routeData));
  }

  @Test
  public void testUpdate() {

    RouteData routeData = createRouteData();
    _repository.insertRoute(routeData);

    ArrayList<Position> positions = new ArrayList<>(routeData.getPath().getPoints());
    positions.add(generatePosition());
    RouteData routeData2 = new RouteData(routeData.getDescription(), new Path(positions));

    _repository.updateRoute(routeData2);

    //try get all
    RouteData updatedData = _repository.selectRouteData(routeData2.getId());
    assertThat(updatedData.getPath(), equalTo(routeData2.getPath()));
  }

  @Test
  public void testDelete() throws Exception {
    RouteData routeData = createRouteData();

    _repository.insertRoute(routeData);

    _repository.deleteRoute(routeData.getId());

    assertThat(_repository.selectAllRouteDescriptions(), hasSize(0));
  }

  @Test
  public void whenRouteDataInserted_thenInsertEventFired() throws Exception {
    RouteData routeData = createRouteData();

    TestSubscriber testSubscriber = new TestSubscriber();
    _dataBus.register(testSubscriber);

    _repository.insertRoute(routeData);

    Assertions.assertThat(testSubscriber._eventFired).isEqualTo(1);
  }

  //endregion

  //region Methods

  @NonNull
  protected RouteData createRouteData() {
    List<Position> positions = generatePositions(3);

    //build test route
    RouteDescription routeDescription = new RouteDescription(UUID.randomUUID(), new Date(), new Date(), "Test");
    ArrayList<TransportChangeSpec> specs = new ArrayList<>();
    specs.add(new TransportChangeSpec(generatePosition().latLng, TransportChangeSpec.TRANSPORT_TYPE_TRAIN, "ds"));
    specs.add(new TransportChangeSpec(generatePosition().latLng, TransportChangeSpec.TRANSPORT_TYPE_BUS, "uii"));

    ArrayList<NoteSpec> noteSpecs = new ArrayList<>();
    noteSpecs.add(new NoteSpec(generatePosition().latLng, UUID.randomUUID(), "das"));

    return new RouteData(routeDescription, new Path(positions), specs, noteSpecs);
  }

  @NonNull
  protected List<Position> generatePositions(int pointsCount) {
    List<Position> positions = new ArrayList<>(pointsCount);
    for (int i = 0; i < pointsCount; i++) {
      positions.add(generatePosition());
    }
    return positions;
  }

  public static Position generatePosition() {
    Random random = new Random();
    return new Position(new LatLng(random.nextDouble() * 50, random.nextDouble() * 50), System.currentTimeMillis(), 30.0f, "GPS");
  }

  //endregion

  //region Nested classes

  public static class TestSubscriber {
    public int _eventFired;

    public void onEvent(TravelDataRepository.NewRouteEvent event) {
      _eventFired++;
    }
  }

  //endregion
}
