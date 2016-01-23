package com.jraska;

import android.app.Application;
import com.jraska.pwmd.travel.BuildConfig;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import timber.log.Timber;

/**
 * All unit tests should inherit from this class to enable Robolectric runners
 * automatically for all of them.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = {UnitTestTree.class})
public abstract class BaseTest {
  //region Constructors

  public BaseTest() {
    Timber.plant(new UnitTestTree());
  }

  //endregion

  //region Properties

  protected Application getApplication() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
