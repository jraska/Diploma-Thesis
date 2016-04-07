package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.data.RouteIcon;

public class PickRouteIconDialog extends DialogFragment {
  private static final String TAG = PickRouteIconDialog.class.getName();
  private static final String ARG_SELECTED_ID = "selectedId";

  @Bind(R.id.route_icon_pick_recycler) RecyclerView _recyclerView;

  private int getSelectedId() {
    return getArguments().getInt(ARG_SELECTED_ID);
  }

  @Override @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Activity activity = getActivity();
    Dialog dialog = new Dialog(activity);
    dialog.setTitle(R.string.pick_route_icon);

    dialog.setContentView(R.layout.dialog_choose_route_icon);
    ButterKnife.bind(this, dialog);

    LayoutInflater inflater = LayoutInflater.from(activity);
    IconsAdapter iconsAdapter = new IconsAdapter(this, inflater);
    _recyclerView.setAdapter(iconsAdapter);
    _recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));

    return dialog;
  }

  private void onIconClick(RouteIcon routeIcon) {
    RouteRecordActivity recordActivity = (RouteRecordActivity) getActivity();
    recordActivity.onRouteIconPicked(routeIcon);
    dismiss();
  }

  public static void show(RouteRecordActivity routeRecordActivity, RouteIcon selectedIcon) {
    PickRouteIconDialog pickRouteIconDialog = new PickRouteIconDialog();
    Bundle args = new Bundle();
    args.putInt(ARG_SELECTED_ID, selectedIcon.id);
    pickRouteIconDialog.setArguments(args);

    pickRouteIconDialog.show(routeRecordActivity.getSupportFragmentManager(), TAG);
  }

  static class IconHolder extends RecyclerView.ViewHolder {
    private final IconsAdapter _adapter;

    @Bind(R.id.route_icon_pick_view) ImageView _iconView;

    IconHolder(IconsAdapter adapter, View itemView) {
      super(itemView);

      _adapter = adapter;
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.route_icon_pick_view) void onIconClick() {
      _adapter.onIconClick(getAdapterPosition());
    }
  }

  static class IconsAdapter extends RecyclerView.Adapter<IconHolder> {
    private final LayoutInflater _inflater;
    private final PickRouteIconDialog _dialog;

    IconsAdapter(PickRouteIconDialog dialog, LayoutInflater inflater) {
      _dialog = dialog;
      _inflater = inflater;
    }

    @Override
    public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = _inflater.inflate(R.layout.item_route_icon_pick, parent, false);
      return new IconHolder(this, view);
    }

    @Override public void onBindViewHolder(IconHolder holder, int position) {
      RouteIcon routeIcon = RouteIcon.ALL.get(position);
      int iconResId = routeIcon.iconResId;

      holder._iconView.setImageResource(iconResId);
      if (routeIcon.id == _dialog.getSelectedId()) {
        holder._iconView.setSelected(true);
      }
    }

    @Override public int getItemCount() {
      return RouteIcon.ALL.size();
    }

    void onIconClick(int position) {
      _dialog.onIconClick(RouteIcon.ALL.get(position));
    }
  }
}
