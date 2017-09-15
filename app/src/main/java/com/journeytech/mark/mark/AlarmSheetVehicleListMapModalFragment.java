package com.journeytech.mark.mark;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

import static com.journeytech.mark.mark.activity.MainActivity.client_table;
import static com.journeytech.mark.mark.activity.MainActivity.counter;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.plate_num;

public class AlarmSheetVehicleListMapModalFragment extends BottomSheetDialogFragment {

    private String baseUrl = "http://mark.journeytech.com.ph/mobile_api/test/";
    private NetworkAPI networkAPI;
    private ListView lv;
    ArrayList<HashMap<String, String>> alarms = new ArrayList<HashMap<String, String>>();

    private interface NetworkAPI {
        @POST("alarm_api.php")
        @Headers({"Content-Type:application/json; charset=UTF-8"})
        Call<JsonElement> alarm(@Body AlarmPojo body);
    }

    private static class AlarmPojo {
        String client_table;
        String ucsi_num;

        private AlarmPojo(String client_table, String ucsi_num) {
            this.ucsi_num = ucsi_num;
            this.client_table = client_table;
        }
    }

    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();

            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        alarms.clear();

        View contentView = View.inflate(getContext(), R.layout.list_alarm_bottomsheet3, null);
        lv = (ListView) contentView.findViewById(R.id.list);

        dialog.setContentView(contentView);

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

        AlarmPojo alarm = new AlarmPojo (client_table, MainActivity.ucsi_num);

        Call<JsonElement> call = networkAPI.alarm(alarm);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                // success response
                if (response.body().isJsonObject()) {
                    JsonObject data = response.body().getAsJsonObject();

                    JsonArray array = data.getAsJsonArray("data");

                    for (int i = 0; i < array.size(); i++) {
                        JsonElement je = array.get(i);
                        JsonObject jo = je.getAsJsonObject();
                        for (int j = 0; j < jo.size(); j++) {
                            JsonElement jo_inner = jo.getAsJsonObject();

                            String plate_n = jo_inner.getAsJsonObject().get("plateno").getAsString();
                            String plate_nString = plate_n;
                            plate_nString = plate_nString.replace("\"", "");
                            String plate_nu = String.valueOf(plate_nString);

                            if(plate_num.equals(plate_nu) || plate_num==plate_nu) {
                                String locat = jo_inner.getAsJsonObject().get("location").getAsString();
                                String locatString = locat;
                                locatString = locatString.replace("\"", "");
                                String location = String.valueOf(locatString);

                                String da = jo_inner.getAsJsonObject().get("date").getAsString();
                                String daString = da;
                                daString = daString.replace("\"", "");
                                String date = String.valueOf(daString);

                                String ti = jo_inner.getAsJsonObject().get("time").getAsString();
                                String tiString = ti;
                                tiString = tiString.replace("\"", "");
                                String time = String.valueOf(tiString);

                                String al = jo_inner.getAsJsonObject().get("msg").getAsString();
                                String alString = al;
                                alString = alString.replace("\"", "");
                                String msg_type = String.valueOf(alString);

                                // tmp hash map for detail [single]
                                HashMap<String, String> details = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                details.put("plate_num", plate_num);
                                details.put("location", location);
                                details.put("date", date);
                                details.put("time", time);
                                details.put("msg", msg_type);

                                // adding vehicle to vehicle list
                                alarms.add(details);

                            /*Updating parsed JSON data into ListView*/

                                final ListAdapter adapter = new SimpleAdapter(getContext(), alarms,
                                        R.layout.list_alarm, new String[]{"plate_num", "date", "time",
                                        "location", "msg"},
                                        new int[]{R.id.plate_num, R.id.date, R.id.time, R.id.location, R.id.msg});

                                lv.setAdapter(adapter);
                            }
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
}
