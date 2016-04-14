package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import com.jraska.ActivityBaseTest;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import com.jraska.pwmd.travel.util.TimeProvider;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDialog;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RouteRecordActivityTest extends ActivityBaseTest<RouteRecordActivity> {
  @Test
  public void whenIsRecordingMoreThenMinuteAndBackPressed_thenDialogIsShown() throws Exception {
    RouteRecordActivity activity = Robolectric.setupActivity(RouteRecordActivity.class);
    activity._timeProvider = mock(TimeProvider.class);
    activity._trackingManager = mock(TrackingManager.class);
    when(activity._trackingManager.getRouteData(any())).thenReturn(mock(RouteData.class));
    when(activity._trackingManager.isTracking()).thenReturn(true);
    activity.startTracking();
    when(activity._timeProvider.currentTime()).thenReturn(1000 * 61L);

    activity.onBackPressed();

    assertThat(activity).isNotFinishing();
    Dialog latestDialog = ShadowDialog.getLatestDialog();
    assertThat(latestDialog).isNotNull();
  }

  @Test
  public void whenNotRecordingAndBackPressed_thenFinishes() throws Exception {
    RouteRecordActivity activity = Robolectric.setupActivity(RouteRecordActivity.class);

    activity.onBackPressed();

    assertThat(activity).isFinishing();
  }
}