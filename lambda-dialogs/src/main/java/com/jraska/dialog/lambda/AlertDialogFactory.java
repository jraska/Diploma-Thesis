package com.jraska.dialog.lambda;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

public final class AlertDialogFactory implements DialogFactory {
  @Override
  public AlertDialog onCreateDialog(FragmentActivity activity, FactoryData factoryData) {
    DialogFields fields = factoryData._fields;

    return new AlertDialog.Builder(activity)
        .setTitle(fields.title)
        .setMessage(fields.message)
        .setIcon(fields.iconRes)
        .setPositiveButton(fields.positiveText, call(fields.positiveAction, activity))
        .setNeutralButton(fields.neutralText, call(fields.neutralAction, activity))
        .setNegativeButton(fields.negativeText, call(fields.negativeAction, activity))
        .setCancelable(fields.cancelable)
        .create();
  }

  @SuppressWarnings("unchecked")
  DialogInterface.OnClickListener call(ActivityAction action, FragmentActivity activity) {
    if (action == null) {
      return null;
    } else {
      return (dialogInterface, i) -> action.call(activity);
    }
  }
}
