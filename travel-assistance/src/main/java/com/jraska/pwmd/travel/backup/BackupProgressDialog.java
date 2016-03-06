package com.jraska.pwmd.travel.backup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class BackupProgressDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = BackupProgressDialog.class.getSimpleName();

  private static final String EXTRA_TITLE = "title";
  private static final String EXTRA_MESSAGE = "message";

  //endregion

  //region Constructors

  public BackupProgressDialog() {
  }

  public static BackupProgressDialog newInstance(String title, String message) {
    Bundle args = new Bundle();
    args.putString(EXTRA_TITLE, title);
    args.putString(EXTRA_MESSAGE, message);

    BackupProgressDialog backupProgressDialog = new BackupProgressDialog();
    backupProgressDialog.setArguments(args);
    return backupProgressDialog;
  }

  //endregion

  //region Properties

  String getTitle() {
    return getArguments().getString(EXTRA_TITLE);
  }

  String getMessage() {
    return getArguments().getString(EXTRA_MESSAGE);
  }

  //endregion

  //region DialogFragment impl

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    ProgressDialog progressDialog = new ProgressDialog(getContext());
    progressDialog.setTitle(getTitle());
    progressDialog.setMessage(getMessage());
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);

    return progressDialog;
  }


  //endregion
}
