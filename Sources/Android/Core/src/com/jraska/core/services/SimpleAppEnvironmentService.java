package com.jraska.core.services;

import com.jraska.common.ArgumentCheck;
import com.jraska.common.exceptions.JRRuntimeException;

import java.io.File;

public class SimpleAppEnvironmentService implements IAppEnvironmentService
{
	//region Fields

	private final File mRootAppDir;

	//endregion

	//region Constructors

	public SimpleAppEnvironmentService(File rootAppDir)
	{
		ArgumentCheck.notNull(rootAppDir, "rootAppDir");

		mRootAppDir = rootAppDir;

		ensureDirExists(mRootAppDir);
	}

	//endregion

	//region IAppEnvironmentService impl

	@Override
	public File getAppDataRootDirectory()
	{
		return mRootAppDir;
	}

	//endregion

	//region methods

	protected void ensureDirExists(File rootAppDir)
	{
		if (!rootAppDir.exists())
		{
			boolean created = rootAppDir.mkdirs();
			if (!created)
			{
				throw new JRRuntimeException(String.format("Error creating RootAppDir: %s", rootAppDir.getAbsolutePath()));
			}
		}
	}

	//endregion
}
