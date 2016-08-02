package com.jraska.dialog.lambda;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import java.io.Serializable;

interface DialogDelegateProvider<T extends FragmentActivity> extends Serializable {
  DialogInterface.OnClickListener delegate(T activity);
}
