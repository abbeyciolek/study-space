package edu.umich.imlc.studyspace.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Abbey Ciolek on 2/15/15.
 */
public class CircleBorderTransformation extends BitmapTransformation {

    private static final String TAG = "CircleTransformation";

    public CircleBorderTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap source, int outWidth, int outHeight) {
        int size = Math.min(outWidth, outHeight);
        float radius = size / 2f;
        int borderWidth = 3;
        int matrixWidth = (source.getWidth() - size) / 2;
        int matrixHeight = (source.getHeight() - size) / 2;

        Bitmap.Config config =
                source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = bitmapPool.get(size, size, config);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, config);
        }

        // draw bitmap to new canvas
        Canvas canvas = new Canvas(bitmap);
        Paint bitmapPaint = new Paint();
        BitmapShader shader =
                new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setTranslate(-matrixWidth, -matrixHeight);
        shader.setLocalMatrix(matrix);
        bitmapPaint.setShader(shader);
        bitmapPaint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius - borderWidth, bitmapPaint);

        // draw border to new canvas
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);
        canvas.drawCircle(radius, radius, radius - borderWidth, borderPaint);

        return bitmap;
    }

    @Override
    public String getId() {
        return "CircleBorderTransformation()";
    }
}
