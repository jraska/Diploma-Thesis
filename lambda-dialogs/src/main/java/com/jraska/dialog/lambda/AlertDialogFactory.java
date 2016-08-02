package com.jraska.dialog.lambda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

public final class AlertDialogFactory implements DialogFactory {
  @Override
  public Dialog onCreateDialog(FactoryData factoryData) {
    FragmentActivity activity = factoryData._activity;
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    DialogFields fields = factoryData._fields;

    return builder.setTitle(fields.title)
        .setMessage(fields.message)
        .setIcon(fields.iconRes)
        .setPositiveButton(fields.positiveText, delegate(fields.positiveProvider, activity))
        .setNeutralButton(fields.neutralText, delegate(fields.neutralProvider, activity))
        .setNegativeButton(fields.negativeText, delegate(fields.negativeProvider, activity))
        .setCancelable(fields.cancelable)
        .create();
  }

  @SuppressWarnings("unchecked")
  DialogInterface.OnClickListener delegate(DialogDelegateProvider provider, FragmentActivity activity) {
    if (provider == null) {
      return null;
    } else {
      return provider.delegate(activity);
    }
  }
}
