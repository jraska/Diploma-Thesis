package com.jraska.pwmd.travel.dialog;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import java.io.Serializable;

public interface DialogDelegateProvider<T extends FragmentActivity> extends Serializable {
  DialogInterface.OnClickListener delegate(T activity);
}
