package com.journeytech.mark.mark.list_fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.DirectionsJSONParser;
import com.journeytech.mark.mark.GPSTracker;
import com.journeytech.mark.mark.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.date;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.engine;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.latitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.location;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.longitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.plate_num;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.remarks;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.time;

public class ProximityBottomSheetModalMapFragment extends BottomSheetDialogFragment {

    // GPSTracker class
    GPSTracker gps;

    public static TextView distanc, tim;

    ArrayList<LatLng> markerPoints;

    public static Handler handler = new Handler();

    public static Runnable refresh;

    public static String distan;

    ProgressDialog pDialog;



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

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void onDetach() {
        handler.removeCallbacks(refresh);
        super.onDetach();
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.proximity_bottomsheet3, null);

        TextView plate_n = (TextView) contentView.findViewById(R.id.plate_num);
        distanc = (TextView) contentView.findViewById(R.id.TVDistance);
        tim = (TextView) contentView.findViewById(R.id.TVTime);
        plate_n.setText("PLATE NO.: " + plate_num + "\n" +
                "LAST SEEN: " + date + " " + time + "\n" +
                "LOCATION: " + location + "\n" +
                "ENGINE: " + engine + "\n" +
                "REMARKS: " + remarks);

        Double lati = Double.parseDouble(latitudeListMap);
        Double longi = Double.parseDouble(longitudeListMap);

        LatLng l1 = new LatLng(lati, longi);

        // create class object
        gps = new GPSTracker(getContext());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitudeGPS = gps.getLatitude();
            double longitudeGPS = gps.getLongitude();

            LatLng l2 = new LatLng(latitudeGPS, longitudeGPS);

            LatLng origin = l1;
            LatLng dest = l2;

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        refresh = new Runnable() {
            @Override
            public void run() {
                Double lati = Double.parseDouble(latitudeListMap);
                Double longi = Double.parseDouble(longitudeListMap);

                LatLng l1 = new LatLng(lati, longi);

                gps = new GPSTracker(getContext());

                double latitudeGPS = gps.getLatitude();
                double longitudeGPS = gps.getLongitude();

                LatLng l2 = new LatLng(latitudeGPS, longitudeGPS);

                LatLng origin = l1;
                LatLng dest = l2;

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                handler.postDelayed(this, 60 * 1000);
            }
        };

        handler.postDelayed(refresh, 60 * 1000);

        dialog.setContentView(contentView);
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            // Showing progress dialog
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String duration = "";
            String distance = "";

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

            }

            distanc.setText(distance);
            tim.setText(duration);

            if(distanc.getText().toString().equals(null) || distanc.getText().toString() == "") {
                dismiss();
            }

        }
    }

}
