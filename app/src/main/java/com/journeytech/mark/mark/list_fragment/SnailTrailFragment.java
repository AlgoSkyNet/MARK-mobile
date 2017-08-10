package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.R;

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
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListMapFragment.dateFrom;
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListMapFragment.dateTo;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.vm;

public class SnailTrailFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    public static GoogleMap mMapSnailTrail;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    Context context;
    static Activity activity;

    public static Double latitude, longitude;

    public SnailTrailFragment(Context c, Activity a) {
        context = c;
        activity = a;
    }

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
    }

    public void onBackPressed() {
        return;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapSnailTrail = googleMap;
        // Remove asynctask since you are already using retrofit enqueue
        // new GetSnailTrail().execute();
        //Create a new method to call api using retrofit
        getDataFromServer();
        mMapSnailTrail.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 6));
    }

    private void getDataFromServer() {

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

        SnailTrailPojo loginRequest = new SnailTrailPojo(vm.getPlate_num(), dateFrom, dateTo, client_table);
        System.out.println(vm.getPlate_num() + dateFrom+ dateTo+ client_table +" JsonArray");
        Call<JsonElement> call = networkAPI.loginRequest(loginRequest);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body().getAsJsonArray().size() != 0) {
                    // Showing progress dialog
                    pDialog = new ProgressDialog(getContext());
                    pDialog.setMessage("Plotting.... Please wait.");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    final PolylineOptions polylineOptions = new PolylineOptions();
                    for (int i = 0; i < response.body().getAsJsonArray().size(); i++) {
                        JsonElement je = response.body().getAsJsonArray().get(i);
                        JsonObject jo = je.getAsJsonObject();

                        String loc = jo.get("location").toString();
                        String locString = loc;
                        locString = locString.replace("\"", "");
                        String location = String.valueOf(locString);

                        String lati = jo.get("lat").toString();
                        String latiString = lati;
                        latiString = latiString.replace("\"", "");
                        String lat = String.valueOf(latiString);

                        String longi = jo.get("lng").toString();
                        String longiString = longi;
                        longiString = longiString.replace("\"", "");
                        String lng = String.valueOf(longiString);

                        String rem = jo.get("remarks").toString();
                        String remString = rem;
                        remString = remString.replace("\"", "");
                        String remarks = String.valueOf(remString);

/*                        SharedPreferences preferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = preferences.edit();

                        Gson gson = new Gson();

                        String jsonText = gson.toJson(list_location);
                        prefsEditor.putString("key", jsonText);
                        prefsEditor.commit();

                        //Retrieve SharedPreferences
                        SharedPreferences myPrefs;
                        myPrefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        String jsonTextGet = myPrefs.getString("key", null);

                        Type collectionType = new TypeToken<List<LocationHolder>>(){}.getType();
                        List<LocationHolder> addArray= (List<LocationHolder>) new Gson()
                                .fromJson( jsonTextGet , collectionType);*/

                        // Setting the color of the polyline
                        polylineOptions.color(Color.RED);

                        // Setting the width of the polyline
                        polylineOptions.width(3);

                        if (lat != null || !lat.equals("null") || (lng != null && !lng.equals("null") || (lat != "" || (lng != "") ||
                                (!lat.isEmpty()) || (!lng.isEmpty())))) {
//                            list_location.add(new LocationHolder(lat, lng, location, remarks));
                            Double d1 = Double.parseDouble(lat);
                            Double d2 = Double.parseDouble(lng);
                            // Setting points of polyline
                            polylineOptions.add(new LatLng(d1, d2));
                            createMarker(0, d1, d2, location, remarks);

                            if (i + 1 == response.body().getAsJsonArray().size()) {
                                System.out.println(i + " asap");
                                latitude = Double.parseDouble(lat);
                                longitude = Double.parseDouble(lng);
                                // Dismiss the progress dialog
                                if (pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }
                            }
                        }
                    }
                    // Adding the polyline to the map
                    mMapSnailTrail.addPolyline(polylineOptions);
                } else if (response.body().getAsJsonArray().size() == 0) {
                    Activity activity = getActivity();
//                    Toast.makeText(activity, "No Data to display.", Toast.LENGTH_LONG).show();
                    showToast("No Data to display.");
                    pDialog = new ProgressDialog(getContext());
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // failure response
                System.out.println("Fail " + call.toString());
            }

        });
    }

    public void createMarker(int index, Double latitude, Double longitude, String location, String remarks) {
        // Adding the taped point to the ArrayList
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        mMapSnailTrail.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(location)
                .snippet(remarks)
                .icon(image));

        mMapSnailTrail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

        mMapSnailTrail.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
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

    public void showToast(String msg) {
        if (SnailTrailFragment.this.isVisible() && msg != null & activity == null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
