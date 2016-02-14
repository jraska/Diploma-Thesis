package com.jraska.pwmd.travel.util;

import android.util.Log;
import timber.log.Timber;

public class TravelReleaseTree extends Timber.DebugTree {
  //region DebugTree overrides

  @Override protected boolean isLoggable(int priority) {
    return priority >= Log.WARN;
  }

  //endregion
}
