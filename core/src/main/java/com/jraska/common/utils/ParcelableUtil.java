package com.jraska.common.utils;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ParcelableUtil {
  //region Constructors

  private ParcelableUtil() {
  }

  //endregion

  //region Methods

  public static byte[] marshall(Parcelable p) {
    Parcel parcel = Parcel.obtain();
    p.writeToParcel(parcel, 0);
    byte[] bytes = parcel.marshall();
    parcel.recycle(); // not sure if needed or a good idea
    return bytes;
  }

  public static Parcel unMarshall(byte[] bytes) {
    Parcel parcel = Parcel.obtain();
    parcel.unmarshall(bytes, 0, bytes.length);
    parcel.setDataPosition(0); // this is extremely important!
    return parcel;
  }

  public static <T> T unMarshall(byte[] bytes, Parcelable.Creator<T> creator) {
    Parcel parcel = unMarshall(bytes);
    return creator.createFromParcel(parcel);
  }

  //endregion
}