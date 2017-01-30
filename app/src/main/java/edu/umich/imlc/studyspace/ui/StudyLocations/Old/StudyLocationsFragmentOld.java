package edu.umich.imlc.studyspace.ui.StudyLocations.Old;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.adapters.StudyLocationAdapterOld;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.StudyLocationDetail.FloatingActionButton;
import edu.umich.imlc.studyspace.ui.StudyLocationDetail.StudyLocationDetailActivity;
import edu.umich.imlc.studyspace.ui.AddStudyLocationFragment;
import edu.umich.imlc.studyspace.ui.CreateAccountFragment;
import edu.umich.imlc.studyspace.ui.StudyLocations.SearchLocationsFragment;
import edu.umich.imlc.studyspace.ui.SignInFragment;
import edu.umich.imlc.studyspace.ui.view.MultiSwipeRefreshLayout;

/**
 * Created by Abbey Ciolek on 2/14/15.
 */
public class StudyLocationsFragmentOld extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
                   StudyLocationAdapterOld.OnItemClickListener {

    private static final String TAG = "StudyLocationsFragmentOld";

    private MultiSwipeRefreshLayout mRefreshLayout;
    private ViewSwitcher            mViewSwitcher;
    private TextView                mLoadMessage;
    private RecyclerView            mRecyclerView;
    private Toolbar                 mToolbar;
    private RelativeLayout          mListHeader;
    private TextView                mHeaderTitle;
    private StudyLocationAdapterOld mAdapter;
    private FloatingActionButton    mAddStudyLocation;

    private static final int SORT_BY_RATING       = 0;
    private static final int SORT_BY_MOST_POPULAR = 1;
    private static final int SORT_BY_NAME         = 2;
    private              int mCurrentSort         = 0;

    private int mToolbarAlpha = 0;

    private int mHeaderHeight;
    private int mScreenWidth;
    private int mToolbarHeight;
    private static final float MAX_TEXT_SCALE_DELTA = .3f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); // this calls onCreateOptionsMenu
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_locations_old, container, false);

        mRefreshLayout = (MultiSwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.study_location_list);
        mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);
        mLoadMessage = (TextView) rootView.findViewById(R.id.load_message);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mListHeader = (RelativeLayout) inflater.inflate(R.layout.study_location_list_header,
                                                        container,
                                                        false);
        mHeaderTitle = (TextView) getActivity().findViewById(R.id.text_header);
        mAddStudyLocation = (FloatingActionButton) rootView.findViewById(R.id.add_location);

        mAddStudyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                             .beginTransaction()
                             .setCustomAnimations(android.R.anim.fade_in,
                                                  android.R.anim.fade_out)
                             .replace(R.id.container, new AddStudyLocationFragment())
                             .commit();
            }
        });

        if (ParseUser.getCurrentUser() == null) {
            mAddStudyLocation.setVisibility(View.INVISIBLE);
        }

        // title setup
        mHeaderTitle.setPivotX(0);
        mHeaderTitle.setPivotY(0);
        mHeaderTitle.setScaleX(1 + MAX_TEXT_SCALE_DELTA);
        mHeaderTitle.setScaleY(1 + MAX_TEXT_SCALE_DELTA);
        mHeaderTitle.setVisibility(View.GONE);

        // action bar setup
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar.getBackground().setAlpha(0);

        // calculate height of header
        // calculation is done manually so that header height can have 3:2 aspect ratio
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mHeaderHeight = (int) (mScreenWidth / 1.5);

        // set height of dummy header to the calculated header height
        ImageView dummyHeader = (ImageView) getActivity().findViewById(R.id.dummy_image_header);
        dummyHeader.setLayoutParams(new RelativeLayout.LayoutParams(mScreenWidth,
                                                                    mHeaderHeight));
        dummyHeader.requestLayout();

        // retrieve toolbar height
        final TypedArray attributeArray =
                getActivity().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mToolbarHeight = attributeArray.getDimensionPixelSize(0, 0);
        attributeArray.recycle();

        //refresh layout setup
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setProgressViewEndTarget(true, mToolbarHeight * 2);
        mRefreshLayout.setSwipeableChildren(R.id.study_location_list, R.id.load_message);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_purple,
                                               android.R.color.holo_green_light,
                                               android.R.color.holo_orange_light,
                                               android.R.color.holo_red_light);

        // recycler view set up
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                                                                           LinearLayoutManager.VERTICAL,
                                                                           false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mViewSwitcher.setDisplayedChild(0);

        setupActionBar();

        return rootView;
    }

    private void setupActionBar() {
       /* ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setHomeAsUpIndicator(null);*/
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        mRefreshLayout.setRefreshing(true);

        ParseQuery<StudyLocation> studyLocationParseQuery = initStudyLocationQuery();

        studyLocationParseQuery.findInBackground(new FindCallback<StudyLocation>() {
            @Override
            public void done(List<StudyLocation> studyLocations, ParseException e) {
                mRefreshLayout.setRefreshing(false);

                if (e == null) {
                    mAdapter = new StudyLocationAdapterOld(getActivity(),
                                                        studyLocations,
                                                        mListHeader,
                                                        mScreenWidth,
                                                        mHeaderHeight,
                                                        StudyLocationsFragmentOld.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setOnScrollListener(new StudyLocationsAdapterScrollListener(
                            mToolbar,
                            mListHeader,
                            mRefreshLayout,
                            mHeaderTitle,
                            mHeaderHeight,
                            mToolbarHeight));
                    mViewSwitcher.setDisplayedChild(1);
                    mHeaderTitle.setVisibility(View.VISIBLE);
                }
                else {
                    Log.e(TAG, "Error Occurred", e);
                    Toast.makeText(getActivity(),
                                   "An Error Occurred. Please Try Again.",
                                   Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private ParseQuery<StudyLocation> initStudyLocationQuery() {
        ParseQuery<StudyLocation> studyLocationQuery = ParseQuery.getQuery(StudyLocation.class);
        studyLocationQuery.whereEqualTo(StudyLocation.UNIVERSITY_COL, "umich");
        setSort(studyLocationQuery);
        return studyLocationQuery;
    }

    private void setSort(ParseQuery<StudyLocation> studyLocationQuery) {
        switch (mCurrentSort) {
            case SORT_BY_RATING:
                studyLocationQuery.orderByDescending(StudyLocation.RATING_COL);
                break;
            case SORT_BY_NAME:
                studyLocationQuery.orderByAscending(StudyLocation.NAME_COL);
                break;
            case SORT_BY_MOST_POPULAR:
                studyLocationQuery.orderByDescending(StudyLocation.NUM_REVIEWS_COL);
                break;
            default:
                throw new IllegalArgumentException("Invalid Sort Option Selected");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.getBackground().setAlpha(mToolbarAlpha);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mToolbar.getBackground() instanceof ColorDrawable) {
            mToolbarAlpha = ((ColorDrawable) mToolbar.getBackground()).getColor() >>> 24;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.study_locations_menu_old, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (ParseUser.getCurrentUser() != null) {
            menu.findItem(R.id.sign_in).setVisible(false);
            menu.findItem(R.id.create_account).setVisible(false);
            menu.findItem(R.id.log_out).setVisible(true);
            mAddStudyLocation.setVisibility(View.VISIBLE);
        }
        else {
            menu.findItem(R.id.sign_in).setVisible(true);
            menu.findItem(R.id.create_account).setVisible(true);
            menu.findItem(R.id.log_out).setVisible(false);
            mAddStudyLocation.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                search();
                return true;
            case R.id.sort_by:
                sortBy();
                return true;
            case R.id.sign_in:
                sign_in();
                return true;
            case R.id.create_account:
                create_acct();
                return true;
            case R.id.log_out:
                ParseUser.logOut();
                getActivity().invalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sign_in() {
        SignInFragment frag = new SignInFragment();
        frag.setTargetFragment(this, 1);
        frag.show(getActivity().getSupportFragmentManager(), "Sign In");
    }

    private void create_acct() {

        new CreateAccountFragment().show(getActivity().getSupportFragmentManager(), "Create Acct");
    }

    private void search() {
        getActivity().getSupportFragmentManager()
                     .beginTransaction()
                     .replace(R.id.container, new SearchLocationsFragment())
                     .commit();
    }

    private void sortBy() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Sort By");
        dialog.setSingleChoiceItems(R.array.sort_by_array,
                                    mCurrentSort,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialogInterface,
                                                int which) {

                                            switch (which) {
                                                case SORT_BY_RATING:
                                                    mCurrentSort = SORT_BY_RATING;
                                                    break;
                                                case SORT_BY_MOST_POPULAR:
                                                    mCurrentSort = SORT_BY_MOST_POPULAR;
                                                    break;
                                                case SORT_BY_NAME:
                                                    mCurrentSort = SORT_BY_NAME;
                                                    break;
                                            }

                                            dialogInterface.dismiss();
                                            refresh();
                                        }
                                    }
                                   );
        dialog.show();
    }

    @Override
    public void onItemClick(int position, View view) {
        /*StudyLocationAdapterOld.StudyLocationViewHolder viewHolder =
                (StudyLocationAdapterOld.StudyLocationViewHolder) mRecyclerView.getChildViewHolder(view);*/

        Intent intent = new Intent(getActivity(), StudyLocationDetailActivity.class);
        intent.putExtra("studyLocation", mAdapter.getStudyLocation(position));
        intent.putExtra("url", mAdapter.getStudyLocation(position).getImage().getUrl());

        /*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                viewHolder.studyLocationAvatar,
                "revealTransition");*/

        /*Pair<View, String> pair1 = Pair.create((View) viewHolder.studyLocationAvatar,
                                               StudyLocationDetailFragment.VIEW_NAME_HEADER_IMAGE);
        Pair<View, String> pair2 = Pair.create((View) viewHolder.studyLocationName,
                                               StudyLocationDetailFragment.VIEW_NAME_HEADER_TITLE);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                pair1,
                pair2);*/

        // getActivity().startActivity(intent, options.toBundle());
        startActivity(intent);
    }
}