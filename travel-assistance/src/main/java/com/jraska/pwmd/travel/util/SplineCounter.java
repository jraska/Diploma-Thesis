package com.jraska.pwmd.travel.util;

import com.google.android.gms.maps.model.LatLng;
import com.jraska.dagger.PerApp;

import javax.inject.Inject;
import java.util.List;

import static com.jraska.pwmd.travel.navigation.Navigator.toGoogleLatLng;

@PerApp
public class SplineCounter {

  @Inject
  public SplineCounter() {
  }

  public LatLng[] calculateSpline(List<com.jraska.pwmd.core.gps.LatLng> latLngs) {
    // Nonsense to do splines with just few points
    if (latLngs.size() < 5) {
      LatLng[] toReturn = new LatLng[latLngs.size()];
      for (int i = 0; i < latLngs.size(); i++) {
        toReturn[i] = toGoogleLatLng(latLngs.get(i));
      }

      return toReturn;
    }

    double[] lats = new double[latLngs.size()];
    double[] lons = new double[latLngs.size()];

    for (int i = 0, size = latLngs.size(); i < size; i++) {
      com.jraska.pwmd.core.gps.LatLng latLng = latLngs.get(i);
      lats[i] = latLng._latitude;
      lons[i] = latLng._longitude;
    }

    double t, ax, ay, bx, by, cx, cy, dx, dy, lat, lon;
    LatLng points[] = new LatLng[lats.length - 2]; //start and end are added
    points[0] = toGoogleLatLng(latLngs.get(0));

    for (int i = 2, pointIndex = 1; i < lats.length - 2; i++, pointIndex++) {
      for (t = 0; t < 1; t += 0.2) {
        ax = (-lats[i - 2] + 3 * lats[i - 1] - 3 * lats[i] + lats[i + 1]) / 6;
        ay = (-lons[i - 2] + 3 * lons[i - 1] - 3 * lons[i] + lons[i + 1]) / 6;
        bx = (lats[i - 2] - 2 * lats[i - 1] + lats[i]) / 2;
        by = (lons[i - 2] - 2 * lons[i - 1] + lons[i]) / 2;
        cx = (-lats[i - 2] + lats[i]) / 2;
        cy = (-lons[i - 2] + lons[i]) / 2;
        dx = (lats[i - 2] + 4 * lats[i - 1] + lats[i]) / 6;
        dy = (lons[i - 2] + 4 * lons[i - 1] + lons[i]) / 6;
        lat = ax * Math.pow(t + 0.1, 3) + bx * Math.pow(t + 0.1, 2) + cx * (t + 0.1) + dx;
        lon = ay * Math.pow(t + 0.1, 3) + by * Math.pow(t + 0.1, 2) + cy * (t + 0.1) + dy;
        points[pointIndex] = new LatLng(lat, lon);
      }
    }

    points[points.length - 1] = toGoogleLatLng(latLngs.get(latLngs.size() - 1));

    return points;
  }
}
