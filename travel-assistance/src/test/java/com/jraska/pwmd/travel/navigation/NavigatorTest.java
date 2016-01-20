package com.jraska.pwmd.travel.navigation;

import com.jraska.BaseTest;
import de.greenrobot.event.EventBus;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class NavigatorTest extends BaseTest {
  //region Test Methods

  @Test
  public void testComputeDesiredDirection() throws Exception {
    int[][] data = {
        // real, route, expected result
        {15, 75, 150},
        {240, 90, 300},
        {90, 150, 150},
        {150, 90, 30}};

    DirectionDecisionStrategy realDirectionSrategy = mock(DirectionDecisionStrategy.class);
    DirectionDecisionStrategy routeDirectionSrategy = mock(DirectionDecisionStrategy.class);
    Navigator navigator = new Navigator(mock(EventBus.class), mock(EventBus.class),
        realDirectionSrategy, routeDirectionSrategy);

    for (int[] testCase : data) {
      doReturn(testCase[0]).when(realDirectionSrategy).getDirection();
      doReturn(testCase[1]).when(routeDirectionSrategy).getDirection();

      assertThat(navigator.computeDesiredDirection()).isEqualTo(testCase[2]);
    }
  }


  //endregion
}