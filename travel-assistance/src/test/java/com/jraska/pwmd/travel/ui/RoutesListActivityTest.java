package com.jraska.pwmd.travel.ui;

import com.jraska.ActivityBaseTest;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RoutesListActivityTest extends ActivityBaseTest<RoutesListActivity> {
  @Test
  public void testRecyclerViewUpdatedAfterNewRoute() throws Exception {
    RoutesListActivity activity = Robolectric.setupActivity(RoutesListActivity.class);

    activity._routesRecycler.layout(1,1,1,1);
    RouteDescription routeDescription = mock(RouteDescription.class);
    activity._dataEventBus.post(new TravelDataRepository.NewRouteEvent(routeDescription));

    assertThat(activity._routesRecycler).hasLayoutRequested();
  }
}