package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.adapters.ReviewListAdapter;
import edu.umich.imlc.studyspace.model.Review;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.CreateAccountFragment;
import edu.umich.imlc.studyspace.ui.SignInFragment;

/**
 * Created by Abbey Ciolek on 3/10/15.
 */
public class StudyLocationReviewsFragment extends Fragment
        implements ReviewListAdapter.VoteClickListener {

    private static final String TAG = StudyLocationReviewsFragment.class.getName();

    private StudyLocation mStudyLocation;

    private ViewSwitcher         mViewSwitcher;
    private RecyclerView         mRecyclerView;
    private FloatingActionButton mFab;

    public static StudyLocationReviewsFragment newInstance(StudyLocation studyLocation) {
        Bundle args = new Bundle();
        args.putParcelable("studyLocation", studyLocation);

        StudyLocationReviewsFragment frag = new StudyLocationReviewsFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mStudyLocation = getArguments().getParcelable("studyLocation");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_study_location_reviews, container, false);
        mViewSwitcher = (ViewSwitcher) root.findViewById(R.id.view_switcher);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.review_list);
        mFab = (FloatingActionButton) root.findViewById(R.id.add_review);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReviewFragment frag = AddReviewFragment.newInstance(mStudyLocation);
                getActivity().getSupportFragmentManager()
                             .beginTransaction()
                             .setCustomAnimations(android.R.anim.fade_in,
                                                  android.R.anim.fade_out,
                                                  android.R.anim.fade_in,
                                                  android.R.anim.fade_out)
                             .addToBackStack(null)
                             .replace(R.id.container, frag)
                             .commit();
            }
        });

        if (ParseUser.getCurrentUser() == null) {
            mFab.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager llm =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);
        query.whereEqualTo(Review.STUDY_LOCATION_COL, mStudyLocation);
        query.orderByDescending(Review.UPDATED_AT_COL);
        query.include(Review.USER_COL);
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviews, ParseException e) {
                ReviewListAdapter adapter =
                        new ReviewListAdapter(reviews,
                                              StudyLocationReviewsFragment.this.getActivity(),
                                              StudyLocationReviewsFragment.this);
                mRecyclerView.setAdapter(adapter);
                mViewSwitcher.setDisplayedChild(1);
            }
        });

        setUpToolBar();

        return root;
    }

    private void setUpToolBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mStudyLocation.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(255);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((StudyLocationDetailActivity) getActivity()).hideHeader();
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
            mFab.setVisibility(View.VISIBLE);
        }
        else {
            menu.findItem(R.id.sign_in).setVisible(true);
            menu.findItem(R.id.create_account).setVisible(true);
            menu.findItem(R.id.log_out).setVisible(false);
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
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
    public void onVoteClick(final Review review, View view) {
        Log.v("onClick", "vote");

        CharSequence options[] = new CharSequence[]{"Helpful", "Unhelpful"};

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Mark review as");
        dialog.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int cur_votes = review.getVotes();

                if (which == 0) { //helpful
                    review.setVotes(cur_votes + 1);
                }
                if (which == 1) { //not helpful
                    review.setVotes(cur_votes - 1);
                }

                review.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(),
                                           "Your feedback has been submitted!",
                                           Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
