package com.jraska.pwmd.core.gps;

import com.jraska.common.ArgumentCheck;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Position {
  //region Fields

  public final LatLng latLng;
  public final long time;
  public final float accuracy;
  public final String provider;


  //endregion

  //region Constructors

  public Position(LatLng latLng, long time, float accuracy, String provider) {
    ArgumentCheck.notNull(latLng);
    ArgumentCheck.notNull(provider);

    this.time = time;
    this.accuracy = accuracy;
    this.provider = provider;
    this.latLng = latLng;
  }

  //endregion
}
