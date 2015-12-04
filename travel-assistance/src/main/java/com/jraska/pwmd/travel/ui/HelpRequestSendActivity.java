package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.help.EmailSender;
import com.jraska.pwmd.travel.help.LostMessageTextBuilder;
import com.jraska.pwmd.travel.help.SmsSender;
import com.jraska.pwmd.travel.ui.BaseActivity;

import javax.inject.Inject;

public class HelpRequestSendActivity extends BaseActivity {
  //region Fields

  @Bind(R.id.helpMessageText) TextView _messageView;
  @Inject LocationService _locationService;
  @Inject SmsSender _smsSender;

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

    setContentView(R.layout.help_request_send);
    ButterKnife.bind(this);
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

    String message = getMessage(position);

    _messageView.setText(message);

    SmsSender smsSender = _smsSender;
    // TODO: assistant number
    if (smsSender.sendSms("0420721380088", message)) {
      showToast(getString(R.string.sent));
    } else {
      showToast(getString(R.string.not_sent));
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

    String message = getMessage(position);

    _messageView.setText(message);

    EmailSender emailSender = new EmailSender(this);

    // TODO: assistant email
    emailSender.sendEmail("josef.raska@gmail.com", getString(R.string.i_am_lost), message);
  }

  protected void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  protected boolean checkPosition(Position position) {
    if (position == null) {
      showToast(getString(R.string.no_position));
      return false;
    }

    return true;
  }


  //endregion
}
