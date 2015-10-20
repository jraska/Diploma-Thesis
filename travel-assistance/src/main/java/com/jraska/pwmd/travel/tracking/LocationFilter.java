package com.jraska.pwmd.travel.tracking;

import com.jraska.common.utils.Filter;
import com.jraska.pwmd.core.gps.Position;

public interface LocationFilter extends Filter<Position> {
  //region Nested classes

  class Empty implements LocationFilter {
    public static final Empty Instance = new Empty();

    private Empty() {
    }

    @Override
    public boolean accept(Position position) {
      return true;
    }
  }

  //endregion

}
