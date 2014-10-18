package com.jraska.pwdm.travel.data;

import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.IEventArgs;

import java.util.Date;
import java.util.UUID;

public class RouteDescription implements IEventArgs
{
	//region Fields

	private final UUID mId;
	private final Date mStart;
	private final Date mEnd;
	private final String mTitle;

	//endregion

	//region Constructors

	public RouteDescription(UUID id, Date start, Date end, String title)
	{
		ArgumentCheck.notNull(id);
		ArgumentCheck.notNull(start);
		ArgumentCheck.notNull(end);
		ArgumentCheck.notNull(title);

		mId = id;
		mStart = start;
		mEnd = end;
		mTitle = title;
	}

	//endregion

	//region Properties

	public UUID getId()
	{
		return mId;
	}

	public Date getStart()
	{
		return mStart;
	}

	public Date getEnd()
	{
		return mEnd;
	}

	public String getTitle()
	{
		return mTitle;
	}

	//endregion

	//region Object impl

	@Override
	public String toString()
	{
		return "RouteDescription{" +
				"mId=" + mId +
				", mStart=" + mStart +
				", mEnd=" + mEnd +
				", mTitle='" + mTitle + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RouteDescription that = (RouteDescription) o;

		if (!mEnd.equals(that.mEnd)) return false;
		if (!mId.equals(that.mId)) return false;
		if (!mStart.equals(that.mStart)) return false;
		if (!mTitle.equals(that.mTitle)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = mId.hashCode();
		result = 31 * result + mStart.hashCode();
		result = 31 * result + mEnd.hashCode();
		result = 31 * result + mTitle.hashCode();
		return result;
	}

	//endregion
}
