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
import org.junit.Ignore;
import org.junit.Test;
import timber.log.Timber;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Ignore // Not working with async saving and deleting
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

    CountDownLatch insertLatch = new SubscriberLatch(1);
    _dataBus.register(insertLatch);

    _repository.insertOrUpdate(insertedData);
    boolean await = insertLatch.await(1000, TimeUnit.MILLISECONDS);
    assertTrue("Not inserted in time", await);

    List<RouteData> routeDescriptions = _repository.selectAll();
    assertThat(routeDescriptions, hasSize(1));

    RouteData loadedData = _repository.select(routeDescriptions.get(0).getId());

    assertThat(loadedData.getNoteSpecs(), equalTo(insertedData.getNoteSpecs()));
    assertThat(loadedData.getLocations(), equalTo(insertedData.getLocations()));
    assertThat(loadedData.getTransportChangeSpecs(), equalTo(insertedData.getTransportChangeSpecs()));
  }

  @Test @Ignore //Update not tested now
  public void testUpdate() {

    RouteData routeData = createRouteData();
    _repository.insertOrUpdate(routeData);

    ArrayList<LatLng> locations = new ArrayList<>(routeData.getPath());
    locations.add(generatePosition());
    RouteData routeData2 = new RouteData(routeData.getDescription(), locations);

    _repository.insertOrUpdate(routeData2);

    //try get all
    RouteData updatedData = _repository.select(routeData2.getId());
    assertThat(updatedData.getPath(), equalTo(routeData2.getPath()));
  }

  @Test
  public void testDelete() throws Exception {
    RouteData routeData = createRouteData();

    Timber.d("Test");

    CountDownLatch insertLatch = new SubscriberLatch(1);
    _dataBus.register(insertLatch);

    _repository.insertOrUpdate(routeData);
    boolean await = insertLatch.await(1000, TimeUnit.MILLISECONDS);
    assertTrue("Not inserted in time", await);

    assertThat(_repository.selectAll(), hasSize(1));

    CountDownLatch deleteLatch = new SubscriberLatch(1);
    _dataBus.register(deleteLatch);
    _repository.delete(routeData);

    await = deleteLatch.await(1000, TimeUnit.MILLISECONDS);
    assertTrue("Not deleted in time", await);

    assertThat(_repository.selectAll(), hasSize(0));
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

  //region Nested classes

  public static class Tree extends Timber.Tree {
    @Override protected void log(int priority, String tag, String message, Throwable t) {
      System.out.println(message);
    }
  }

  public static class SubscriberLatch extends CountDownLatch {

    public SubscriberLatch(int count) {
      super(count);
    }

    @Subscribe
    public void onNewRoute(TravelDataRepository.NewRouteEvent e) {
      countDown();
    }

    @Subscribe
    public void onRouteDeleted(TravelDataRepository.RouteDeletedEvent e) {
      countDown();
    }
  }

  public static class TestSubscriber {
    public int _eventFired;

    @Subscribe
    public void onRouteDeleted(TravelDataRepository.NewRouteEvent event) {
      _eventFired++;
    }
  }

  //endregion
}
