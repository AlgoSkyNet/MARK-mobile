package com.journeytech.mark.mark.map_fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.model.VehicleMap;

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

import static com.journeytech.mark.mark.R.id.engine;
import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.markutype;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    public static GoogleMap mMapFragment;

    Context context;
    public static Activity activity;
    public static FragmentManager fm;
    public static VehicleMap vm;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    public static ArrayList<Marker> list = null;

    public static Double latitudeG, longitudeG;

    public static String location;

    String da, loca, plate_n;

    public static String[] container;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        MainActivity csActivity;
        csActivity = (MainActivity) getActivity();
        csActivity.getSupportActionBar().setTitle("Map View");


        Bundle arguments = getArguments();
//        lati =  arguments.getDouble("Lat");
//        longi = arguments.getDouble("Long");

        return v;
    }

    public void onBackPressed() {
        return;
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
            pDialog.setMax(100);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            vehicleRequest_validate();
        }

    }

    public void vehicleRequest_validate() {

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
                    System.out.println(objectWhichYouNeed + " Object");

                    list = new ArrayList<>();

                    for (int i = 0; i < response.body().getAsJsonArray().size(); i++) {
                        JsonElement je = response.body().getAsJsonArray().get(i);
                        JsonObject jo = je.getAsJsonObject();

                        String plate_n = jo.get("plate_num").toString();
                        String plate_nString = plate_n;
                        plate_nString = plate_nString.replace("\"", "");
                        String plate_num = String.valueOf(plate_nString);

                        String gps_num = jo.get("gps_num").toString();

                        String dat = jo.get("date").toString();
                        String date = dat.replace("\"", "");

                        String tim = jo.get("time").toString();
                        String time = tim.replace("\"", "");

                        String loca = jo.get("location").toString();
                        location = loca.replace("\"", "");

                        String lati = jo.get("lat").toString();
                        String latiString = lati;
                        latiString = latiString.replace("\"", "");
                        String lat = String.valueOf(latiString);

                        String longi = jo.get("lng").toString();
                        String longiString = longi;
                        longiString = longiString.replace("\"", "");
                        String lng = String.valueOf(longiString);

                        String engi = jo.get("engine").toString();
                        String engine = engi.replace("\"", "");

                        String remar = jo.get("remarks").toString();
                        String remarks = remar.replace("\"", "");

                        if (lat != null && !lat.equals("null")
                                && (lng != null && !lng.equals("null")
                                || (lat != "" && lat != "")
                                && lng != "") && (lng != "")) {

                            Double d = Double.parseDouble(lat);
                            Double d2 = Double.parseDouble(lng);

                            MarkerOptions opts = createMarker(date, time, location, engine, remarks, d, d2, plate_num);
                            Marker m = mMapFragment.addMarker(opts);
                            list.add(m);

                            System.out.print(list + " Snippp");
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
    }

    public MarkerOptions createMarker(String Date, final String Time, String Location, final String Engine,
                                      final String Remarks, Double latitude, final Double longitude, final String Plate_num) {
        da = Date;
        loca = Location;

        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        System.out.println(Location + " lLocation");

/*        mMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title("Plate No.")
                .snippet(Plate_num)
                .icon(image));*/

        mMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9.0f));

        mMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                vm = new VehicleMap();

                vm.setPlate_num(marker.getSnippet());

                latitudeG = marker.getPosition().latitude;
                longitudeG = marker.getPosition().longitude;

                marker.showInfoWindow();

                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetModalMapFragment(activity);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

                return true;
            }
        });

        mMapFragment.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents_vehicle_map, null);
                TextView plate_nu = (TextView) v.findViewById(R.id.plate_num);
//                TextView dat = (TextView) v.findViewById(R.id.date);
//                TextView tim = (TextView) v.findViewById(R.id.time);
//                TextView loc = (TextView) v.findViewById(R.id.location);
//                TextView engin = (TextView) v.findViewById(engine);
//                TextView remark = (TextView) v.findViewById(R.id.rema);
                plate_nu.setText(marker.getSnippet());
//                dat.setText(da);
//                tim.setText(Time);
//                loc.setText(loca);
//                engin.setText(Engine);
//                remark.setText(Remarks);

                return v;
            }
        });

        container = new String[] {Plate_num, Date, Time, Location, Engine, Remarks};

        return new MarkerOptions()

                .position(new LatLng(latitude, longitude))
                .snippet("Plate No.: " +container[0] + "\n"
                        +"Last Seen: " + container[1] + " " + container[2]+ "\n"+
                        "Location: " +
                        container[3] +
                        "\n"+"Engine: "+ container[4] + "\n" + "Remarks: " +
                        container[5])
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    }

    public void onMapReady(GoogleMap googleMap) {
        mMapFragment = googleMap;
 /*       LatLng latlong = new LatLng(12.405888, 123.273419);
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latlong, 16);
        mMap.moveCamera(cameraPosition);
        mMap.animateCamera(cameraPosition);*/

        new GetVehicles().execute();
        mMapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 6));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 13));

        setUpMap();
    }

    /*set up map*/
    private void setUpMap() {
        mMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapFragment.setTrafficEnabled(true);
        mMapFragment.setIndoorEnabled(true);
        mMapFragment.getUiSettings().setMyLocationButtonEnabled(true);
        mMapFragment.setBuildingsEnabled(true);
        mMapFragment.getUiSettings().setZoomControlsEnabled(false);

        //set marker here
    }

}
