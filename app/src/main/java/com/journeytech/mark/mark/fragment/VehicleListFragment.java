package com.journeytech.mark.mark.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.fragment_unused.VehicleListMapFragment;
import com.journeytech.mark.mark.model.VehicleListMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleListFragment extends Fragment {

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1.json";

    public static ArrayList<HashMap<String, String>> vehicle;
    public static VehicleListMap vlm;
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
        lv = (ListView) v.findViewById(R.id.list);

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
                try {
                    JSONArray jr = new JSONArray(jsonStr);

                    vehicle = new ArrayList<>();
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

                        vlm = new VehicleListMap();

                        vlm.setLoc(location);
                        vlm.setDate(date);
                        vlm.setEngine(engine);
                        vlm.setLati(lat);
                        vlm.setLongi(lng);
                        vlm.setRemarks(remarks);
                        vlm.setTime(time);

                        // adding vehicle to vehicle list
                        vehicle.add(details);
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

            /**
             * Updating parsed JSON data into ListView
             * */
            final ListAdapter adapter = new SimpleAdapter(getActivity(), vehicle,
                    R.layout.list_vehicle, new String[]{"plate_num", "gps_num",
                    "location", "date", "time", "lat", "lng", "engine",
                    "remarks"},
                    new int[]{ R.id.plate_num, R.id.gps_num, R.id.location, R.id.date, R.id.time, R.id.latitude, R.id.longitude,
                            R.id.engine, R.id.remarks });

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                    Fragment mFragment = new VehicleListMapFragment(getActivity(), getActivity());
                    getFragmentManager().beginTransaction().replace(R.id.mainLayout, mFragment).commit();
//                    Toast.makeText(getContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("geoLoc", adapter.getItem(position).toString());
                    mFragment.setArguments(bundle);
                }
            });

        }

    }
}