package com.jraska.pwmd.travel;

import android.app.Activity;
import com.jraska.common.ArgumentCheck;

import javax.inject.Inject;
import javax.inject.Provider;

public class TopActivityProvider implements Provider<Activity> {
  //region Fields

  private final TravelAssistanceApp _app;

  //endregion

  //region Constructors

  @Inject
  public TopActivityProvider(TravelAssistanceApp app) {
    ArgumentCheck.notNull(app);

    _app = app;
  }

  //endregion

  //region Provider impl

  @Override public Activity get() {
    return _app.getTopActivity();
  }

  //endregion
}
