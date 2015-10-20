package com.jraska.pwmd.travel.help;

import com.jraska.common.ArgumentCheck;
import com.jraska.core.BaseApp;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;

import java.util.Date;

public class LostMessageTextBuilder {
  //region Constants

  public static final int DEFAULT_ZOOM = 15;

  //endregion

  //region Fields

  private double _latitude;
  private double _longitude;
  private float _accuracy;
  private Date _time;
  private int _zoom = DEFAULT_ZOOM;
  private String _smsTextTemplate;

  //endregion

  //region Properties

  public double getLatitude() {
    return _latitude;
  }

  public LostMessageTextBuilder setLatitude(double latitude) {
    _latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return _longitude;
  }

  public LostMessageTextBuilder setLongitude(double longitude) {
    _longitude = longitude;
    return this;
  }

  public float getAccuracy() {
    return _accuracy;
  }

  public LostMessageTextBuilder setAccuracy(float accuracy) {
    _accuracy = accuracy;
    return this;
  }

  public Date getTime() {
    return _time;
  }

  public LostMessageTextBuilder setTime(Date time) {
    _time = time;
    return this;
  }

  public int getZoom() {
    return _zoom;
  }

  public LostMessageTextBuilder setZoom(int zoom) {
    _zoom = zoom;
    return this;
  }

  public String getSmsTextTemplate() {
    return _smsTextTemplate;
  }

  public LostMessageTextBuilder setSmsTextTemplate(String smsTextTemplate) {
    ArgumentCheck.notNull(smsTextTemplate, "smsTextTemplate");

    _smsTextTemplate = smsTextTemplate;
    return this;
  }

  protected String getSmsTextTemplateInternal() {
    if (_smsTextTemplate == null) {
      return getString(R.string.lost_sms_message_template);
    }

    return _smsTextTemplate;
  }

  //endregion

  //region Methods

  protected String formatAppDate(Date date) {
    return TravelAssistanceApp.USER_DETAILED_TIME_FORMAT.format(date);
  }

  protected String getString(int resId) {
    return BaseApp.getCurrent().getString(resId);
  }

  public LostMessageTextBuilder setFromPosition(Position p) {
    ArgumentCheck.notNull(p, "p");

    return setAccuracy(p.accuracy).setLatitude(p.latitude).setLongitude(p.longitude).setTime(new Date(p.time));
  }

  public String buildGoogleMapsUrl() {
    return "http://maps.google.com/maps?" + "&z=" + _zoom + "&q=" + _latitude + "+" + _longitude;
  }

  public String buildSmsText() {
    String dateText = _time == null ? getString(R.string.unknown_position) : formatAppDate(_time);
    return String.format(getSmsTextTemplateInternal(), buildGoogleMapsUrl(), _accuracy, dateText);
  }

  //endregion
}
