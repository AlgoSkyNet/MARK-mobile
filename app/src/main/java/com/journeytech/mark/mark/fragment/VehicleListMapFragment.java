package com.journeytech.mark.mark.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.journeytech.mark.mark.BottomSheetModalFragment;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.model.VehicleHolder;

import java.util.ArrayList;

import static com.journeytech.mark.mark.fragment.VehicleListFragment.vlm;
import static com.journeytech.mark.mark.fragment.VehicleMapFragment.mMapFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleListMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1.json";

    public static GoogleMap mMapVehicleListMapFragment;
    public static ArrayList<VehicleHolder> vehicleHolder;

    Context context;
    static Activity activity;

    String catcher = "";

    TextView tv2, tv4, tv6;

    public VehicleListMapFragment(Context c, Activity a) {
        context = c;
        activity = a;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vehicle_list_map, container, false);
        tv2 = (TextView) v.findViewById(R.id.tv2);
        tv4 = (TextView) v.findViewById(R.id.tv4);
        tv6 = (TextView) v.findViewById(R.id.tv6);

        catcher = getArguments().getString("geoLoc");

        return v;
    }


    public void onMapReady(GoogleMap googleMap) {
        mMapVehicleListMapFragment = googleMap;

        mMapFragment.clear();
        mMapVehicleListMapFragment.clear();

        setUpMap();
    }

    /*set up map*/
    private void setUpMap() {
        mMapVehicleListMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapVehicleListMapFragment.setTrafficEnabled(true);
        mMapVehicleListMapFragment.setIndoorEnabled(true);
        mMapVehicleListMapFragment.setBuildingsEnabled(true);
        mMapVehicleListMapFragment.getUiSettings().setZoomControlsEnabled(true);

        final Double lati = Double.parseDouble(vlm.getLati());
        final Double longi = Double.parseDouble(vlm.getLongi());

        mMapVehicleListMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longi))
                .anchor(0.5f, 0.5f)
                .title("Location: " +vlm.getLoc())
                .snippet("Engine: " + vlm.getEngine())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

//        mMapVehicleListMapFragment.

        mMapVehicleListMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mMapVehicleListMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 16.0f));

        mMapVehicleListMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {

                marker.showInfoWindow();
                bounceMarker(marker);

                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetModalFragment(activity);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;
            }
        });

        String a = vlm.getDate() + " " + vlm.getTime();
        tv2.setText(a);
        tv4.setText(vlm.getEngine());
        tv6.setText(vlm.getRemarks());
    }

    private void bounceMarker (final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/duration), 0);
                marker.setAnchor(0.5f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 25);
                } else {
                    bounceMarker(marker);
                }
            }
        });
    }
}
