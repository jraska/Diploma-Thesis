package com.jraska.pwmd.travel.help;

import android.location.Location;
import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.LatLng;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LostMessageTextBuilderTest extends BaseTest {
  //region Constants

  public static final Location TEST_LOCATION = new LatLng(19.22216271, 49.190901121).toLocation();

  //endregion


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
    _builder.setFromLocation(TEST_LOCATION);
    _builder.setZoom(12);

    String googleMapsUrl = _builder.buildGoogleMapsUrl();
    assertThat(googleMapsUrl).endsWith("?&z=12&q=19.222163+49.190901");
  }

  @Test
  public void testBuildSmsTextLength() throws Exception {
    Location location = TEST_LOCATION;

    _builder.setFromLocation(location);

    String buildSmsText = _builder.buildSmsText();
    assertThat(buildSmsText.length()).isLessThanOrEqualTo(SmsSender.MAX_SMS_LENGTH);
  }

  //endregion
}