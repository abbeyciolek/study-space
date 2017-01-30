package edu.umich.imlc.studyspace.ui.StudyLocations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.adapters.StudyLocationAdapter;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.StudyLocationDetail.StudyLocationDetailActivity;

/**
 * Created by Abbey Ciolek on 2/18/15.
 */
public class SearchLocationsFragment extends Fragment
        implements StudyLocationAdapter.OnItemClickListener {

    private static final String TAG = "SearchLocationsFragment";

    private ViewSwitcher mViewSwitcher;
    private RecyclerView mRecyclerView;
    private ProgressBar  mProgressIndicator;
    private Toolbar      mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); // calls onCreateOptionsMenu
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_locations, container, false);

        mProgressIndicator = (ProgressBar) rootView.findViewById(R.id.progress_indicator);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.study_location_list);
        mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                                                                           LinearLayoutManager.VERTICAL,
                                                                           false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        ((StudyLocationsActivity) getActivity()).setToolbarElevation(5);

        setUpToolBar();

        return rootView;
    }

    private void setUpToolBar() {
        ((StudyLocationsActivity) getActivity()).enableDrawerIndicator(false);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Search");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_locations_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.expandActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                refresh(s);
                return true;
            }
        });
    }

    private void refresh(String searchToken) {

        ParseQuery<StudyLocation> studyLocationParseQuery = initStudyLocationQuery(searchToken);

        studyLocationParseQuery.findInBackground(new FindCallback<StudyLocation>() {
            @Override
            public void done(List<StudyLocation> studyLocations, ParseException e) {
                mViewSwitcher.setDisplayedChild(1);

                if (e == null) {
                    StudyLocationAdapter adapter =
                            new StudyLocationAdapter(studyLocations,
                                                     getActivity(),
                                                     SearchLocationsFragment.this);
                    mRecyclerView.setAdapter(adapter);
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

    private ParseQuery<StudyLocation> initStudyLocationQuery(String searchToken) {
        ParseQuery<StudyLocation> studyLocationName = ParseQuery.getQuery(StudyLocation.class);
        studyLocationName.whereEqualTo(StudyLocation.UNIVERSITY_COL, "umich");
        studyLocationName.whereContains(StudyLocation.NAME_COL, searchToken);

        ParseQuery<StudyLocation> studyLocationBuilding = ParseQuery.getQuery(StudyLocation.class);
        studyLocationBuilding.whereEqualTo(StudyLocation.UNIVERSITY_COL, "umich");
        studyLocationBuilding.whereContains(StudyLocation.BUILDING_NAME_COL, searchToken);

        List<ParseQuery<StudyLocation>> studyLocations = new ArrayList<>();
        studyLocations.add(studyLocationName);
        studyLocations.add(studyLocationBuilding);

        ParseQuery<StudyLocation> studyLocationQuery = ParseQuery.or(studyLocations);

        setSort(studyLocationQuery);
        return studyLocationQuery;
    }

    private void setSort(ParseQuery<StudyLocation> studyLocationQuery) {
        studyLocationQuery.orderByDescending(StudyLocation.RATING_COL);
    }

    @Override
    public void onItemClick(int position, View view) {
        Intent intent = new Intent(getActivity(), StudyLocationDetailActivity.class);
        StudyLocationAdapter adapter = (StudyLocationAdapter) mRecyclerView.getAdapter();
        intent.putExtra("studyLocation", adapter.getStudyLocation(position));
        intent.putExtra("url", adapter.getStudyLocation(position).getImage().getUrl());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
    }
}
