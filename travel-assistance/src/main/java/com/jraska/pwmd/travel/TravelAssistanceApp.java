package com.jraska.pwmd.travel;

import android.content.Context;
import com.jraska.core.BaseApp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TravelAssistanceApp extends BaseApp {
  //region Constants

  public static String DB_NAME = "TravelAssistanceData";
  public static final DateFormat USER_DETAILED_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss, MM.dd.yyyy", Locale.US);

  //endregion

  //region Fields

  private TravelAssistanceComponent _component;

  //endregion

  //region TravelAssistanceApplication overrides

  @Override public void onCreate() {
    super.onCreate();

    _component = DaggerTravelAssistanceComponent.builder()
        .travelAssistanceModule(new TravelAssistanceModule(this)).build();
  }

  //endregion

  //region Methods

  public static TravelAssistanceComponent getComponent(Context context) {
    return ((TravelAssistanceApp) context.getApplicationContext())._component;
  }

  //endregion

  //region Nested classes


  //endregion
}
