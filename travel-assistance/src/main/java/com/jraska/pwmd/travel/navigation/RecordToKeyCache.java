package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class RecordToKeyCache {
  //region Fields

  private final Map<String, BaseModel> _map = new HashMap<>();

  //endregion

  //region Methods

  String newKey(@NonNull TransportChangeSpec transportChange) {
    String key = newKey();
    _map.put(key, transportChange);
    return key;
  }

  String newKey(@NonNull NoteSpec noteSpec) {
    String key = newKey();
    _map.put(key, noteSpec);
    return key;
  }

  @Nullable BaseModel get(String key) {
    return _map.get(key);
  }

  void clear() {
    _map.clear();
  }

  public boolean isEmpty() {
    return _map.isEmpty();
  }

  public int size() {
    return _map.size();
  }

  private String newKey() {
    return UUID.randomUUID().toString();
  }

  //endregion
}
