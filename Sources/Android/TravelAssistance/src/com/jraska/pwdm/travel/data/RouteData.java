package com.jraska.pwdm.travel.data;

import java.util.Date;
import java.util.UUID;

public class RouteData
{
	//region Fields

	private final RouteDescription mDescription;
	private final Path mRoute;

	//endregion

	//region Constructors

	public RouteData(RouteDescription description, Path route)
	{
		//TODO: checks

		mDescription = description;
		mRoute = route;
	}

	//endregion

	//region Properties

	public RouteDescription getDescription()
	{
		return mDescription;
	}

	public UUID getId()
	{
		return mDescription.getId();
	}

	public Path getPath()
	{
		return mRoute;
	}

	public Date getStart()
	{
		return mDescription.getStart();
	}

	public Date getEnd()
	{
		return mDescription.getEnd();
	}

	public String getTitle()
	{
		return mDescription.getTitle();
	}

	//endregion
}
