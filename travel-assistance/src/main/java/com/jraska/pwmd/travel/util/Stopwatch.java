package com.jraska.pwmd.travel.util;


import android.os.SystemClock;

/**
 * Class measuring elapsed time in milliseconds. Can be set to zero state,
 * pause and continued or set to some initial time and start after that.
 */
public final class Stopwatch {
  //region Fields

  private long m_elapsedMs = 0;
  private long m_lastMs = 0;
  private boolean m_running = false;

  //endregion

  //region Properties

  /**
   * Gets if the StopWatch is running or not.
   *
   * @return True if the StopWatch is in running state, false otherwise.
   */
  public boolean isRunning() {
    return m_running;
  }

  /**
   * Gets total elapsed millisecond time of running these StopWatch
   *
   * @return Time which StopWatch spent in running state.
   */
  public long getElapsedMs() {
    if (m_running) {
      long systemMs = SystemClock.elapsedRealtime();
      return m_elapsedMs + (systemMs - m_lastMs);
    }

    return m_elapsedMs;
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

    if (m_running) {
      //on running clear elapsed and handle everything with mLastElapsed
      long now = SystemClock.elapsedRealtime();
      m_elapsedMs = 0;
      m_lastMs = now - elapsed; //this causes now elapsed time to elapsed
    } else {
      m_elapsedMs = elapsed;
    }
  }

  //endregion

  //region Methods

  /**
   * Starts count elapsed time.
   */
  public void start() {
    //do nothing if the stopwatch is already running
    if (m_running) {
      return;
    }
    m_running = true;

    //get the time information as last part for better precision
    m_lastMs = SystemClock.elapsedRealtime();
  }

  /**
   * Stops counting elapsed time.
   */
  public void stop() {
    //get the time information as first part for better precision
    long systemMs = SystemClock.elapsedRealtime();

    //if it was already stopped or did not even run, do nothing
    if (!m_running) {
      return;
    }

    m_elapsedMs += (systemMs - m_lastMs);

    m_running = false;
  }

  /**
   * Reset the StopWatch to init state with elapsed time of zero.
   */
  public void reset() {
    m_running = false;
    m_elapsedMs = 0;
    m_lastMs = 0;
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
    return "Stopwatch: " + "Elapsed millis: " + getElapsedMs() + (m_running ? " Running" : " Not running");
  }

  //endregion
}
