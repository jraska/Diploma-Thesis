package com.jraska.pwdm.core.gps;

public class LocationSettings
{
	//region Fields

	public final long minTime;
	public final float minDistance;

	//endregion

	//region Constructors

	public LocationSettings(long minTime, float minDistance)
	{
		this.minTime = minTime;
		this.minDistance = minDistance;
	}

	//endregion
}
