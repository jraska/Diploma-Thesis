package com.jraska.pwmd.core.gps;

import android.os.Parcel;
import android.os.Parcelable;

public class LatLng implements Parcelable
{
	//region Fields

	public final double latitude;
	public final double longitude;

	//endregion

	//region Constructors

	public LatLng(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}


	//endregion

	//region Parcelable impl

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

	public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>()
	{
		public LatLng createFromParcel(Parcel p)
		{
			return new LatLng(p.readDouble(), p.readDouble());
		}

		public LatLng[] newArray(int size)
		{
			return new LatLng[size];
		}
	};

	//endregion

	//region Object impl

	@Override
	public String toString()
	{
		return getClass().getSimpleName() +
				" latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LatLng latLng = (LatLng) o;

		if (Double.compare(latLng.latitude, latitude) != 0) return false;
		if (Double.compare(latLng.longitude, longitude) != 0) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	//endregion
}
