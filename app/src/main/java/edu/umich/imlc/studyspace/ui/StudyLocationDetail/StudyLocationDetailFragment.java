package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.Review;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.CreateAccountFragment;
import edu.umich.imlc.studyspace.ui.SignInFragment;

/**
 * Created by Abbey Ciolek on 2/24/15.
 */
public class StudyLocationDetailFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = StudyLocationDetailFragment.class.getName();

    private Toolbar      mToolbar;
    private TextView     mHeaderTitle;
    private ScrollView   mScrollView;
    private ImageView    mStudyLocationImage;
    private TextView     mAddress;
    private TextView     mHours;
    private ViewSwitcher mRatingViewSwitcher;
    private View         mOverallRating;
    private RatingBar    mQuietnessRating;
    private RatingBar    mWifiRating;
    private RatingBar    mOutletsRating;
    private RatingBar    mTablesRating;
    private LinearLayout mReviewContentArea;
    private ViewSwitcher mViewSwitcher;
    private TextView     mNoReviews;
    private Button       mAddReview;
    private Button       mViewMore;

    private LayoutInflater mLayoutInflater;

    Typeface mIconTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

    private static final float SCROLL_MULTIPLIER    = 0.5f;
    private static final float MAX_TEXT_SCALE_DELTA = .3f;

    private int mScreenWidth;
    private int mHeaderHeight;
    private int mMaxOffset;
    private int   mToolbarAlpha = 0;
    private float mScaleX       = 1 + MAX_TEXT_SCALE_DELTA;
    private float mScaleY       = 1 + MAX_TEXT_SCALE_DELTA;

    private StudyLocation mStudyLocation;
    private String        mParseFileUrl;

    public static StudyLocationDetailFragment newInstance(StudyLocation studyLocation, String url) {
        Bundle args = new Bundle();
        args.putParcelable("studyLocation", studyLocation);
        args.putString("url", url);

        StudyLocationDetailFragment frag = new StudyLocationDetailFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mStudyLocation = args.getParcelable("studyLocation");
        mParseFileUrl = args.getString("url");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_study_location_detail, container, false);

        mScrollView = (ScrollView) rootView.findViewById(R.id.study_location_scroll_view);
        mStudyLocationImage = (ImageView) rootView.findViewById(R.id.study_location_image);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mHeaderTitle = (TextView) getActivity().findViewById(R.id.text_header);
        mRatingViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.rating_view_switcher);
        mOverallRating = rootView.findViewById(R.id.overall_rating);
        Button infoContentArea =
                (Button) rootView.findViewById(R.id.view_hours);
        infoContentArea.setOnClickListener(this);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mAddress.setOnClickListener(this);
        mHours = (TextView) rootView.findViewById(R.id.hours);
        mQuietnessRating = (RatingBar) rootView.findViewById(R.id.quietness_rating);
        mWifiRating = (RatingBar) rootView.findViewById(R.id.wifi_rating);
        mOutletsRating = (RatingBar) rootView.findViewById(R.id.outlets_rating);
        mTablesRating = (RatingBar) rootView.findViewById(R.id.tables_rating);
        mReviewContentArea = (LinearLayout) rootView.findViewById(R.id.review_content_area);
        mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);
        mNoReviews = (TextView) rootView.findViewById(R.id.no_reviews);
        mAddReview = (Button) rootView.findViewById(R.id.add_review);
        mViewMore = (Button) rootView.findViewById(R.id.view_more);
        mAddReview.setOnClickListener(this);
        mViewMore.setOnClickListener(this);

        if (ParseUser.getCurrentUser() == null) {
            mAddReview.setEnabled(false);
            mAddReview.setTextColor(getResources().getColor(R.color.secondary_text_disabled_material_light));
        }

        // calculate height of header
        // calculation is done manually so that header height can have 3:2 aspect ratio
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mHeaderHeight = (int) (mScreenWidth / 1.5);

        // set height of dummy header and correct header to the calculated header height
        ImageView dummyHeader = (ImageView) getActivity().findViewById(R.id.dummy_image_header);
        dummyHeader.setLayoutParams(new RelativeLayout.LayoutParams(mScreenWidth,
                                                                    mHeaderHeight));
        dummyHeader.requestLayout();
        mStudyLocationImage.setLayoutParams(new RelativeLayout.LayoutParams(mScreenWidth,
                                                                            mHeaderHeight));
        mStudyLocationImage.requestLayout();

        // retrieve toolbar height
        final TypedArray attributeArray =
                getActivity().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = attributeArray.getDimensionPixelSize(0, 0);
        attributeArray.recycle();
        mMaxOffset = mHeaderHeight - toolbarHeight;

        // header setup
        setUpHeader();

        // use glide to load header from parse
        ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
        final Uri headerUri = Uri.parse(mParseFileUrl);
        Glide.with(getActivity())
             .load(headerUri)
             .crossFade()
             .placeholder(drawable)
             .diskCacheStrategy(DiskCacheStrategy.NONE)
             .centerCrop()
             .into(mStudyLocationImage);

        // scrollview setup
        mScrollView.getViewTreeObserver()
                   .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                       @Override
                       public void onScrollChanged() {
                           parallaxScroll();
                       }
                   });

        // set address
        mAddress.setText(
                mStudyLocation.getBuildingName() + "\n" + mStudyLocation.getAddress() + "\n" +
                mStudyLocation.getCity() + ", " + mStudyLocation.getState() + " " +
                mStudyLocation.getZipCode());

        // set operating hours
        try {
            JSONObject obj = new JSONObject(mStudyLocation.getHours());
            String curDay = DateTime.now().dayOfWeek().getAsText();
            String openKey = curDay + "Open";
            String closeKey = curDay + "Close";
            LocalTime open = LocalTime.parse(obj.getString(openKey));
            LocalTime close = LocalTime.parse(obj.getString(closeKey));
            if (open.equals(close)) {
                mHours.setText(curDay + ": Open 24 Hours");
            }
            else {
                mHours.setText(curDay + ": " + open.toString("hh:mma") + " to " +
                               close.toString("hh:mma"));
            }
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
        new GetRatingTask().executeOnExecutor(executor);
        new GetTopReviewsTask().executeOnExecutor(executor);

        return rootView;
    }

    private void setUpHeader() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);

        ((StudyLocationDetailActivity) getActivity()).showHeader();

        // toolbar setup
        mToolbar.getBackground().setAlpha(0);

        // title setup
        mHeaderTitle.setPivotX(0);
        mHeaderTitle.setPivotY(0);
        mHeaderTitle.setScaleX(1 + MAX_TEXT_SCALE_DELTA);
        mHeaderTitle.setScaleY(1 + MAX_TEXT_SCALE_DELTA);
        mHeaderTitle.setText(mStudyLocation.getName());
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private void parallaxScroll() {
        int scrollY = mScrollView.getScrollY();
        float scaledOffset = (float) scrollY * SCROLL_MULTIPLIER;

        // changes header translation value by scaled offset
        // which gives the parallax scroll effect
        mStudyLocationImage.setTranslationY(scaledOffset);

        // translate header title up to toolbar
        // the offset can never be bigger than the max offset
        // this prevents the title from being translated above the toolbar
        int trueOffset = Math.min(mMaxOffset, Math.round((float) scrollY));
        mHeaderTitle.setTranslationY(-trueOffset);

        // scale header title text
        // min scale value is 1.0 which done when header is fully collapsed
        // max scale value is 1.3 which is done when the header is fully expanded
        float scale = 1 + Math.min(MAX_TEXT_SCALE_DELTA,
                                   Math.max(0, (mMaxOffset - (float) scrollY) / mMaxOffset));
        mHeaderTitle.setPivotX(0);
        mHeaderTitle.setPivotY(0);
        mHeaderTitle.setScaleX(scale);
        mHeaderTitle.setScaleY(scale);

        // changes toolbar alpha value
        // toolbar is fully transparent when header is fully expanded
        // toolbar is fully opaque when header is fully collapsed
        float percentage = Math.min(1, (scaledOffset / (mHeaderHeight * SCROLL_MULTIPLIER)));
        Drawable c = mToolbar.getBackground();
        c.setAlpha(Math.round(percentage * 255));
        mToolbar.setBackground(c);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address:
                // Creates an Intent that will load a map of San Francisco
                String add = "geo:0,0?q=my+";
                add += mStudyLocation.getAddress() + ", " + mStudyLocation.getCity() + ", " +
                       mStudyLocation.getState() + ", " + mStudyLocation.getZipCode();
                Uri gmmIntentUri = Uri.parse(add);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            case R.id.view_hours:
                StudyLocationInfoFragment.newInstance(mStudyLocation)
                                         .show(getActivity().getSupportFragmentManager(),
                                               "StudyInfo");
                break;
            case R.id.add_review:
                AddReviewFragment frag = AddReviewFragment.newInstance(mStudyLocation);
                frag.setTargetFragment(this, 1);
                getActivity().getSupportFragmentManager()
                             .beginTransaction()
                             .setCustomAnimations(android.R.anim.fade_in,
                                                  android.R.anim.fade_out,
                                                  android.R.anim.fade_in,
                                                  android.R.anim.fade_out)
                             .addToBackStack(null)
                             .replace(R.id.container, frag)
                             .commit();
                break;
            case R.id.view_more:
                StudyLocationReviewsFragment
                        reviewFrag = StudyLocationReviewsFragment.newInstance(mStudyLocation);
                getActivity().getSupportFragmentManager()
                             .beginTransaction()
                             .setCustomAnimations(android.R.anim.fade_in,
                                                  android.R.anim.fade_out,
                                                  android.R.anim.fade_in,
                                                  android.R.anim.fade_out)
                             .addToBackStack(null)
                             .replace(R.id.container, reviewFrag)
                             .commit();
                break;
        }
    }

    private class GetRatingTask extends AsyncTask<Void, Void, StudyLocation> {

        @Override
        protected void onPreExecute() {
            mRatingViewSwitcher.setDisplayedChild(0);
        }

        @Override
        protected StudyLocation doInBackground(Void... params) {
            try {
                ParseQuery<StudyLocation> query = ParseQuery.getQuery(StudyLocation.class);
                StudyLocation studyLocation = query.get(mStudyLocation.getObjectId());
                mStudyLocation = studyLocation;
                return studyLocation;
            }
            catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(StudyLocation studyLocation) {
            if (studyLocation != null) {
                // set rating
                RatingCircleIcon rating = new RatingCircleIcon(studyLocation.getRating(),
                                                               getResources().getColor(R.color.theme_primary),
                                                               mIconTypeface);
                mOverallRating.setBackground(rating);
                mQuietnessRating.setRating(studyLocation.getQuietness());
                mWifiRating.setRating(studyLocation.getWifiRating());
                mOutletsRating.setRating(studyLocation.getOutletsRating());
                mTablesRating.setRating(studyLocation.getTableRating());
                mRatingViewSwitcher.setDisplayedChild(1);
            }
        }
    }

    private class GetTopReviewsTask extends AsyncTask<Void, Void, List<Review>> {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        @Override
        protected void onPreExecute() {
            mViewSwitcher.setDisplayedChild(0);
        }

        @Override
        protected List<Review> doInBackground(Void... params) {
            try {
                ParseQuery<Review> parseQuery = ParseQuery.getQuery(Review.class);
                parseQuery.whereEqualTo(Review.STUDY_LOCATION_COL, mStudyLocation);
                parseQuery.orderByDescending(Review.UPDATED_AT_COL);
                parseQuery.setLimit(3);
                parseQuery.include(Review.USER_COL);
                return parseQuery.find();
            }
            catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews == null || reviews.size() == 0) {
                mNoReviews.setVisibility(View.VISIBLE);
                mViewMore.setEnabled(false);
                mViewMore.setTextColor(getResources().getColor(R.color.secondary_text_disabled_material_light));
            }
            else {
                for (int i = 0; i < reviews.size(); ++i) {
                    initLayout(reviews.get(i), i);
                }
            }
            mViewSwitcher.setDisplayedChild(1);
        }

        private void initLayout(Review review, int index) {
            RelativeLayout reviewItem =
                    (RelativeLayout) mLayoutInflater.inflate(R.layout.review_item, null);
            View avatar = reviewItem.findViewById(R.id.review_avatar);
            TextView name = (TextView) reviewItem.findViewById(R.id.review_name);
            RatingBar rating = (RatingBar) reviewItem.findViewById(R.id.review_rating);
            TextView date = (TextView) reviewItem.findViewById(R.id.review_date);
            TextView title = (TextView) reviewItem.findViewById(R.id.review_title);
            TextView details = (TextView) reviewItem.findViewById(R.id.review_details);
            ImageButton vote_button = (ImageButton) reviewItem.findViewById(R.id.vote);
            vote_button.setVisibility(View.INVISIBLE);

            String userName = review.getUser().getString("Name");
            avatar.setBackground(new CharCircleIcon(userName.charAt(0),
                                                    review.getUser().getUsername(),
                                                    mIconTypeface,
                                                    getResources()));
            name.setText(userName);
            rating.setRating(review.getRating());
            date.setText(formatter.format(review.getUpdatedAt()));
            title.setText(review.getTitle());
            details.setText(review.getDetail());

            mReviewContentArea.addView(reviewItem, index);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.study_locations_detail_menu, menu);

        if (ParseUser.getCurrentUser() != null) {
            menu.findItem(R.id.sign_in).setVisible(false);
            menu.findItem(R.id.create_account).setVisible(false);
            menu.findItem(R.id.log_out).setVisible(true);
        }
        else {
            menu.findItem(R.id.sign_in).setVisible(true);
            menu.findItem(R.id.create_account).setVisible(true);
            menu.findItem(R.id.log_out).setVisible(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (ParseUser.getCurrentUser() != null) {
            menu.findItem(R.id.sign_in).setVisible(false);
            menu.findItem(R.id.create_account).setVisible(false);
            menu.findItem(R.id.log_out).setVisible(true);
            mAddReview.setEnabled(true);
            mAddReview.setTextColor(getResources().getColor(R.color.theme_primary));
        }
        else {
            menu.findItem(R.id.sign_in).setVisible(true);
            menu.findItem(R.id.create_account).setVisible(true);
            menu.findItem(R.id.log_out).setVisible(false);
            mAddReview.setEnabled(false);
            mAddReview.setTextColor(getResources().getColor(R.color.secondary_text_disabled_material_light));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.sign_in:
                SignInFragment frag = new SignInFragment();
                frag.setTargetFragment(this, 1);
                frag.show(getActivity().getSupportFragmentManager(), "Sign In");
                return true;
            case R.id.create_account:
                new CreateAccountFragment().show(getActivity().getSupportFragmentManager(),
                                                 "Create Acct");
                return true;
            case R.id.log_out:
                ParseUser.logOut();
                getActivity().invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mHeaderTitle.setPivotX(0);
        mHeaderTitle.setPivotY(0);
        mHeaderTitle.setScaleX(mScaleX);
        mHeaderTitle.setScaleY(mScaleY);
        mToolbar.getBackground().setAlpha(mToolbarAlpha);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mToolbar.getBackground() instanceof ColorDrawable) {
            mToolbarAlpha = ((ColorDrawable) mToolbar.getBackground()).getColor() >>> 24;
        }
        mScaleX = mHeaderTitle.getScaleX();
        mScaleY = mHeaderTitle.getScaleY();
    }
}
