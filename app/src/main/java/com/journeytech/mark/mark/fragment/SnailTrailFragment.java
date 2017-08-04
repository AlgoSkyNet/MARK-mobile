package com.journeytech.mark.mark.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.BottomSheetModalFragment;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.model.LocationHolder;

import java.util.ArrayList;

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
import static com.journeytech.mark.mark.fragment.VehicleMapFragment.vm;

public class SnailTrailFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    public static ArrayList<LocationHolder> list_location;

    public static GoogleMap mMap;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    public interface NetworkAPI {
        @POST("snailtrail.php")
        @Headers({"Content-Type:application/json; charset=UTF-8"})
        Call<JsonElement> loginRequest(@Body SnailTrailPojo body);
    }

    public static class SnailTrailPojo {
        String platenum;
        String datetimefrom;
        String datetimeto;
        String client_table;

        public SnailTrailPojo(String platenum, String datetimefrom, String datetimeto, String client_table) {
            this.platenum = platenum;
            this.datetimefrom = datetimefrom;
            this.datetimeto = datetimeto;
            this.client_table = client_table;
        }
    }

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

        new GetSnailTrail().execute();
    }

    class GetSnailTrail extends AsyncTask<Void, Void, Void> {

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

            SnailTrailPojo loginRequest = new SnailTrailPojo(vm.getPlate_num(), BottomSheetModalFragment.dateFrom, "08/04/2017 23:59:59", client_table);

            System.out.println(vm.getPlate_num()+client_table+ BottomSheetModalFragment.dateFrom+" asdas");

            Call<JsonElement> call = networkAPI.loginRequest(loginRequest);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    // success response
                    if (response.body().isJsonArray()) {
                        JsonArray objectWhichYouNeed = response.body().getAsJsonArray();
//                    System.out.println(response.body() + " "+ "Response");

                        for (int i = 0; i < response.body().getAsJsonArray().size(); i++) {

                            JsonElement location_array = response.body().getAsJsonArray().get(i);
                            JsonObject location_obj = location_array.getAsJsonObject();
                            String location = location_obj.get("location").toString();

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

                            JsonElement remarks_array = response.body().getAsJsonArray().get(i);
                            JsonObject remarks_obj = remarks_array.getAsJsonObject();
                            String remarks = remarks_obj.get("remarks").toString();

                            if (lat != null && !lat.equals("null") && (lng != null && !lng.equals("null"))) {
                                Double d = Double.parseDouble(lat);
                                Double d2 = Double.parseDouble(lng);
                                createMarker(1, d, d2, "");
                            }


                        }
                    } else {
                        System.out.println("Not a JSONArray.");
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    // failure response
                    System.out.println("Fail " + call.toString());
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

    public void createMarker(int index, Double latitude, Double longitude, String snippet) {
        // Adding the taped point to the ArrayList
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(snippet)
                .snippet(snippet)
                .icon(image));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

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
