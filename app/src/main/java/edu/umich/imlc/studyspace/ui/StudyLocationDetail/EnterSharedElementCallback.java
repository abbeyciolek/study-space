package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.content.Context;
import android.support.v4.app.SharedElementCallback;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import edu.umich.imlc.studyspace.R;

public class EnterSharedElementCallback extends SharedElementCallback {


    private final float mStartTextSize = 48f;
    private final float mEndTextSize = 60f;
    private final int   mStartColor;
    private final int   mEndColor;

    public EnterSharedElementCallback(Context context) {
        mStartColor = context.getResources().getColor(R.color.primary_text_default_material_light);
        mEndColor = context.getResources().getColor(android.R.color.white);
    }

    @Override
    public void onSharedElementStart(List<String> sharedElementNames,
                                     List<View> sharedElements,
                                     List<View> sharedElementSnapshots) {
        TextView textView = (TextView) sharedElements.get(1);
        textView.setTextColor(mStartColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStartTextSize);
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames,
                                   List<View> sharedElements,
                                   List<View> sharedElementSnapshots) {
        TextView textView = (TextView) sharedElements.get(1);
        textView.setTextColor(mEndColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEndTextSize);
    }
}
