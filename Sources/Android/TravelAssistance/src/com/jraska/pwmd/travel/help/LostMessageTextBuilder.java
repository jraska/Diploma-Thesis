package com.jraska.pwmd.travel.help;

import com.jraska.common.ArgumentCheck;
import com.jraska.core.JRApplication;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApplication;

import java.util.Date;

public class LostMessageTextBuilder
{
	//region Constants

	public static final int DEFAULT_ZOOM = 15;

	//endregion

	//region Fields

	private double mLatitude;
	private double mLongitude;
	private float mAccuracy;
	private Date mTime;
	private int mZoom = DEFAULT_ZOOM;
	private String mSmsTextTemplate;

	//endregion

	//region Properties

	public double getLatitude()
	{
		return mLatitude;
	}

	public LostMessageTextBuilder setLatitude(double latitude)
	{
		mLatitude = latitude;
		return this;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public LostMessageTextBuilder setLongitude(double longitude)
	{
		mLongitude = longitude;
		return this;
	}

	public float getAccuracy()
	{
		return mAccuracy;
	}

	public LostMessageTextBuilder setAccuracy(float accuracy)
	{
		mAccuracy = accuracy;
		return this;
	}

	public Date getTime()
	{
		return mTime;
	}

	public LostMessageTextBuilder setTime(Date time)
	{
		mTime = time;
		return this;
	}

	public int getZoom()
	{
		return mZoom;
	}

	public LostMessageTextBuilder setZoom(int zoom)
	{
		mZoom = zoom;
		return this;
	}

	public String getSmsTextTemplate()
	{
		return mSmsTextTemplate;
	}

	public LostMessageTextBuilder setSmsTextTemplate(String smsTextTemplate)
	{
		ArgumentCheck.notNull(smsTextTemplate, "smsTextTemplate");

		mSmsTextTemplate = smsTextTemplate;
		return this;
	}

	protected String getSmsTextTemplateInternal()
	{
		if(mSmsTextTemplate == null)
		{
			return getString(R.string.lost_sms_message_template);
		}

		return mSmsTextTemplate;
	}

	//endregion

	//region Methods

	protected String formatAppDate(Date date)
	{
		return TravelAssistanceApplication.USER_DETAILED_TIME_FORMAT.format(date);
	}

	protected String getString(int resId)
	{
		return JRApplication.getCurrent().getString(resId);
	}

	public LostMessageTextBuilder setFromPosition(Position p)
	{
		ArgumentCheck.notNull(p, "p");

		return setAccuracy(p.accuracy).setLatitude(p.latitude).setLongitude(p.longitude).setTime(new Date(p.time));
	}

	public String buildGoogleMapsUrl()
	{
		return "http://maps.google.com/maps?" + "&z=" + mZoom + "&q=" + mLatitude + "+" + mLongitude;
	}

	public String buildSmsText()
	{
		String dateText = mTime == null ? getString(R.string.unknown_position) : formatAppDate(mTime);
		return String.format(getSmsTextTemplateInternal(), buildGoogleMapsUrl(), mAccuracy, dateText);
	}

	//endregion
}
