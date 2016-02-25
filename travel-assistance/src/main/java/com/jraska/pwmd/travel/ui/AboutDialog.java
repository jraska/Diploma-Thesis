package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.R;
import timber.log.Timber;

public class AboutDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = AboutDialog.class.getSimpleName();

  //endregion

  //region DialogFragment overrides

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Context context = getContext();
    AlertDialog.Builder builder = new AlertDialog.Builder(context);

    AlertDialog alertDialog = builder.setIcon(R.drawable.ic_logo)
        .setTitle(getAppInfoTitle(getContext()))
        .setMessage(R.string.about)
        .setCancelable(true)
        .setPositiveButton(android.R.string.ok, null)
        .setNeutralButton(R.string.about_feedback, (dialog, which) -> startFeedbackActivity())
        .create();

    return alertDialog;
  }

  private void startFeedbackActivity() {
    if (getActivity() instanceof FeedbackRequestCallback) {
      ((FeedbackRequestCallback) getActivity()).onFeedbackRequested();
    } else {
      Timber.w("Dialog shown in wrong activity.");
    }
  }

  //endregion

  //region Methods

  public static String getAppInfoTitle(Context context) {
    return context.getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME;
  }

  //endregion

  //region Nested classes

  interface FeedbackRequestCallback {
    void onFeedbackRequested();
  }

  //endregion
}
