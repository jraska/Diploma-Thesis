package com.jraska.pwmd.travel.util;

import timber.log.Timber;

public class TravelDebugTree extends Timber.DebugTree {
  //region DebugTree overrides

  @Override
  protected String createStackElementTag(StackTraceElement element) {
    // Add custom chars to easy regex in Android Monitor
    return "##" + super.createStackElementTag(element);
  }


  //endregion
}
