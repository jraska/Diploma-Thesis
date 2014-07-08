package com.jraska.pwdm.travel.tracking;

import com.jraska.common.utils.IFilter;
import com.jraska.pwdm.core.gps.Position;

public interface ILocationFilter extends IFilter<Position>
{
	//region Nested classes

	static class Empty implements ILocationFilter
	{
		public static final Empty Instance = new Empty();

		private Empty()
		{
		}

		@Override
		public boolean accept(Position position)
		{
			return true;
		}
	}

	//endregion

}
