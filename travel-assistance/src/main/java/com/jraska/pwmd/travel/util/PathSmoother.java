package com.jraska.pwmd.travel.util;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LatLng;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PerApp
public class PathSmoother {

  private static final int MIN_REQUIRED_POINTS = 5;

  @Inject
  public PathSmoother() {
  }

  public List<LatLng> smoothPath(List<LatLng> latLngs) {
    // Nonsense to do B-spline
    if (latLngs.size() < MIN_REQUIRED_POINTS) {
      return new ArrayList<>(latLngs);
    }

    double[] lats = new double[latLngs.size()];
    double[] lons = new double[latLngs.size()];

    for (int i = 0, size = latLngs.size(); i < size; i++) {
      LatLng latLng = latLngs.get(i);
      lats[i] = latLng._latitude;
      lons[i] = latLng._longitude;
    }

    double t, ax, ay, bx, by, cx, cy, dx, dy, lat, lon;
    List<LatLng> points = new ArrayList<>();

    // Add first point to have exact start
    points.add(latLngs.get(0));

    for (int i = 2, pointIndex = 1, searchLength = lats.length - 1; i < searchLength; i++, pointIndex++) {
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
        points.add(new LatLng(lat, lon));
      }
    }

    // add last point as it is not included
    points.add(latLngs.get(latLngs.size() - 1));

    return points;
  }
}
