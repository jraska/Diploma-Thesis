package com.jraska.pwmd.travel.help;

import android.telephony.SmsManager;
import com.jraska.common.ArgumentCheck;
import timber.log.Timber;

import javax.inject.Inject;

public class SmsSender {
  //region Constants

  public static final int MAX_SMS_LENGTH = 160;

  //endregion

  //region Constructors

  @Inject
  public SmsSender() {
  }

  //endregion

  //region Methods

  public boolean sendSms(String number, String message) {
    ArgumentCheck.notNull(number, "number");
    ArgumentCheck.notNull(message, "message");

    if (message.length() > MAX_SMS_LENGTH) {
      Timber.e("Message %s is %d characters long, which is longer than common sms maximum %d",
          message, message.length(), MAX_SMS_LENGTH);

      return false;
    }

    SmsManager smsManager = SmsManager.getDefault();
    if (smsManager == null) {
      return false;
    }

    smsManager.sendTextMessage(number, null, message, null, null);

    return true;
  }

  //endregion
}
