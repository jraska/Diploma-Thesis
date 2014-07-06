package com.jraska.pwdm.travel.data;

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
		//TODO: checks

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
}
