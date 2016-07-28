package com.jraska.pwmd.travel.gms;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.TopActivityProvider;
import com.jraska.pwmd.travel.dialog.LambdaDialogFragment;
import com.jraska.pwmd.travel.ui.BaseActivity;
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
    BaseActivity topActivity = _topActivityProvider.get();
    if (topActivity != null) {
      ShowFailedConnectionDialogRunnable showDialogRunnable =
          new ShowFailedConnectionDialogRunnable(topActivity, connectionResult, this);
      topActivity.runOnUiThread(showDialogRunnable);
    } else {
      Timber.i("No activity found to show failed error, silently just log the error");
    }
  }

  protected void showFailedResultOnUIThread(BaseActivity activity, ConnectionResult connectionResult) {
    if (activity.isFinishing()) {
      Timber.w("Could not show error to user, %s is finishing.", activity.getClass().getSimpleName());
      return;
    }

    if (activity.isChangingConfigurations()) {
      Timber.e("Could not show error to user, %s is changing configurations, we finish it.",
          activity.getClass().getSimpleName());
      return;
    }

    LambdaDialogFragment.builder(activity)
        .validateEagerly(BuildConfig.DEBUG)
        .setTitle(_messageResolver.resolveTitle(connectionResult))
        .setMessage(_messageResolver.resolveMessage(connectionResult))
        .setCancelable(false)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveText(android.R.string.ok)
        .setPositiveMethod(Activity::finish)
        .show(activity.getSupportFragmentManager());
  }

  //endregion

  //region Nested classes

  static class ShowFailedConnectionDialogRunnable implements Runnable {
    private final BaseActivity _activity;
    private final ConnectionResult _connectionResult;
    private final DefaultConnectionFailedListener _listener;

    public ShowFailedConnectionDialogRunnable(BaseActivity activity, ConnectionResult connectionResult,
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
