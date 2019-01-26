package com.readboy.wearlauncher.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtils {


    public Bitmap getRoundCornerBitmapPercent(Bitmap bitmap, float ratio) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }

        if (ratio <= 0.0f) {
            ratio = 1;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        float rx = width / ratio;
        float ry = height / ratio;
        canvas.drawRoundRect(rectF, rx, ry, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmapPx(Bitmap bitmap, float roundPx) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            roundPx = 14;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    //bitmap转drawable
    public static Drawable Bitmap2Drawable(Bitmap bmp) {
        Drawable drawable = null;
        drawable = new BitmapDrawable(bmp);

        return drawable;
    }

    //drawable转Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bmp = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ?
                        Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public static Bitmap toRoundRect(Bitmap bmp, float rx, float ry) {
        int w = bmp.getWidth(), h = bmp.getHeight();
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);

        int id = canvas.saveLayer(0, 0, w, h, null, Canvas.ALL_SAVE_FLAG);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawRoundRect(new RectF(0, 0, w, h), rx, ry, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, new Matrix(), paint);

        canvas.restoreToCount(id);

        return b;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap convertSQbitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width == height) {
            return bitmap;
        }

        if (width > height) {
            Bitmap cbmp = Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height);
            Bitmap scaleBmp = Bitmap.createScaledBitmap(cbmp, width, width, true);
            return scaleBmp;
        } else {
            Bitmap cbmp = Bitmap.createBitmap(bitmap, 0, (height - width) / 2, width, width);
            Bitmap scaleBmp = Bitmap.createScaledBitmap(cbmp, height, height, true);
            return scaleBmp;
        }
    }
}
