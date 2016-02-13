package com.jraska.pwmd.core.gps;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LatLng implements Parcelable {
  //region Fields

  public final double _latitude;
  public final double _longitude;

  //endregion

  //region Constructors

  public LatLng(double latitude, double longitude) {
    _latitude = latitude;
    _longitude = longitude;
  }


  //endregion

  //region Parcelable impl

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(_latitude);
    dest.writeDouble(_longitude);
  }

  public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
    public LatLng createFromParcel(Parcel p) {
      return new LatLng(p.readDouble(), p.readDouble());
    }

    public LatLng[] newArray(int size) {
      return new LatLng[size];
    }
  };

  //endregion
}
