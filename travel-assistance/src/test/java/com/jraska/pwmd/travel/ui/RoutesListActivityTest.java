package com.jraska.pwmd.travel.ui;

import com.jraska.ActivityBaseTest;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RoutesListActivityTest extends ActivityBaseTest<RoutesListActivity> {
  @Test
  public void testRecyclerViewUpdatedAfterNewRoute() throws Exception {
    RoutesListActivity activity = Robolectric.setupActivity(RoutesListActivity.class);

    activity._routesRecycler.layout(1, 1, 1, 1);
    RouteData routeData = mock(RouteData.class);
    activity._eventBus.post(new TravelDataRepository.NewRouteEvent(routeData));

    assertThat(activity._routesRecycler).hasLayoutRequested();
  }
}