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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1.json";

    ArrayList<HashMap<String, String>> contactList;
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
        contactList = new ArrayList<>();

        lv = (ListView) v.findViewById(R.id.list);

        TextView tv = (TextView) v.findViewById(R.id.tv3);
        tv.setText(a);
        Toast.makeText(getContext(), a, Toast.LENGTH_SHORT).show();
        new GetContacts().execute();
        return v;
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

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

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("plate_num", plate_num);
                        contact.put("gps_num", gps_num);
                        contact.put("location", location);
                        contact.put("date", date);
                        contact.put("time", time);
                        contact.put("lat", lat);
                        contact.put("lng", lng);
                        contact.put("engine", engine);
                        contact.put("remarks", remarks);

                        a = contact.get("date");

                        // adding contact to contact list
                        contactList.add(contact);
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

            ListAdapter adapter = new SimpleAdapter(getActivity(), contactList,
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
