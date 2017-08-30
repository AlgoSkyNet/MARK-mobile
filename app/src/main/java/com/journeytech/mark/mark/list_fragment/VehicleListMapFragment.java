package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.AlarmSheetModalFragment;
import com.journeytech.mark.mark.AlarmSheetVehicleListMapModalFragment;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.model.VehicleHolder;

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
import static com.journeytech.mark.mark.activity.MainActivity.counter;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleListMapFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://mark.journeytech.com.ph/json/1.json";

    public static GoogleMap mMapVehicleListMapFragment;
    public static ArrayList<VehicleHolder> vehicleHolder;

    Context context;
    static Activity activity;

    public static String plate_num = "";
    public static String date, time, location, engine, remarks;

    TextView tv2, tv4, tv6;

    public static String latitudeListMap, longitudeListMap;

    Marker m;

    private String baseUrl = "http://mark.journeytech.com.ph/mobile_api/test/";
    private NetworkAPI networkAPI;

    private interface NetworkAPI {
        @POST("alarm_api.php")
        @Headers({"Content-Type:application/json; charset=UTF-8"})
        Call<JsonElement> alarm(@Body AlarmPojo body);
    }

    private static class AlarmPojo {
        String client_table;
        String ucsi_num;
        String plateno;

        private AlarmPojo(String client_table, String ucsi_num, String plate_num) {
            this.ucsi_num = ucsi_num;
            this.client_table = client_table;
            this.plateno = plate_num;
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

        MainActivity csActivity;
        csActivity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vehicle_list_map, container, false);
        Button traffic = (Button) v.findViewById(R.id.traffic);
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMapVehicleListMapFragment.isTrafficEnabled() == false) {
                    mMapVehicleListMapFragment.setTrafficEnabled(true);
                } else {
                    mMapVehicleListMapFragment.setTrafficEnabled(false);
                }
            }
        });

        tv2 = (TextView) v.findViewById(R.id.tv2);
        tv4 = (TextView) v.findViewById(R.id.tv4);
        tv6 = (TextView) v.findViewById(R.id.tv6);

        plate_num = getArguments().getString("plate_num");
        date = getArguments().getString("date");
        time = getArguments().getString("time");
        location = getArguments().getString("location");
        latitudeListMap = getArguments().getString("latitude");
        longitudeListMap = getArguments().getString("longitude");
        engine = getArguments().getString("engine");
        remarks = getArguments().getString("remarks");

        String a = date + " " + time;
        tv2.setText(a);
        tv4.setText(engine);
        tv6.setText(remarks);

        csActivity.getSupportActionBar().setTitle("Map View - " + plate_num);

        BottomSheetModalListFragment bottomSheetDialogFragment = new BottomSheetModalListFragment(activity);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

        Counter();

        return v;
    }

    void Counter() {
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

        AlarmPojo alarm = new AlarmPojo(client_table, MainActivity.ucsi_num, plate_num);

        Call<JsonElement> call = networkAPI.alarm(alarm);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                // success response
                if (response.body().isJsonObject()) {
                    JsonObject data = response.body().getAsJsonObject();

                    JsonElement count = data.get("count");
                    counter.setText(count.toString());

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // failure response
                System.out.println(call.toString());
            }

        });
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
        if (!getUserVisibleHint())
        {
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmSheetVehicleListMapModalFragment bottomSheetDialogFragment = new AlarmSheetVehicleListMapModalFragment();
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMapVehicleListMapFragment = googleMap;

//        mMapFragment.clear();
//        mMapVehicleListMapFragment.clear();

        setUpMap();

    }

    private class GetMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

    /*set up map*/
    private void setUpMap() {
        mMapVehicleListMapFragment.clear();
        mMapVehicleListMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapVehicleListMapFragment.setIndoorEnabled(true);
        mMapVehicleListMapFragment.setBuildingsEnabled(true);
        mMapVehicleListMapFragment.getUiSettings().setZoomControlsEnabled(false);

        final Double lati = Double.parseDouble(latitudeListMap);
        final Double longi = Double.parseDouble(longitudeListMap);

        m = mMapVehicleListMapFragment.addMarker(new MarkerOptions()
                .position(new LatLng(lati, longi))
                .anchor(0.5f, 0.5f)
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMapVehicleListMapFragment.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.showInfoWindow();
            }
        });

        mMapVehicleListMapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 19.0f));
        bounceMarker();

        mMapVehicleListMapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                BottomSheetModalListFragment bottomSheetDialogFragment = new BottomSheetModalListFragment(activity);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }

                return true;
            }
        });

        mMapVehicleListMapFragment.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

        m.showInfoWindow();
    }

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents_list_vehicle_map, null);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView snippet = (TextView) v.findViewById(R.id.snippet);
            snippet.setVisibility(View.GONE);
            title.setText("Location: " + location);
//            snippet.setText("Engine: " + engine);

            return v;
        }
    }

    private void bounceMarker() {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                m.setAnchor(0.5f, 1.0f + t);
                if (t > 0.0) {
                    handler.postDelayed(this, 25);
                }
            }
        });
    }
}
