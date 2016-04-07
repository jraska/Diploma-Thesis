package com.jraska.pwmd.travel.data.migrations;

import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteData_Table;
import com.jraska.pwmd.travel.data.TravelDatabase;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(version = 2, database = TravelDatabase.class)
public class AddRouteIconColumnMigration extends AlterTableMigration<RouteData> {
  public AddRouteIconColumnMigration() {
    super(RouteData.class);
  }

  @Override public void onPreMigrate() {
    super.onPreMigrate();

    addColumn(SQLiteType.INTEGER, RouteData_Table._iconId.getCursorKey());
  }
}
