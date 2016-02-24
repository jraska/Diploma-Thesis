package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.jraska.pwmd.travel.R;

public final class RecordingPromptDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = "RecordingPromptDialog";

  //endregion

  //region Dialog impl

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

    // TODO: 24/02/16 Better dialog icons
    AlertDialog alertDialog = builder.setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.record_prompt_title)
        .setMessage(R.string.record_prompt_message)
        .setCancelable(true)
        .setNegativeButton(R.string.record_prompt_button_finish, (dialog, which) -> {
          if (getActivity() != null) {
            getActivity().finish();
          }
        })
        .setPositiveButton(R.string.record_prompt_button_continue, null)
        .create();


    return alertDialog;
  }


  //endregion
}
