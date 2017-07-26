package com.journeytech.mark.mark.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.SnailTrail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleFragment extends Fragment {

    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1.json";

    public static ArrayList<HashMap<String, String>> vehicle;
    SnailTrail g = new SnailTrail();
    TextView tv;
    String a = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vehicle, container, false);
        vehicle = new ArrayList<>();

        lv = (ListView) v.findViewById(R.id.list);

        TextView tv = (TextView) v.findViewById(R.id.tv3);
        tv.setText(a);
        Toast.makeText(getContext(), a, Toast.LENGTH_SHORT).show();
        new GetVehicles().execute();
        return v;
    }

    private class GetVehicles extends AsyncTask<Void, Void, Void> {

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
                JSONObject data = null, jsonObj = null, jsonInner, jInner;
                JSONArray jsonArray = null;
                JSONArray getter = null;
                try {
                    JSONArray jr = new JSONArray(jsonStr);

                    vehicle = new ArrayList<>();
                    for(int i=0;i<jsonStr.length();i++) {
                        JSONObject jb = (JSONObject)jr.getJSONObject(i);
                        plate_num = jb.getString("plate_num");
                        gps_num = jb.getString("gps_num");
                        location = jb.getString("location");
                        date = jb.getString("date");
                        time = jb.getString("time");
                        lat = jb.getString("lat");
                        lng = jb.getString("lng");
                        engine = jb.getString("engine");
                        remarks = jb.getString("remarks");

                        // tmp hash map for detail [single]
                        HashMap<String, String> details = new HashMap<>();

                        // adding each child node to HashMap key => value
                        details.put("plate_num", plate_num);
                        details.put("gps_num", gps_num);
                        details.put("location", location);
                        details.put("date", date);
                        details.put("time", time);
                        details.put("lat", lat);
                        details.put("lng", lng);
                        details.put("engine", engine);
                        details.put("remarks", remarks);

                        a = details.get("date");

                        // adding vehicle to vehicle list
                        vehicle.add(details);
                    }
                } catch (final JSONException e) {

                }
            } else {

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

            ListAdapter adapter = new SimpleAdapter(getActivity(), vehicle,
                    R.layout.list_vehicle, new String[]{"plate_num", "gps_num",
                    "location", "date", "time", "lat", "lng", "engine",
                    "remarks", "remarks", "remarks", "remarks", "remarks", "remarks", "remarks", "remarks"},
                    new int[]{R.id.account,R.id.trxdate, R.id.trxtime, R.id.longitude, R.id.latitude, R.id.location,
                            R.id.direction, R.id.compass, R.id.id, R.id.remarks, R.id.status, R.id.kmrun, R.id.speed,
                            R.id.totalkm, R.id.engine, R.id.plateno});

            /*tv = (TextView) findViewById(R.id.tv);
            tv.setText(g.getAccount()+ " "+g.getTrxdate()+ " " +g.getTrxtime()+ "\n" +g.getLongitude()+ "\n" +g.getLatitude()+ " " +g.getLocation()+ " " +g.getDirection()+ " "+
                    g.getCompass()+ " " + g.getId() + " "+g.getRemarks()+ " " + g.getStatus() + " "+g.getKmrun()+ " "+
                    g.getSpeed()+ " "+g.getTotalkm()+ " "+g.getEngine()+g.getPlateno());*/
            lv.setAdapter(adapter);
        }

    }
}
