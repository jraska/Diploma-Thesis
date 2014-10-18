package com.jraska.gpsbatterytest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GpsBatteryTestMainActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startService(new Intent(GpsBatteryTestMainActivity.this, GpsBatteryTestService.class));
			}
		});

		findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				stopService(new Intent(GpsBatteryTestMainActivity.this, GpsBatteryTestService.class));
			}
		});
	}
}
