package com.jraska.pwdm.travel;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jraska.pwdm.travel.data.RouteDescription;

public class RoutesAdapter extends ArrayAdapter<RouteDescription>
{
	//region Constructors

	public RoutesAdapter(Context context)
	{
		super(context, 0);
	}

	//endregion

	//region Adapter implementation

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_list_row, null);
		}

		RouteDescription routeDescription = getItem(position);

		TextView title = (TextView) convertView.findViewById(android.R.id.title);
		title.setText(routeDescription.getTitle());

		TextView date = (TextView) convertView.findViewById(R.id.route_date);
		date.setText(TravelAssistanceApplication.USER_DETAILED_TIME_FORMAT.format(routeDescription.getEnd()));

		TextView duration = (TextView) convertView.findViewById(R.id.route_duration);
		String elapsedTime = DateUtils.formatElapsedTime((routeDescription.getEnd().getTime() - routeDescription.getStart().getTime()) / 1000);
		duration.setText(elapsedTime);

		return convertView;
	}


	//endregion
}
