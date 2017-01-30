package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by Abbey Ciolek on 3/9/15.
 */
public class RatingCircleIcon extends ShapeDrawable {

    private double  mRating;
    private Paint mTextPaint;

    public RatingCircleIcon(double rating, int color, Typeface typeface) {
        super(new OvalShape());

        this.getPaint().setColor(color);
        mRating = rating;
        mRating *= 100;
        mRating /= 100;
        mTextPaint = new Paint();

        //text paint settings
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);

        // draw text
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        mTextPaint.setTextSize((height / 2) * 1.01f);
        canvas.drawText(String.format("%.1f", mRating),
                        width / 2,
                        height / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2),
                        mTextPaint);
    }
}
