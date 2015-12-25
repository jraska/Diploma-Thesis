package com.jraska.pwmd.travel.help;

import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.Position;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LostMessageTextBuilderTest extends BaseTest {

  //region Fields

  private LostMessageTextBuilder _builder;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    _builder = new LostMessageTextBuilder(getApplication());
  }

  //endregion

  //region Test Methods

  @Test
  public void testBuildGoogleMapsUrl() throws Exception {
    long timeMillis = System.currentTimeMillis();
    Position position = new Position(19.22216271, 49.190901121, timeMillis, 1.1f, "unknown");

    _builder.setFromPosition(position);
    _builder.setZoom(12);

    String googleMapsUrl = _builder.buildGoogleMapsUrl();
    assertThat(googleMapsUrl, endsWith("?&z=12&q=19.222163+49.190901"));
  }

  @Test
  public void testBuildSmsTextLength() throws Exception {
    long timeMillis = System.currentTimeMillis();
    Position position = new Position(19.22216271, 49.190901121, timeMillis, 1.1f, "unknown");

    _builder.setFromPosition(position);

    String buildSmsText = _builder.buildSmsText();
    assertTrue(buildSmsText.length() <= SmsSender.MAX_SMS_LENGTH); //TODO: better assertions
  }

  //endregion
}