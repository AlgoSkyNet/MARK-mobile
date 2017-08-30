package com.journeytech.mark.mark.fragment_unused;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.maps.android.SphericalUtil;
import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.drawroute.DataParser;
import com.journeytech.mark.mark.model.LocationHolder;

import org.json.JSONArray;
import org.json.JSONException;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleDetailsProximityNavigationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();

//        lati = getArguments().getDouble("Lat");
//        longi = getArguments().getDouble("Long");

        return v;
    }

    public static void createSnailTrail(int index, String latitude, String longitude, String snippet) {
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
        /*map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(this.getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                TextView snippet3 = new TextView(getContext());
                snippet3.setTextColor(Color.GRAY);
                snippet3.setText(marker.getSnippet());


                info.addView(title);
                info.addView(snippet);
                info.addView(snippet3);

                return info;
            }
        });*/

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
/*         map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                for (int i = 0; i < list_location.size(); i++) {
                    Log.d("TAG", "getLatitude:" + list_location.get(i).getLatitude() + " getLongitude:" + list_location.get(i).getLongitude());
                    if (marker.getSnippet().equals(list_location.get(i).getLocation())) {
                        Log.d("TAG", "Selected Marker");
                    }
                }
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View myContentView = inflater.inflate(R.layout.marker_popup, null);
                TextView loc = ((TextView) myContentView
                        .findViewById(R.id.textView2));
                TextView plate_num = ((TextView) myContentView
                        .findViewById(R.id.textView4));
                for (int i = 0; i < list_location.size(); i++) {
                    Log.d("TAG", "getLatitude:" + list_location.get(i).getLatitude() + " getLongitude:" + list_location.get(i).getLongitude());
                    if (marker.getSnippet().equals(list_location.get(i).getLocation())) {
                        Log.d("TAG", "Selected Marker");
                    }
                    loc.setText(list_location.get(i).getLatitude());
                    plate_num.setText(list_location.get(i).getLongitude());
                }



*//*                Button btnDismiss = (Button) myContentView.findViewById(R.id.dismissbtn);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });*//*
                return myContentView;
            }
        });*/
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
        }

    }

    Location l;

    public void setLocation(Location loc) {
        l = loc;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        origin = new LatLng(lati, longi);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longi))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9.0f));

    }

    public static void createProximity(String latitude, String longitude) {
        Double lat = Double.parseDouble(latitude);
        Double Longitude = Double.parseDouble(longitude);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Longitude), 9.0f));
    }

    public static void createNavigation(String latitude, String longitude) {
        origin = new LatLng(lati, longi);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longi))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 12.0f));

        Double lat = Double.parseDouble(latitude);
        Double Longitude = Double.parseDouble(longitude);

        LatLng dest = new LatLng(lat, Longitude);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        Location locationA = new Location("point A");

        locationA.setLatitude(lati);
        locationA.setLongitude(longi);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat);
        locationB.setLongitude(Longitude);

        float distance = locationA.distanceTo(locationB);
        String meter = String.valueOf(distance);
        Toast.makeText(activity, meter, Toast.LENGTH_SHORT).show();

        String url = getUrl(origin, dest);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    private static String getUrl(LatLng origin, LatLng dest) {

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
    private static String downloadUrl(String strUrl) throws IOException {
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
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private static class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
/*
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


}
