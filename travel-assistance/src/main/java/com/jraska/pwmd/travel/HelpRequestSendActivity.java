package com.jraska.pwmd.travel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.pwmd.core.gps.LocationService;
import com.jraska.pwmd.core.gps.LocationSettings;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.help.EmailSender;
import com.jraska.pwmd.travel.help.LostMessageTextBuilder;
import com.jraska.pwmd.travel.help.SmsSender;

public class HelpRequestSendActivity extends BaseTravelActivity {
  //region Fields

  @Bind(R.id.btnSendEmail) Button _sendEmail;
  @Bind(R.id.btnSendSms) Button _btnSendSms;
  @Bind(R.id.helpMessageText) TextView _messageView;

  private boolean _gpsStarted;

  //endregion

  //region Properties

  protected LocationService getLocationService() {
    return LocationService.Stub.asInterface();
  }

  protected Position getPosition() {
    return getLocationService().getLastPosition();
  }

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.help_request_send);

    ButterKnife.bind(this);
  }

  @Override
  protected void onResume() {
    super.onResume();

    boolean tracking = getLocationService().isTracking();
    if (!tracking) {
      getLocationService().startTracking(new LocationSettings(5, 5));
      _gpsStarted = true;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (_gpsStarted) {
      getLocationService().stopTracking();
      _gpsStarted = false;
    }
  }

  //endregion

  //region Methods

  @OnClick(R.id.btnSendSms)
  void sendSms() {
    Position position = getPosition();
    if (!checkPosition(position)) {
      return;
    }

    String message = getMessage(position);

    _messageView.setText(message);

    SmsSender smsSender = new SmsSender();
    if (smsSender.sendSms("0420721380088", message)) {
      showToast(getString(R.string.sent));
    } else {
      showToast(getString(R.string.not_sent));
    }
  }

  protected String getMessage(Position position) {
    LostMessageTextBuilder builder = new LostMessageTextBuilder();
    builder.setFromPosition(position);
    return builder.buildSmsText();
  }

  @OnClick(R.id.btnSendEmail)
  void sendEmail() {
    Position position = getPosition();
    if (!checkPosition(position)) {
      return;
    }

    String message = getMessage(position);

    _messageView.setText(message);

    EmailSender emailSender = new EmailSender(this);
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
