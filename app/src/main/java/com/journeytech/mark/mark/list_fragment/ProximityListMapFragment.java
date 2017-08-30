package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import java.util.ArrayList;

import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.latitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.longitudeListMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProximityListMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;
    private ListView lv;

    public static GoogleMap mMapProximity;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;

    Context context;
    static Activity activity;

    static LatLng origin;

    static Double lati = 0.0, longi = 0.0;

    GPSTracker gps;

    public ProximityListMapFragment(Activity a, Context c) {
        this.activity = a;
        this.context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        // Initializing
        MarkerPoints = new ArrayList<>();

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

    public void onBackPressed() {
        FragmentManager manager;
        VehicleListMapFragment vehicleListMapFragment;
        vehicleListMapFragment = new VehicleListMapFragment();
        manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, vehicleListMapFragment).commit();
        return;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapProximity = googleMap;
        mMapProximity.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 5));

        final Double lati = Double.parseDouble(latitudeListMap);
        final Double longi = Double.parseDouble(longitudeListMap);

        createProximity(lati, longi);

    }

    public void createProximity(Double latitude, Double longitude) {

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

            /*Integer cam = Integer.parseInt(distan);
            if(cam <= 50) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14.0f));
            }
            else if(cam >= 100) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13.0f));
            }  else if(cam >= 200) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 11.0f));
            } else if(cam >= 400) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9.0f));
            } else if(cam >= 600) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 7.0f));
            } else if(cam >= 800) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 5.0f));
            } else if(cam >= 1000) {
                mMapProximity.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 4.0f));
            }*/

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        //Passing Snail Trail Geo Location for plotting
        //Vehicle - Destination
        mMapProximity.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title("Your Vehicle")
                .snippet("This is where your vehicle was fetch.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

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
