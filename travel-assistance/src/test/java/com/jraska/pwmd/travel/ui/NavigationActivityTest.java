package com.jraska.pwmd.travel.ui;

import android.view.View;
import com.jraska.ActivityBaseTest;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.navigation.Navigator;
import com.jraska.pwmd.travel.persistence.DBFlowDataRepositoryTest;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class NavigationActivityTest extends ActivityBaseTest<NavigationActivity> {

  //region Test Methods

  @Test
  public void testDirectionChangedOnChangedEvent() throws Exception {

    RouteData routeData = DBFlowDataRepositoryTest.createRouteData();

    TravelDataRepository mockDataRepository = mock(TravelDataRepository.class);
    doReturn(routeData).when(mockDataRepository).select(any(long.class));

    ActivityController<NavigationActivity> activityController =
        Robolectric.buildActivity(NavigationActivity.class);

    activityController.create();
    activityController.get()._travelDataRepository = mockDataRepository;
    NavigationActivity navigationActivity = activityController.start().resume().get();

    View arrowMock = mock(View.class);
    navigationActivity._desiredDirectionView = arrowMock;

    navigationActivity._navigator.getEventBus().post(new Navigator.RequiredDirectionEvent(1));

    verify(navigationActivity._desiredDirectionView, times(1)).setRotation(any(float.class));
  }

  //endregion
}