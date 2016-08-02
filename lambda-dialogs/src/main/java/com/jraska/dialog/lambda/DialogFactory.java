package com.jraska.dialog.lambda;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import java.io.Serializable;

public interface DialogFactory extends Serializable {
  Dialog onCreateDialog(FactoryData factoryData);

  class FactoryData {
    public final FragmentActivity _activity;
    public final DialogFields _fields;

    public FactoryData(@NonNull FragmentActivity activity, @NonNull DialogFields fields) {
      _activity = activity;
      this._fields = fields;
    }
  }
}
