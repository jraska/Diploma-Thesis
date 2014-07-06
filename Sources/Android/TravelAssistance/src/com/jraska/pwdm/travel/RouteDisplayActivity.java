package com.jraska.pwdm.travel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.persistence.ITravelDataPersistenceService;

import java.util.List;
import java.util.UUID;

public class RouteDisplayActivity extends Activity
{
	//region Constants

	public static final String ROUTE_ID = "RouteId";
	protected static final int ROUTE_WIDTH = 10;

	//endregion

	//region Fields

	private GoogleMap mMapView;

	//endregion

	//region Properties

	protected GoogleMap getMap()
	{
		if (mMapView == null)
		{
			mMapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}

		return mMapView;
	}

	protected ITravelDataPersistenceService getTravelDataPersistenceService()
	{
		return ITravelDataPersistenceService.Stub.asInterface();
	}

	//endregion

	//region Activity overrides

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_display);

		loadAndShowRoute();
	}

	//endregion

	//region Methods

	protected void loadAndShowRoute()
	{
		UUID routeId = (UUID) getIntent().getSerializableExtra(ROUTE_ID);

		RouteData routeData = getTravelDataPersistenceService().getRouteData(routeId);

		setTitle(routeData.getTitle());
		displayOnMap(routeData);
	}

	protected void displayOnMap(RouteData routeData)
	{
		GoogleMap map = getMap();
		PolylineOptions polylineOptions = new PolylineOptions().width(ROUTE_WIDTH).color(Color.BLUE);

		List<Position> points = routeData.getPath().getPoints();

		if (points.size() == 0)
		{
			throw new IllegalStateException("No points to display");
		}

		for (Position position : points)
		{
			polylineOptions.add(toGoogleLatLng(position));
		}

		Polyline line = map.addPolyline(polylineOptions);

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(toGoogleLatLng(points.get(0)), 15);
		getMap().animateCamera(cameraUpdate);
	}

	protected LatLng toGoogleLatLng(com.jraska.pwdm.core.gps.LatLng latLng)
	{
		return new LatLng(latLng.latitude, latLng.longitude);
	}

	//endregion
}
