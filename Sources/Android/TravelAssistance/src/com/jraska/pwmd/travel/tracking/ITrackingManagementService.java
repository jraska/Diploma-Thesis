package com.jraska.pwmd.travel.tracking;

import com.jraska.core.JRApplication;
import com.jraska.core.services.IAppService;
import com.jraska.pwmd.travel.data.Path;

import java.util.Date;

public interface ITrackingManagementService extends IAppService
{
	//region Properties

	boolean isTracking();

	//endregion

	//region Methods

	void startTracking();

	PathInfo getLastPath();

	void stopTracking();

	//endregion

	//region Nested classes

	static class PathInfo
	{
		private final Date mStart;
		private final Date mEnd;
		private final Path mPath;

		public PathInfo(Date start, Date end, Path path)
		{
			mStart = start;
			mEnd = end;
			mPath = path;
		}

		public Date getStart()
		{
			return mStart;
		}

		public Date getEnd()
		{
			return mEnd;
		}

		public Path getPath()
		{
			return mPath;
		}
	}

	static class Stub
	{
		public static ITrackingManagementService asInterface()
		{
			return JRApplication.getService(ITrackingManagementService.class);
		}
	}

	//endregion
}
