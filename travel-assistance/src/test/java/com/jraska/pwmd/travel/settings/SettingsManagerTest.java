package com.jraska.pwmd.travel.settings;

import com.jraska.BaseTest;
import org.junit.Test;
import org.robolectric.fakes.RoboSharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

public class SettingsManagerTest extends BaseTest {

  //region Test Methods

  @Test
  public void testAssistantSetting() throws Exception {

    Map<String, Map<String, Object>> content = new HashMap<>();
    RoboSharedPreferences preferences = new RoboSharedPreferences(content, "name", MODE_PRIVATE);
    SettingsManager settingsManager = new SettingsManager(preferences);

    assertThat(settingsManager.getAssistantEmail(), isEmptyOrNullString());

    String email = "email";
    settingsManager.setAssistantEmail(email);

    assertThat(settingsManager.getAssistantEmail(), equalTo(email));
  }

  //endregion

}