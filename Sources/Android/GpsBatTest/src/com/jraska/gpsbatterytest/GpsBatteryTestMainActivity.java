package com.jraska.gpsbatterytest;

import android.app.Activity;
import android.os.Bundle;
import com.jraska.pwdm.core.gps.IGpsService;
import com.jraska.pwdm.core.gps.LatLng;

public class GpsBatteryTestMainActivity extends Activity
{
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		final IGpsService gpsService = IGpsService.Stub.asInterface();

		final LatLng lastPosition = gpsService.getLastPosition();
	}
}
