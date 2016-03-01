package com.jraska;

import android.app.Application;
import com.jraska.pwmd.travel.BuildConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import org.junit.After;
import org.junit.Before;
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
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.jraska.pwmd.travel")
public abstract class BaseTest {
  //region Setup Methods

  @Before
  public void plantTimber() {
    Timber.uprootAll();
    Timber.plant(new UnitTestTree());
  }

  @After
  public void cleanDbFlow(){
    FlowManager.destroy();
  }

  @After
  public void uprootTimber() {
    Timber.uprootAll();
  }

  //endregion

  //region Properties

  protected Application getApplication() {
    return RuntimeEnvironment.application;
  }

  //endregion
}
