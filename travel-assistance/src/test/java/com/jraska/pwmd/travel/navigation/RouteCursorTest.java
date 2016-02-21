package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static com.jraska.pwmd.travel.navigation.ClosestLocationFinderTest.pos;
import static org.assertj.core.api.Assertions.assertThat;

public class RouteCursorTest extends BaseTest {
  //region Constants

  private static final LatLng FIRST = pos(0, 0);
  private static final LatLng SECOND = pos(1, 0.5);
  private static final LatLng BETWEEN = pos(1, 0.6);
  private static final LatLng THIRD = pos(1.1, 1.2);

  public static final List<LatLng> TEST_PATH = Lists.newArrayList(FIRST, SECOND, THIRD);

  //endregion

  //region Methods

  @Test
  public void whenExactRouteIsClosest_thenReturnsBearinOfThisAndFollowingPoint() throws Exception {
    ClosestLocationFinder finder = new ClosestLocationFinder(TEST_PATH);
    RouteCursor routeCursor = new RouteCursor(finder);

    float routeBearing = routeCursor.getRouteDirection(FIRST.toLocation())._routeBearing;
    assertThat(routeBearing).isEqualTo(FIRST.bearingTo(SECOND));

    routeBearing = routeCursor.getRouteDirection(SECOND.toLocation())._routeBearing;
    assertThat(routeBearing).isEqualTo(SECOND.bearingTo(THIRD));
  }

  @Test
  public void whenLastPointIsClosest_thenReturnBearingFromPreviousToLast() throws Exception {
    ClosestLocationFinder finder = new ClosestLocationFinder(TEST_PATH);
    RouteCursor routeCursor = new RouteCursor(finder);

    float routeBearing = routeCursor.getRouteDirection(THIRD.toLocation())._routeBearing;
    assertThat(routeBearing).isEqualTo(SECOND.bearingTo(THIRD));
  }

  @Test
  public void whenPointBetween_thenReturnBearingFromPreviousToLast() throws Exception {
    ClosestLocationFinder finder = new ClosestLocationFinder(TEST_PATH);
    RouteCursor routeCursor = new RouteCursor(finder);

    float routeBearing = routeCursor.getRouteDirection(BETWEEN.toLocation())._routeBearing;
    assertThat(routeBearing).isEqualTo(SECOND.bearingTo(THIRD));
  }

  //endregion
}