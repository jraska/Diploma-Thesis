package com.jraska.core;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AppContextModule
{
	@Provides
	public Application provideApplication()
	{
		return JRApplication.getCurrent();
	}

	@Provides
	public Context provideContext()
	{
		return provideApplication();
	}
}
