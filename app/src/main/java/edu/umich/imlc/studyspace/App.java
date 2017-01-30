package edu.umich.imlc.studyspace;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import net.danlew.android.joda.JodaTimeAndroid;

import edu.umich.imlc.studyspace.model.Review;
import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 2/3/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        ParseObject.registerSubclass(StudyLocation.class);
        ParseObject.registerSubclass(Review.class);
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "yGZGMuVvMIf560ckMmF38MWKeaWJV0LkPMCBBBuD", "1R86Xn9n5TzHnPRBUIVkzIJGbv8ofS0uOPrxe48Z");
        JodaTimeAndroid.init(this);
    }
}
