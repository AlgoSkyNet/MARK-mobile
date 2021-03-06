package com.journeytech.mark.mark.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.CustomTypeFaceSpan;
import com.journeytech.mark.mark.GPSTracker;
import com.journeytech.mark.mark.HttpHandler;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.list_fragment.VehicleListFragment;
import com.journeytech.mark.mark.map_fragment.VehicleMapFragment;
import com.journeytech.mark.mark.model.LocationHolder;
import com.journeytech.mark.mark.model.Proximity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import static android.content.ContentValues.TAG;
import static com.journeytech.mark.mark.list_fragment.SnailTrailTwoHrsFragment.dateFromListFragment;
import static com.journeytech.mark.mark.list_fragment.SnailTrailTwoHrsFragment.dateToListFragment;
import static com.journeytech.mark.mark.map_fragment.BottomSheetModalMapFragment.dateFromMapFragment;
import static com.journeytech.mark.mark.map_fragment.BottomSheetModalMapFragment.dateToMapFragment;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.list;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.mMapFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {

    public static Toolbar toolbar;

    public Proximity p;

    ArrayList<LocationHolder> al = new ArrayList<LocationHolder>();

    public static FragmentManager manager;
    VehicleListFragment vehicleListFragment;

    public static MenuItem searchItem;

    public static String status, ucsi_num, client_table, markutype;

    public static Context _context;

    public ImageView fab;

    public static TextView counter;

    private String baseUrl = "http://mark.journeytech.com.ph/mobile_api/test/";
    private NetworkAPI networkAPI;

    public static String dialog_msg = "";

    String address;
    GPSTracker gps = new GPSTracker(_context);
    private ProgressDialog progress;
    Double sLatitude, sLongitude;

    TelephonyManager telephonyManager = null;

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

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    private SuggestionsAdapter mSuggestionsAdapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        for (Marker m : list) {
            System.out.println(list + m.getSnippet() + m.getTitle() + m.getPosition() + " snippet");
            if (m.getSnippet().toLowerCase().equals(query)) {
                Toast.makeText(this, "You searched for: " + query, Toast.LENGTH_SHORT).show();
                mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 13.0f));
                break; // stop the loop
            } else if (!m.getSnippet().toLowerCase().equals(query)) {
//                mMapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 6.0f));
                Toast.makeText(this, "Invalid Plate No.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
        String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        Toast.makeText(this, "Suggestion clicked: " + query, Toast.LENGTH_LONG).show();
        return true;
    }

    private class SuggestionsAdapter extends CursorAdapter {

        public SuggestionsAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv = (TextView) view;
            final int textIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
            tv.setText(cursor.getString(textIndex));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Counter();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _context = this;

//        getSupportActionBar().

        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            status = (String) b.get("status");
            ucsi_num = (String) b.get("ucsi_num");
            client_table = (String) b.get("client_table");
            markutype = (String) b.get("markutype");
//            Toast.makeText(getApplicationContext(), status + ucsi_num, Toast.LENGTH_LONG).show();
        }

/*        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        searchView.setVoiceSearch(true);*/
        p = new Proximity();

        fab = (ImageView) findViewById(R.id.fab);

/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmSheetModalFragment bottomSheetDialogFragment = new AlarmSheetModalFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });*/

        counter = (TextView) findViewById(R.id.counter);
        counter.setText("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.name);
        name.setText(/*ucsi_num*/ "");

        //Set typeface roboto font to Navigation Text
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

//        Search();

        vehicleListFragment = new VehicleListFragment();
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, vehicleListFragment).commit();

        Counter();
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

        AlarmPojo alarm = new AlarmPojo(client_table, MainActivity.ucsi_num);

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

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

/*    void Search() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);

//        et = (EditText) headerview.findViewById(R.id.search_text);
        final SearchView et;
        et = (SearchView) headerview.findViewById(search);
        et.setActivated(true);
        et.setQueryHint("What is your Plate No.?");
        et.onActionViewExpanded();
        et.setIconified(false);
        et.clearFocus();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != et) {
            et.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            et.setIconifiedByDefault(false);
        }
        et.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(MainActivity.this, newText, Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        et.setOnQueryTextListener(queryTextListener);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the search view
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("What is your vehicle plate no.?");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);

        if (mSuggestionsAdapter == null) {
            MatrixCursor cursor = new MatrixCursor(COLUMNS);

            AutoCompleteTextView actv = new AutoCompleteTextView(this);
            actv.setThreshold(1);

/*            VehicleHolder vh = new VehicleHolder("");
            for (int i = 0; i < vh.getVehicle().length(); i++) {

                String is = String.valueOf(i);
//                cursor.addRow(new String[]{is, list_vehicle.get(0).get("plate_num").toString()});
                cursor.addRow(new String[]{is, list_vehicle.get(i).getVehicle().toString()});
            }*/

            mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
        }
        searchView.setSuggestionsAdapter(mSuggestionsAdapter);

        searchItem = menu.add("Search");
        searchItem.setIcon(true ? R.drawable.ic_action_action_search : R.drawable.ic_action_action_search);
        searchItem.setActionView(searchView);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        searchItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.vehicle_list) {
            VehicleListFragment vehicleFragment = new VehicleListFragment();
            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, vehicleFragment).commit();

            dateFromMapFragment = null;
            dateToMapFragment = null;

            dateFromListFragment = null;
            dateToListFragment = null;

            Counter();

        } else if (id == R.id.map) {
            VehicleMapFragment vehicleMapFragment = new VehicleMapFragment(MainActivity.this, MainActivity.this);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();

            searchItem.setVisible(true);
            dateFromMapFragment = null;
            dateToMapFragment = null;

            dateFromListFragment = null;
            dateToListFragment = null;

            Counter();

        /*if (id == R.id.snailtrail) {
            tvdist = (TextView) findViewById(R.id.tvDistance);
            tvdura = (TextView) findViewById(R.id.tvDuration);
            tvdist.setText("");
            tvdura.setText("");
            manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();
            mMap.clear();
            tvdist.setText("");
            tvdura.setText("");

            new getSnailTrail().execute();

        } else if (id == R.id.proximity) {
            tvdist = (TextView) findViewById(R.id.tvDistance);
            tvdura = (TextView) findViewById(R.id.tvDuration);
            manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();
            mMap.clear();
            String lat = "";
            String Longitude = "";
            if (p != null) {
                lat = String.valueOf(p.getLatitude());
                Longitude = String.valueOf(p.getLongitude());

                VehicleMapFragment.createProximity(lat, Longitude);
                Double lat2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLatitude());
                Double long2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLongitude());
                LatLng l1 = new LatLng(p.getLatitude(), p.getLongitude());
                LatLng l2 = new LatLng(lat2, long2);

                Double m = VehicleMapFragment.DistanceBetween(l1, l2);
                double km = 1000;
                double distanceInMeters = m / km;

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);

                tvdist.setText(df.format(distanceInMeters) + " km");

                //For example spead is 10 meters per minute.
                int speedIs10MetersPerMinute = 10;
                Double estimatedDriveTimeInMinutes = m / speedIs10MetersPerMinute;
                estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

                tvdura.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

               *//* float distance;
                Location locationA=new Location("A");
                locationA.setLatitude(p.getLatitude());
                locationA.setLongitude(p.getLongitude());

                Location locationB = new Location("B");
                locationB.setLatitude(lat2);
                locationB.setLongitude(long2);

                distance = locationA.distanceTo(locationB)/1000;

                LatLng From = new LatLng(p.getLatitude(),p.getLongitude());
                LatLng To = new LatLng(lat2,long2);

                int speedIs1KmMinute = 100;
                float estimatedDriveTimeInMinutes2 = distance / speedIs1KmMinute;
                Toast.makeText(this,String.valueOf(distance+
                        "Km"),Toast.LENGTH_SHORT).show();
                Toast.makeText(this,String.valueOf(estimatedDriveTimeInMinutes2+" Time"),Toast.LENGTH_SHORT).show();*//*


            }
        } else if (id == R.id.navigation) {
            tvdist = (TextView) findViewById(R.id.tvDistance);
            tvdura = (TextView) findViewById(R.id.tvDuration);
            Double lat2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLatitude());
            Double long2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLongitude());
            LatLng l1 = new LatLng(p.getLatitude(), p.getLongitude());
            LatLng l2 = new LatLng(lat2, long2);

            Double m = VehicleMapFragment.DistanceBetween(l1, l2);
            double km = 1000;
            double distanceInMeters = m / km;

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            tvdist.setText(df.format(distanceInMeters) + " km");

            //For example spead is 10 meters per minute.
            int speedIs10MetersPerMinute = 10;
            Double estimatedDriveTimeInMinutes = m / speedIs10MetersPerMinute;
            estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

            tvdura.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

            mMap.clear();
            Navigation n = new Navigation();
//            Double lat = Double.parseDouble(al.get().getLatitude());
//            Double Longitude = Double.parseDouble(al.get(al.size()-1).getLongitude());
            n.setLatitude(14.507743);
            n.setLongitude(121.003601);
            VehicleMapFragment.createNavigation(list_location.get(list_location.size() - 1).getLatitude(), list_location.get(list_location.size() - 1).getLongitude());
//            Toast.makeText(getApplicationContext(), lat+Longitude.toString(), Toast.LENGTH_SHORT).show();
     */
        } /*else if (id == R.id.account) {
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
        }*/ else if (id == R.id.system_alert) {
            // create class object
            gps = new GPSTracker(_context);
            sLatitude = gps.getLatitude();
            sLongitude = gps.getLongitude();

            final Dialog dialog = new Dialog(MainActivity.this); // here write the name of your activity in place of "YourActivity"
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.system_alert);

            TextView bt = (TextView) dialog.findViewById(R.id.btSend);
            final EditText et = (EditText) dialog.findViewById(R.id.et_msg);

            bt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog_msg = et.getText().toString();
                    if (dialog_msg.equals("") || dialog_msg == "" || dialog_msg.equals(null)) {
                        Toast.makeText(_context, "Invalid Message Alert!", Toast.LENGTH_SHORT).show();
                    } else {

                        new GetAddress().execute();
                        dialog.dismiss();
                    }

                }
            });

            TextView bt2 = (TextView) dialog.findViewById(R.id.btCancel);
            bt2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } else if (id == R.id.sign_out) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to Log Out?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, LogIn.class);// New activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish(); // Call once you redirect to another activity
                        }

                    })
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // System Alert
    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c) {

            this.context = c;
//            this.error = status;
//            this.type = t;

        }

        protected void onPreExecute() {
/*            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();*/
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateAndTime = sdf.format(new Date());
                System.out.println(address + dialog_msg+currentDateAndTime+" "+Build.SERIAL+" 11211222312123");

                URL url = new URL("http://mark.journeytech.com.ph/mobile_alerts_api.php?location=" + URLEncoder.encode(address, "UTF-8") + "&msg=" + URLEncoder.encode(dialog_msg, "UTF-8") + "&datetime=" + URLEncoder.encode(currentDateAndTime, "UTF-8") + "&id=" + URLEncoder.encode(Build.SERIAL, "UTF-8"));



                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters ");
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            progress.dismiss();
            Toast.makeText(_context, "Your message has been forwarded.", Toast.LENGTH_SHORT).show();
        }

    }

    // System Alert..
    // Parsing Address
    private class GetAddress extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Getting your Location. \nPlease wait...");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("http://52.53.141.13:81/reverse.php?lat=" + sLatitude + "&lon=" + sLongitude + "&format=jsonv2");

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    address = jsonObj.getString("display_name");
                    // Getting JSON Array node
//                    address = jsonObj.getJSONArray("display_name");
                    System.out.println(address + " asdasdasdassdsa");
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(_context, address.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progress.isShowing())
                progress.dismiss();
            new PostClass(_context).execute();
        }
    }
}