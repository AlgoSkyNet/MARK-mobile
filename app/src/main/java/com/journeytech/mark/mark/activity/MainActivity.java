package com.journeytech.mark.mark.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.fragment.VehicleListFragment;
import com.journeytech.mark.mark.fragment.VehicleMapFragment;
import com.journeytech.mark.mark.getaccuratelocation.BaseActivityLocation;
import com.journeytech.mark.mark.model.LocationHolder;
import com.journeytech.mark.mark.model.Proximity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import static com.journeytech.mark.mark.R.id.search;
import static com.journeytech.mark.mark.fragment_unused.VehicleDetailsProximityNavigationFragment.createSnailTrail;
import static com.journeytech.mark.mark.fragment_unused.VehicleDetailsProximityNavigationFragment.list_location;
import static com.journeytech.mark.mark.fragment_unused.VehicleDetailsProximityNavigationFragment.mMap;

public class MainActivity extends BaseActivityLocation
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {

    public Proximity p;

    ArrayList<LocationHolder> al = new ArrayList<>();

    public static FragmentManager manager;
    VehicleMapFragment vehicleMapFragment;

    TextView tvdist, tvdura;

    MaterialSearchView searchView;

    ProgressDialog pDialog;

    public static String status, ucsi_num, client_table, markutype;

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    private SuggestionsAdapter mSuggestionsAdapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "You searched for: " + query, Toast.LENGTH_LONG).show();
        return true;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null) {
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is a notification Message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        presser();

        initLocationFetching(MainActivity.this);

        vehicleMapFragment = new VehicleMapFragment(getApplicationContext(), this);
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();
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

    void presser() {
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

    }

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

//            Toast.makeText(MainActivity.this, list_vehicle.get(0).getVehicle().toString(), Toast.LENGTH_SHORT).show();
            mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
        }
//        Toast.makeText(this, vehicle.get(0).get("plate_num").toString() + "A", Toast.LENGTH_SHORT).show();
        searchView.setSuggestionsAdapter(mSuggestionsAdapter);


        menu.add("Search")
                .setIcon(true ? R.drawable.ic_action_action_search : R.drawable.ic_action_action_search)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    private void VehiclePlateNum(String plate_num) {

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
        } else if (id == R.id.map) {
            VehicleMapFragment vehicleMapFragment = new VehicleMapFragment(MainActivity.this, MainActivity.this);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();

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

                Double m = VehicleMapFragment.distanceBetween(l1, l2);
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

            Double m = VehicleMapFragment.distanceBetween(l1, l2);
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
     */   } else if (id == R.id.account) {
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
        } else if (id == R.id.sign_out) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to Log Out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent ii=new Intent(MainActivity.this, LogIn.class);
                            startActivity(ii);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private class getSnailTrail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected Void doInBackground(Void... args) {
            // do background work here
            return null;
        }

        protected void onPostExecute(Void result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Instantiating the class PolylineOptions to plot polyline in the map
            final PolylineOptions polylineOptions = new PolylineOptions();
//            al = list_location;
            for (int i = 0; i < list_location.size(); i++) {
                // Setting the color of the polyline
                polylineOptions.color(Color.RED);

                // Setting the width of the polyline
                polylineOptions.width(3);

                Double lat = Double.parseDouble(list_location.get(i).getLatitude());
                Double Longitude = Double.parseDouble(list_location.get(i).getLongitude());

                // Setting points of polyline
                polylineOptions.add(new LatLng(lat, Longitude));

                createSnailTrail(i, list_location.get(i).getLatitude(), list_location.get(i).getLongitude(), list_location.get(i).getLocation());
            }

            // Adding the polyline to the map
            mMap.addPolyline(polylineOptions);
        }
    }

    @Override
    public void locationFetched(Location mLocal, Location oldLocation, String time, String locationProvider) {
        super.locationFetched(mLocal, oldLocation, time, locationProvider);
        p.setLatitude(mLocal.getLatitude());
        p.setLongitude(mLocal.getLongitude());

//        setMLocal(mLocal);

//        vehicleMapFragment.setLocation(mLocal);

        //After initLocationFetching.
        Bundle bundle = new Bundle();
        bundle.putDouble("Lat", mLocal.getLatitude());
        bundle.putDouble("Long", mLocal.getLongitude());
        vehicleMapFragment.setArguments(bundle);
//        manager.beginTransaction().replace(R.id.mainLayout, vehicleMapFragment).commit();

    }

    public Location mLocal;

    public Location getMLocal() {
        return mLocal;
    }

    public void setMLocal(Location mLocal) {
        this.mLocal = mLocal;
    }
}
