package com.jraska.pwmd.travel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import com.jraska.console.timber.ConsoleTree;
import com.jraska.pwmd.travel.util.ActivityMonitorCallbacks;
import com.jraska.pwmd.travel.util.TravelDebugTree;
import com.jraska.pwmd.travel.util.TravelReleaseTree;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raizlabs.android.dbflow.config.FlowManager;
import timber.log.Timber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TravelAssistanceApp extends Application {
  //region Constants

  public static final DateFormat USER_DETAILED_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss, dd.MM", Locale.US);
  public static final DateFormat USER_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);

  //endregion

  //region Fields

  private AppComponent _component;
  private ActivityMonitorCallbacks _monitorCallbacks;

  //endregion

  //region Properties

  public Iterable<Activity> getRunningActivities() {
    return _monitorCallbacks.getActivities();
  }

  @Nullable
  public Activity getTopActivity() {
    return _monitorCallbacks.getTopActivity();
  }

  //endregion

  //region Application overrides

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new TravelDebugTree());
      Timber.plant(new ConsoleTree());
    } else {
      Timber.plant(new TravelReleaseTree());
    }

    FlowManager.init(this);

    _monitorCallbacks = new ActivityMonitorCallbacks();
    registerActivityLifecycleCallbacks(_monitorCallbacks);

    _component = DaggerAppComponent.builder()
        .appModule(new AppModule(this)).build();

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
    ImageLoader.getInstance().init(config);
  }

  //endregion

  //region Methods

  public static AppComponent getComponent(Context context) {
    return ((TravelAssistanceApp) context.getApplicationContext())._component;
  }

  //endregion
}
