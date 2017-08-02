package com.journeytech.mark.mark.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.journeytech.mark.mark.activity.MainActivity;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    public static GoogleMap mMapFragment;

    Context context;
    static Activity activity;
    static FragmentManager fm;

    public static String baseUrl ="http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;


    public VehicleMapFragment(Context c, Activity a) {
        context = c;
        activity = a;
    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        new GetVehicles().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

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
            vehicleRequest_validate();
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

    public void vehicleRequest_validate(){

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
                if(response.body().isJsonArray()){
                    JsonArray objectWhichYouNeed = response.body().getAsJsonArray();
                    System.out.println(objectWhichYouNeed);
//                    if(response.body().)
                    for(int i = 0; i< response.body().getAsJsonArray().size(); i++){
                        JsonElement plate_num_array = response.body().getAsJsonArray().get(i);
                        JsonObject plate_num_obj = plate_num_array.getAsJsonObject();
                        String plate_num = plate_num_obj.get("plate_num").toString();

                        JsonElement gps_num_array = response.body().getAsJsonArray().get(i);
                        JsonObject gps_num_obj = gps_num_array.getAsJsonObject();
                        String gps_num = gps_num_obj.get("gps_num").toString();

                        JsonElement location_array = response.body().getAsJsonArray().get(i);
                        JsonObject location_obj = location_array.getAsJsonObject();
                        String location = location_obj.get("location").toString();

                        JsonElement date_array = response.body().getAsJsonArray().get(i);
                        JsonObject date_obj = date_array.getAsJsonObject();
                        String date = date_obj.get("date").toString();

                        JsonElement time_array = response.body().getAsJsonArray().get(i);
                        JsonObject time_obj = time_array.getAsJsonObject();
                        String time = time_obj.get("time").toString();

                        JsonElement lat_array = response.body().getAsJsonArray().get(i);
                        JsonObject lat_obj = lat_array.getAsJsonObject();
                        Double lat = lat_obj.get("lat").getAsDouble();

                        JsonElement lng_array = response.body().getAsJsonArray().get(i);
                        JsonObject lng_obj = lng_array.getAsJsonObject();
                        Double lng = lng_obj.get("lng").getAsDouble();

                        JsonElement engine_array = response.body().getAsJsonArray().get(i);
                        JsonObject engine_obj = engine_array.getAsJsonObject();
                        String engine = engine_obj.get("engine").toString();

                        JsonElement remarks_array = response.body().getAsJsonArray().get(i);
                        JsonObject remarks_obj = remarks_array.getAsJsonObject();
                        String remarks = engine_obj.get("remarks").toString();

                        createMarker(lat, lng);

                        System.out.println(plate_num + "Plate Num");
//                        Toast.makeText(activity, plate_num, Toast.LENGTH_LONG).show();
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
    }

    public void createMarker(Double latitude, Double longitude) {
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        mMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(image));

        mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9.0f));

        mMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetModalFragment();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;
            }
        });

        mMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();

            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMapFragment = googleMap;
 /*       LatLng latlong = new LatLng(12.405888, 123.273419);
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latlong, 16);
        mMap.moveCamera(cameraPosition);
        mMap.animateCamera(cameraPosition);*/

        mMapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 6));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 13));

        setUpMap();
    }

    /*set up map*/
    private void setUpMap() {
        mMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapFragment.setTrafficEnabled(true);
        mMapFragment.setIndoorEnabled(true);
        mMapFragment.setBuildingsEnabled(true);
        mMapFragment.getUiSettings().setZoomControlsEnabled(true);

        //set marker here
    }

}
