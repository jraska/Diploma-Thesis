package com.jraska.pwmd.travel.ui;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jraska.pwmd.travel.R;

public abstract class BaseActivity extends AppCompatActivity {
  //region Fields

  @Nullable
  @Bind(R.id.toolbar) protected Toolbar _toolbar;

  //endregion

  //region Activity overrides

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    onSetContentView();
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    onSetContentView();
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    onSetContentView();
  }

  //endregion

  //region Methods

  protected void onSetContentView() {
    ButterKnife.bind(this);

    if (_toolbar != null) {
      setSupportActionBar(_toolbar);
    }
  }

  //endregion
}
