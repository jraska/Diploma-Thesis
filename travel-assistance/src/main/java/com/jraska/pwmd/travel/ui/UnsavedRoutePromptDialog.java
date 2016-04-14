package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import com.jraska.pwmd.travel.R;

public final class UnsavedRoutePromptDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = UnsavedRoutePromptDialog.class.getSimpleName();

  //endregion

  //region Dialog impl

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

    AlertDialog alertDialog = builder.setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.record_prompt_title)
        .setMessage(R.string.record_unsaved_prompt_message)
        .setCancelable(true)
        .setNegativeButton(R.string.record_prompt_button_finish, (dialog, which) -> {
          FragmentActivity activity = getActivity();
          if (activity != null && activity instanceof RouteRecordActivity) {
            RouteRecordActivity routeRecordActivity = (RouteRecordActivity) activity;
            routeRecordActivity.stopTracking();
          }
        })
        .setPositiveButton(R.string.record_prompt_button_continue, null)
        .create();


    return alertDialog;
  }


  //endregion

  //region Methods

  public static void show(FragmentActivity activity) {
    UnsavedRoutePromptDialog unsavedRoutePromptDialog = new UnsavedRoutePromptDialog();
    unsavedRoutePromptDialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
  }

  //endregion
}
