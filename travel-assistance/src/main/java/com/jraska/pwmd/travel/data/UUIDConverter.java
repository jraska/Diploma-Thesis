package com.jraska.pwmd.travel.data;

import android.text.TextUtils;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.UUID;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class UUIDConverter extends TypeConverter<String, UUID> {

  @Override
  public String getDBValue(UUID id) {
    if (id == null) {
      return "";
    } else {
      return id.toString();
    }
  }

  @Override
  public UUID getModelValue(String data) {
    if (TextUtils.isEmpty(data)) {
      return null;
    } else {
      return UUID.fromString(data);
    }
  }
}
