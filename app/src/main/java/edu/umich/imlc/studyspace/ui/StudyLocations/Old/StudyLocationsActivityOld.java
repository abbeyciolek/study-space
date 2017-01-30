package edu.umich.imlc.studyspace.ui.StudyLocations.Old;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.ui.AddStudyLocationFragment;
import edu.umich.imlc.studyspace.ui.StudyLocations.SearchLocationsFragment;


public class StudyLocationsActivityOld extends ActionBarActivity {

    private static final String TAG = "StudyLocationsActivityOld";

    //private ActionBarDrawerToggle mDrawerToggle;

    private ImageView mDummyImage;
    private TextView  mHeaderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_locations_old);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDummyImage = (ImageView) findViewById(R.id.dummy_image_header);
        mHeaderTitle = (TextView) findViewById(R.id.text_header);

        //initNavDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.container, new StudyLocationsFragmentOld())
                       .commit();
    }

    public void hideHeader() {
        mDummyImage.setVisibility(View.INVISIBLE);
        mHeaderTitle.setVisibility(View.INVISIBLE);
    }

    /*private void initNavDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout view = (FrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

        // Width of nav drawer depends on width of screen
        // width should be screenWidth - actionBarHeight or actionBarHeight*5, whichever is smaller
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize,
                                        typedValue,
                                        true)) {
            int actionBarSize = TypedValue.complexToDimensionPixelSize(typedValue.data,
                                                                       getResources().getDisplayMetrics());
            int width = getResources().getDisplayMetrics().widthPixels;
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) view.getLayoutParams();
            params.width = Math.min(width - actionBarSize, 5 * actionBarSize);
            view.setLayoutParams(params);
        }

        // Set up drawer toggle and drawer layout
        mDrawerToggle = new ActionBarDrawerToggle(this,
                                                  drawerLayout,
                                                  R.string.navigation_drawer_open,
                                                  R.string.navigation_drawer_close);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.theme_primary_dark));
        drawerLayout.setDrawerListener(mDrawerToggle);

        // alter nav drawer views
        initNavDrawerView();
    }

    private void initNavDrawerView() {
        TextView test = (TextView) findViewById(R.id.settings);
        test.setText("Settings Renamed");
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        ParseUser.logOut();
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.container);
        if (frag instanceof SearchLocationsFragment || frag instanceof AddStudyLocationFragment) {
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, new StudyLocationsFragmentOld())
                                       .commit();
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return false;
    }

}
