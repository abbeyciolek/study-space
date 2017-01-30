package edu.umich.imlc.studyspace.ui.StudyLocations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.adapters.StudyLocationAdapter;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.StudyLocationDetail.StudyLocationDetailActivity;
import edu.umich.imlc.studyspace.ui.view.MultiSwipeRefreshLayout;


/**
 * Created by Abbey Ciolek on 4/10/15.
 */
public abstract class BaseStudyLocationListFragment extends Fragment
        implements StudyLocationAdapter.OnItemClickListener,
                   MultiSwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = BaseStudyLocationListFragment.class.getName();

    private MultiSwipeRefreshLayout mRefreshLayout;
    private ViewSwitcher            mViewSwitcher;
    private TextView                mLoadMessage;
    private RecyclerView            mRecyclerView;
    private Toolbar                 mToolbar;
    private StudyLocationAdapter    mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.study_location_list_fragment, container, false);

        mRefreshLayout = (MultiSwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.study_location_list);
        mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);
        mLoadMessage = (TextView) rootView.findViewById(R.id.load_message);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        //refresh layout setup
        mRefreshLayout.setOnRefreshListener(this);
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

        return rootView;
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
                    mAdapter = new StudyLocationAdapter(studyLocations,
                                                        getActivity(),
                                                        BaseStudyLocationListFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mViewSwitcher.setDisplayedChild(1);
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

    protected abstract void setSort(ParseQuery<StudyLocation> query);

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
    }


    @Override
    public void onItemClick(int position, View view) {
        Intent intent = new Intent(getActivity(), StudyLocationDetailActivity.class);
        intent.putExtra("studyLocation", mAdapter.getStudyLocation(position));
        intent.putExtra("url", mAdapter.getStudyLocation(position).getImage().getUrl());
        startActivity(intent);
    }
}
