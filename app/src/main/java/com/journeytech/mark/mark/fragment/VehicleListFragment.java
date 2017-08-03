package com.journeytech.mark.mark.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.model.VehicleListMap;

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

import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.markutype;

public class VehicleListFragment extends Fragment {

    private ProgressDialog pDialog;
    private ListView lv;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    public ArrayList<HashMap<String, String>> vehicle = new ArrayList<>();
    public static VehicleListMap vlm;
    String a = "";

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
                            JsonElement plate_num_array = response.body().getAsJsonArray().get(i);
                            JsonObject plate_num_obj = plate_num_array.getAsJsonObject();
                            String plate_n = plate_num_obj.get("plate_num").toString();
                            String plate_nString = plate_n;
                            plate_nString = plate_nString.replace("\"", "");
                            String plate_num = String.valueOf(plate_nString);

                            JsonElement gps_num_array = response.body().getAsJsonArray().get(i);
                            JsonObject gps_num_obj = gps_num_array.getAsJsonObject();
                            String gps_num = gps_num_obj.get("gps_num").toString();

                            JsonElement location_array = response.body().getAsJsonArray().get(i);
                            JsonObject location_obj = location_array.getAsJsonObject();
                            String location = location_obj.get("location").toString();

                            JsonElement date_array = response.body().getAsJsonArray().get(i);
                            JsonObject date_obj = date_array.getAsJsonObject();
                            String da = date_obj.get("date").toString();
                            String daString = da;
                            daString = daString.replace("\"", "");
                            String date = String.valueOf(daString);

                            JsonElement time_array = response.body().getAsJsonArray().get(i);
                            JsonObject time_obj = time_array.getAsJsonObject();
                            String ti = time_obj.get("time").toString();
                            String tiString = ti;
                            tiString = tiString.replace("\"", "");
                            String time = String.valueOf(tiString);

                            JsonElement lat_array = response.body().getAsJsonArray().get(i);
                            JsonObject lat_obj = lat_array.getAsJsonObject();
                            String lati = lat_obj.get("lat").toString();
                            String latiString = lati;
                            latiString = latiString.replace("\"", "");
                            String lat = String.valueOf(latiString);

                            JsonElement lng_array = response.body().getAsJsonArray().get(i);
                            JsonObject lng_obj = lng_array.getAsJsonObject();
                            String longi = lng_obj.get("lng").toString();
                            String longiString = longi;
                            longiString = longiString.replace("\"", "");
                            String lng = String.valueOf(longiString);

                            JsonElement engine_array = response.body().getAsJsonArray().get(i);
                            JsonObject engine_obj = engine_array.getAsJsonObject();
                            String en = engine_obj.get("engine").toString();
                            String enString = en;
                            enString = enString.replace("\"", "");
                            String engine = String.valueOf(enString);

                            JsonElement remarks_array = response.body().getAsJsonArray().get(i);
                            JsonObject remarks_obj = remarks_array.getAsJsonObject();
                            String re = remarks_obj.get("remarks").toString();
                            String reString = re;
                            reString = reString.replace("\"", "");
                            String remarks = String.valueOf(reString);

                            if (lat != null && !lat.equals("null") || (lng != null && !lng.equals("null"))) {
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

                                vlm.setPlate_num(plate_num);
                                vlm.setLoc(location);
                                vlm.setDate(date);
                                vlm.setEngine(engine);
                                vlm.setLati(lat);
                                vlm.setLongi(lng);
                                vlm.setRemarks(remarks);
                                vlm.setTime(time);

                                // adding vehicle to vehicle list
                                vehicle.add(details);

                                /**
                                 * Updating parsed JSON data into ListView
                                 * */
                                final ListAdapter adapter = new SimpleAdapter(getActivity(), vehicle,
                                        R.layout.list_vehicle, new String[]{"plate_num", "gps_num",
                                        "location", "date", "time", "lat", "lng", "engine",
                                        "remarks"},
                                        new int[]{R.id.plate_num, R.id.gps_num, R.id.location, R.id.date, R.id.time, R.id.latitude, R.id.longitude,
                                                R.id.engine, R.id.remarks});

                                lv.setAdapter(adapter);

                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                                        Fragment mFragment = new VehicleListMapFragment(getActivity(), getActivity());
                                        getFragmentManager().beginTransaction().replace(R.id.mainLayout, mFragment).commit();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("geoLoc", adapter.getItem(position).toString());
                                        mFragment.setArguments(bundle);
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
            System.out.println(vehicle + "Vehicle1");

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}