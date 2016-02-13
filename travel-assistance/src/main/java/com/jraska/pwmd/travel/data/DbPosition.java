package com.jraska.pwmd.travel.data;

import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(database = TravelDatabase.class)
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "_id")
public class DbPosition extends BaseModel {
  @PrimaryKey(autoincrement = true) long _id;
  @Column long _routeId;
  @Column
  public LatLng latLng;

  DbPosition() {
  }

  public DbPosition(LatLng latLng) {
    this.latLng = latLng;
  }
}
