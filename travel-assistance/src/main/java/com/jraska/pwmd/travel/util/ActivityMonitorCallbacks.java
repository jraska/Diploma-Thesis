package com.jraska.pwmd.travel.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.jraska.pwmd.travel.ui.BaseActivity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ActivityMonitorCallbacks implements Application.ActivityLifecycleCallbacks {
  //region Fields

  private final Set<Activity> _activities = new HashSet<>();
  private BaseActivity _topActivity;

  //endregion

  //region Properties

  public Iterable<Activity> getActivities() {
    return Collections.unmodifiableSet(_activities);
  }

  @Nullable
  public BaseActivity getTopActivity() {
    return _topActivity;
  }

  //endregion

  //region ActivityLifecycleCallbacks impl

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    _activities.add(activity);
    _topActivity = (BaseActivity) activity;
  }

  @Override public void onActivityStarted(Activity activity) {
    _topActivity = (BaseActivity) activity;
  }

  @Override public void onActivityResumed(Activity activity) {
    _topActivity = (BaseActivity) activity;
  }

  @Override public void onActivityPaused(Activity activity) {
  }

  @Override public void onActivityStopped(Activity activity) {
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override public void onActivityDestroyed(Activity activity) {
    _activities.remove(activity);
    if (activity == _topActivity) {
      _topActivity = null;
    }
  }


  //endregion
}
