package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import com.jraska.ActivityBaseTest;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDialog;

import static org.assertj.android.api.Assertions.assertThat;

public class RouteRecordActivityTest extends ActivityBaseTest<RouteRecordActivity> {
  @Test
  public void whenIsRecordingAndBackPressed_thenDialogIsShown() throws Exception {
    RouteRecordActivity activity = Robolectric.setupActivity(RouteRecordActivity.class);
    activity.startTracking();

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