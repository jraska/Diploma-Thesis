package com.jraska.core.services;

import com.jraska.core.BaseApplication;

public class DefaultExternalStorageAppEnvironmentService extends SimpleAppEnvironmentService
{
	//region Constructors

	public DefaultExternalStorageAppEnvironmentService()
	{
	  super(BaseApplication.getCurrent().getExternalFilesDir(null)); //null is for default dir
	}

	//endregion
}
