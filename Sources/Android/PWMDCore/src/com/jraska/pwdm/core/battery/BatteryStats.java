package com.jraska.pwdm.core.battery;

import com.jraska.pwdm.core.utils.DateHelper;

public class BatteryStats
{
	//region Fields

	public final float percent;
	public final boolean isCharging;
	public final long time;

	//endregion

	//region Constructors

	public BatteryStats(float percent, boolean isCharging)
	{
		this(percent, isCharging, System.currentTimeMillis());
	}

	public BatteryStats(float percent, boolean isCharging, long time)
	{
		this.percent = percent;
		this.isCharging = isCharging;
		this.time = time;
	}

	//endregion

	//region Object impl


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ").append(percent).append("% ");
		sb.append(" Time: ").append(DateHelper.formatToDateTimeValue(time));
		if (isCharging)
		{
			sb.append(" Charging");
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BatteryStats that = (BatteryStats) o;

		if (isCharging != that.isCharging) return false;
		if (Float.compare(that.percent, percent) != 0) return false;
		if (time != that.time) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = (percent != +0.0f ? Float.floatToIntBits(percent) : 0);
		result = 31 * result + (isCharging ? 1 : 0);
		result = 31 * result + (int) (time ^ (time >>> 32));
		return result;
	}

	//endregion
}
