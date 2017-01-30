package edu.umich.imlc.studyspace.ui.StudyLocations;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;


/**
 * Created by Abbey Ciolek on 4/10/15.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
                                                               GoogleApiClient.ConnectionCallbacks,
                                                               GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapFragment.class.getName();

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap       mGoogleMap;
    private Map<Address, List<StudyLocation>> mAddressMap = new HashMap<>();
    private Handler                           mHandler    = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleApiClient.connect();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setInfoWindowAdapter(new InfoAdapter(getActivity().getLayoutInflater()));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f));

        Thread thread = new Thread(new GetLocations());
        thread.start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class GetLocations implements Runnable {

        @Override
        public void run() {
            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses;
                Map<String, List<StudyLocation>> studyLocationMap = new HashMap<>();

                ParseQuery<StudyLocation> studyLocationQuery =
                        ParseQuery.getQuery(StudyLocation.class);
                studyLocationQuery.whereEqualTo(StudyLocation.UNIVERSITY_COL, "umich");
                List<StudyLocation> studyLocations = studyLocationQuery.find();

                for (StudyLocation studyLocation : studyLocations) {
                    String address =
                            studyLocation.getAddress() + ", " + studyLocation.getCity() + ", " +
                            studyLocation.getCity() + ", " + studyLocation.getZipCode();
                    List<StudyLocation> studyLocations1 = studyLocationMap.get(address);
                    if (studyLocations1 == null) {
                        studyLocations1 = new ArrayList<>();
                        studyLocationMap.put(address, studyLocations1);
                    }
                    studyLocations1.add(studyLocation);
                }

                for (Map.Entry<String, List<StudyLocation>> entry : studyLocationMap.entrySet()) {
                    addresses = geocoder.getFromLocationName(entry.getKey(), 1);
                    mAddressMap.put(addresses.get(0), entry.getValue());
                }

                for (Map.Entry<Address, List<StudyLocation>> entry : mAddressMap.entrySet()) {
                    Address address = entry.getKey();
                    List<StudyLocation> studyLocations1 = entry.getValue();
                    LatLng pos = new LatLng(address.getLatitude(), address.getLongitude());
                    String title = studyLocations1.get(0).getBuildingName();
                    String snippet = "";
                    for (StudyLocation studyLocation : studyLocations1) {
                        snippet += studyLocation.getName() + ": " +
                                   String.format("%.2f", studyLocation.getRating()) + "\n";
                    }

                    final MarkerOptions markerOptions =
                            new MarkerOptions().position(pos).title(title).snippet(snippet);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGoogleMap.addMarker(markerOptions);
                        }
                    });
                }


            }
            catch (ParseException | IOException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class InfoAdapter implements GoogleMap.InfoWindowAdapter {

        LayoutInflater inflater;

        public InfoAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = inflater.inflate(R.layout.snippet_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(marker.getTitle());
            TextView snippet = (TextView) view.findViewById(R.id.snippet);
            snippet.setText(marker.getSnippet());
            return view;
        }
    }
}
