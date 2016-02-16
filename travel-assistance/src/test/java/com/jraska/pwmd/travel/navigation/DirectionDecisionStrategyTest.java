package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import org.assertj.core.data.Offset;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class DirectionDecisionStrategyTest extends BaseTest {

  //region Constants

  public static final List<LatLng> TEST_DATA = Collections.unmodifiableList(Lists.newArrayList(
      new LatLng(95, 85),
      new LatLng(85, 95),
      new LatLng(80, 70),
      new LatLng(70, 65),
      new LatLng(60, 70)));

  public static final List<LatLng> TEST_DATA2 = Collections.unmodifiableList(Lists.newArrayList(
      new LatLng(42.1234584, -83.1234577),
      new LatLng(42.1234583, -83.1234510)));

  public static final int TEST_DATA_DIRECTION = 180 + 33;
  public static final int TEST_DATA_DIRECTION2 = 91;

  //endregion

  //region Test Methods

  @Test
  public void testGetCoefficient() throws Exception {
    DirectionDecisionStrategy decisionStrategy = new DirectionDecisionStrategy(5);
    for (LatLng latLng : TEST_DATA) {
      decisionStrategy.addPoint(latLng);
    }

    double coefficient = decisionStrategy.computeDirectionCoefficient();

    assertThat(coefficient).isCloseTo(0.644, Offset.offset(0.001));
  }

  @Test
  public void testGetDirectionTestData2() throws Exception {
    DirectionDecisionStrategy decisionStrategy = new DirectionDecisionStrategy(2);
    for (LatLng latLng : TEST_DATA2) {
      decisionStrategy.addPoint(latLng);
    }

    int direction = decisionStrategy.getDirection();
    assertThat(direction).isEqualTo(TEST_DATA_DIRECTION2);
  }

  @Test
  public void testGetDirectionTestData() throws Exception {
    DirectionDecisionStrategy decisionStrategy = new DirectionDecisionStrategy(5);
    for (LatLng latLng : TEST_DATA) {
      decisionStrategy.addPoint(latLng);
    }

    int direction = decisionStrategy.getDirection();
    assertThat(direction).isEqualTo(TEST_DATA_DIRECTION);
  }

  @Test
  public void testGetDirectionReverseData() throws Exception {
    DirectionDecisionStrategy decisionStrategy = new DirectionDecisionStrategy(5);
    ArrayList<LatLng> reverseData = new ArrayList<>(TEST_DATA);
    Collections.reverse(reverseData);
    for (LatLng latLng : reverseData) {
      decisionStrategy.addPoint(latLng);
    }

    int direction = decisionStrategy.getDirection();
    assertThat(direction).isEqualTo(33);
  }

  @Test
  public void testCoefficientToAngle() throws Exception {
    double[][] expected = {{1, 45}, {-1, -45}, {0.644, 33}};

    for (double[] testCase : expected) {
      int angle = DirectionDecisionStrategy.coefficientToAngle(testCase[0]);
      assertThat(angle).isEqualTo((int) testCase[1]);
    }
  }

  @Test
  public void testKeepsOnlyLastResults() throws Exception {
    DirectionDecisionStrategy decisionStrategy = new DirectionDecisionStrategy(5);
    Random random = new Random();

    decisionStrategy.addPoint(new LatLng(random.nextDouble(), random.nextDouble()));
    decisionStrategy.addPoint(new LatLng(356, 117));

    for (LatLng pos : TEST_DATA) {
      decisionStrategy.addPoint(pos);
    }

    int direction = decisionStrategy.getDirection();
    assertThat(direction).isEqualTo(TEST_DATA_DIRECTION);
  }

  @Test
  public void testTwoPointsDirection() throws Exception {
    LatLng start = new LatLng(0, 0);
    LatLng end = new LatLng(1, 1);

    int direction = DirectionDecisionStrategy.getDirection(start, end);
    assertThat(direction).isEqualTo(45);

    direction = DirectionDecisionStrategy.getDirection(end, start);
    assertThat(direction).isEqualTo(225);

    DirectionDecisionStrategy directionDecisionStrategy = new DirectionDecisionStrategy(2);
    directionDecisionStrategy.addPoint(start);
    directionDecisionStrategy.addPoint(end);

    assertThat(directionDecisionStrategy.getDirection()).isEqualTo(45);
  }

  //endregion
}