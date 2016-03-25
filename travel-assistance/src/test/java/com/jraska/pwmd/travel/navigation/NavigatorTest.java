package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.persistence.DBFlowDataRepositoryTest;
import com.jraska.pwmd.travel.util.PathSmoother;
import org.assertj.core.util.Lists;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jraska.pwmd.travel.navigation.ClosestLocationFinderTest.pos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigatorTest extends BaseTest {
  //region Constants

  private static final LatLng FIRST = pos(49, 19);
  private static final LatLng SECOND = pos(49.0001, 19.000001);
  private static final LatLng THIRD = pos(49.00021, 19.000004);
  private static final List<LatLng> TEST_PATH = Lists.newArrayList(FIRST, SECOND, THIRD);

  private static final LatLng POINT_FAR_AWAY = pos(0, 0);
  private static final LatLng POINT_CLOSE = pos(49.00021, 19.000002);

  //endregion

  //region Fields

  private Navigator _navigator;
  private Compass _compass;
  private EventBus _eventBus;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() {
    _eventBus = new EventBus();
    _compass = mock(Compass.class);
    _navigator = new Navigator(_eventBus, _compass, new PathSmoother());
    RouteData routeData = DBFlowDataRepositoryTest.createRouteData(TEST_PATH);

    _navigator.startNavigation(routeData);
  }

  @After
  public void tearDown() {
    _navigator.stopNavigation();
  }

  //endregion

  //region Test Methods

  @Test
  public void testComputeDesiredDirection() {
    float[][] data = {
        // real, route, expected result
        {15, 75, 60},
        {240, 90, -150},
        {90, 150, 60},
        {150, 90, -60}};

    for (float[] testCase : data) {
      float desiredDirection = Navigator.computeDesiredBearing(testCase[0], testCase[1]);
      assertThat(desiredDirection).isEqualTo(testCase[2]);
    }
  }

  @Test
  public void whenClosePointToRouteFound_theSwitchedToOnRouteState() {
    assertThat(_navigator.getState()).isExactlyInstanceOf(Navigator.ApproachingToRouteState.class);

    _eventBus.post(POINT_FAR_AWAY.toLocation());
    assertThat(_navigator.getState()).isExactlyInstanceOf(Navigator.ApproachingToRouteState.class);

    _eventBus.post(POINT_CLOSE.toLocation());
    assertThat(_navigator.getState()).isExactlyInstanceOf(Navigator.OnRouteState.class);
  }

  @Test
  public void whenFarAwayPointToRouteFound_theSwitchedToOnApproachingState() {
    _eventBus.post(POINT_CLOSE.toLocation());
    assertThat(_navigator.getState()).isExactlyInstanceOf(Navigator.OnRouteState.class);

    _eventBus.post(POINT_FAR_AWAY.toLocation());
    assertThat(_navigator.getState()).isExactlyInstanceOf(Navigator.ApproachingToRouteState.class);
  }

  @Test
  public void whenFarAwayLocationReceived_theCorrectDesiredDirectionEventFired() {

    float testBearing = 24.33f;
    when(_compass.getBearing()).thenReturn(testBearing);

    StoreRequiredDirectionSubscriber testSubscriber = new StoreRequiredDirectionSubscriber();
    _eventBus.register(testSubscriber);
    _eventBus.post(POINT_FAR_AWAY.toLocation());
    LatLng closest = new ClosestLocationFinder(TEST_PATH).findClosestLocation(POINT_FAR_AWAY);
    float bearingToRoute = POINT_FAR_AWAY.bearingTo(closest);
    float expectedDesiredBearing = Navigator.computeDesiredBearing(testBearing, bearingToRoute);

    assertThat(testSubscriber._directionEvent).isNotNull();
    assertThat(testSubscriber._directionEvent._bearing).isEqualTo(expectedDesiredBearing);
  }

  @Test
  public void whenOnRouteLocationReceived_theCorrectDesiredDirectionByRouteEventFired() {
    float testBearing = 14.73f;
    when(_compass.getBearing()).thenReturn(testBearing);

    StoreRequiredDirectionSubscriber testSubscriber = new StoreRequiredDirectionSubscriber();
    _eventBus.register(testSubscriber);
    _eventBus.post(POINT_CLOSE.toLocation());
    float routeBearing = SECOND.bearingTo(THIRD);
    float expectedDesiredBearing = Navigator.computeDesiredBearing(testBearing, routeBearing);

    assertThat(testSubscriber._directionEvent).isNotNull();
    assertThat(testSubscriber._directionEvent._bearing).isEqualTo(expectedDesiredBearing);
  }

  //endregion

  //region Nested class

  public static class StoreRequiredDirectionSubscriber {
    Navigator.RequiredDirectionEvent _directionEvent;

    @Subscribe
    public void onDesiredDirection(Navigator.RequiredDirectionEvent event) {
      _directionEvent = event;
    }
  }

  //endregion
}