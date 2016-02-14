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
  private static final String ASSIST_PHONE = "wn32k11l1k2j";

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
    setPreferenceValue(ASSIST_EMAIL, email);
  }

  /**
   * @return Assistant phone if available, null otherwise
   */
  @Nullable
  public String getAssistantPhone() {
    return _preferences.getString(ASSIST_PHONE, null);
  }

  public void setAssistantPhone(@Nullable String assistantPhone) {
    setPreferenceValue(ASSIST_PHONE, assistantPhone);
  }

  //endregion

  //region Methods

  private void setPreferenceValue(String key, String value) {
    _preferences.edit().putString(key, value).apply();
  }

  //endregion
}
