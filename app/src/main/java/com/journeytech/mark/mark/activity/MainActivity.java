package com.journeytech.mark.mark.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.LocationHolder;
import com.journeytech.mark.mark.Navigation;
import com.journeytech.mark.mark.Proximity;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.fragment.MapFragment;
import com.journeytech.mark.mark.fragment.VehicleFragment;
import com.journeytech.mark.mark.getaccuratelocation.BaseActivityLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.journeytech.mark.mark.R.id.search;
import static com.journeytech.mark.mark.fragment.MapFragment.list_location;
import static com.journeytech.mark.mark.fragment.MapFragment.mMap;

public class MainActivity extends BaseActivityLocation
        implements NavigationView.OnNavigationItemSelectedListener {

    public Proximity p;

    ArrayList<LocationHolder> al = new ArrayList<>();

    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    FragmentManager manager;
    MapFragment mapFragment;

    TextView tvdist, tvdura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvdist = (TextView) findViewById(R.id.tvDistance);
        tvdura = (TextView) findViewById(R.id.tvDuration);

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

        mapFragment = new MapFragment(MainActivity.this, this);
        manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();
    }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            VehicleFragment vehicleFragment = new VehicleFragment();
            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, vehicleFragment).commit();
        } /*else if (id == map) {
            MapFragment mapFragment = new MapFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();*/
        if (id == R.id.snailtrail) {

            manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();
            mMap.clear();
            tvdist.setText(""); tvdura.setText("");
            if(manager != null) {
                // Instantiating the class PolylineOptions to plot polyline in the map
                final PolylineOptions polylineOptions = new PolylineOptions();
                al = list_location;
                for (int i = 0; i < al.size(); i++) {

                    // Setting the color of the polyline
                    polylineOptions.color(Color.RED);

                    // Setting the width of the polyline
                    polylineOptions.width(3);

                    Double lat = Double.parseDouble(al.get(i).getLatitude());
                    Double Longitude = Double.parseDouble(al.get(i).getLongitude());

                    // Setting points of polyline
                    polylineOptions.add(new LatLng(lat, Longitude));

                    MapFragment.createMarker(i, al.get(i).getLatitude(), al.get(i).getLongitude(), al.get(i).getLocation());
                }
                // Adding the polyline to the map
                mMap.addPolyline(polylineOptions);
            }

        } else if (id == R.id.proximity) {

            manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();
            mMap.clear();
            String lat = "";
            String Longitude = "";
            if (p != null) {
                 lat = String.valueOf(p.getLatitude());
                 Longitude = String.valueOf(p.getLongitude());

            MapFragment.createProximity(lat,Longitude);
                Double lat2 = Double.parseDouble(list_location.get(list_location.size()-1).getLatitude());
                Double long2 = Double.parseDouble(list_location.get(list_location.size()-1).getLongitude());
                LatLng l1 = new LatLng(p.getLatitude(), p.getLongitude());
                LatLng l2 = new LatLng(lat2, long2);

                Double m = MapFragment.distanceBetween(l1, l2);
                double km = 1000;
                double distanceInMeters =m/km;

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);

                tvdist.setText( df.format(distanceInMeters )+ " km");

                //For example spead is 10 meters per minute.
                int speedIs10MetersPerMinute = 10;
                Double estimatedDriveTimeInMinutes = m  / speedIs10MetersPerMinute;
                estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

                tvdura.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

               /* float distance;
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
                Toast.makeText(this,String.valueOf(estimatedDriveTimeInMinutes2+" Time"),Toast.LENGTH_SHORT).show();*/


            }
        } else if (id == R.id.navigation) {
            Double lat2 = Double.parseDouble(list_location.get(list_location.size()-1).getLatitude());
            Double long2 = Double.parseDouble(list_location.get(list_location.size()-1).getLongitude());
            LatLng l1 = new LatLng(p.getLatitude(), p.getLongitude());
            LatLng l2 = new LatLng(lat2, long2);

            Double m = MapFragment.distanceBetween(l1, l2);
            double km = 1000;
            double distanceInMeters =m/km;

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            tvdist.setText( df.format(distanceInMeters )+ " km");

            //For example spead is 10 meters per minute.
            int speedIs10MetersPerMinute = 10;
            Double estimatedDriveTimeInMinutes = m  / speedIs10MetersPerMinute;
            estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

            tvdura.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

            mMap.clear();
            Navigation n = new Navigation();
//            Double lat = Double.parseDouble(al.get().getLatitude());
//            Double Longitude = Double.parseDouble(al.get(al.size()-1).getLongitude());
            n.setLatitude(14.507743);
            n.setLongitude(121.003601);
            MapFragment.createNavigation(list_location.get(list_location.size()-1).getLatitude(), list_location.get(list_location.size()-1).getLongitude());
//            Toast.makeText(getApplicationContext(), lat+Longitude.toString(), Toast.LENGTH_SHORT).show();
        }

        else if (id == R.id.account) {
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
        } else if (id == R.id.sign_out) {
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void locationFetched(Location mLocal, Location oldLocation, String time, String locationProvider) {
        super.locationFetched(mLocal, oldLocation, time, locationProvider);
        p.setLatitude(mLocal.getLatitude());
        p.setLongitude(mLocal.getLongitude());

//        setMLocal(mLocal);

//        mapFragment.setLocation(mLocal);

        //After initLocationFetching.
        Bundle bundle = new Bundle();
        bundle.putDouble("Lat",mLocal.getLatitude());
        bundle.putDouble("Long",mLocal.getLongitude());
        mapFragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();

//        Toast.makeText(getApplication(), "Lat : " + p.getLatitude() + " Lng : " + mLocal.getLongitude(), Toast.LENGTH_SHORT).show();
/*        if(mLocal.getAltitude() == 0.0 && mLocal.getLongitude() == 0.0){
            Toast.makeText(getApplicationContext(), R.string.not_found, Toast.LENGTH_SHORT).show();
        }else{
            mLocalTV.setText("Lat : " + mLocal.getLatitude() + " Lng : " + mLocal.getLongitude());
        }
        mLocationProviderTV.setText(locationProvider);
        mlocationTimeTV.setText(time);*/
    }
    public Location mLocal;
    public Location getMLocal() {
        return mLocal;
    }

    public void setMLocal(Location mLocal) {
        this.mLocal= mLocal;
    }
}
