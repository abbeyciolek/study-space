package edu.umich.imlc.studyspace.ui.StudyLocations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.ui.CreateAccountFragment;
import edu.umich.imlc.studyspace.ui.SignInFragment;

/**
 * Created by Abbey Ciolek on 4/9/15.
 */
public class StudyLocationsFragment extends Fragment {

    public static final String TAG = StudyLocationsFragment.class.getName();

    private PagerSlidingTabStrip mTabs;
    private ViewPager            mViewPager;

    public static StudyLocationsFragment newInstance() {
        return new StudyLocationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_locations, container, false);

        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        mTabs.setViewPager(mViewPager);

        getActionBar().setTitle("University of Michigan");
        getActionBar().setDisplayHomeAsUpEnabled(false);
        ((StudyLocationsActivity) getActivity()).enableDrawerIndicator(true);

        return view;
    }

    public ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_study_locations_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // calls activity onPrepareOptionsMenu, which sets up nav drawer header
        super.onPrepareOptionsMenu(menu);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.search:
                search();
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

    private void search() {
        getActivity().getSupportFragmentManager()
                     .beginTransaction()
                     .replace(R.id.container, new SearchLocationsFragment())
                     .commit();
    }

    private void sign_in() {
        SignInFragment frag = new SignInFragment();
        frag.setTargetFragment(this, 1);
        frag.show(getActivity().getSupportFragmentManager(), "Sign In");
    }

    private void create_acct() {
        new CreateAccountFragment().show(getActivity().getSupportFragmentManager(), "Create Acct");
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES =
                {"Top Rated", "Quietest", "Most Tables", "Most Outlets", "Best WiFi", "Map View"};

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    return new TopRatedFragment();
                case 1:
                    return new QuietestFragment();
                case 2:
                    return new TablesFragment();
                case 3:
                    return new OutletsFragment();
                case 4:
                    return new WifiFragment();
                case 5:
                    return new MapFragment();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StudyLocationsActivity) getActivity()).setToolbarElevation(0);
        ViewCompat.setElevation(mTabs, 5);
    }
}
