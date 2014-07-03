package com.jraska.core.services;

import com.jraska.core.JRApplication;

import java.io.File;

public class DefaultExternalStorageAppEnvironmentService extends SimpleAppEnvironmentService
{
	//region Constructors

	public DefaultExternalStorageAppEnvironmentService()
	{
		super(JRApplication.getCurrent().getExternalFilesDir(null)); //null is for default dir
	}

	//endregion
}
