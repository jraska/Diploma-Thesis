package com.jraska.pwmd.travel.ui;

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
  @Inject NfcWriter _writer;

  private Snackbar _lastNfcOffSnackbar;

  private long _routeId;

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

  @Override protected void onResume() {
    super.onResume();

    if (!isFinishing() && _nfcStatusChecker.isNfcOff()) {
      showNfcOffMessage();
    }
  }

  @Override protected void onPause() {
    if (_lastNfcOffSnackbar != null) {
      _lastNfcOffSnackbar.dismiss();
      _lastNfcOffSnackbar = null;
    }

    super.onPause();
  }

  @Override protected void onDestroy() {
    _eventBus.unregister(this);

    super.onDestroy();
  }

  //endregion

  //region Methods

  public void onEvent(NfcStatusChecker.NfcSettingsChangedEvent event) {
    onNfcTagWriteRequested();
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
    }
  }

  private void startNfcWrite() {
    // TODO: 13/02/16 Start Nfc Write
    onNfcTagWritten();
  }


  private void showNfcOffMessage() {
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
