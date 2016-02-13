package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.nfc.NfcStatusChecker;
import com.jraska.pwmd.travel.nfc.NfcWriter;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import javax.inject.Inject;

public class NfcWriteActivity extends BaseActivity {
  //region Constants

  public static final String KEY_ROUTE_ID = "RouteIdToNfcWrite";
  public static final int REQUEST_CODE_WRITE_NFC = 2384; // random value

  //endregion

  //region Fields

  @Bind(R.id.nfc_write_info_text) TextView _messageView;
  @Bind(R.id.nfc_write_success_text) TextView _successView;

  @Inject EventBus _eventBus;
  @Inject NfcStatusChecker _nfcStatusChecker;
  @Inject NfcWriter _nfcWriter;

  private Snackbar _lastNfcOffSnackbar;

  private long _routeId;
  private boolean _resumed;
  private boolean _writeRequestPending;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_nfc);
    TravelAssistanceApp.getComponent(this).inject(this);

    _routeId = getIntent().getLongExtra(KEY_ROUTE_ID, -1);
    if (_routeId < 0) {
      Timber.e("No route id provided to intent %s", getIntent());
      finish();
      return;
    }

    if (!_nfcStatusChecker.hasDeviceNfcFeature()) {
      showNoNfcMessage();
      return;
    }

    _eventBus.register(this);

    onNfcTagWriteRequested();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (!isFinishing() && _nfcStatusChecker.isNfcOff()) {
      showNfcOffMessage();
    }

    _resumed = true;
    if (_writeRequestPending) {
      startNfcWrite();
    }
  }

  @Override
  protected void onPause() {
    if (_lastNfcOffSnackbar != null) {
      _lastNfcOffSnackbar.dismiss();
      _lastNfcOffSnackbar = null;
    }

    _resumed = false;
    stopNfcWrite();

    super.onPause();
  }

  @Override
  protected void onDestroy() {
    _eventBus.unregister(this);
    _nfcWriter.removePendingWrites();

    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    _nfcWriter.onNewIntent(intent);
  }

  //endregion

  //region Methods

  public void onEvent(NfcStatusChecker.NfcSettingsChangedEvent event) {
    onNfcTagWriteRequested();
  }

  public void onEvent(NfcWriter.TagWriteResultEvent event) {
    if (event._routeId != _routeId) {
      return;
    }

    if (event.isSuccess()) {
      onNfcTagWritten();
    } else {
      onNfcTagWriteError(event._result);
    }
  }

  private void stopNfcWrite() {
    _nfcWriter.cancelPendingTagWrites(this);
    _writeRequestPending = false;
  }

  @OnClick(R.id.nfc_write_info_text) void onIconClicked() {
    if (_nfcStatusChecker.isNfcOff()) {
      startEnableNfcSettings();
    }
  }

  protected void onNfcTagWriteRequested() {
    _messageView.setVisibility(View.VISIBLE);
    _successView.setVisibility(View.GONE);

    if (_nfcStatusChecker.isNfcOn()) {
      startNfcWrite();
    } else {
      showNfcOffMessage();
    }
  }

  private void startNfcWrite() {
    if (_resumed) {
      _nfcWriter.requestTagWrite(this, _routeId);
    } else {
      _writeRequestPending = true;
    }
  }


  private void showNfcOffMessage() {
    if (_lastNfcOffSnackbar != null && _lastNfcOffSnackbar.isShownOrQueued()) {
      return;
    }

    Snackbar snackbar = Snackbar.make(_messageView, R.string.nfc_disabled, Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction(R.string.nfc_enable, new View.OnClickListener() {
      @Override public void onClick(View v) {
        startEnableNfcSettings();
      }
    });

    snackbar.show();
    _lastNfcOffSnackbar = snackbar;
  }

  protected void startEnableNfcSettings() {
    _nfcStatusChecker.startEnableNfcSettings(this);
  }

  protected void onNfcTagWritten() {
    _messageView.setVisibility(View.GONE);
    _successView.setVisibility(View.VISIBLE);

    setResult(RESULT_OK);
  }

  protected void onNfcTagWriteError(int result) {
    stopNfcWrite();
    Snackbar snackbar = Snackbar.make(_messageView, R.string.nfc_error_write, Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction(R.string.nfc_try_again, new View.OnClickListener() {
      @Override public void onClick(View v) {
        onNfcTagWriteRequested();
      }
    });

    snackbar.show();
  }

  private void showNoNfcMessage() {
    Snackbar snackbar = Snackbar.make(_messageView, R.string.nfc_not_supported,
        Snackbar.LENGTH_INDEFINITE);

    snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    // to ensure activity finishes when user swipes snackbar away
    snackbar.setCallback(new Snackbar.Callback() {
      @Override public void onDismissed(Snackbar snackbar, int event) {
        finish();
      }
    });

    snackbar.show();
  }

  //endregion
}
