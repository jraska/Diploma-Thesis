package com.jraska.pwmd.travel.data;

import com.jraska.common.ArgumentCheck;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@EqualsAndHashCode
@ToString
public class RouteDescription {
  //region Fields

  private final Date _mStart;
  private final Date _end;
  private final String _title;

  //endregion

  //region Constructors

  public RouteDescription(Date start, Date end, String title) {
    ArgumentCheck.notNull(start);
    ArgumentCheck.notNull(end);
    ArgumentCheck.notNull(title);

    _mStart = start;
    _end = end;
    _title = title;
  }

  //endregion

  //region Properties

  public Date getStart() {
    return _mStart;
  }

  public Date getEnd() {
    return _end;
  }

  public String getTitle() {
    return _title;
  }

  //endregion
}
