package com.jraska.core.services;

import com.jraska.core.JRApplication;

import java.io.File;

public interface IAppEnvironmentService extends IAppService
{
	//region Methods

	File getAppDataRootDirectory();

	//endregion

	//region Nested class

	static class Stub
	{
		public static IAppEnvironmentService asInterface()
		{
			return JRApplication.getService(IAppEnvironmentService.class);
		}
	}

	//endregion
}
