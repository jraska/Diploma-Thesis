package com.jraska.core;

import android.app.Application;
import com.jraska.core.services.IAppService;

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
		return getCurrent().mServices.safeGet(serviceType);
	}

	//endregion

	//region Fields

	private final AppServiceCollection mServices = new AppServiceCollection();

	//endregion

	//region Constructors

	public JRApplication()
	{
		sCurrent = this;
	}

	//endregion

	//region Properties

	public AppServiceCollection getServices()
	{
		return mServices;
	}

	//endregion

	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	//endregion

	//region Methods

	protected final void putService(Class serviceClass, IAppService instance)
	{
		mServices.putService(serviceClass, instance);
	}

	//endregion
}
