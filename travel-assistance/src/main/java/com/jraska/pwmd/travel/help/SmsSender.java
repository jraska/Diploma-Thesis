package com.jraska.pwmd.travel.help;

import android.telephony.SmsManager;
import com.jraska.common.ArgumentCheck;

import javax.inject.Inject;

public class SmsSender {
  //region Constructors

  @Inject
  public SmsSender() {
  }

  //endregion

  //region Methods

  public boolean sendSms(String number, String message) {
    ArgumentCheck.notNull(number, "number");
    ArgumentCheck.notNull(message, "message");

    SmsManager smsManager = SmsManager.getDefault();
    if (smsManager == null) {
      return false;
    }

    smsManager.sendTextMessage(number, null, message, null, null);

    return true;
  }

  //endregion
}
