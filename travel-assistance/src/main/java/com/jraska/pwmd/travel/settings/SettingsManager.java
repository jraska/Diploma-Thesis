package com.jraska.pwmd.travel.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Provides fast access to all available preferences of application
 */
public class SettingsManager {
  //region Constants

  private static final String ASSIST_EMAIL = "237a802asd";
  private static final String ASSIST_PHONE = "wn32k11l1k2j";
  private static final String LAST_BACKUP = "8236ba5bo";

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

  @Nullable
  public Date getLastBackupTime() {
    long time = _preferences.getLong(LAST_BACKUP, -1);
    if (time == -1) {
      return null;
    }

    return new Date(time);
  }

  public void setLastBackupTime(Date lastBackupTime) {
    _preferences.edit().putLong(LAST_BACKUP, lastBackupTime.getTime()).apply();
  }

  //endregion

  //region Methods

  private void setPreferenceValue(String key, String value) {
    _preferences.edit().putString(key, value).apply();
  }

  //endregion
}
