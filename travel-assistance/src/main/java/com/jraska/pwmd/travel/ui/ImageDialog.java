package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.media.PicturesManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.UUID;

public class ImageDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = ImageDialog.class.getSimpleName();
  private static final String ARG_IMAGE_ID = "imageId";
  private static final String ARG_CAPTION = "message";
  private static final String ARG_DURATION = "duration";

  //endregion

  //region Fields

  @Inject PicturesManager _picturesManager;
  @Inject LayoutInflater _inflater;

  @Bind(R.id.dialog_image_img) ImageView _imageView;
  @Bind(R.id.dialog_image_caption) TextView _caption;

  private final ImageLoadingListener _imageLoadedListener = new ImageLoadingListener() {
    @Override public void onLoadingStarted(String imageUri, View view) {
    }

    @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    }

    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
      _caption.setWidth(loadedImage.getWidth());
      _caption.setVisibility(View.VISIBLE);
    }

    @Override public void onLoadingCancelled(String imageUri, View view) {
    }
  };

  //endregion

  //region Constructors

  public static ImageDialog newInstance(UUID imageId, String message, long displayDuration) {
    ArgumentCheck.notNull(imageId);

    if (displayDuration < 500) {
      Timber.w("Displaying just blinking dialog for %d seconds is pointless.", displayDuration);
    }

    Bundle args = new Bundle();
    args.putSerializable(ARG_IMAGE_ID, imageId);
    args.putString(ARG_CAPTION, message);
    args.putLong(ARG_DURATION, displayDuration);

    ImageDialog fragment = new ImageDialog();
    fragment.setArguments(args);
    return fragment;
  }

  public ImageDialog() {
  }

  //endregion

  //region Properties

  UUID getImageId() {
    return (UUID) getArguments().getSerializable(ARG_IMAGE_ID);
  }

  long getDuration() {
    return getArguments().getLong(ARG_DURATION);
  }

  String getCaption() {
    return getArguments().getString(ARG_CAPTION);
  }

  //endregion

  //region FragmentDialog overrides

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TravelAssistanceApp.getComponent(getActivity()).inject(this);
  }

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View inflated = _inflater.inflate(R.layout.dialog_image, null);
    ButterKnife.bind(this, inflated);

    _caption.setText(getCaption());
    _caption.setVisibility(View.GONE);

    setMaxDimensions(_imageView);

    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
        .considerExifParams(true)
        .build();

    Uri pictureUri = _picturesManager.createPictureUri(getImageId());
    ImageLoader.getInstance().displayImage(pictureUri.toString(), _imageView,
        imageOptions, _imageLoadedListener);

    Dialog dialog = new Dialog(getActivity());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(inflated);

    return dialog;
  }

  //endregion

  //region Methods

  @OnClick(R.id.dialog_image_img)
  public void onImageClicked() {
    dismiss();
  }

  public void setMaxDimensions(ImageView imageView) {
    Display display = getActivity().getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;
    imageView.setMaxWidth((width * 3) / 4);
    imageView.setMaxHeight((height * 4) / 5);
  }

  //endregion
}
