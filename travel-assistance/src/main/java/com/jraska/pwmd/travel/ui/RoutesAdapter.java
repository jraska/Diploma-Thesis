package com.jraska.pwmd.travel.ui;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.util.ShowContentDescriptionLongClickListener;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>
    implements Iterable<RouteData> {
  //region Fields

  private final List<RouteData> _routes = new ArrayList<>();
  private final DateFormat _endFormat = TravelAssistanceApp.USER_DETAILED_TIME_FORMAT;

  private OnItemClickListener _itemClickListener;
  private OnItemMenuListener _itemMenuListener;

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

  public void setItemDeleteClickListener(OnItemMenuListener itemDeleteListener) {
    _itemMenuListener = itemDeleteListener;
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

    int routeIcon = getRouteIcon(rout);
    holder._routeIcon.setImageResource(routeIcon);
  }

  @Override public int getItemCount() {
    return _routes.size();
  }

  public static int getRouteIcon(RouteData rout) {
    return R.drawable.ic_location_48dp;
  }

  //endregion

  //region Iterable<RouteData> impl

  @Override
  public Iterator<RouteData> iterator() {
    return _routes.iterator();
  }

  //endregion

  //region Methods

  protected void onItemClicked(int position, View itemView) {
    if (_itemClickListener != null) {
      _itemClickListener.onItemClick(position, itemView);
    }
  }

  protected void onRouteDeleteClick(int position, View itemView) {
    if (_itemMenuListener != null) {
      _itemMenuListener.onItemDeleteClick(position, itemView);
    }
  }

  protected void onNavigateRouteClick(int position, View itemView) {
    if (_itemMenuListener != null) {
      _itemMenuListener.onItemNavigateClick(position, itemView);
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


  static class RouteViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @Bind(R.id.route_item_title) TextView _routeTitle;
    @Bind(R.id.route_date) TextView _routeDate;
    @Bind(R.id.route_item_duration) TextView _routeDuration;
    @Bind(R.id.route_item_icon) ImageView _routeIcon;

    private final RoutesAdapter _routesAdapter;

    public RouteViewHolder(RoutesAdapter routesAdapter, View itemView) {
      super(itemView);
      _routesAdapter = routesAdapter;

      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.route_item_container) void onItemClick(View view) {
      _routesAdapter.onItemClicked(getAdapterPosition(), itemView);
    }

    @OnClick(R.id.route_item_more) void showMore(View view) {
      // This is fix because of parsing error fail with some theme issues
      Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopupMenuCompat);
      PopupMenu popup = new PopupMenu(wrapper, view);
      popup.inflate(R.menu.menu_route_popup);

      popup.setOnMenuItemClickListener(this);

      popup.show();
    }

    @OnLongClick({R.id.route_item_more, R.id.route_item_navigate})
    boolean showContentDescription(View v){
      return ShowContentDescriptionLongClickListener.showContentDescription(v);
    }

    protected void deleteRoute() {
      _routesAdapter.onRouteDeleteClick(getAdapterPosition(), itemView);
    }

    @OnClick(R.id.route_item_navigate) void navigateRoute() {
      _routesAdapter.onNavigateRouteClick(getAdapterPosition(), itemView);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
        case R.id.route_item_menu_delete:
          deleteRoute();
          return true;
        default:
          return false;
      }
    }
  }

  public interface OnItemClickListener {
    void onItemClick(int position, View itemView);
  }

  public interface OnItemMenuListener {
    void onItemNavigateClick(int position, View itemView);

    void onItemDeleteClick(int position, View itemView);
  }

  //endregion
}
