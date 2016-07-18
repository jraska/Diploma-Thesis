package com.jraska.pwmd.travel.dialog;

import android.support.v4.app.FragmentActivity;

import java.io.Serializable;

public interface ActivityAction1<T extends FragmentActivity> extends Serializable {
  void call(T activity);
}
