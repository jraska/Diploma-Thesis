package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.console.Console;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.collection.CircularFifoQueue;

import javax.inject.Inject;
import java.util.Collection;
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

  public static final int DEFAULT_BUFFER_SIZE = 2;
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
    Console.writeLine("");
    Console.writeLine("--------------------");

    double coefficient = computeDirectionCoefficient();

    int angle = coefficientToAngle(coefficient);
    // we need to check if the data has ascending or descending x coordinate, due to that,
    // we determine the direction of the line
    boolean ascending = isLatitudeAscending();

    if (!ascending) {
      angle += 180;
    }

    Console.writeLine("Computed angle: " + angle);
    Console.writeLine("--------------------");

    return angle;
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
    Collection<LatLng> data = _dataBuffer;

    int order = 1;
    for (LatLng latLng : _dataBuffer) {
      Console.writeLine("#" + order++ + ": " + latLng._latitude + ", " + latLng._longitude);
    }


    // first pass: read in data, compute xBar and yBar
    double sumX = 0.0;
    double sumY = 0.0;
    for (LatLng pos : data) {
      sumX += pos._latitude;
      sumY += pos._longitude;
    }

    double xBar = sumX / data.size();
    double yBar = sumY / data.size();

    // second pass: compute summary statistics
    double xxBar = 0.0;
    double xyBar = 0.0;

    for (LatLng pos : data) {
      xxBar += (pos._latitude - xBar) * (pos._latitude - xBar);
      xyBar += (pos._latitude - xBar) * (pos._longitude - yBar);
    }

    double coefficient = xyBar / xxBar;
    return coefficient;
  }

  /**
   * @param coefficient Coefficient of line
   * @return Angle in degrees from <-90, 90>
   */
  protected static int coefficientToAngle(double coefficient) {
    return (int) Math.round(Math.toDegrees(Math.atan(coefficient)));
  }

  //endregion
}
