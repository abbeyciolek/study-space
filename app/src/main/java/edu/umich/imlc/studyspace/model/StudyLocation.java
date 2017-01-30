package edu.umich.imlc.studyspace.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Abbey Ciolek on 2/14/15.
 */
@ParseClassName("StudyLocation")
public class StudyLocation extends ParseObject implements Parcelable {

    public static final String NAME_COL          = "Name";
    public static final String BUILDING_NAME_COL = "BuildingName";
    public static final String RATING_COL        = "Rating";
    public static final String UNIVERSITY_COL    = "University";
    public static final String IMAGE_COL         = "Image";
    public static final String ADDRESS_COL       = "Address";
    public static final String CITY_COL          = "City";
    public static final String STATE_COL         = "State";
    public static final String ZIP_COL           = "Zip";
    public static final String QUIETNESS_COL     = "Quietness";
    public static final String OUTLETS_COL       = "Outlets";
    public static final String TABLE_COL         = "Table";
    public static final String WIFI_COL          = "WiFi";
    public static final String NUM_REVIEWS_COL   = "NumReviews";
    public static final String HOURS_COL         = "Hours";

    public StudyLocation() {

    }

    public String getName() {
        return getString(NAME_COL);
    }

    public void setName(String name) {
        put(NAME_COL, name);
    }

    public String getBuildingName() {
        return getString(BUILDING_NAME_COL);
    }

    public void setBuildingName(String buildingName) {
        put(BUILDING_NAME_COL, buildingName);
    }

    public float getRating() {
        return (float) getDouble(RATING_COL);
    }

    public void setRating(float rating) {
        put(RATING_COL, rating);
    }

    public String getUniversity() {
        return getString(UNIVERSITY_COL);
    }

    public void setUniversity(String university) {
        put(UNIVERSITY_COL, university);
    }

    public ParseFile getImage() {
        return getParseFile(IMAGE_COL);
    }

    public void setImage(ParseFile image) {
        put(IMAGE_COL, image);
    }

    public String getAddress() {
        return getString(ADDRESS_COL);
    }

    public void setAddress(String address) {
        put(ADDRESS_COL, address);
    }

    public String getCity() {
        return getString(CITY_COL);
    }

    public void setCity(String city) {
        put(CITY_COL, city);
    }

    public String getState() {
        return getString(STATE_COL);
    }

    public void setState(String state) {
        put(STATE_COL, state);
    }

    public String getZipCode() {
        return getString(ZIP_COL);
    }

    public void setZipCode(String zipCode) {
        put(ZIP_COL, zipCode);
    }

    public float getQuietness() {
        return (float) getDouble(QUIETNESS_COL);
    }

    public void setQuietness(double quietness) {
        put(QUIETNESS_COL, quietness);
    }

    public float getOutletsRating() {
        return (float) getDouble(OUTLETS_COL);
    }

    public void setOutletsRating(double outletsRating) {
        put(OUTLETS_COL, outletsRating);
    }

    public float getTableRating() {
        return (float) getDouble(TABLE_COL);
    }

    public void setTableRating(double tableRating) {
        put(TABLE_COL, tableRating);
    }

    public float getWifiRating() {
        return (float) getDouble(WIFI_COL);
    }

    public void setWifiRating(double wifiRating) {
        put(WIFI_COL, wifiRating);
    }

    public int getNumReviews() {
        return getInt(NUM_REVIEWS_COL);
    }

    public void setNumReviewsCol(int numReviews) {
        put(NUM_REVIEWS_COL, numReviews);
    }

    public String getHours() {
        return getString(HOURS_COL);
    }

    public void setHours(String hours) {
        put(HOURS_COL, hours);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getObjectId());
        parcel.writeString(getName());
        parcel.writeString(getBuildingName());
        parcel.writeFloat(getRating());
        parcel.writeString(getUniversity());
        /*Gson parseFileGson = new Gson();
        parcel.writeString(parseFileGson.toJson(getImage()));*/
        parcel.writeString(getAddress());
        parcel.writeString(getCity());
        parcel.writeString(getState());
        parcel.writeString(getZipCode());
        parcel.writeFloat(getQuietness());
        parcel.writeFloat(getOutletsRating());
        parcel.writeFloat(getTableRating());
        parcel.writeFloat(getWifiRating());
        parcel.writeInt(getNumReviews());
        parcel.writeString(getHours());
    }

    public static final Creator<StudyLocation> CREATOR = new Creator<StudyLocation>() {
        @Override
        public StudyLocation createFromParcel(Parcel source) {
            return new StudyLocation(source);
        }

        @Override
        public StudyLocation[] newArray(int size) {
            return new StudyLocation[size];
        }
    };

    private StudyLocation(Parcel source) {
        setObjectId(source.readString());
        setName(source.readString());
        setBuildingName(source.readString());
        setRating(source.readFloat());
        setUniversity(source.readString());
        /*String imageGsonString = source.readString();
        Gson gson = new Gson();
        setImage(gson.fromJson(imageGsonString, ParseFile.class));*/
        setAddress(source.readString());
        setCity(source.readString());
        setState(source.readString());
        setZipCode(source.readString());
        setQuietness(source.readFloat());
        setOutletsRating(source.readFloat());
        setTableRating(source.readFloat());
        setWifiRating(source.readFloat());
        setNumReviewsCol(source.readInt());
        setHours(source.readString());
    }

}
