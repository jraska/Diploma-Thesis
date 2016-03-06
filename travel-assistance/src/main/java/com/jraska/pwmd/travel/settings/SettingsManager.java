package com.jraska.pwmd.travel.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.dagger.PerApp;

import javax.inject.Inject;
import java.util.Date;

/**
 * Provides fast access to all available preferences of application
 */
@PerApp
public class SettingsManager {
  //region Constants

  private static final String ASSIST_EMAIL = "237a802asd";
  private static final String ASSIST_PHONE = "wn32k11l1k2j";
  private static final String LAST_BACKUP = "8236ba5bo";
  private static final String LAST_DRIVE_SYNC_REQUEST = "566372bi72";

  //endregion

  //region Fields

  private final SharedPreferences _preferences;

  //endregion

  //region Constructors

  @Inject
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
    return getPreferenceDateValue(LAST_BACKUP);
  }

  public void setLastBackupTime(Date lastBackupTime) {
    setPreferenceValue(LAST_BACKUP, lastBackupTime);
  }

  public Date getLastDriveRequestSync() {
    return getPreferenceDateValue(LAST_DRIVE_SYNC_REQUEST);
  }

  public void setLastDriveSyncRequest(@NonNull Date time) {
    setPreferenceValue(LAST_DRIVE_SYNC_REQUEST, time);
  }

  //endregion

  //region Methods

  private void setPreferenceValue(String key, String value) {
    _preferences.edit().putString(key, value).apply();
  }

  @Nullable
  private Date getPreferenceDateValue(String key) {
    long time = _preferences.getLong(key, -1);
    if (time == -1) {
      return null;
    }

    return new Date(time);
  }


  private void setPreferenceValue(String key, Date date) {
    _preferences.edit().putLong(key, date.getTime()).apply();
  }

  //endregion
}
