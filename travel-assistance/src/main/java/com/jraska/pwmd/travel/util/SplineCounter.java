package com.jraska.pwmd.travel.util;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LatLng;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

@PerApp
public class SplineCounter {

  private static final int MIN_REQUIRED_POINTS = 5;

  @Inject
  public SplineCounter() {
  }

  public LatLng[] calculateSpline(List<LatLng> latLngs) {
    Stopwatch stopwatch = Stopwatch.started();
    LatLng[] result = calculateSplineInternal(latLngs);
    stopwatch.stop();
    Timber.d("Making spline for %d points took %d ms", latLngs.size(), stopwatch.getElapsedMs());

    return result;
  }

  LatLng[] calculateSplineInternal(List<LatLng> latLngs) {
    // Nonsense to do splines with just few points
    if (latLngs.size() < MIN_REQUIRED_POINTS) {
      LatLng[] toReturn = new LatLng[latLngs.size()];
      for (int i = 0; i < latLngs.size(); i++) {
        toReturn[i] = latLngs.get(i);
      }

      return toReturn;
    }

    double[] lats = new double[latLngs.size()];
    double[] lons = new double[latLngs.size()];

    for (int i = 0, size = latLngs.size(); i < size; i++) {
      LatLng latLng = latLngs.get(i);
      lats[i] = latLng._latitude;
      lons[i] = latLng._longitude;
    }

    double t, ax, ay, bx, by, cx, cy, dx, dy, lat, lon;
    LatLng points[] = new LatLng[lats.length - 2]; //start and end are added
    points[0] = latLngs.get(0);

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

    points[points.length - 1] = latLngs.get(latLngs.size() - 1);

    return points;
  }
}
