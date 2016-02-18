package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

public class NavigatorTest extends BaseTest {
  //region Test Methods

  @Test
  public void testComputeDesiredDirection() throws Exception {
    float[][] data = {
        // real, route, expected result
        {15, 75, 150},
        {240, 90, 300},
        {90, 150, 150},
        {150, 90, 30}};

    for (float[] testCase : data) {
      float desiredDirection = Navigator.computeDesiredDirection(testCase[0], testCase[1]);
      assertThat(desiredDirection).isEqualTo(testCase[2]);
    }
  }

  //endregion
}