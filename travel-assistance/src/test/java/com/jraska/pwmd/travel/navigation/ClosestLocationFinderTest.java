package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ClosestLocationFinderTest extends BaseTest {
  //region Constants

  private static final LatLng ZERO = pos(0, 0);
  private static final LatLng NORTH_WEST = pos(-1, 1);
  private static final LatLng SOUTH_WEST = pos(-1, -1);
  private static final LatLng NORTH_EAST = pos(1, 1);
  private static final LatLng SOUTH_EAST = pos(1, -1);

  //endregion

  @Test
  public void testFindClosestLocation() throws Exception {
    ArrayList<LatLng> locations = Lists.newArrayList(ZERO, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST);

    ClosestLocationFinder closestLocationFinder = new ClosestLocationFinder(locations);

    LatLng closestPosition = closestLocationFinder.findClosestLocation(pos(0.5, 0.8));
    assertThat(closestPosition).isEqualTo(NORTH_EAST);

    closestPosition = closestLocationFinder.findClosestLocation(pos(-0.5, 0.4));
    assertThat(closestPosition).isEqualTo(ZERO);

    closestPosition = closestLocationFinder.findClosestLocation(pos(-0.05, 0.7));
    assertThat(closestPosition).isEqualTo(ZERO);

    closestPosition = closestLocationFinder.findClosestLocation(pos(-0.5, 0.51));
    assertThat(closestPosition).isEqualTo(NORTH_WEST);

    closestPosition = closestLocationFinder.findClosestLocation(pos(0.7, -0.4));
    assertThat(closestPosition).isEqualTo(SOUTH_EAST);

    closestPosition = closestLocationFinder.findClosestLocation(pos(-0.5, -0.6));
    assertThat(closestPosition).isEqualTo(SOUTH_WEST);
  }

  private static LatLng pos(double lat, double lon) {
    return new LatLng(lat, lon);
  }
}