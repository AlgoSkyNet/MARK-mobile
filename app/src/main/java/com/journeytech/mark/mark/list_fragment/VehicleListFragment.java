package com.journeytech.mark.mark.list_fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.AlarmSheetModalFragment;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.markutype;
import static com.journeytech.mark.mark.activity.MainActivity.toolbar;

public class VehicleListFragment extends Fragment {

    ProgressDialog pDialog;
    private ListView lv;
    ListAdapter adapter;
    final Handler handler = new Handler();

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    public ArrayList<HashMap<String, String>> vehicle = new ArrayList<HashMap<String, String>>();
    String a = "";

    ArrayList pna;

    Runnable refresh;

    public interface NetworkAPI {
        @POST("vehicle_details.php")
        @Headers({"Content-Type:application/json; charset=UTF-8"})
        Call<JsonElement> loginRequest(@Body VehicleRequestPojo body);
    }

    public static class VehicleRequestPojo {
        String ucsi_num;
        String client_table;
        String markutype;

        public VehicleRequestPojo(String ucsi_num, String client_table, String markutype) {
            this.ucsi_num = ucsi_num;
            this.client_table = client_table;
            this.markutype = markutype;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        searchItem.setVisible(false);
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(refresh);
        super.onDestroyView();

    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //This will clear the vehicle list.
        vehicle.clear();

        if (!getUserVisibleHint())
        {
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmSheetModalFragment bottomSheetDialogFragment = new AlarmSheetModalFragment();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setTitle(R.string.vehicleList);

        refresh = new Runnable() {
            @Override
            public void run() {
                vehicle.clear();
                new GetVehicles().execute();
                handler.postDelayed(this, 60 * 1000);
            }
        };

        handler.postDelayed(refresh, 60 * 1000);

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
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            networkAPI = retrofit.create(NetworkAPI.class);

            VehicleRequestPojo loginRequest = new VehicleRequestPojo(MainActivity.ucsi_num, client_table, markutype);

            Call<JsonElement> call = networkAPI.loginRequest(loginRequest);

            call.enqueue(new Callback<JsonElement>() {

                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    // success response
                    if (response.body().isJsonArray()) {
                        JsonArray objectWhichYouNeed = response.body().getAsJsonArray();
                        System.out.println(objectWhichYouNeed);

                        for (int i = 0; i < response.body().getAsJsonArray().size(); i++) {
                            JsonElement je = response.body().getAsJsonArray().get(i);
                            JsonObject jo = je.getAsJsonObject();

                            String plate_n = jo.get("plate_num").toString();
                            String plate_nString = plate_n;
                            plate_nString = plate_nString.replace("\"", "");
                            String plate_num = String.valueOf(plate_nString);

                            String gps_n = jo.get("gps_num").toString();
                            String gps_nString = gps_n;
                            gps_nString = gps_nString.replace("\"", "");
                            String gps_num = String.valueOf(gps_nString);

                            String locat = jo.get("location").toString();
                            String locatString = locat;
                            locatString = locatString.replace("\"", "");
                            String location = String.valueOf(locatString);

                            String da = jo.get("date").toString();
                            String daString = da;
                            daString = daString.replace("\"", "");
                            String date = String.valueOf(daString);

                            String ti = jo.get("time").toString();
                            String tiString = ti;
                            tiString = tiString.replace("\"", "");
                            String time = String.valueOf(tiString);

                            String lati = jo.get("lat").toString();
                            String latiString = lati;
                            latiString = latiString.replace("\"", "");
                            String lat = String.valueOf(latiString);

                            String longi = jo.get("lng").toString();
                            String longiString = longi;
                            longiString = longiString.replace("\"", "");
                            String lng = String.valueOf(longiString);

                            String en = jo.get("engine").toString();
                            String enString = en;
                            enString = enString.replace("\"", "");
                            String engine = String.valueOf(enString);

                            String re = jo.get("remarks").toString();
                            String reString = re;
                            reString = reString.replace("\"", "");
                            String remarks = String.valueOf(reString);

                            if (lat != null && !lat.equals("null") || (lng != null && !lng.equals("null"))) {
                                // tmp hash map for detail [single]
                                HashMap<String, String> details = new HashMap<String, String>();

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

                                /**
                                 * Updating parsed JSON data into ListView
                                 * */
                                adapter = new SimpleAdapter(_context, vehicle,
                                        R.layout.list_vehicle, new String[]{"plate_num",
                                        "location", "date", "time", "lat", "lng", "engine",
                                        "remarks"},
                                        new int[]{R.id.plate_num, R.id.location, R.id.date, R.id.time, R.id.latitude, R.id.longitude,
                                                R.id.engine, R.id.remarks});

                                lv.setAdapter(adapter);

//                                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();

                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                                        TextView plate_n = (TextView) v.findViewById(R.id.plate_num);
                                        TextView dat = (TextView) v.findViewById(R.id.date);
                                        TextView tim = (TextView) v.findViewById(R.id.time);
                                        TextView loc = (TextView) v.findViewById(R.id.location);
                                        TextView lat = (TextView) v.findViewById(R.id.latitude);
                                        TextView lng = (TextView) v.findViewById(R.id.longitude);
                                        TextView eng = (TextView) v.findViewById(R.id.engine);
                                        TextView rem = (TextView) v.findViewById(R.id.remarks);

                                        String plate_num = plate_n.getText().toString();
                                        String date = dat.getText().toString();
                                        String time = tim.getText().toString();
                                        String location = loc.getText().toString();
                                        String latitude = lat.getText().toString();
                                        String longitude = lng.getText().toString();
                                        String engine = eng.getText().toString();
                                        String remarks = rem.getText().toString();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("plate_num", plate_num);
                                        bundle.putString("date", date);
                                        bundle.putString("time", time);
                                        bundle.putString("location", location);
                                        bundle.putString("latitude", latitude);
                                        bundle.putString("longitude", longitude);
                                        bundle.putString("engine", engine);
                                        bundle.putString("remarks", remarks);

                                        if((!latitude.equals("") && latitude != null) || (!longitude.equals("") && longitude != null) )
                                        {
                                            Fragment mFragment = new VehicleListMapFragment();
                                            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainLayout, mFragment).commit();

                                            mFragment.setArguments(bundle);
                                        } else {
                                            Toast.makeText(getContext(), "Invalid Data.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }

                        }

                    } else {
                        System.out.println("Not a JSONArray.");
                    }

                }


                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    // failure response
                    System.out.println(call.toString());
                }

            });
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
}