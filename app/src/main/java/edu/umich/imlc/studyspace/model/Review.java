package edu.umich.imlc.studyspace.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Abbey Ciolek on 2/21/15.
 */
@ParseClassName("Review")
public class Review extends ParseObject implements Parcelable {

    public static final String UPDATED_AT_COL     = "updatedAt";
    public static final String STUDY_LOCATION_COL = "StudyLocation";
    public static final String USER_COL           = "User";
    public static final String DETAIL_COL         = "Detail";
    public static final String WIFI_COL           = "WiFi";
    public static final String TABLE_COL          = "Table";
    public static final String OUTLETS_COL        = "Outlets";
    public static final String QUIETNESS_COL      = "Quietness";
    public static final String RATING_COL         = "Rating";
    public static final String TITLE_COL          = "Title";
    public static final String VOTES_COL          = "netVotes";

    public Review() {

    }

    public StudyLocation getStudyLocation() {
        return (StudyLocation) getParseObject(STUDY_LOCATION_COL);
    }

    public void setStudyLocation(StudyLocation studyLocation) {
        put(STUDY_LOCATION_COL, studyLocation);
    }

    public ParseUser getUser() {
        return getParseUser(USER_COL);
    }

    public void setUser(ParseUser user) {
        put(USER_COL, user);
    }

    public String getDetail() {
        return getString(DETAIL_COL);
    }

    public void setDetail(String detail) {
        put(DETAIL_COL, detail);
    }

    public int getWifiRating() {
        return getInt(WIFI_COL);
    }

    public void setWifiRating(int wifiRating) {
        put(WIFI_COL, wifiRating);
    }

    public int getTableRating() {
        return getInt(TABLE_COL);
    }

    public void setTableRating(int tableRating) {
        put(TABLE_COL, tableRating);
    }

    public int getOutletsRating() {
        return getInt(OUTLETS_COL);
    }

    public void setOutletsRating(int outletsRating) {
        put(OUTLETS_COL, outletsRating);
    }

    public int getQuietnessRating() {
        return getInt(QUIETNESS_COL);
    }

    public void setQuietnessRating(int quietnessRating) {
        put(QUIETNESS_COL, quietnessRating);
    }

    public float getRating() {
        return (float) getDouble(RATING_COL);
    }

    public void setRating(float rating) {
        put(RATING_COL, rating);
    }

    public void setRating() {
        float avg =
                (getWifiRating() + getTableRating() + getOutletsRating() + getQuietnessRating()) /
                4f;
        put(RATING_COL, avg);
    }

    public String getTitle() {
        return getString(TITLE_COL);
    }

    public void setTitle(String title) {
        put(TITLE_COL, title);
    }

    public int getVotes() { return getInt(VOTES_COL); }

    public void setVotes(int votes) {  put(VOTES_COL, votes); }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getObjectId());
        dest.writeValue(getUpdatedAt());
        dest.writeParcelable(getStudyLocation(), PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeValue(getUser());
        dest.writeString(getDetail());
        dest.writeInt(getWifiRating());
        dest.writeInt(getTableRating());
        dest.writeInt(getOutletsRating());
        dest.writeInt(getQuietnessRating());
        dest.writeFloat(getRating());
        dest.writeString(getTitle());
        dest.writeInt(getVotes());
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private Review(Parcel source) {
        setObjectId(source.readString());
        put(UPDATED_AT_COL, source.readValue(Date.class.getClassLoader()));
        setStudyLocation((StudyLocation) source.readParcelable(StudyLocation.class.getClassLoader()));
        setUser((ParseUser) source.readValue(ParseUser.class.getClassLoader()));
        setDetail(source.readString());
        setWifiRating(source.readInt());
        setTableRating(source.readInt());
        setOutletsRating(source.readInt());
        setQuietnessRating(source.readInt());
        setRating(source.readFloat());
        setTitle(source.readString());
        setVotes(source.readInt());
    }
}
