package com.jraska.pwmd.travel.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.help.Dialer;
import com.jraska.pwmd.travel.help.EmailSender;
import com.jraska.pwmd.travel.help.LostMessageTextBuilder;
import com.jraska.pwmd.travel.help.SmsSender;
import com.jraska.pwmd.travel.settings.SettingsManager;

import javax.inject.Inject;

public class HelpRequestSendActivity extends BaseActivity {
  //region Fields

  @Bind(R.id.helpMessageText) TextView _messageView;

  @Inject LocationService _locationService;
  @Inject SmsSender _smsSender;
  @Inject SettingsManager _settingsManager;

  private boolean _gpsStarted;

  //endregion

  //region Properties

  protected Location getLocation() {
    return _locationService.getLastLocation();
  }

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TravelAssistanceApp.getComponent(this).inject(this);

    setContentView(R.layout.activity_send_help_request);
  }

  @Override
  protected void onResume() {
    super.onResume();

    boolean tracking = _locationService.isTracking();
    if (!tracking) {
      _locationService.startTracking(new LocationSettings(5, 5));
      _gpsStarted = true;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (_gpsStarted) {
      _locationService.stopTracking();
      _gpsStarted = false;
    }
  }

  //endregion

  //region Methods

  @OnClick(R.id.help_btnSendSms) void sendSms() {
    Location location = getLocation();
    if (!checkLocation(location)) {
      return;
    }

    String assistantPhone = _settingsManager.getAssistantPhone();
    if (TextUtils.isEmpty(assistantPhone)) {
      // TODO: Check this on startup to disable buttons
      showSnackbar(R.string.no_assistant_number);
      return;
    }

    // TODO: 06/12/15 Validation if the device can send sms

    String message = getMessage(location);
    if (BuildConfig.DEBUG) {
      _messageView.setText(message);
    }

    boolean messageSent = _smsSender.sendSms(assistantPhone, message);
    if (messageSent) {
      showSnackbar(R.string.sent);
    } else {
      showSnackbar(R.string.not_sent);
    }
  }

  protected String getMessage(Location location) {
    LostMessageTextBuilder builder = new LostMessageTextBuilder(this);
    builder.setFromLocation(location);
    return builder.buildSmsText();
  }


  @OnClick(R.id.help_btnCall) void callAsistant() {
    String assistantPhone = _settingsManager.getAssistantPhone();
    if (TextUtils.isEmpty(assistantPhone)) {
      showSnackbar(R.string.no_assistant_number);
      return;
    }

    Dialer dialer = new Dialer(this);
    boolean phoneCall = dialer.phoneCall(assistantPhone);

    if (!phoneCall) {
      showSnackbar(R.string.no_app_for_call);
    }
  }

  @OnClick(R.id.btnSendEmail) void sendEmail() {
    Location location = getLocation();
    if (!checkLocation(location)) {
      return;
    }

    String assistantEmail = _settingsManager.getAssistantEmail();
    if (assistantEmail == null) {
      assistantEmail = ""; // will launch email client without recipient
    }

    String message = getMessage(location);

    if (BuildConfig.DEBUG) {
      _messageView.setText(message);
    }

    EmailSender emailSender = new EmailSender(this); // No DI because we need Activity context here
    boolean emailSent = emailSender.sendEmail(assistantEmail, getString(R.string.i_am_lost), message);

    if (!emailSent) {
      onEmailSentFailed();
    }
  }

  private void onEmailSentFailed() {
    showSnackbar(R.string.help_no_app_for_email);
  }

  protected void showSnackbar(int messageRes) {
    Snackbar.make(_messageView, messageRes, Snackbar.LENGTH_LONG).show();
  }

  protected boolean checkLocation(Location location) {
    if (location == null) {
      showSnackbar(R.string.no_position);
      return false;
    }

    return true;
  }

  //endregion
}
