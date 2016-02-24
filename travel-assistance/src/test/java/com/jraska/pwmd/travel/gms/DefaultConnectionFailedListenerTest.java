package com.jraska.pwmd.travel.gms;

import android.app.Dialog;
import com.google.android.gms.common.ConnectionResult;
import com.jraska.BaseTest;
import com.jraska.pwmd.travel.TopActivityProvider;
import com.jraska.pwmd.travel.ui.RoutesListActivity;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDialog;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultConnectionFailedListenerTest extends BaseTest {
  @Test
  public void whenConnectionResult_thenDialogShown() {
    RoutesListActivity routesListActivity = Robolectric.setupActivity(RoutesListActivity.class);
    TopActivityProvider providerMock = mock(TopActivityProvider.class);
    DefaultConnectionFailedListener listener = new DefaultConnectionFailedListener(providerMock,
        mock(ConnectionFailedMessageResolver.class));

    listener.showFailedResultOnUIThread(routesListActivity, new ConnectionResult(ConnectionResult.API_UNAVAILABLE));

    Dialog alert = ShadowDialog.getLatestDialog();
    assertThat(alert).isNotNull();
  }
}