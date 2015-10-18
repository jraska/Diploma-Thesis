package com.jraska.pwmd.travel.help;

import android.telephony.SmsManager;
import com.jraska.common.ArgumentCheck;

public class SmsSender
{
	//region Methods

	public boolean sendSms(String number, String message)
	{
		ArgumentCheck.notNull(number, "number");
		ArgumentCheck.notNull(message, "message");

		SmsManager smsManager = SmsManager.getDefault();
		if (smsManager == null)
		{
			return false;
		}

		smsManager.sendTextMessage(number, null, message, null, null);

		return true;
	}

	//endregion
}
