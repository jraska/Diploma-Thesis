package com.jraska.pwmd.travel.nfc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import timber.log.Timber;

import javax.inject.Inject;

@PerApp
public class NfcRouteEncoder {
  //region Constants

  public static final String DEEP_LINK_SCHEME = "http";
  public static final String DEEP_LINK_AUTHORITY = "pwmd.travel";
  public static final String PATH_ROUTE = "navigate";
  public static final String PARAM_ROUTE_ID = "id";

  //endregion

  //region Fields

  private final Context _context;

  //endregion

  @Inject
  public NfcRouteEncoder(Context context) {
    ArgumentCheck.notNull(context);

    _context = context.getApplicationContext();
  }

  //region Methods

  public NdefMessage encodeRouteNavigation(long routeId) {
    Uri routeUri = createRouteUri(routeId);
    Timber.d("Encoded uri=%s for routeId=%d", routeUri, routeId);

    NdefRecord deepLinkRecord = NdefRecord.createUri(routeUri);
    NdefRecord applicationRecord = NdefRecord.createApplicationRecord(_context.getPackageName());

    return new NdefMessage(deepLinkRecord, applicationRecord);
  }

  public long extractNavigationRouteId(@NonNull Intent intent) {
    ArgumentCheck.notNull(intent);

    Uri routeUri = extractUri(intent);
    Timber.d("Extracted NFC uri: %s", routeUri);

    return extractNavigationRouteId(routeUri);
  }

  protected Uri createRouteUri(long routeId) {
    Uri.Builder uriBuilder = new Uri.Builder();
    uriBuilder.scheme(DEEP_LINK_SCHEME)
        .authority(DEEP_LINK_AUTHORITY)
        .appendPath(PATH_ROUTE)
        .appendQueryParameter(PARAM_ROUTE_ID, String.valueOf(routeId));

    return uriBuilder.build();
  }

  protected long extractNavigationRouteId(Uri routeUri) {
    if (routeUri == null) {
      return 0;
    }

    String routeIdParameter = routeUri.getQueryParameter(PARAM_ROUTE_ID);
    try {
      return Long.valueOf(routeIdParameter);
    }
    catch (NumberFormatException ex) {
      Timber.e(ex, "Error when trying to parse routeId from routeUri: %s", routeUri);
      return 0;
    }
  }

  @Nullable
  protected Uri extractUri(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      return data;
    }

    Parcelable[] messages = intent.getParcelableArrayExtra(
        NfcAdapter.EXTRA_NDEF_MESSAGES);

    if (messages != null && messages.length > 0 && messages[0] instanceof NdefMessage) {
      return extractUri((NdefMessage) messages[0]);
    }

    return null;
  }

  @Nullable
  protected Uri extractUri(NdefMessage message) {
    for (NdefRecord record : message.getRecords()) {
      try {
        return record.toUri();
      }
      catch (Exception ex) {
        Timber.e(ex, "Error when transforming to Uri %s", record);
      }
    }

    return null;
  }

  //endregion
}
