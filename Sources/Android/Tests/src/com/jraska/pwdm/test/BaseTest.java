package com.jraska.pwdm.test;

import android.content.Context;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public abstract class BaseTest
{
	//region Methods

	protected Context getAppContext()
	{
		return Robolectric.application;
	}

	protected void log(String text)
	{
		System.out.println(text);
	}

	//endregion
}
