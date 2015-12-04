package com.jraska.pwmd.travel.ui;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteDescription;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {
  //region Fields

  private final List<RouteDescription> _routes = new ArrayList<>();
  private final DateFormat _endFormat = TravelAssistanceApp.USER_DETAILED_TIME_FORMAT;

  private OnItemClickListener _itemClickListener;

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

  //endregion

  //region Adapter implementation

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View inflated = _layoutInflater.inflate(R.layout.item_route_row, parent, false);
    final RouteViewHolder routeViewHolder = new RouteViewHolder(inflated);

    inflated.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (_itemClickListener != null) {
          _itemClickListener.onItemClick(routeViewHolder._position, v);
        }
      }
    });

    return routeViewHolder;
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    RouteDescription rout = _routes.get(position);

    holder._routeTitle.setText(rout.getTitle());
    holder._routeDate.setText(_endFormat.format(rout.getEnd()));

    long durationSeconds = (rout.getEnd().getTime() - rout.getStart().getTime()) / 1000;
    String timeText = DateUtils.formatElapsedTime(durationSeconds);
    holder._routeDuration.setText(timeText);
  }

  @Override public int getItemCount() {
    return _routes.size();
  }

  //endregion

  //region Methods

  public RouteDescription getItem(int position) {
    return _routes.get(position);
  }

  public void add(RouteDescription route) {
    _routes.add(route);
  }

  public void addAll(Collection<? extends RouteDescription> collection) {
    _routes.addAll(collection);
  }

  public void clear() {
    _routes.clear();
  }

  //endregion

  //region Nested classes

  static class RouteViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.route_title) TextView _routeTitle;
    @Bind(R.id.route_date) TextView _routeDate;
    @Bind(R.id.route_duration) TextView _routeDuration;

    int _position;

    public RouteViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(int position, View itemView);
  }

  //endregion
}
