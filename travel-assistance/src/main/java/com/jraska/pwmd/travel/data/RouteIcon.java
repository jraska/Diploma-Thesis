package com.jraska.pwmd.travel.data;

import com.jraska.pwmd.travel.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteIcon {
  private static final int DEFAULT_ID = 0;
  private static final int BANK_ID = 1;
  private static final int APARTMENT_ID = 2;
  private static final int SCHOOL_ID = 3;
  private static final int TRAIN_ID = 4;
  private static final int SPORT_ID = 5;
  private static final int SUPPORT_ID = 6;
  private static final int SHOPPING_ID = 7;
  private static final int SHOPPING_BAG_ID = 8;

  public static final RouteIcon DEFAULT = new RouteIcon(DEFAULT_ID, R.drawable.ic_location_48dp);
  private static final RouteIcon BANK = new RouteIcon(BANK_ID, R.drawable.ic_bank_48dp);
  private static final RouteIcon APARTMENT = new RouteIcon(APARTMENT_ID, R.drawable.ic_apartment_48dp);
  private static final RouteIcon SCHOOL = new RouteIcon(SCHOOL_ID, R.drawable.ic_school_48dp);
  private static final RouteIcon TRAIN = new RouteIcon(TRAIN_ID, R.drawable.ic_train_48dp);
  private static final RouteIcon SPORT = new RouteIcon(SPORT_ID, R.drawable.ic_sport_48dp);
  private static final RouteIcon SUPPORT = new RouteIcon(SUPPORT_ID, R.drawable.ic_support_48dp);
  private static final RouteIcon SHOPPING = new RouteIcon(SHOPPING_ID, R.drawable.ic_shopping_48dp);
  private static final RouteIcon SHOPPING_BAG = new RouteIcon(SHOPPING_BAG_ID, R.drawable.ic_shopping_bag_48dp);

  public static final List<RouteIcon> ALL;

  static {
    List<RouteIcon> icons = new ArrayList<>();

    icons.add(DEFAULT);
    icons.add(SHOPPING);
    icons.add(BANK);
    icons.add(APARTMENT);
    icons.add(SCHOOL);
    icons.add(TRAIN);
    icons.add(SPORT);
    icons.add(SUPPORT);
    icons.add(SHOPPING_BAG);

    ALL = Collections.unmodifiableList(icons);
  }

  public final int id;
  public final int iconResId;

  RouteIcon(int id, int iconResId) {
    this.id = id;
    this.iconResId = iconResId;
  }

  public static RouteIcon fromId(int id) {
    switch (id) {
      case BANK_ID:
        return BANK;
      case APARTMENT_ID:
        return APARTMENT;
      case SCHOOL_ID:
        return SCHOOL;
      case TRAIN_ID:
        return TRAIN;
      case SPORT_ID:
        return SPORT;
      case SUPPORT_ID:
        return SUPPORT;
      case SHOPPING_ID:
        return SHOPPING;
      case SHOPPING_BAG_ID:
        return SHOPPING_BAG;
      default:
        return DEFAULT;
    }
  }
}
