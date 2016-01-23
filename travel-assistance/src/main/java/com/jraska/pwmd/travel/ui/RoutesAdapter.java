package com.jraska.pwmd.travel.ui;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {
  //region Fields

  private final List<RouteData> _routes = new ArrayList<>();
  private final DateFormat _endFormat = TravelAssistanceApp.USER_DETAILED_TIME_FORMAT;

  private OnItemClickListener _itemClickListener;
  private OnItemDeleteListener _itemDeleteListener;

  private final LayoutInflater _layoutInflater;

  //endregion

  //region Constructors

  @Inject
  public RoutesAdapter(LayoutInflater inflater) {
    ArgumentCheck.notNull(inflater, "inflater");

    _layoutInflater = inflater;
  }

  //endregion

  //region Properties

  public void setItemClickListener(OnItemClickListener itemClickListener) {
    _itemClickListener = itemClickListener;
  }

  public void setItemDeleteClickListener(OnItemDeleteListener itemDeleteListener) {
    _itemDeleteListener = itemDeleteListener;
  }

  //endregion

  //region Adapter implementation

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View inflated = _layoutInflater.inflate(R.layout.item_route_row, parent, false);
    final RouteViewHolder routeViewHolder = new RouteViewHolder(this, inflated);

    return routeViewHolder;
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    RouteData rout = _routes.get(position);

    holder._routeTitle.setText(rout.getTitle());
    holder._routeDate.setText(_endFormat.format(rout.getEnd()));

    long durationSeconds = (rout.getEnd().getTime() - rout.getStart().getTime()) / 1000;
    String timeText = DateUtils.formatElapsedTime(durationSeconds);
    holder._routeDuration.setText(timeText);

    holder._position = position;
  }

  @Override public int getItemCount() {
    return _routes.size();
  }

  //endregion

  //region Methods

  protected void onItemClicked(int position, View view) {
    if (_itemClickListener != null) {
      _itemClickListener.onItemClick(position, view);
    }
  }

  protected void onRouteDeleteClick(int position, View v) {
    if (_itemDeleteListener != null) {
      _itemDeleteListener.onItemDelete(position, v);
    }
  }

  public RouteData getItem(int position) {
    return _routes.get(position);
  }

  public void add(RouteData route) {
    _routes.add(route);
  }

  public void addAll(Collection<? extends RouteData> collection) {
    _routes.addAll(collection);
  }

  public void clear() {
    _routes.clear();
  }

  public void remove(RouteData deletedRoute) {
    for (RouteData route : _routes) {
      if (route.getId() == deletedRoute.getId()) {
        _routes.remove(deletedRoute);
        return;
      }
    }
  }

  //endregion

  //region Nested classes

  static class RouteViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.route_title) TextView _routeTitle;
    @Bind(R.id.route_date) TextView _routeDate;
    @Bind(R.id.route_duration) TextView _routeDuration;

    private final RoutesAdapter _routesAdapter;

    int _position;

    public RouteViewHolder(RoutesAdapter routesAdapter, View itemView) {
      super(itemView);
      _routesAdapter = routesAdapter;

      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.route_item_container) void onItemClick(View v) {
      _routesAdapter.onItemClicked(_position, v);
    }

    @OnClick(R.id.route_delete) void deleteRoute(View v) {
      _routesAdapter.onRouteDeleteClick(_position, v);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(int position, View itemView);
  }

  public interface OnItemDeleteListener {
    void onItemDelete(int position, View itemView);
  }

  //endregion
}
