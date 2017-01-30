package edu.umich.imlc.studyspace.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements OnTimeSetListener {

    public interface OnTimeSelected {

        public void onTimeSelected(int day, TimePicker view, int hourOfDay, int minute);
    }

    private OnTimeSelected mListener;
    private int            mDay;

    public static TimePickerFragment newInstance(int day) {
        Bundle args = new Bundle();
        args.putInt("day", day);

        TimePickerFragment frag = new TimePickerFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDay = getArguments().getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use current time is default values
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                    this,
                                    hour,
                                    minute,
                                    DateFormat.is24HourFormat(getActivity()));
        if(mDay%2 == 0){
            timePickerDialog.setTitle("Set Open Time");
        }
        else{
            timePickerDialog.setTitle("Set Close Time");
        }

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(view.isShown()) {
            mListener.onTimeSelected(mDay, view, hourOfDay, minute);
        }
    }

    public void setTimeSelectedListener(OnTimeSelected listener) {
        mListener = listener;
    }
}
