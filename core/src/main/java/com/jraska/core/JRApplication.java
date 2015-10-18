package com.jraska.core;

import android.app.Application;
import com.jraska.core.services.IAppService;
import dagger.ObjectGraph;

/**
 * Base class for all applications
 */
public abstract class JRApplication extends Application
{
	//region Static

	private static JRApplication sCurrent;

	public static JRApplication getCurrent()
	{
		return sCurrent;
	}

	public static <T> T getService(Class<T> serviceType)
	{
		return getCurrent().mObjectGraph.get(serviceType);
	}

	//endregion

	//region Fields

	private ObjectGraph mObjectGraph;

	//endregion

	//region Constructors

	public JRApplication()
	{
		sCurrent = this;
	}

	//endregion

	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		mObjectGraph = ObjectGraph.create(getModules());
	}

	//endregion

	//region Methods

	public void inject(Object o)
	{
		mObjectGraph.inject(o);
	}

	protected Object[] getModules()
	{
		Object[] modules = {new AppContextModule()};
		return modules;
	}

	//endregion
}
