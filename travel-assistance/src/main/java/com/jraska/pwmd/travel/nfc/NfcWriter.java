package com.jraska.pwmd.travel.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;

@PerApp
public class NfcWriter {
  //region Constants

  public static final int NDEF_SUCCESS = 0;
  public static final int NDEF_NOT_WRITABLE = 1;
  public static final int NDEF_TOO_SMALL = 2;
  public static final int NDEF_FAILED = 3;
  public static final int NDEF_CANNOT_WRITE_TECH = 4;

  public static final int NO_ROUTE_ID = -1;

  public static final IntentFilter[] WRITE_TAG_FILTERS = {
      new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
      new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
      new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
  };

  //endregion

  //region Fields

  private final Context _context;
  private final EventBus _eventBus;
  private final NfcAdapter _nfcAdapter;

  private long _routeId = NO_ROUTE_ID;

  //endregion

  //region Constructors

  @Inject
  public NfcWriter(Context context, EventBus eventBus) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(eventBus);

    _context = context.getApplicationContext();
    _eventBus = eventBus;
    _nfcAdapter = NfcAdapter.getDefaultAdapter(_context);
  }

  //endregion

  //region Properties

  public boolean isPendingWrite() {
    return _routeId != NO_ROUTE_ID;
  }

  //endregion

  //region Methods

  public void requestTagWrite(@NonNull Activity activity, long routeId) {
    ArgumentCheck.notNull(activity);

    _routeId = routeId;

    PendingIntent pendingIntent = pendingWriteIntentFor(activity);
    _nfcAdapter.enableForegroundDispatch(activity, pendingIntent, WRITE_TAG_FILTERS, null);
  }

  private PendingIntent pendingWriteIntentFor(Activity activity) {
    Intent intent = new Intent(activity, activity.getClass());
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return PendingIntent.getActivity(activity, 0, intent, 0);
  }

  public void cancelPendingTagWrites(Activity activity) {
    _nfcAdapter.disableForegroundDispatch(activity);
  }

  public void onNewIntent(Intent intent) {
    if (!isPendingWrite()) {
      return;
    }

    String action = intent.getAction();
    if (TextUtils.isEmpty(action)) {
      return;
    }

    switch (action) {
      case NfcAdapter.ACTION_NDEF_DISCOVERED:
      case NfcAdapter.ACTION_TAG_DISCOVERED:
      case NfcAdapter.ACTION_TECH_DISCOVERED:
        onDetectedNfcIntent(intent);
        break;

      default:
        Timber.w("Unexpected action: %s", action);
        break;
    }
  }

  public void removePendingWrites() {
    // We cannot do this when disabling foreground dispatch,
    // because we need to wait for the new Intent
    _routeId = NO_ROUTE_ID;
  }

  protected void onDetectedNfcIntent(Intent intent) {
    write(createRouteNdefMessage(), intent);
  }

  private NdefMessage createRouteNdefMessage() {
    throw new UnsupportedOperationException(); // TODO: 13/02/16
  }

  protected int write(NdefMessage rawMessage, Intent tagReadyIntent) {
    Tag tag = tagReadyIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

    NdefFormatable format = NdefFormatable.get(tag);
    if (format != null) {
      return writeFormatMessage(rawMessage, format);
    } else {
      Ndef ndef = Ndef.get(tag);
      if (ndef != null) {
        return writeNdefMessage(rawMessage, ndef);
      } else {
        return NDEF_CANNOT_WRITE_TECH;
      }
    }
  }

  private int writeNdefMessage(NdefMessage rawMessage, Ndef ndef) {
    try {
      ndef.connect();
      if (!ndef.isWritable()) {
        return NDEF_NOT_WRITABLE;
      }

      if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
        Timber.e("Tag size is too small, have %d, need %d.", ndef.getMaxSize(),
            rawMessage.toByteArray().length);

        return NDEF_TOO_SMALL;
      }
      ndef.writeNdefMessage(rawMessage);

      return NDEF_SUCCESS;
    }
    catch (Exception e) {
      Timber.e(e, "Error writing Ndef");
      return NDEF_FAILED;
    }
    finally {

      try {
        ndef.close();
      }
      catch (IOException e) {
        Timber.e(e, "Error clossing ndef");
      }
    }
  }

  private int writeFormatMessage(NdefMessage rawMessage, NdefFormatable format) {
    try {
      format.connect();
      format.format(rawMessage);

      return NDEF_SUCCESS;
    }
    catch (Exception e) {
      Timber.e(e, "Error writing format tag.");
      return NDEF_FAILED;
    }
    finally {

      try {
        format.close();
      }
      catch (IOException e) {
        Timber.e(e, "Error clossing formatable tag");
      }
    }
  }

  protected void onTagWriteResult(TagWriteResultEvent event) {
    _eventBus.post(event);
  }

  //endregion

  //region Nested classes

  public static class TagWriteResultEvent {
    public final long _routeId;
    public final int _result;

    public TagWriteResultEvent(long routeId, int result) {
      _routeId = routeId;
      _result = result;
    }

    public boolean isSuccess() {
      return _result == NDEF_SUCCESS;
    }
  }

  //endregion
}
