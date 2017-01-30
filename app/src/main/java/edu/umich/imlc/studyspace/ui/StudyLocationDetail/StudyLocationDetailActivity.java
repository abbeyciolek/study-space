package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;

public class StudyLocationDetailActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private ImageView mDummyImage;
    private TextView  mHeaderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_location_detail);

        mDummyImage = (ImageView) findViewById(R.id.dummy_image_header);
        mHeaderTitle = (TextView) findViewById(R.id.text_header);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(mToolbar, 5);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        StudyLocation studyLocation = extras.getParcelable("studyLocation");
        String url = extras.getString("url");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.container,
                                            StudyLocationDetailFragment.newInstance(studyLocation, url))
                                       .commit();
        }
    }

    public void showHeader() {
        mHeaderTitle.setVisibility(View.VISIBLE);
    }

    public void hideHeader() {
        mDummyImage.setVisibility(View.INVISIBLE);
        mHeaderTitle.setVisibility(View.INVISIBLE);
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study_location_detail, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.container);
        if ( frag instanceof StudyLocationReviewsFragment || frag instanceof AddReviewFragment) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            finish();
        }
    }
}
