package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.navigation.DirectionDecisionStrategy;
import com.jraska.pwmd.travel.navigation.Navigator;

import javax.inject.Inject;
import java.util.UUID;

public class NavigationActivity extends BaseActivity {

  //region Fields

  @Bind(R.id.arrow_view) View _arrowView;

  @Inject Navigator _navigator;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    TravelAssistanceApp.getComponent(this).inject(this);

    updateDesiredDirection(_navigator.getLastRequiredDirection());
    _navigator.getEventBus().register(this);

    // TODO: 18/01/16 Test code
    updateDesiredDirection(45);
  }

  @Override
  protected void onDestroy() {
    _navigator.getEventBus().unregister(this);

    super.onDestroy();
  }

  //endregion

  //region Event consuming

  public void onEvent(Navigator.RequiredDirectionEvent changedEvent) {
    updateDesiredDirection(changedEvent._directionDegrees);
  }

  //endregion

  //region Methods

  protected void updateDesiredDirection(int degrees) {
    if (degrees == DirectionDecisionStrategy.UNKNOWN_DIRECTION) {
      _arrowView.setVisibility(View.GONE);
    } else {
      _arrowView.setVisibility(View.VISIBLE);
    }

    // Rotation must be counter clockwise
    _arrowView.setRotation(-degrees);
  }

  public static void startNavigationActivity(Activity fromActivity, UUID routeId) {
    Intent startNavigationIntent = new Intent(fromActivity, NavigationActivity.class);
    startNavigationIntent.putExtra(RouteDetailActivity.ROUTE_ID, routeId);

    fromActivity.startActivity(startNavigationIntent);
  }

  //endregion
}
