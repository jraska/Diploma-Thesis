package com.jraska.pwdm.core.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.jraska.common.ArgumentCheck;
import com.jraska.core.JRApplication;

public class SimpleBatteryStatsService implements IBatteryStatsService
{
	//region Fields

	private final Context mContext;

	//endregion

	//region Constructors

	public SimpleBatteryStatsService(Context context)
	{
		ArgumentCheck.notNull(context);

		mContext = context;
	}

	//endregion

	//region IBatteryStatsService impl

	@Override
	public BatteryStats getCurrentBatteryStats()
	{
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = mContext.registerReceiver(null, ifilter);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float) scale;

		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				status == BatteryManager.BATTERY_STATUS_FULL;

		return new BatteryStats(batteryPct, isCharging);
	}

	//endregion
}
