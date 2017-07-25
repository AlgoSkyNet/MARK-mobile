package com.journerytech.mark.mark.Fragment;


import android.app.ProgressDialog;
import android.location.LocationManager;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.journerytech.mark.mark.HttpHandler;
import com.journerytech.mark.mark.LocationHolder;
import com.journerytech.mark.mark.R;
import com.journerytech.mark.mark.SnailTrail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    SnailTrail st;
    public static GoogleMap map, map2;
    PopupWindow popupWindow;
    LocationManager mLocationManager;

    Button b;

    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/immo_dev/mark_live_active/snailtrailApi.php?timeFrom=07%2F05%2F2017+00%3A00%3A00&timeTo=07%2F05%2F2017+23%3A59%3A59&plateNo=AJA5963&server=active";

    ArrayList<SnailTrail> stt = new ArrayList<SnailTrail>();
    SnailTrail g = new SnailTrail();

    public static ArrayList<LocationHolder>list_location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
/*
        // Instantiating the class PolylineOptions to plot polyline in the map
        final PolylineOptions polylineOptions = new PolylineOptions();

        b = (Button) v.findViewById(R.id.b1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                map.addPolyline(polylineOptions);
            }
            });*/
        return v;
    }

    public static void createMarker(int index, String latitude, String longitude, String snippet) {
        // Adding the taped point to the ArrayList
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera);
        Double lat = Double.parseDouble(latitude);
        Double Longitude = Double.parseDouble(longitude);
/*        float color = 0;
        if (index == 1)
            color = BitmapDescriptorFactory.HUE_AZURE;
        else if (index == list_location.size()-1)
            color = BitmapDescriptorFactory.HUE_VIOLET;*/

        if (index == 1)
            image = BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_gallery);
        else if (index == list_location.size()-1)
            image = BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_slideshow);
        else {
            image = BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera);
        }

        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, Longitude))
                .anchor(0.5f, 0.5f)
                .title(snippet)
                .snippet(list_location.get(index).getLatitude())
                .icon(image));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Longitude), 12.0f));
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

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
/*        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

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
        public View getInfoContents(Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.marker_popup, null);
/*            for (int i = 0; i < list_location.size(); i++) {
                Log.d("TAG", "getLatitude:" + list_location.get(i).getLatitude() + " getLongitude:" + list_location.get(i).getLongitude());
                if (marker.getSnippet().equals(list_location.get(i).getLocation())) {
                    Log.d("TAG", "Selected Marker");

                }

            }*/
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

        new GetContacts().execute();
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

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

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("snailtrail_data");

                    list_location=new ArrayList<>();
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String account = c.getString("account");
                        String trxdate = c.getString("trxdate");
                        String trxtime = c.getString("trxtime");
                        String longitude = c.getString("long");
                        String latitude = c.getString("lat");
                        String location = c.getString("location");
                        String direction = c.getString("direction");
                        String compass = c.getString("compass");
                        String id = c.getString("id");
                        String remarks = c.getString("remarks");
                        String status = c.getString("status");
                        String kmrun = c.getString("kmrun");
                        String speed = c.getString("speed");
                        String totalkm = c.getString("totalkm");
                        String engine = c.getString("engine");
                        String plateno = c.getString("plateno");

                        list_location.add(new LocationHolder(longitude,latitude,location,direction));
                    }

                   /* // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String account = c.getString("account");
                        g.setAccount(account);
                        String trxdate = c.getString("trxdate");
                        g.setTrxdate(trxdate);
                        String trxtime = c.getString("trxtime");
                        g.setTrxtime(trxtime);
                        String longitude = c.getString("long");
                        g.setLongitude(longitude);
                        String latitude = c.getString("lat");
                        g.setLatitude(latitude);
                        String location = c.getString("location");
                        g.setLocation(location);
                        String direction = c.getString("direction");
                        g.setDirection(direction);
                        String compass = c.getString("compass");
                        g.setCompass(compass);
                        String id = c.getString("id");
                        g.setId(id);
                        String remarks = c.getString("remarks");
                        g.setRemarks(remarks);
                        String status = c.getString("status");
                        g.setStatus(status);
                        String kmrun = c.getString("kmrun");
                        g.setKmrun(kmrun);
                        String speed = c.getString("speed");
                        g.setSpeed(speed);
                        String totalkm = c.getString("totalkm");
                        g.setTotalkm(totalkm);
                        String engine = c.getString("engine");
                        g.setEngine(engine);
                        String plateno = c.getString("plateno");
                        g.setPlateno(plateno);

                        // tmp hash map for single contact
                        HashMap<String, String> vehicle = new HashMap<>();

                        // adding each child node to HashMap key => value
                        vehicle.put("account", account);
                        vehicle.put("trxdate", trxdate);
                        vehicle.put("trxtime", trxtime);
                        vehicle.put("longitude", longitude);
                        vehicle.put("latitude", latitude);
                        vehicle.put("location", location);
                        vehicle.put("direction", direction);
                        vehicle.put("compass", compass);
                        vehicle.put("id", id);
                        vehicle.put("remarks", remarks);
                        vehicle.put("status", status);
                        vehicle.put("kmrun", kmrun);
                        vehicle.put("speed", speed);
                        vehicle.put("totalkm", totalkm);
                        vehicle.put("engine", engine);
                        vehicle.put("plateno", plateno);

                        // adding contact to contact list
                        contactList.add(vehicle);

                    }*/
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


            /**
             * Updating parsed JSON data into ListView
             * */

            /*ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
                    R.layout.list_vehicle, new String[]{"account", "trxdate",
                    "trxtime", "longitude", "latitude", "location", "direction", "compass",
                    "id", "remarks", "status", "kmrun", "speed", "totalkm", "engine", "plateno"}, new int[]{R.id.account,
                    R.id.trxdate, R.id.trxtime, R.id.longitude, R.id.latitude, R.id.location, R.id.direction, R.id.compass,
                    R.id.id, R.id.remarks, R.id.status, R.id.kmrun, R.id.speed, R.id.totalkm, R.id.engine, R.id.plateno});

            tv = (TextView) findViewById(R.id.tv);
            tv.setText(g.getAccount()+ " "+g.getTrxdate()+ " " +g.getTrxtime()+ "\n" +g.getLongitude()+ "\n" +g.getLatitude()+ " " +g.getLocation()+ " " +g.getDirection()+ " "+
                    g.getCompass()+ " " + g.getId() + " "+g.getRemarks()+ " " + g.getStatus() + " "+g.getKmrun()+ " "+
                    g.getSpeed()+ " "+g.getTotalkm()+ " "+g.getEngine()+g.getPlateno());
            lv.setAdapter(adapter);*/
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MarkerOptions option = new MarkerOptions();
        g.setLocation("3rd Floor Sky Freight Building\n" +
                "Ninoy Aquino Avenue,Paranque City\n" +
                "1704 Philippines\n" +
                "Tel. No: +639.2.852-8410 \n" +
                "Fax. No: +639.2.851-8745 ");
        map = googleMap;

        String lat1 =  "-34.8799074";
        String long1 = "174.7565664";
        double latitude = Double.parseDouble(lat1);
        double longitude = Double.parseDouble(long1);

/*        String[] latlong =  "-34.8799074,174.7565664".split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);*/

        LatLng pp = new LatLng(latitude, longitude);

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
                loc.setText(marker.getTitle());

                TextView plate_num = ((TextView) myContentView
                        .findViewById(R.id.textView4));
                plate_num.setText(marker.getSnippet());
/*                Button btnDismiss = (Button) myContentView.findViewById(R.id.dismissbtn);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });*/
                return myContentView;
            }
        });

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pp, 8));

        map.addMarker(new MarkerOptions()
                .position(pp)
                .title(g.getLocation())
                .snippet("SkyFreight")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        map.addMarker(new MarkerOptions()
                .title("India")
                .snippet("New Delhi")
                .position(new LatLng(20.59, 78.96))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

/*        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Toast.makeText(getActivity(), "TOOOAST", Toast.LENGTH_LONG);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(getActivity(), "TOOOAST", Toast.LENGTH_LONG);
                return true;
            }
        });*/

    };


}
