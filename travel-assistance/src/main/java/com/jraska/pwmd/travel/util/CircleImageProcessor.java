package com.jraska.pwmd.travel.util;

import android.graphics.*;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class CircleImageProcessor implements BitmapProcessor {
  private final int _size;

  public CircleImageProcessor(int size) {
    _size = size;
  }

  @Override public Bitmap process(Bitmap bitmap) {
    return getCroppedBitmap(bitmap);
  }

  private Bitmap getCroppedBitmap(Bitmap loadedImage) {
    int size = _size;
    Bitmap bmp = Bitmap.createScaledBitmap(loadedImage, size, size, false);

    Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);

    paint.setColor(color);

    int radius = Math.min(bmp.getHeight(), bmp.getWidth()) / 2;
    canvas.drawCircle(bmp.getWidth() / 2, bmp.getHeight() / 2, radius, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bmp, rect, rect, paint);
    return output;
  }
}
