package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.collection.CircularFifoQueue;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * Class responsible of determining direction based on coordinates obtained.
 * <p/>
 * It takes n points provided and it interspaces the line through them.
 * <p/>
 * Direction is in degrees int interval <0, 360) and if it is not possible
 * to determine the direction, it uses {@link #UNKNOWN_DIRECTION}
 */
public class DirectionDecisionStrategy {
  //region Constants

  public static final int DEFAULT_BUFFER_SIZE = 4;
  public static final int UNKNOWN_DIRECTION = -1;

  //endregion

  //region Fields

  private final CircularFifoQueue<LatLng> _dataBuffer;

  //endregion

  //region Constructors

  @Inject
  public DirectionDecisionStrategy() {
    this(DEFAULT_BUFFER_SIZE);
  }

  public DirectionDecisionStrategy(int bufferSize) {
    if (bufferSize < 2) {
      String errorMessage = "Smallest size can be 2 to determine direction. Not " + bufferSize;
      throw new IllegalArgumentException(errorMessage);
    }

    _dataBuffer = new CircularFifoQueue<>(bufferSize);
  }

  //endregion

  //region Methods

  public void addPoint(@NonNull LatLng latLng) {
    _dataBuffer.add(latLng);
  }

  public int getDirection(@NonNull LatLng latLng) {
    addPoint(latLng);
    return getDirection();
  }

  public int getDirection() {
    //  we need at least two points
    if (_dataBuffer.size() <= 1) {
      return UNKNOWN_DIRECTION;
    }

    return linearRegression();
  }

  protected int linearRegression() {
    double coefficient = computeDirectionCoefficient();

    int angle = coefficientToAngle(coefficient);
    // we need to check if the data has ascending or descending x coordinate, due to that,
    // we determine the direction of the line
    boolean ascending = isLatitudeAscending();
    if (ascending) {
      return angle;
    } else {
      return 180 + angle;
    }
  }

  protected boolean isLatitudeAscending() {
    if (_dataBuffer.size() < 2) {
      return true; //just default to return something
    }

    int ascendingCount = 0;
    int descendingCount = 0;

    Iterator<LatLng> dataIterator = _dataBuffer.iterator();
    LatLng previous = dataIterator.next();
    while (dataIterator.hasNext()) {
      LatLng current = dataIterator.next();
      if (current._latitude > previous._latitude) {
        ascendingCount++;
      } else {
        descendingCount++;
      }
      previous = current;
    }

    if (ascendingCount >= descendingCount) {
      return true;
    } else {
      return false;
    }
  }

  protected double computeDirectionCoefficient() {
    Iterable<LatLng> data = _dataBuffer;

    // first pass: read in data, compute xbar and ybar
    double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
    for (LatLng pos : data) {
      sumx += pos._latitude;
      sumx2 += pos._latitude * pos._latitude;
      sumy += pos._longitude;
    }

    double xbar = sumx / _dataBuffer.size();
    double ybar = sumy / _dataBuffer.size();

    // second pass: compute summary statistics
    double xxbar = 0.0, yybar = 0.0, xybar = 0.0;

    for (LatLng pos : _dataBuffer) {
      xxbar += (pos._latitude - xbar) * (pos._latitude - xbar);
      yybar += (pos._longitude - ybar) * (pos._longitude - ybar);
      xybar += (pos._latitude - xbar) * (pos._longitude - ybar);
    }

    double coefficient = xybar / xxbar;
    return coefficient;
  }

  /**
   * @param coefficient Coeffficient of line
   * @return Angle in degrees from <-90, 90>
   */
  protected static int coefficientToAngle(double coefficient) {
    return (int) Math.round(Math.toDegrees(Math.atan(coefficient)));
  }

  //endregion
}
