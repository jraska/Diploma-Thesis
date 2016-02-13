package com.jraska.pwmd.travel.nfc;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;

@PerApp
public class NfcStatusChecker {
  //region Constants

  public static final String ACTION_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";
  public static final String EXTRA_ADAPTER_STATE = "android.nfc.extra.ADAPTER_STATE";

  // Copied status flags, because there are part of public api only since API 18
  public static final int STATE_OFF = 1;
  //  public static final int STATE_TURNING_ON = 2;
  public static final int STATE_ON = 3;
//  public static final int STATE_TURNING_OFF = 4;

  //endregion

  //region Fields

  private final Context _context;
  private final EventBus _eventBus;

  @Nullable
  private final NfcAdapter _nfcAdapter;

  private final BroadcastReceiver _nfcStateChangeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final int state = intent.getIntExtra(EXTRA_ADAPTER_STATE, -1);
      if (state == STATE_OFF || state == STATE_ON) {
        onNfcStatusChanged();
      }
    }
  };

  //endregion

  //region Constructor

  @Inject
  public NfcStatusChecker(Context context, EventBus eventBus) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(eventBus);

    _context = context.getApplicationContext();
    _eventBus = eventBus;
    _nfcAdapter = NfcAdapter.getDefaultAdapter(_context);

    IntentFilter stateChangedFilter = new IntentFilter(ACTION_ADAPTER_STATE_CHANGED);
    _context.registerReceiver(_nfcStateChangeReceiver, stateChangedFilter);
  }

  //endregion

  //region Methods

  public boolean hasDeviceNfcFeature() {
    return _context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)
        && _nfcAdapter != null;
  }

  public boolean isNfcOn() {
    return _nfcAdapter != null && _nfcAdapter.isEnabled();
  }

  public boolean isNfcOff() {
    return !isNfcOn();
  }

  /**
   * @return True if settings are accessible, false otherwise.
   */
  public boolean startEnableNfcSettings(Activity fromActivity) {
    Intent nfcSettingsIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
    ComponentName componentName = nfcSettingsIntent.resolveActivity(_context.getPackageManager());
    if (componentName == null) {
      return false;
    }

    fromActivity.startActivity(nfcSettingsIntent);
    return true;
  }

  protected void onNfcStatusChanged() {
    NfcSettingsChangedEvent nfcSettingsChangedEvent = new NfcSettingsChangedEvent(isNfcOn());

    _eventBus.post(nfcSettingsChangedEvent);
  }

  //endregion

  //region Nested class

  public static final class NfcSettingsChangedEvent {
    public final boolean _enabled;

    public NfcSettingsChangedEvent(boolean enabled) {
      _enabled = enabled;
    }
  }

  //endregion
}
