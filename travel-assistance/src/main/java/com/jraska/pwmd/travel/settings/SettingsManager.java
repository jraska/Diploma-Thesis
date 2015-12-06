package com.jraska.pwmd.travel.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provides fast access to all available preferences of application
 */
public class SettingsManager {
  //region Constants

  private static final String ASSIST_EMAIL = "237a802asd";

  //endregion

  //region Fields

  private final SharedPreferences _preferences;

  //endregion

  //region Constructors

  public SettingsManager(@NonNull SharedPreferences preferences) {
    _preferences = preferences;
  }

  //endregion

  //region Properties

  /**
   * @return Assistant email if exists, null otherwise
   */
  @Nullable
  public String getAssistantEmail() {
    return _preferences.getString(ASSIST_EMAIL, null);
  }

  public void setAssistantEmail(@Nullable String email) {
    _preferences.edit().putString(ASSIST_EMAIL, email).apply();
  }

  //endregion
}
