package com.jraska.pwdm.travel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jraska.pwdm.core.gps.ILocationService;
import com.jraska.pwdm.core.gps.LocationSettings;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.travel.help.EmailSender;
import com.jraska.pwdm.travel.help.LostMessageTextBuilder;
import com.jraska.pwdm.travel.help.SmsSender;

public class HelpRequestSendActivity extends BaseTravelActivity
{
	//region Fields

	@InjectView(R.id.btnSendEmail)
	Button mSendEmail;

	@InjectView(R.id.btnSendSms)
	Button mBtnSendSms;

	@InjectView(R.id.helpMessageText)
	TextView mMessageView;

	private boolean mGpsStarted;

	//endregion

	//region Properties

	protected ILocationService getLocationService()
	{
		return ILocationService.Stub.asInterface();
	}

	protected Position getPosition()
	{
		return getLocationService().getLastPosition();
	}

	//endregion

	//region Activity overrides

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help_request_send);

		ButterKnife.inject(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		boolean tracking = getLocationService().isTracking();
		if (!tracking)
		{
			getLocationService().startTracking(new LocationSettings(5, 5));
			mGpsStarted = true;
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if (mGpsStarted)
		{
			getLocationService().stopTracking();
			mGpsStarted = false;
		}
	}

	//endregion

	//region Methods

	@OnClick(R.id.btnSendSms)
	void sendSms()
	{
		Position position = getPosition();
		if (!checkPosition(position))
		{
			return;
		}

		String message = getMessage(position);

		mMessageView.setText(message);

		SmsSender smsSender = new SmsSender();
		if (smsSender.sendSms("0420721380088", message))
		{
			showToast(getString(R.string.sent));
		}
		else
		{
			showToast(getString(R.string.not_sent));
		}
	}

	protected String getMessage(Position position)
	{
		LostMessageTextBuilder builder = new LostMessageTextBuilder();
		builder.setFromPosition(position);
		return builder.buildSmsText();
	}

	@OnClick(R.id.btnSendEmail)
	void sendEmail()
	{
		Position position = getPosition();
		if (!checkPosition(position))
		{
			return;
		}

		String message = getMessage(position);

		mMessageView.setText(message);

		EmailSender emailSender = new EmailSender(this);
		emailSender.sendEmail("josef.raska@gmail.com", getString(R.string.i_am_lost), message);
	}

	protected void showToast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	protected boolean checkPosition(Position position)
	{
		if (position == null)
		{
			showToast(getString(R.string.no_position));
			return false;
		}

		return true;
	}


	//endregion
}
