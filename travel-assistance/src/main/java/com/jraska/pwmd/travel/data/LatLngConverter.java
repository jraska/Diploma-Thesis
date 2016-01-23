package com.jraska.pwmd.travel.data;

import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.converter.TypeConverter;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class LatLngConverter extends TypeConverter<String, LatLng> {

  @Override
  public String getDBValue(LatLng latLng) {
    return latLng == null ? null : String.valueOf(latLng._latitude) + "," + latLng._longitude;
  }

  @Override
  public LatLng getModelValue(String data) {
    String[] values = data.split(",");
    if (values.length != 2) {
      return null;
    } else {
      double lat = Double.parseDouble(values[0]);
      double lon = Double.parseDouble(values[1]);
      return new LatLng(lat, lon);
    }
  }
}
