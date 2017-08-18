package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.model.VehicleHolder;

import java.util.ArrayList;

import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.mMapFragment;


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

    public static String plate_num = "", latitude, longitude;
    public static String date, time, location, engine, remarks;

    TextView tv2, tv4, tv6;

    public static Double latitudeListMap, longitudeListMap;

    Marker m;

    Handler handler = new Handler();

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

        MainActivity csActivity;
        csActivity = (MainActivity) getActivity();
        csActivity.getSupportActionBar().setTitle("Map View");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vehicle_list_map, container, false);
        tv2 = (TextView) v.findViewById(R.id.tv2);
        tv4 = (TextView) v.findViewById(R.id.tv4);
        tv6 = (TextView) v.findViewById(R.id.tv6);

        plate_num = getArguments().getString("plate_num");
        date = getArguments().getString("date");
        time = getArguments().getString("time");
        location = getArguments().getString("location");
        latitude = getArguments().getString("latitude");
        longitude = getArguments().getString("longitude");
        engine = getArguments().getString("engine");
        remarks = getArguments().getString("remarks");

        String a = date + " " + time;
        tv2.setText(a);
        tv4.setText(engine);
        tv6.setText(remarks);

/*        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                new GetVehicles().execute();
                handler.postDelayed(this, 60 * 1000);
            }
        };

        handler.postDelayed(refresh, 60 * 1000);*/

        return v;
    }

/*    @Override
    public void onDetach() {
        FragmentManager manager;
        VehicleListFragment vehicleListFragment;
        vehicleListFragment = new VehicleListFragment();
        manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, vehicleListFragment).commit();
        super.onDetach();
    }*/

    public void onMapReady(GoogleMap googleMap) {
        mMapVehicleListMapFragment = googleMap;

        mMapFragment.clear();
        mMapVehicleListMapFragment.clear();

        setUpMap();

//        new GetMap().execute();
    }

    private class GetMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

    /*set up map*/
    private void setUpMap() {
        mMapVehicleListMapFragment.clear();
        mMapVehicleListMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapVehicleListMapFragment.setTrafficEnabled(true);
        mMapVehicleListMapFragment.setIndoorEnabled(true);
        mMapVehicleListMapFragment.setBuildingsEnabled(true);
        mMapVehicleListMapFragment.getUiSettings().setZoomControlsEnabled(false);

        final Double lati = Double.parseDouble(latitude);
        final Double longi = Double.parseDouble(longitude);

        m = mMapVehicleListMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longi))
                .anchor(0.5f, 0.5f)
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        mMapVehicleListMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.showInfoWindow();
            }
        });

        mMapVehicleListMapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15.0f));
        bounceMarker();

        mMapVehicleListMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                latitudeListMap = marker.getPosition().latitude;
                longitudeListMap = marker.getPosition().longitude;

                BottomSheetModalListFragment bottomSheetDialogFragment = new BottomSheetModalListFragment(activity);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }

                return true;
            }
        });

        mMapVehicleListMapFragment.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

        m.showInfoWindow();
    }

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents_list_vehicle_map, null);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView snippet = (TextView) v.findViewById(R.id.snippet);
            snippet.setVisibility(View.GONE);
            title.setText("Location: " + location);
//            snippet.setText("Engine: " + engine);

            return v;
        }
    }

    private void bounceMarker() {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                m.setAnchor(0.5f, 1.0f + t);
                if (t > 0.0) {
                    handler.postDelayed(this, 25);
                }
            }
        });
    }
}
