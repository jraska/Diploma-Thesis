package com.jraska.pwmd.travel.data;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;

import java.util.UUID;

public class PictureSpec {
  //region Fields

  @NonNull public final UUID imageId;
  @NonNull public final String caption;
  @NonNull public final LatLng latLng;

  //endregion

  //region Constructors

  public PictureSpec(@NonNull LatLng latLng, @NonNull UUID imageId, @NonNull String caption) {
    ArgumentCheck.notNull(latLng);
    ArgumentCheck.notNull(imageId);
    ArgumentCheck.notNull(caption);

    this.latLng = latLng;
    this.imageId = imageId;
    this.caption = caption;
  }

  //endregion

  //region Object impl

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PictureSpec that = (PictureSpec) o;

    if (!imageId.equals(that.imageId)) return false;
    return caption.equals(that.caption);

  }

  @Override public int hashCode() {
    int result = imageId.hashCode();
    result = 31 * result + caption.hashCode();
    return result;
  }


  //endregion
}
