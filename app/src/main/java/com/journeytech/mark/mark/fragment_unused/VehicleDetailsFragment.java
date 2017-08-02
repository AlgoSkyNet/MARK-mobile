package com.journeytech.mark.mark.fragment_unused;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.model.LocationHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleDetailsFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/immo_dev/mark_live_active/snailtrailApi.php?timeFrom=07%2F05%2F2017+00%3A00%3A00&timeTo=07%2F05%2F2017+23%3A59%3A59&plateNo=AJA5963&server=active";

    public static ArrayList<LocationHolder> list_location;

    public static GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;

    Context context;
    static Activity activity;

    static double lati = 0.0;
    static double longi = 0.0;
    static LatLng origin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        new GetVehicleValue().execute();
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
            String account = "",
                    trxdate = "",
                    trxtime = "",
                    longitude = "",
                    latitude = "",
                    lat = "",
                    location = "",
                    direction = "",
                    compass = "",
                    id = "",
                    remarks = "",
                    status = "",
                    kmrun = "",
                    speed = "",
                    totalkm = "",
                    engine = "",
                    plateno = "";

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("snailtrail_data");

                    list_location = new ArrayList<>();
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject jb = contacts.getJSONObject(i);
                        account = jb.getString("account");
                        trxdate = jb.getString("trxdate");
                        trxtime = jb.getString("trxtime");
                        longitude = jb.getString("long");
                        latitude = jb.getString("lat");
                        location = jb.getString("location");
                        direction = jb.getString("direction");
                        compass = jb.getString("compass");
                        id = jb.getString("id");
                        remarks = jb.getString("remarks");
                        status = jb.getString("status");
                        kmrun = jb.getString("kmrun");
                        speed = jb.getString("speed");
                        totalkm = jb.getString("totalkm");
                        engine = jb.getString("engine");
                        plateno = jb.getString("plateno");

                        list_location.add(new LocationHolder(longitude, latitude, location, direction));
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

            // Instantiating the class PolylineOptions to plot polyline in the map
            final PolylineOptions polylineOptions = new PolylineOptions();
//            al = list_location;
            for (int i = 0; i < list_location.size(); i++) {
                // Setting the color of the polyline
                polylineOptions.color(Color.RED);

                // Setting the width of the polyline
                polylineOptions.width(3);

                Double lat = Double.parseDouble(list_location.get(i).getLatitude());
                Double Longitude = Double.parseDouble(list_location.get(i).getLongitude());

                // Setting points of polyline
                polylineOptions.add(new LatLng(lat, Longitude));

                createSnailTrail(i, list_location.get(i).getLatitude(), list_location.get(i).getLongitude(), list_location.get(i).getLocation());
            }

            // Adding the polyline to the map
            mMap.addPolyline(polylineOptions);
        }

    }

    public void createSnailTrail(int index, String latitude, String longitude, String snippet) {
        // Adding the taped point to the ArrayList
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);
        Double lat = Double.parseDouble(latitude);
        Double Longitude = Double.parseDouble(longitude);
/*        float color = 0;
        if (index == 1)
            color = BitmapDescriptorFactory.HUE_AZURE;
        else if (index == list_location.size()-1)
            color = BitmapDescriptorFactory.HUE_VIOLET;*/

        if (index == 1)
            image = BitmapDescriptorFactory.fromResource(R.drawable.vehicle_list);
        else {
            image = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        }
        if (index == list_location.size() - 1)
            image = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title(snippet)
                .snippet(list_location.get(index).getLatitude())
                .icon(image));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Longitude), 12.0f));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
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
            View v = getActivity().getLayoutInflater().inflate(R.layout.marker_popup, null);
            Button b = (Button) v.findViewById(R.id.button2);
            b.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    marker.hideInfoWindow();
                }
            });
            TextView markerLabel = (TextView) v.findViewById(R.id.textView2);
            markerLabel.setText("");

            return v;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 13.0f));
    }

}
