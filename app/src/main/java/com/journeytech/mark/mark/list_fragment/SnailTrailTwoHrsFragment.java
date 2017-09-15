package com.journeytech.mark.mark.list_fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.journeytech.mark.mark.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.manager;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.plate_num;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SnailTrailTwoHrsFragment extends Fragment implements OnMapReadyCallback,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private ProgressDialog pDialog;

    public static GoogleMap mMapSnailTrail;

    public static String baseUrl = "http://mark.journeytech.com.ph/mobile_api/";
    public static NetworkAPI networkAPI;

    Context context;
    static Activity activity;

    public static Double latitude, longitude;

    Button select_date;

    Response<JsonElement> response_last;

    private org.joda.time.LocalDateTime mLocalDateTime = new org.joda.time.LocalDateTime();

    String currentDateAndTime, minusTwoHourDateAndTime;
    public static String dateFromListFragment = null, dateToListFragment = null;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    String dayFinalString, monthFinalString, yearFinalString, hourFinalString, minuteFinalString;

    TextView et2;

    String dateFrom;
    String Dates, time;

    public SnailTrailTwoHrsFragment(Context c, Activity a) {
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

    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        Dates = (monthFinal < 10 ? ("0" + monthFinal) : (monthFinal)) + "-" + (dayFinal < 10 ? ("0" + dayFinal) : (dayFinal)) + "-" +  yearFinal ;

    }

    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        time = (hourFinal < 10 ? ("0" + hourFinal) : (hourFinal)) + ":" + (minuteFinal < 10 ? ("0" + minuteFinal) : (minuteFinal));

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

                et2 = (TextView) dialog.findViewById(R.id.editText2);
                et2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                yearFinal = i;
                                monthFinal = i1 + 1;
                                dayFinal = i2;

                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR_OF_DAY);
                                minute = c.get(Calendar.MINUTE);

                                Dates = (monthFinal < 10 ? ("0" + monthFinal) : (monthFinal)) + "/" + (dayFinal < 10 ? ("0" + dayFinal) : (dayFinal)) + "/" +  yearFinal ;

                                TimePickerDialog timePickerDialog = new TimePickerDialog(_context, new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hourFinal = i;
                                        minuteFinal = i1;

                                        time = (hourFinal < 10 ? ("0" + hourFinal) : (hourFinal)) + ":" + (minuteFinal < 10 ? ("0" + minuteFinal) : (minuteFinal));
                                        et2.setText(Dates + " " + time + ":00");
                                        dateFromListFragment = et2.getText().toString();
                                    }
                                }, hour, minute, DateFormat.is24HourFormat(_context));
                                timePickerDialog.show();
                            }
                        }, year, month, day);

                        datePickerDialog.show();
                    }
                });

                final TextView et3 = (TextView) dialog.findViewById(R.id.editText3);
                et3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                yearFinal = i;
                                monthFinal = i1 + 1;
                                dayFinal = i2;

                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR_OF_DAY);
                                minute = c.get(Calendar.MINUTE);

                                Dates = (monthFinal < 10 ? ("0" + monthFinal) : (monthFinal)) + "/" + (dayFinal < 10 ? ("0" + dayFinal) : (dayFinal)) + "/" +  yearFinal ;

                                TimePickerDialog timePickerDialog = new TimePickerDialog(_context, new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hourFinal = i;
                                        minuteFinal = i1;

                                        time = (hourFinal < 10 ? ("0" + hourFinal) : (hourFinal)) + ":" + (minuteFinal < 10 ? ("0" + minuteFinal) : (minuteFinal));
                                        et3.setText(Dates + " " + time + ":00");
                                        dateToListFragment = et3.getText().toString();
                                    }
                                }, hour, minute, DateFormat.is24HourFormat(_context));
                                timePickerDialog.show();
                            }
                        }, year, month, day);

                        datePickerDialog.show();
                    }
                });

                ImageView b5 = (ImageView) dialog.findViewById(R.id.button5);
                b5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dateFromListFragment == null || dateFromListFragment.equals("null")) {
                            showToast("Please select Date From.");
                        } else if (dateToListFragment == null || dateToListFragment.equals("null")) {
                            showToast("Please select Date To.");
                        } else if (dateFromListFragment == null || dateFromListFragment.equals("null") && dateToListFragment == null || dateToListFragment.equals("null")) {
                            showToast("Date From and Date To is 'empty'.");
                        } else if (dateToListFragment == dateFromListFragment) {
                            showToast("Invalid Date.");
                        } else if (dateFromListFragment != null && dateFromListFragment != null) {
                            System.out.println(dateFromListFragment + " " + dateToListFragment + " asdsd22222222");
                            //Check if Date From is ahead than Date To..
                            try {
                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                                String str1 = "10/13/2013 11:00:00";
                                Date date1 = formatter.parse(dateFromListFragment);

                                String str2 = "10/13/2013 12:00:00";
                                Date date2 = formatter.parse(dateToListFragment);

                                //Check if Date From is ahead than Date To..
                                if (date2.compareTo(date1) < 0) {
                                    Toast.makeText(_context, "Invalid Date!!", Toast.LENGTH_LONG).show();
                                    System.out.println(dateFromListFragment + " " + dateToListFragment + " asdsdddddddd");
                                } else {
                                    SnailTrailDatesFragment stf = new SnailTrailDatesFragment(getContext(), getActivity());
                                    manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, stf).commit();

                                    dialog.dismiss();
                                }

                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

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

    private String formatDateString(org.joda.time.LocalDateTime localDateTime) {
        return localDateTime.toString("MM/dd/yyyy HH:mm:ss");
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

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        currentDateAndTime = sdf.format(new Date());
        minusTwoHourDateAndTime = sdf.format(new Date(System.currentTimeMillis() - 7200000));

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

                System.out.println(response.body().toString() + " Asdasdasssss");

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
                    Toast.makeText(activity, "No Data to display.", Toast.LENGTH_LONG).show();
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
        } else if (index == response_last.body().getAsJsonArray().size()) {
            image = BitmapDescriptorFactory.fromResource(R.drawable.end);
            mMapSnailTrail.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(location)
                    .snippet(remarks)
                    .icon(image));
        } else {
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

    public Bitmap makeBitmap(Context context, String text) {
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

        return bitmap;
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
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
