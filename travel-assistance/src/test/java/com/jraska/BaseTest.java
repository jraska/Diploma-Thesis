package com.jraska;

import android.app.Application;
import com.jraska.pwmd.travel.BuildConfig;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public abstract class BaseTest {
  //region Properties

  protected Application getApplication() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
