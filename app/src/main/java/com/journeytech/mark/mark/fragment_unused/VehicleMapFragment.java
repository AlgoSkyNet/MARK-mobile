package com.journeytech.mark.mark.fragment_unused;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journeytech.mark.mark.BottomSheetModalFragment;
import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.model.LocationHolder;
import com.journeytech.mark.mark.model.VehicleHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1 - Copy.json";

    public static GoogleMap mMapFragment;
    public static ArrayList<VehicleHolder> vehicleHolder;

    Context context;
    static Activity activity;

    public VehicleMapFragment(Context c, Activity a) {
        context = c;
        activity = a;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        new GetVehicleValue().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        return v;
    }

    public void createMarker(int index, String latitude, String longitude, String vehicle) {
        // Adding the taped point to the ArrayList
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);
        Double lat = Double.parseDouble(latitude);
        Double Longitude = Double.parseDouble(longitude);
        float color = 0;

        mMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet"  )
                .icon(image));

        mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Longitude), 9.0f));

        mMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetModalFragment(activity);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;
            }
        });

        mMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();

            }
        });
        mMapFragment.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {

        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
           View v = activity.getLayoutInflater().inflate(R.layout.marker_popup, null);

/*            BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetModalFragment();
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());*/
/*             Button b = (Button) v.findViewById(R.id.button2);
            b.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    marker.hideInfoWindow();
                }
            });
            TextView markerLabel = (TextView) v.findViewById(R.id.textView2);
            markerLabel.setText("");*/

            return v;
        }
    }

    class GetVehicleValue extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            String plate_num = "",
                    gps_num = "",
                    location = "",
                    date = "",
                    time = "",
                    lat = "",
                    lng = "",
                    engine = "",
                    remarks = "";
            if (jsonStr != null) {
                try {
                    JSONArray jr = new JSONArray(jsonStr);

                    vehicleHolder = new ArrayList<>();
                    for (int i = 0; i < jsonStr.length(); i++) {
                        JSONObject jb = (JSONObject) jr.getJSONObject(i);
                        plate_num = jb.getString("plate_num");
                        gps_num = jb.getString("gps_num");
                        location = jb.getString("location");
                        date = jb.getString("date");
                        time = jb.getString("time");
                        lat = jb.getString("lat");
                        lng = jb.getString("lng");
                        engine = jb.getString("engine");
                        remarks = jb.getString("remarks");

                        vehicleHolder.add(new VehicleHolder(lat, lng));

                        SharedPreferences preferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = preferences.edit();

                        Gson gson = new Gson();

                        String jsonText = gson.toJson(vehicleHolder );
                        prefsEditor.putString("key", jsonText);
                        prefsEditor.commit();
                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("JSON:", "is null");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            for(int i =0; i < vehicleHolder.size(); i++) {
                createMarker(i,vehicleHolder.get(i).getLatitude(),vehicleHolder.get(i).getLongitude(),vehicleHolder.get(i).getVehicle());
            }

//            final List<LocationHolder> addArray  = new ArrayList<>();
            Gson gson = new Gson();
            SharedPreferences myPrefs;
            myPrefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String jsonText = myPrefs.getString("key", null);

            Type collectionType = new TypeToken<List<LocationHolder>>(){}.getType();
            List<LocationHolder> addArray= (List<LocationHolder>) new Gson()
                    .fromJson( jsonText , collectionType);

//            addArray = Arrays.asList(text);
//            addArray = new ArrayList(addArray);

            for(int i = 0; i < addArray.size(); i++) {
                String lati = addArray.get(i).getLatitude();
//                Toast.makeText(context, lati.toString() /*+ addArray.toString()*/, Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void onMapReady(GoogleMap googleMap) {
        mMapFragment = googleMap;
 /*       LatLng latlong = new LatLng(12.405888, 123.273419);
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latlong, 16);
        mMap.moveCamera(cameraPosition);
        mMap.animateCamera(cameraPosition);*/

        mMapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 6));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 13));

        setUpMap();
    }

    /*set up map*/
    private void setUpMap() {
        mMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapFragment.setTrafficEnabled(true);
        mMapFragment.setIndoorEnabled(true);
        mMapFragment.setBuildingsEnabled(true);
        mMapFragment.getUiSettings().setZoomControlsEnabled(true);

        //set marker here
    }

}
