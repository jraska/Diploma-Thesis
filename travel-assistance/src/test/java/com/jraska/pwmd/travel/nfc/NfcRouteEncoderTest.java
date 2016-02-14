package com.jraska.pwmd.travel.nfc;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import com.jraska.BaseTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NfcRouteEncoderTest extends BaseTest {
  //region Constants

  public static final long[] TEST_IDS = {156120, 42, 1281, 972};

  //endregion

  //region Fields

  private NfcRouteEncoder _encoder;

  //endregion

  //region SetupMethods

  @Before
  public void setUp() {
    _encoder = new NfcRouteEncoder(getApplication());
  }


  //endregion

  //region Test Methods

  @Test
  public void testEncodedUriEqualsDecoded() throws Exception {
    NfcRouteEncoder encoder = _encoder;

    for (long id : TEST_IDS) {
      Uri routeUri = encoder.createRouteUri(id);
      long extractedId = encoder.extractNavigationRouteId(routeUri);

      assertThat(extractedId).isEqualTo(id);
    }
  }

  @Test
  public void testEncodedNdefRecordParsedToSameDecoded() throws Exception {
    NfcRouteEncoder encoder = _encoder;

    for (long id : TEST_IDS) {
      NdefMessage ndefMessage = encoder.encodeRouteNavigation(id);
      Uri routeUri = encoder.extractUri(ndefMessage);
      long extractedId = encoder.extractNavigationRouteId(routeUri);

      assertThat(extractedId).isEqualTo(id);
    }
  }

  @Test
  public void testUriFoundInIntentData() throws Exception {
    Uri expectedUri = _encoder.createRouteUri(TEST_IDS[0]);
    Intent intent = new Intent();
    intent.setData(expectedUri);

    Uri extractedUri = _encoder.extractUri(intent);

    assertThat(extractedUri).isEqualTo(expectedUri);
  }

  @Test
  public void testUriFoundWithEmptyData() throws Exception {
    long testId = TEST_IDS[0];
    Uri expectedUri = _encoder.createRouteUri(testId);
    Parcelable[] messages = {_encoder.encodeRouteNavigation(testId)};
    Intent intent = new Intent();
    intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, messages);

    Uri extractedUri = _encoder.extractUri(intent);

    assertThat(extractedUri).isEqualTo(expectedUri);
  }

  //endregion
}