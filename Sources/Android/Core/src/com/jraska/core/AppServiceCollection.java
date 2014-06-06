package com.jraska.core;

import com.jraska.common.ArgumentCheck;
import com.jraska.core.services.IAppService;

import java.util.HashMap;
import java.util.Map;

public class AppServiceCollection
{
	//region Fields

	private final Map<Class, IAppService> mServicesMap;

	//endregion

	//region Constructors

	public AppServiceCollection()
	{
		mServicesMap = new HashMap<Class, IAppService>();
	}

	//endregion

	//region Methods

	void putService(Class<?> appServiceClass, IAppService service)
	{
		ArgumentCheck.notNull(appServiceClass);
		ArgumentCheck.notNull(service);

		if (!appServiceClass.isAssignableFrom(service.getClass()))
		{
			throw new IllegalArgumentException(String.format("Service %s is not assignable from %s class", service.getClass(), appServiceClass));
		}

		mServicesMap.put(appServiceClass, service);
	}

	@SuppressWarnings("unchecked") //check at put
	public <T> T get(Class<T> serviceType)
	{
		return (T) mServicesMap.get(serviceType);
	}

	public <T> T safeGet(Class<T> serviceType)
	{
		final T service = get(serviceType);
		if (service == null)
		{
			throw new IllegalArgumentException(String.format("Unknown service class %s", serviceType));
		}

		return service;
	}

	//endregion
}
