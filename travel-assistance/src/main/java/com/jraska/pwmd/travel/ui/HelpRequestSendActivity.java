package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
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

  protected Position getPosition() {
    return _locationService.getLastPosition();
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

  @OnClick(R.id.btnSendSms) void sendSms() {
    Position position = getPosition();
    if (!checkPosition(position)) {
      return;
    }

    String assistantPhone = _settingsManager.getAssistantPhone();
    if (TextUtils.isEmpty(assistantPhone)) {
      // TODO: Check this on startup to disable buttons
      showToast(R.string.no_assistant_number);
      return;
    }

    // TODO: 06/12/15 Validation if the device can send sms

    String message = getMessage(position);
    _messageView.setText(message);

    SmsSender smsSender = _smsSender;

    if (smsSender.sendSms(assistantPhone, message)) {
      showToast(R.string.sent);
    } else {
      showToast(R.string.not_sent);
    }
  }

  protected String getMessage(Position position) {
    LostMessageTextBuilder builder = new LostMessageTextBuilder(this);
    builder.setFromPosition(position);
    return builder.buildSmsText();
  }

  @OnClick(R.id.btnSendEmail) void sendEmail() {
    Position position = getPosition();
    if (!checkPosition(position)) {
      return;
    }

    String assistantEmail = _settingsManager.getAssistantEmail();
    if(assistantEmail == null){
      assistantEmail = ""; // will launch email client without recipient
    }

    String message = getMessage(position);

    _messageView.setText(message);

    EmailSender emailSender = new EmailSender(this); // No DI because we need Activity context here
    emailSender.sendEmail(assistantEmail, getString(R.string.i_am_lost), message);
  }

  protected void showToast(int messageRes) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
  }

  protected boolean checkPosition(Position position) {
    if (position == null) {
      showToast(R.string.no_position);
      return false;
    }

    return true;
  }

  //endregion
}
