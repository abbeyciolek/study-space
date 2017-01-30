package edu.umich.imlc.studyspace.ui;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseFile;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;
import edu.umich.imlc.studyspace.ui.StudyLocations.StudyLocationsActivity;

/**
 * Created by Abbey Ciolek on 3/11/15.
 */
public class AddStudyLocationFragment extends Fragment implements View.OnClickListener,
                                                                  TimePickerFragment.OnTimeSelected {

    private static final String TAG = AddStudyLocationFragment.class.getName();

    private Toolbar mToolbar;

    private ViewSwitcher mViewSwitcher;
    private EditText     mLocationName;
    private EditText     mBuildingName;
    private EditText     mAddress;
    private EditText     mCity;
    private EditText     mState;
    private EditText     mZipCode;
    private Button       mSundayOpen;
    private Button       mMondayOpen;
    private Button       mTuesdayOpen;
    private Button       mWednesdayOpen;
    private Button       mThursdayOpen;
    private Button       mFridayOpen;
    private Button       mSaturdayOpen;
    private Button       mUploadPhoto;

    private LocalTime mSundayOpenTime;
    private LocalTime mSundayCloseTime;
    private LocalTime mMondayOpenTime;
    private LocalTime mMondayCloseTime;
    private LocalTime mTuesdayOpenTime;
    private LocalTime mTuesdayCloseTime;
    private LocalTime mWednesdayOpenTime;
    private LocalTime mWednesdayCloseTime;
    private LocalTime mThursdayOpenTime;
    private LocalTime mThursdayCloseTime;
    private LocalTime mFridayOpenTime;
    private LocalTime mFridayCloseTime;
    private LocalTime mSaturdayOpenTime;
    private LocalTime mSaturdayCloseTime;
    private ParseFile mParseFile;
    private Uri       mImageUri;

    private int mToolbarAlpha = 255;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        mLocationName = (EditText) view.findViewById(R.id.location_name);
        mBuildingName = (EditText) view.findViewById(R.id.building_name);
        mAddress = (EditText) view.findViewById(R.id.address);
        mCity = (EditText) view.findViewById(R.id.city);
        mState = (EditText) view.findViewById(R.id.state);
        mZipCode = (EditText) view.findViewById(R.id.zipcode);
        mSundayOpen = (Button) view.findViewById(R.id.sundayOpen);
        mMondayOpen = (Button) view.findViewById(R.id.mondayOpen);
        mTuesdayOpen = (Button) view.findViewById(R.id.tuesdayOpen);
        mWednesdayOpen = (Button) view.findViewById(R.id.wednesdayOpen);
        mThursdayOpen = (Button) view.findViewById(R.id.thursdayOpen);
        mFridayOpen = (Button) view.findViewById(R.id.fridayOpen);
        mSaturdayOpen = (Button) view.findViewById(R.id.saturdayOpen);
        mUploadPhoto = (Button) view.findViewById(R.id.upload_photo);

        mSundayOpen.setOnClickListener(this);
        mMondayOpen.setOnClickListener(this);
        mTuesdayOpen.setOnClickListener(this);
        mWednesdayOpen.setOnClickListener(this);
        mThursdayOpen.setOnClickListener(this);
        mFridayOpen.setOnClickListener(this);
        mSaturdayOpen.setOnClickListener(this);
        mUploadPhoto.setOnClickListener(this);

        setupActionBar();

        return view;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Location");
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_review_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                submit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        mViewSwitcher.setDisplayedChild(1);
        final android.os.Handler handler = new android.os.Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StudyLocation studyLocation = new StudyLocation();

                    studyLocation.setName(mLocationName.getText().toString());
                    studyLocation.setBuildingName(mBuildingName.getText().toString());
                    studyLocation.setUniversity("");
                    studyLocation.setAddress(mAddress.getText().toString());
                    studyLocation.setCity(mCity.getText().toString());
                    studyLocation.setState(mState.getText().toString());
                    studyLocation.setZipCode(mZipCode.getText().toString());
                    studyLocation.setRating(0);
                    studyLocation.setWifiRating(0);
                    studyLocation.setTableRating(0);
                    studyLocation.setOutletsRating(0);
                    studyLocation.setQuietness(0);
                    studyLocation.setNumReviewsCol(0);

                    //maybe separate thread async task and put submit in background
                    //progress spinner while loading
                    //on submit set quietness and ratings to 0
                    InputStream iStream =
                            getActivity().getContentResolver().openInputStream(mImageUri);
                    byte[] inputData = getBytes(iStream);


                    String filePath = getPath(getActivity(), mImageUri);
                    String extension = filePath.substring(filePath.lastIndexOf("."));
                    String fileName = mBuildingName.getText().toString() + extension;

                    mParseFile = new ParseFile(fileName, inputData);
                    studyLocation.setImage(mParseFile);

                    JSONObject obj = new JSONObject();
                    obj.put("SundayOpen", mSundayOpenTime);
                    obj.put("SundayClose", mSundayCloseTime);
                    obj.put("MondayOpen", mMondayOpenTime);
                    obj.put("MondayClose", mMondayCloseTime);
                    obj.put("TuesdayOpen", mTuesdayOpenTime);
                    obj.put("TuesdayClose", mTuesdayCloseTime);
                    obj.put("WednesdayOpen", mWednesdayOpenTime);
                    obj.put("WednesdayClose", mWednesdayCloseTime);
                    obj.put("ThursdayOpen", mThursdayOpenTime);
                    obj.put("ThursdayClose", mThursdayCloseTime);
                    obj.put("FridayOpen", mFridayOpenTime);
                    obj.put("FridayClose", mFridayCloseTime);
                    obj.put("SaturdayOpen", mSaturdayOpenTime);
                    obj.put("SaturdayClose", mSaturdayCloseTime);
                    studyLocation.setHours(obj.toString());

                    studyLocation.save();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                           "The location has been submitted. It will be reviewed by the team.",
                                           Toast.LENGTH_LONG).show();
                            ((StudyLocationsActivity) getActivity()).goHome();
                        }
                    });
                }
                catch (ParseException | JSONException | IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                           "There was an error. Please Try Again.",
                                           Toast.LENGTH_LONG).show();
                            mViewSwitcher.setDisplayedChild(0);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                                                        null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
        ViewCompat.setElevation(mToolbar, 5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sundayOpen:
                openTimeDialog(1);
                openTimeDialog(0);
                break;

            case R.id.mondayOpen:
                openTimeDialog(3);
                openTimeDialog(2);
                break;

            case R.id.tuesdayOpen:
                openTimeDialog(5);
                openTimeDialog(4);
                break;

            case R.id.wednesdayOpen:
                openTimeDialog(7);
                openTimeDialog(6);
                break;

            case R.id.thursdayOpen:
                openTimeDialog(9);
                openTimeDialog(8);
                break;

            case R.id.fridayOpen:
                openTimeDialog(11);
                openTimeDialog(10);
                break;

            case R.id.saturdayOpen:
                openTimeDialog(13);
                openTimeDialog(12);
                break;

            case R.id.upload_photo:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
                break;
        }
    }

    private void openTimeDialog(int day) {

        TimePickerFragment frag = TimePickerFragment.newInstance(day);
        frag.setTimeSelectedListener(this);
        frag.show(getActivity().getSupportFragmentManager(), "pick time");

    }

    @Override
    public void onTimeSelected(int day, TimePicker view, int hourOfDay, int minute) {
        CharSequence oldText, formatted;
        DateTimeFormatter outputFormat;

        switch (day) {
            case 0:
                mSundayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mSundayOpenTime.toString(outputFormat);
                mSundayOpen.setText("Sunday: " + formatted);
                break;
            case 1:
                mSundayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mSundayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mSundayCloseTime.toString(outputFormat);
                mSundayOpen.setText(oldText + "-" + formatted);
                break;
            case 2:
                mMondayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mMondayOpenTime.toString(outputFormat);
                mMondayOpen.setText("Monday: " + formatted);
                break;
            case 3:
                mMondayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mMondayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mMondayCloseTime.toString(outputFormat);
                mMondayOpen.setText(oldText + "-" + formatted);
                break;
            case 4:
                mTuesdayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mTuesdayOpenTime.toString(outputFormat);
                mTuesdayOpen.setText("Tuesday: " + formatted);
                break;
            case 5:
                mTuesdayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mTuesdayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mTuesdayCloseTime.toString(outputFormat);
                mTuesdayOpen.setText(oldText + "-" + formatted);
                break;
            case 6:
                mWednesdayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mWednesdayOpenTime.toString(outputFormat);
                mWednesdayOpen.setText("Wednesday: " + formatted);
                break;
            case 7:
                mWednesdayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mWednesdayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mWednesdayCloseTime.toString(outputFormat);
                mWednesdayOpen.setText(oldText + "-" + formatted);
                break;
            case 8:
                mThursdayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mThursdayOpenTime.toString(outputFormat);
                mThursdayOpen.setText("Thursday: " + formatted);
                break;
            case 9:
                mThursdayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mThursdayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mThursdayCloseTime.toString(outputFormat);
                mThursdayOpen.setText(oldText + "-" + formatted);
                break;
            case 10:
                mFridayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mFridayOpenTime.toString(outputFormat);
                mFridayOpen.setText("Friday: " + formatted);
                break;
            case 11:
                mFridayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mFridayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mFridayCloseTime.toString(outputFormat);
                mFridayOpen.setText(oldText + "-" + formatted);
                break;
            case 12:
                mSaturdayOpenTime = new LocalTime(hourOfDay, minute);
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mSaturdayOpenTime.toString(outputFormat);
                mSaturdayOpen.setText("Saturday: " + formatted);
                break;
            case 13:
                mSaturdayCloseTime = new LocalTime(hourOfDay, minute);
                oldText = mSaturdayOpen.getText();
                outputFormat = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
                formatted = mSaturdayCloseTime.toString(outputFormat);
                mSaturdayOpen.setText(oldText + "-" + formatted);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mImageUri = data.getData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
