package com.jraska.pwmd.travel.tracking;

import com.jraska.common.utils.Filter;
import com.jraska.pwmd.core.gps.Position;

public interface LocationFilter extends Filter<Position> {
  //region Nested classes

  LocationFilter Empty = new LocationFilter() {
    @Override public boolean accept(Position o) {
      return false;
    }
  };

  //endregion
}
