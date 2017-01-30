package edu.umich.imlc.studyspace.ui.StudyLocations.Old;

import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Abbey Ciolek on 2/15/15.
 */
public class StudyLocationsAdapterScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = "OnScrollListener";

    private int mTotalYScrolled;

    private Toolbar            mToolbar;
    private View               mHeader;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView           mHeaderTitle;
    private int                mMaxOffset;

    private static final float SCROLL_MULTIPLIER    = 0.5f;
    private static final float MAX_TEXT_SCALE_DELTA = .3f;

    public StudyLocationsAdapterScrollListener(Toolbar toolbar,
                                               View header,
                                               SwipeRefreshLayout refreshLayout,
                                               TextView headerTitle,
                                               int headerHeight,
                                               int toolbarHeight) {
        mToolbar = toolbar;
        mHeader = header;
        mRefreshLayout = refreshLayout;
        mHeaderTitle = headerTitle;
        mMaxOffset = headerHeight - toolbarHeight;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        //mRefreshLayout.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0);

        mTotalYScrolled += dy;

        float offset = mTotalYScrolled;
        float scaledOffset = offset * SCROLL_MULTIPLIER;

        // changes header translation value by scaled offset
        // which gives the parallax scroll effect
        mHeader.setTranslationY(scaledOffset);

        // translate header title up to toolbar
        // the offset can never be bigger than the max offset
        // this prevents the title from being translated above the toolbar
        int trueOffset = Math.min(mMaxOffset, Math.round(offset));
        mHeaderTitle.setTranslationY(-trueOffset);

        // scale header title text
        // min scale value is 1.0 which done when header is fully collapsed
        // max scale value is 1.3 which is done when the header is fully expanded
        float scale =
                1 + Math.min(MAX_TEXT_SCALE_DELTA, Math.max(0, (mMaxOffset - offset) / mMaxOffset));
        mHeaderTitle.setPivotX(0);
        mHeaderTitle.setPivotY(0);
        mHeaderTitle.setScaleX(scale);
        mHeaderTitle.setScaleY(scale);

        // changes toolbar alpha value
        // toolbar is fully transparent when header is fully expanded
        // toolbar is fully opaque when header is fully collapsed
        float percentage = Math.min(1, (scaledOffset / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
        Drawable c = mToolbar.getBackground();
        c.setAlpha(Math.round(percentage * 255));
        mToolbar.setBackground(c);
    }
}
