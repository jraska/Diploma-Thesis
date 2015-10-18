package com.jraska.pwmd.travel.data;

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

	//region Object impl

	@Override
	public String toString()
	{
		return "RouteData{" +
				"mDescription=" + mDescription +
				", mRoute=" + mRoute +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RouteData routeData = (RouteData) o;

		if (!mDescription.equals(routeData.mDescription)) return false;
		if (!mRoute.equals(routeData.mRoute)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = mDescription.hashCode();
		result = 31 * result + mRoute.hashCode();
		return result;
	}

	//endregion
}
