package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;

import edu.umich.imlc.studyspace.R;

/**
 * Created by Abbey Ciolek on 3/9/15.
 */
public class CharCircleIcon extends ShapeDrawable {

    private char  mCharacter;
    private Paint mTextPaint;

    private static TypedArray sColors;
    private static int        sDefaultColor;


    public CharCircleIcon(char character, String email, Typeface typeface, Resources res) {
        super(new OvalShape());

        if (sColors == null) {
            sColors = res.obtainTypedArray(R.array.letter_tile_colors);
            sDefaultColor = res.getColor(R.color.theme_primary);
        }

        this.getPaint().setColor(pickColor(email));
        mCharacter = character;
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
        mTextPaint.setTextSize((height / 2) * 1.2f);
        canvas.drawText(String.valueOf(mCharacter),
                        width / 2,
                        height / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2),
                        mTextPaint);
    }

    /**
     * Returns a deterministic color based on the provided contact identifier string.
     */
    private int pickColor(final String identifier) {
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
}
