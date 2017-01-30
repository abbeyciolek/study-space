package edu.umich.imlc.studyspace.ui.StudyLocationDetail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 3/10/15.
 */
public class StudyLocationInfoFragment extends android.support.v4.app.DialogFragment {

    private static final String TAG = StudyLocationInfoFragment.class.getName();

    private StudyLocation mStudyLocation;



    private List<String> listitems = new ArrayList<String>();

    private ListView mylist;

    public static StudyLocationInfoFragment newInstance(StudyLocation studyLocation) {
        Bundle args = new Bundle();
        args.putParcelable("studyLocation", studyLocation);

        StudyLocationInfoFragment frag = new StudyLocationInfoFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStudyLocation = getArguments().getParcelable("studyLocation");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_location_info, container, false);



        //setUpToolBar();
        mylist = (ListView) rootView.findViewById(R.id.list);


        /*listitems.add(mStudyLocation.getBuildingName() + "\n" + mStudyLocation.getAddress() + "\n" +
                mStudyLocation.getCity() + ", " + mStudyLocation.getState() + " " +
                mStudyLocation.getZipCode());*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, listitems);

        mylist.setAdapter(adapter);




        // set operating hours
        try {
            JSONObject obj = new JSONObject(mStudyLocation.getHours());
            printHour(obj, "Sunday");
            printHour(obj, "Monday");
            printHour(obj, "Tuesday");
            printHour(obj, "Wednesday");
            printHour(obj, "Thursday");
            printHour(obj, "Friday");
            printHour(obj, "Saturday");
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }


        getDialog().setTitle(mStudyLocation.getName()+" Hours");


        return rootView;
    }



    LocalTime open;
    LocalTime close;



    private void printHour(JSONObject obj, String day) throws JSONException {
        open = LocalTime.parse(obj.getString(day + "Open"));
        close = LocalTime.parse(obj.getString(day + "Close"));
        if (open.equals(close)) {
            listitems.add("Open 24 Hours");
        }
        else {
            listitems.add(day+": "+open.toString("hh:mma") + " - " +
                    close.toString("hh:mma"));
        }
    }
}
