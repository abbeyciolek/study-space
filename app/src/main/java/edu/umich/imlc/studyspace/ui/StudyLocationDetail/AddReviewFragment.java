package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.Review;
import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 3/10/15.
 */
public class AddReviewFragment extends Fragment {

    private static final String TAG = AddReviewFragment.class.getName();

    private ViewSwitcher mViewSwitcher;
    private RatingBar    mQuietnessRating;
    private RatingBar    mWifiRating;
    private RatingBar    mOutletsRating;
    private RatingBar    mTableRating;
    private TextView     mReviewTitle;
    private TextView     mReviewDetails;

    private StudyLocation mStudyLocation;

    public static AddReviewFragment newInstance(StudyLocation studyLocation) {
        Bundle args = new Bundle();
        args.putParcelable("studyLocation", studyLocation);

        AddReviewFragment frag = new AddReviewFragment();
        frag.setArguments(args);

        return frag;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mStudyLocation = args.getParcelable("studyLocation");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_review, container, false);

        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        mQuietnessRating = (RatingBar) view.findViewById(R.id.quietness_rating);
        mWifiRating = (RatingBar) view.findViewById(R.id.wifi_rating);
        mOutletsRating = (RatingBar) view.findViewById(R.id.outlets_rating);
        mTableRating = (RatingBar) view.findViewById(R.id.tables_rating);
        mReviewTitle = (TextView) view.findViewById(R.id.review_title);
        mReviewDetails = (TextView) view.findViewById(R.id.review_details);
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);

        setupActionBar();

        return view;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Review");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        ((StudyLocationDetailActivity) getActivity()).hideHeader();
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_review_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.submit:
                submitReview();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitReview() {
        mViewSwitcher.setDisplayedChild(1);

        mReviewTitle.setError(null);
        mReviewDetails.setError(null);

        final int quietness = Math.round(mQuietnessRating.getRating());
        final int wifi = Math.round(mWifiRating.getRating());
        final int outlets = Math.round(mOutletsRating.getRating());
        final int tables = Math.round(mTableRating.getRating());

        String title = mReviewTitle.getText().toString();
        String details = mReviewDetails.getText().toString();

        if (title.isEmpty()) {
            mReviewTitle.setError("Cannot be blank");
            mViewSwitcher.setDisplayedChild(0);
            return;
        }
        else if (details.isEmpty()) {
            mReviewDetails.setError("Cannot be blank");
            mViewSwitcher.setDisplayedChild(0);
            return;
        }

        Review review = new Review();
        review.setStudyLocation(mStudyLocation);
        review.setUser(ParseUser.getCurrentUser());
        review.setQuietnessRating(quietness);
        review.setWifiRating(wifi);
        review.setOutletsRating(outlets);
        review.setTableRating(tables);
        review.setRating();
        review.setDetail(details);
        review.setTitle(title);
        review.setVotes(0);
        review.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    /*int numReviews = mStudyLocation.getNumReviews();
                    float newQuietness = mStudyLocation.getQuietness() * numReviews;
                    float newWifi = mStudyLocation.getWifiRating() * numReviews;
                    float newTable = mStudyLocation.getTableRating() * numReviews;
                    float newOutlets = mStudyLocation.getOutletsRating() * numReviews;

                    newQuietness += quietness;
                    newWifi += wifi;
                    newTable += tables;
                    newOutlets += outlets;
                    numReviews++;

                    newQuietness /= numReviews;
                    newWifi /= numReviews;
                    newTable /= numReviews;
                    newOutlets /= numReviews;
                    float newRating = (newQuietness + newWifi + newTable + newOutlets) / 4;*/

                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getActivity(),
                                   "Review successfully submitted!",
                                   Toast.LENGTH_LONG).show();
                }
                else {
                    mViewSwitcher.setDisplayedChild(0);
                    Toast.makeText(getActivity(),
                                   "There was an error. Please Try Again.",
                                   Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
