package com.journeytech.mark.mark;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.fragment.VehicleDetailsFragment;
import com.journeytech.mark.mark.model.LocationHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.journeytech.mark.mark.fragment.MapFragment.mMapFragment;

public class BottomSheetModalFragment extends BottomSheetDialogFragment {

    ProgressDialog pDialog;
    public ArrayList<LocationHolder> list_location;

    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet3, null);
        dialog.setContentView(contentView);
        BottomSheetBehavior mBottomSheetBehavior;
        mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight(500);
            contentView.requestLayout();
        }
        ImageButton snailtrail = (ImageButton) contentView.findViewById(R.id.snailtrail);
        ImageButton proximity = (ImageButton) contentView.findViewById(R.id.proximity);
        ImageButton navigation = (ImageButton) contentView.findViewById(R.id.navigation);
        snailtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Snail Trail", Toast.LENGTH_SHORT).show();
                VehicleDetailsFragment mapFragment;
                FragmentManager manager;
                mapFragment = new VehicleDetailsFragment();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();

//                new GetVehicleValue().execute();
                dismiss();
            }
        });
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Proximity ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Navigation", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    class GetVehicleValue extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            mMapFragment.clear();
        }

        private String url = "http://mark.journeytech.com.ph/immo_dev/mark_live_active/snailtrailApi.php?timeFrom=07%2F05%2F2017+00%3A00%3A00&timeTo=07%2F05%2F2017+23%3A59%3A59&plateNo=AJA5963&server=active";
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

            for (int i = 0; i < list_location.size(); i++) {
                // Setting the color of the polyline
                polylineOptions.color(Color.RED);

                // Setting the width of the polyline
                polylineOptions.width(3);

                Double lat = Double.parseDouble(list_location.get(i).getLatitude());
                Double Longitude = Double.parseDouble(list_location.get(i).getLongitude());

                // Setting points of polyline
                polylineOptions.add(new LatLng(lat, Longitude));

                createMarker(i, list_location.get(i).getLatitude(), list_location.get(i).getLongitude(), list_location.get(i).getLocation());
            }

            // Adding the polyline to the map
            mMapFragment.addPolyline(polylineOptions);
        }

    }

    public void createMarker(int index, String latitude, String longitude, String snippet) {
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

        mMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title(snippet)
                .snippet(list_location.get(index).getLatitude())
                .icon(image));

        mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Longitude), 12.0f));

        mMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
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


}
