package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.maps.android.ui.IconGenerator;
import com.journeytech.mark.mark.CustomDateTimePicker;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.map_fragment.SnailTrailMapFragment;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

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

import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.manager;
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListFragment.currentDateAndTime;
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListFragment.dateFromListFragment;
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListFragment.dateToListFragment;
import static com.journeytech.mark.mark.list_fragment.BottomSheetModalListFragment.minusTwoHourDateAndTime;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.plate_num;
import static com.journeytech.mark.mark.map_fragment.BottomSheetModalMapFragment.dateFromMapFragment;
import static com.journeytech.mark.mark.map_fragment.BottomSheetModalMapFragment.dateToMapFragment;

public class SnailTrailListFragment extends Fragment implements OnMapReadyCallback {

    private ProgressDialog pDialog;

    public static GoogleMap mMapSnailTrail;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    Context context;
    static Activity activity;

    public static Double latitude, longitude;

    Button select_date;

    Response<JsonElement> response_last;

    CustomDateTimePicker custom, custom2;

    public SnailTrailListFragment(Context c, Activity a) {
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

        select_date = (Button) v.findViewById(R.id.select_date);
        select_date.setVisibility(View.VISIBLE);
//        select_date.setBackgroundColor(getResources().getColor(R.color.skyblue));
        select_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getContext());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.date_time);
                dialog.setCancelable(false);

              ImageView close = (ImageView) dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                final TextView et2 = (TextView) dialog.findViewById(R.id.editText2);
                et2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        if (getActivity() == null) {
                            System.out.println("yes");
                        }
                        custom = new
                                CustomDateTimePicker(activity,
                                new CustomDateTimePicker.ICustomDateTimeListener() {
                                    @Override
                                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                                      Date dateSelected, int year, String monthFullName,
                                                      String monthShortName, int monthNumber, int date,
                                                      String weekDayFullName, String weekDayShortName,
                                                      int hour24, int hour12, int min, int sec,
                                                      String AM_PM) {

                                        DecimalFormat df = new DecimalFormat("00");
                                        String i_hr = df.format(hour24);
                                        String i_min = df.format(min);
                                        String i_sec = df.format(sec);
                                        String i_monthNumber = df.format(monthNumber+1);
                                        String i_calendar_day = df.format(calendarSelected
                                                .get(Calendar.DAY_OF_MONTH));

                                        dateFromListFragment = (i_monthNumber)
                                                + "/" + i_calendar_day + "/" + year
                                                + " " + i_hr + ":" + i_min
                                                + ":" + i_sec;

                                        et2.setText(dateFromListFragment);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                        //Pass Directly current time format it will return AM and PM if you set false
                        custom.set24HourFormat(false);

                        //Pass Directly current data and time to show when it pop up
                        custom.setDate(Calendar.getInstance());
                        custom.showDialog();

                    }
                });

                final TextView et3 = (TextView) dialog.findViewById(R.id.editText3);
                et3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final FragmentManager manager = getFragmentManager();
                        custom2 = new
                                CustomDateTimePicker(activity,
                                new CustomDateTimePicker.ICustomDateTimeListener() {
                                    @Override
                                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                                      Date dateSelected, int year, String monthFullName,
                                                      String monthShortName, int monthNumber, int date,
                                                      String weekDayFullName, String weekDayShortName,
                                                      int hour24, int hour12, int min, int sec, String AM_PM) {

                                        DecimalFormat df = new DecimalFormat("00");
                                        String i_hr = df.format(hour24);
                                        String i_min = df.format(min);
                                        String i_sec = df.format(sec);
                                        String i_monthNumber = df.format(monthNumber+1);
                                        String i_calendar_day = df.format(calendarSelected
                                                .get(Calendar.DAY_OF_MONTH));

                                        dateToListFragment = (i_monthNumber)
                                                + "/" + i_calendar_day + "/" + year
                                                + " " + i_hr + ":" + i_min
                                                + ":" + i_sec;

                                        et3.setText(dateToListFragment);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                        //Pass Directly current time format it will return AM and PM if you set false
                        custom2.set24HourFormat(false);

                        //Pass Directly current data and time to show when it pop up
                        custom2.setDate(Calendar.getInstance());
                        custom2.showDialog();

                    }
                });

                ImageView b5 = (ImageView) dialog.findViewById(R.id.button5);
                b5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dateFromListFragment == null || dateFromListFragment.equals("null")) {
                            showToast("Please select Date From.");
                        }
                        else if(dateToListFragment == null || dateToListFragment.equals("null")) {
                            showToast("Please select Date To.");
                        } else if (dateFromListFragment == null || dateFromListFragment.equals("null") && dateToListFragment == null || dateToListFragment.equals("null")) {
                            showToast("Date From and Date To is 'empty'.");
                        }
                         else if (dateToListFragment == dateFromListFragment) {
                            showToast("Invalid Date.");

                        }
                        if (dateFromListFragment != null && dateFromListFragment != null) {
                            SnailTrailDatesFragment stf = new SnailTrailDatesFragment(getContext(), getActivity());
                            manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, stf).commit();

                            dialog.dismiss();
                        }

                    }
                });

                dialog.show();

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }

    public void onBackPressed() {
        FragmentManager manager;
        VehicleListMapFragment vehicleListMapFragment;
        vehicleListMapFragment = new VehicleListMapFragment();
        manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, vehicleListMapFragment).commit();

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

        SnailTrailPojo loginRequest = new SnailTrailPojo(plate_num, minusTwoHourDateAndTime, currentDateAndTime, client_table);
        Call<JsonElement> call = networkAPI.loginRequest(loginRequest);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                response_last = response;

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
                        locString = loc.replace("\"", "");
                        String location = String.valueOf(locString);
                        System.out.println(location + " locationnn");
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
                            createMarker(i, d1, d2, location, remarks);



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
        index = index + 1;
        String text = String.valueOf(index);

        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(index);

        IconGenerator tc = new IconGenerator(getContext());
        Bitmap bmp = tc.makeIcon(text);
//        Bitmap bmp = makeBitmap(getContext(),text);

        if (index == 1) {
            image = BitmapDescriptorFactory.fromResource(R.drawable.start);
            mMapSnailTrail.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(location)
                    .snippet(remarks)
                    .icon(image));
        }else if (index == response_last.body().getAsJsonArray().size()){
            image = BitmapDescriptorFactory.fromResource(R.drawable.end);
            mMapSnailTrail.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(location)
                    .snippet(remarks)
                    .icon(image));
        }else {
            mMapSnailTrail.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(location)
                    .snippet(remarks)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
        }
        mMapSnailTrail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
        mMapSnailTrail.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

    }

    public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.map_marker);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width() - 10; // 10 for padding from right
        int y = bounds.height();
        canvas.drawText(text, x, y, paint);

        return  bitmap;
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
        if (SnailTrailListFragment.this.isVisible() && msg != null & activity == null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
