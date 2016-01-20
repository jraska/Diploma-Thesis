package com.jraska.pwmd.travel.ui;

import android.view.View;
import com.jraska.ActivityBaseTest;
import com.jraska.pwmd.travel.navigation.Navigator;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class NavigationActivityTest extends ActivityBaseTest<NavigationActivity> {
  //region Test Methods

  @Test
  public void testDirectionChangedOnChangedEvent() throws Exception {
    NavigationActivity navigationActivity = Robolectric.setupActivity(NavigationActivity.class);
    View arrowMock = mock(View.class);
    navigationActivity._arrowView = arrowMock;

    navigationActivity._navigator.getEventBus().post(new Navigator.RequiredDirectionEvent(1));

    verify(navigationActivity._arrowView, times(1)).setRotation(any(float.class));
  }

  //endregion
}