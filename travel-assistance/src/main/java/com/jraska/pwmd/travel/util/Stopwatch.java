package com.jraska.pwmd.travel.util;


import android.os.SystemClock;

/**
 * Class measuring elapsed time in milliseconds. Can be set to zero state,
 * pause and continued or set to some initial time and start after that.
 */
public final class Stopwatch {
  //region Fields

  private long _elapsedMs;
  private long _lastMs;
  private boolean _running;

  //endregion

  //region Properties

  /**
   * Gets if the StopWatch is running or not.
   *
   * @return True if the StopWatch is in running state, false otherwise.
   */
  public boolean isRunning() {
    return _running;
  }

  /**
   * Gets total elapsed millisecond time of running these StopWatch
   *
   * @return Time which StopWatch spent in running state.
   */
  public long getElapsedMs() {
    if (_running) {
      long systemMs = SystemClock.elapsedRealtime();
      return _elapsedMs + (systemMs - _lastMs);
    }

    return _elapsedMs;
  }

  /**
   * Moves StopWatch to state with already measured time.
   * <p/>
   * Not negative value must be set
   *
   * @throws IllegalArgumentException if the setting value is negative
   */
  public void setElapsedMs(long elapsed) {
    if (elapsed < 0) {
      throw new IllegalArgumentException("Elapsed cannot be negative");
    }

    if (_running) {
      //on running clear elapsed and handle everything with mLastElapsed
      long now = SystemClock.elapsedRealtime();
      _elapsedMs = 0;
      _lastMs = now - elapsed; //this causes now elapsed time to elapsed
    } else {
      _elapsedMs = elapsed;
    }
  }

  //endregion

  //region Methods

  /**
   * Starts count elapsed time.
   */
  public void start() {
    //do nothing if the stopwatch is already running
    if (_running) {
      return;
    }
    _running = true;

    //get the time information as last part for better precision
    _lastMs = SystemClock.elapsedRealtime();
  }

  /**
   * Stops counting elapsed time.
   */
  public void stop() {
    //get the time information as first part for better precision
    long systemMs = SystemClock.elapsedRealtime();

    //if it was already stopped or did not even run, do nothing
    if (!_running) {
      return;
    }

    _elapsedMs += (systemMs - _lastMs);

    _running = false;
  }

  /**
   * Reset the StopWatch to init state with elapsed time of zero.
   */
  public void reset() {
    _running = false;
    _elapsedMs = 0;
    _lastMs = 0;
  }

  /**
   * Resets stopwatch to initial state and start count time from beginning.
   */
  public void restart() {
    stop();
    reset();
    start();
  }

  public static Stopwatch started(){
    Stopwatch stopwatch = new Stopwatch();
    stopwatch.start();
    return stopwatch;
  }

  //endregion

  //region Object implementation

  @Override
  public String toString() {
    return "Stopwatch: " + "Elapsed millis: " + getElapsedMs() + (_running ? " Running" : " Not running");
  }

  //endregion
}
