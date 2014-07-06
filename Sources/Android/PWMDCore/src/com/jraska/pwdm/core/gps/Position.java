package com.jraska.pwdm.core.gps;

import android.os.Parcel;
import android.os.Parcelable;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.IEventArgs;
import com.jraska.core.utils.DateHelper;

public class Position extends LatLng implements IEventArgs
{
	//region Fields

	public final long time;
	public final float accuracy;
	public final String provider;

	//endregion

	//region Constructors

	public Position(double latitude, double longitude, long time, float accuracy, String provider)
	{
		super(latitude, longitude);

		ArgumentCheck.notNull(provider);

		this.time = time;
		this.accuracy = accuracy;
		this.provider = provider;
	}

	//endregion

	//region Parcelable impl

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);

		dest.writeLong(time);
		dest.writeFloat(accuracy);
		dest.writeString(provider);
	}

	public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>()
	{
		public Position createFromParcel(Parcel p)
		{
			return new Position(p.readDouble(), p.readDouble(), p.readLong(), p.readFloat(), p.readString());
		}

		public Position[] newArray(int size)
		{
			return new Position[size];
		}
	};

	//endregion

	//region Object impl

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());

		sb.append(" Time: ").append(DateHelper.formatToDateTimeValue(time));
		sb.append(" Accuracy: ").append(accuracy);
		sb.append(" Provider: ").append(provider);

		return sb.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Position position = (Position) o;

		if (Float.compare(position.accuracy, accuracy) != 0) return false;
		if (time != position.time) return false;
		if (provider != null ? !provider.equals(position.provider) : position.provider != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (int) (time ^ (time >>> 32));
		result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
		result = 31 * result + (provider != null ? provider.hashCode() : 0);
		return result;
	}

	//endregion
}
