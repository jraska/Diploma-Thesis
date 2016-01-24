package com.jraska.pwmd.travel.data;

import com.jraska.common.ArgumentCheck;

import java.util.Date;

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

  //region Object impl

  @Override
  public String toString() {
    return "RouteDescription{" +
        ", _start=" + _mStart +
        ", _end=" + _end +
        ", _title='" + _title + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteDescription that = (RouteDescription) o;

    if (!_end.equals(that._end)) return false;
    if (!_mStart.equals(that._mStart)) return false;
    if (!_title.equals(that._title)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = _mStart.hashCode();
    result = 31 * result + _end.hashCode();
    result = 31 * result + _title.hashCode();
    return result;
  }

  //endregion
}
