package com.journeytech.mark.mark.map_fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.journeytech.mark.mark.GPSTracker;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;

import java.util.ArrayList;

import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.latitudeG;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.longitudeG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProximityMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;
    private ListView lv;

    public static GoogleMap mMapProximity;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;

    Context context;
    static Activity activity;

    static LatLng origin;

    static Double lati = 0.0, longi = 0.0;

    // GPSTracker class
    GPSTracker gps;

    public ProximityMapFragment(Activity a, Context c) {
        this.activity = a;
        this.context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        // Initializing
        MarkerPoints = new ArrayList<LatLng>();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        ProximityBottomSheetModalMapFragment bottomSheetDialogFragment = new ProximityBottomSheetModalMapFragment();
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapProximity = googleMap;
        mMapProximity.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 5));

/*        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMapProximityNavigation.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMapProximityNavigation.setMyLocationEnabled(true);
        }*/

        createProximity(latitudeG, longitudeG);

    }

    public void createProximity(Double latitude, Double longitude) {

        // create class object
        gps = new GPSTracker(getContext());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitudeGPS = gps.getLatitude();
            double longitudeGPS = gps.getLongitude();

            //Origin, where you are. Geo Location
            origin = new LatLng(latitudeGPS, longitudeGPS);

            mMapProximity.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudeGPS, longitudeGPS))
                    .anchor(0.5f, 0.5f)
                    .title("My Location")
                    .snippet("This is where you are fetch.")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        } else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14.0f));

        //Passing Snail Trail Geo Location for plotting
        //Destination
        mMapProximity.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title("Your Vehicle")
                .snippet("This is where your vehicle was fetch.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMapProximity.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mMapProximity.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ProximityBottomSheetModalMapFragment bottomSheetDialogFragment = new ProximityBottomSheetModalMapFragment();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                return false;
            }
        });
    }
}
