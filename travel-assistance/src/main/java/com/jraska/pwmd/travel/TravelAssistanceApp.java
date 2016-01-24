package com.jraska.pwmd.travel;

import android.content.Context;
import com.jraska.core.BaseApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raizlabs.android.dbflow.config.FlowManager;
import timber.log.Timber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TravelAssistanceApp extends BaseApp {
  //region Constants

  public static final DateFormat USER_DETAILED_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss, dd.MM", Locale.US);
  public static final DateFormat USER_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);

  //endregion

  //region Fields

  private TravelAssistanceComponent _component;

  //endregion

  //region Application overrides

  @Override public void onCreate() {
    super.onCreate();

    Timber.plant(new Timber.DebugTree());
    FlowManager.init(this);

    _component = DaggerTravelAssistanceComponent.builder()
        .travelAssistanceModule(new TravelAssistanceModule(this)).build();

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
    ImageLoader.getInstance().init(config);
  }

  //endregion

  //region Methods

  public static TravelAssistanceComponent getComponent(Context context) {
    return ((TravelAssistanceApp) context.getApplicationContext())._component;
  }

  //endregion
}
