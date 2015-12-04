package com.jraska.pwmd.travel;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {
  //region Activity overrides

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
    }
  }

  //endregion
}
