package edu.umich.imlc.studyspace.ui.StudyLocations;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.maps.MapView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.ParseUser;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.ui.AddStudyLocationFragment;

public class StudyLocationsActivity extends ActionBarActivity {

    private static final String TAG = StudyLocationsActivity.class.getName();

    private Toolbar              mToolbar;
    private AccountHeader.Result mHeaderResult;
    private Drawer.Result        mDrawerResult;
    private int mSelectedPosition = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_locations);

        // Fixing Later Map loading Delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }
                catch (Exception ignored) {

                }
            }
        }).start();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Create the AccountHeader
        mHeaderResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.angell_hall)
                .withProfileImagesVisible(false)
                .withSelectionListEnabled(false)
                .addProfiles(new ProfileDrawerItem().withName("")
                                                    .withEmail("")
                                                    .withIcon(getResources().getDrawable(R.color.material_blue_grey_800)))
                .build();

        //Now create your drawer and pass the AccountHeader.Result
        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(mHeaderResult)
                .withFireOnInitialOnClick(true)
                .addDrawerItems(new PrimaryDrawerItem().withName("Home"))
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof SearchLocationsFragment) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                             StudyLocationsFragment.newInstance())
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id,
                                            IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (mSelectedPosition == position) {
                            return;
                        }

                        mSelectedPosition = position;
                        switch (position) {
                            case 0:
                                getSupportFragmentManager().beginTransaction()
                                                           .replace(R.id.container,
                                                                    StudyLocationsFragment.newInstance())
                                                           .commit();

                                break;
                            case 1:
                                getSupportFragmentManager().beginTransaction()
                                                           .replace(R.id.container,
                                                                    new AddStudyLocationFragment())
                                                           .commit();
                                break;
                        }
                    }
                })
                .build();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mDrawerResult.removeAllItems();
        mDrawerResult.addItem(new PrimaryDrawerItem().withName("Home"));
        mHeaderResult.removeProfile(0);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser curUser = ParseUser.getCurrentUser();

            mHeaderResult.addProfile(new ProfileDrawerItem().withName(curUser.getString("Name"))
                                                            .withEmail(curUser.getEmail())
                                                            .withIcon(getResources().getDrawable(R.color.material_blue_grey_800)),
                                     0);

            mDrawerResult.addItem(new PrimaryDrawerItem().withName("Add Location"));
        }
        else {
            mHeaderResult.addProfile(new ProfileDrawerItem().withName("")
                                                            .withEmail("")
                                                            .withIcon(getResources().getDrawable(R.color.material_blue_grey_800)),
                                     0);
        }

        return true;
    }

    public void enableDrawerIndicator(boolean enable) {
        mDrawerResult.getActionBarDrawerToggle().setDrawerIndicatorEnabled(enable);
    }

    public void goHome() {
        mDrawerResult.setSelection(0, true);
    }

    public void setToolbarElevation(float elevation) {
        ViewCompat.setElevation(mToolbar, elevation);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerResult != null && mDrawerResult.isDrawerOpen()) {
            mDrawerResult.closeDrawer();
        }
        else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof SearchLocationsFragment) {
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container,
                                                StudyLocationsFragment.newInstance())
                                       .commit();
        }
        else {
            finish();
        }
    }
}
