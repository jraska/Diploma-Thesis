package com.jraska.pwmd.travel.help;

import android.content.Context;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;

import java.util.Date;
import java.util.Locale;

public class LostMessageTextBuilder {
  //region Constants

  public static final int DEFAULT_ZOOM = 15;

  //endregion

  //region Fields

  private final Context _context;

  private double _latitude;
  private double _longitude;
  private float _accuracy;
  private Date _time;
  private int _zoom = DEFAULT_ZOOM;
  private String _smsTextTemplate;

  //endregion

  //region Constructors

  public LostMessageTextBuilder(@NonNull Context context) {
    _context = context;
  }

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
    return _context.getString(resId);
  }

  public LostMessageTextBuilder setFromPosition(Position position) {
    ArgumentCheck.notNull(position, "position");

    return setAccuracy(position.accuracy).setLatitude(position.latLng._latitude)
        .setLongitude(position.latLng._longitude).setTime(new Date(position.time));
  }

  public String buildGoogleMapsUrl() {
    String url = String.format(Locale.US, "http://maps.google.com/maps?&z=%d&q=%.6f+%.6f",
        _zoom, _latitude, _longitude);

    return url;
  }

  public String buildSmsText() {
    String dateText = _time == null ? getString(R.string.unknown_position) : formatAppDate(_time);
    return String.format(getSmsTextTemplateInternal(), buildGoogleMapsUrl(), _accuracy, dateText);
  }

  //endregion
}
