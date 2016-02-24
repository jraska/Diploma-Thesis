package com.jraska.pwmd.travel.gms;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.TopActivityProvider;
import timber.log.Timber;

import javax.inject.Inject;

public class DefaultConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
  //region Fields

  private final TopActivityProvider _topActivityProvider;
  private final ConnectionFailedMessageResolver _messageResolver;

  //endregion

  //region Constructors

  @Inject
  public DefaultConnectionFailedListener(TopActivityProvider topActivityProvider,
                                         ConnectionFailedMessageResolver messageResolver) {
    ArgumentCheck.notNull(topActivityProvider);
    ArgumentCheck.notNull(messageResolver);

    _topActivityProvider = topActivityProvider;
    _messageResolver = messageResolver;
  }

  //endregion

  //region OnConnectionFailedListener impl

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    onConnectionFailedInRandomThread(connectionResult);
  }

  //endregion

  //region Methods

  protected void onConnectionFailedInRandomThread(@NonNull ConnectionResult connectionResult) {
    Timber.e("Connection to services failed result= %s", connectionResult);
    Activity topActivity = _topActivityProvider.get();
    if (topActivity != null) {
      ShowFailedConnectionDialogRunnable showDialogRunnable =
          new ShowFailedConnectionDialogRunnable(topActivity, connectionResult, this);
      topActivity.runOnUiThread(showDialogRunnable);
    } else {
      Timber.i("No activity found to show failed error, silently just log the error");
    }
  }

  protected void showFailedResultOnUIThread(Activity activity, ConnectionResult connectionResult) {
    if (activity.isFinishing()) {
      Timber.w("Could not show error to user, %s is finishing.", activity.getClass().getSimpleName());
      return;
    }

    if (activity.isChangingConfigurations()) {
      Timber.e("Could not show error to user, %s is changing configurations, we finish it.",
          activity.getClass().getSimpleName());
      return;
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(_messageResolver.resolveTitle(connectionResult))
        .setMessage(_messageResolver.resolveMessage(connectionResult))
        .setCancelable(false)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          activity.finish();
        });

    builder.show();
  }

  //endregion

  //region Nested classes

  static class ShowFailedConnectionDialogRunnable implements Runnable {
    private final Activity _activity;
    private final ConnectionResult _connectionResult;
    private final DefaultConnectionFailedListener _listener;

    public ShowFailedConnectionDialogRunnable(Activity activity, ConnectionResult connectionResult,
                                              DefaultConnectionFailedListener listener) {
      _activity = activity;
      _connectionResult = connectionResult;
      _listener = listener;
    }

    @Override public void run() {
      _listener.showFailedResultOnUIThread(_activity, _connectionResult);
    }
  }

  //endregion
}
