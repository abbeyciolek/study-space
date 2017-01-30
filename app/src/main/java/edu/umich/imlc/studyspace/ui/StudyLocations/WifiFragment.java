package edu.umich.imlc.studyspace.ui.StudyLocations;

import com.parse.ParseQuery;

import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 4/10/15.
 */
public class WifiFragment extends BaseStudyLocationListFragment {

    @Override
    protected void setSort(ParseQuery<StudyLocation> query) {
        query.orderByDescending(StudyLocation.WIFI_COL);
    }
}
