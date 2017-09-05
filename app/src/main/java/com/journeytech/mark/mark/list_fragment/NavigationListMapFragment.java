package com.journeytech.mark.mark.list_fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journeytech.mark.mark.AlarmSheetModalFragment;
import com.journeytech.mark.mark.GPSTracker;
import com.journeytech.mark.mark.PlaceAutoCompleteAdapter;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.drawroute.DataParser;
import com.journeytech.mark.mark.modules.DirectionFinder;
import com.journeytech.mark.mark.modules.DirectionFinderListener;
import com.journeytech.mark.mark.modules.Route;
import com.journeytech.mark.mark.route.AbstractRouting;
import com.journeytech.mark.mark.route.RouteException;
import com.journeytech.mark.mark.route.Routing;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.latitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.longitudeListMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationListMapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, DirectionFinderListener {

    public static GoogleMap mMapNavigation;
    private RelativeLayout durationTimeRL;
    private CardView rl;
    private ImageView btnFindPath;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private List<Marker> originMarkers = new ArrayList<Marker>();
    private List<Marker> destinationMarkers = new ArrayList<Marker>();
    private List<Polyline> polylinePaths = new ArrayList<Polyline>();
    private ProgressDialog progressDialog;

    protected LatLng start;
    protected LatLng end;

    private static final String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    private static final LatLngBounds BOUNDS_JAMAICA= new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));

    @Override
    public void onResume()
    {
        super.onResume();

        if (!getUserVisibleHint())
        {
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setVisibility(View.GONE);
        mainActivity.counter.setVisibility(View.GONE);
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

        rl = (CardView) view.findViewById(R.id.rl);
        durationTimeRL = (RelativeLayout) view.findViewById(R.id.durationTimeRL);
        rl.setVisibility(View.VISIBLE);
        durationTimeRL.setVisibility(View.VISIBLE);

        btnFindPath = (ImageView) getActivity().findViewById(R.id.btnFindPath);
        etOrigin = (AutoCompleteTextView) getActivity().findViewById(R.id.etOrigin);
        etDestination = (AutoCompleteTextView) getActivity().findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        mAdapter = new PlaceAutoCompleteAdapter(_context, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_JAMAICA, null);

        etOrigin.setAdapter(mAdapter);
        etDestination.setAdapter(mAdapter);

       /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */

        etOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start=place.getLatLng();
                    }
                });

            }
        });
        etDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        end=place.getLatLng();
                    }
                });

            }
        });

       /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        etOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(end!=null)
                {
                    end=null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(_context, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(_context, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapNavigation = googleMap;
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMapNavigation.setMyLocationEnabled(true);
        mMapNavigation.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.405888, 123.273419), 7));

       /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * map.
        * */
        /*mMapNavigation.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = mMapNavigation.getProjection().getVisibleRegion().latLngBounds;
                mAdapter.setBounds(bounds);
            }
        });


        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(18.013610, -77.498803));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMapNavigation.moveCamera(center);
        mMapNavigation.animateCamera(zoom);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        mMapNavigation.moveCamera(center);
                        mMapNavigation.animateCamera(zoom);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        mMapNavigation.moveCamera(center);
                        mMapNavigation.animateCamera(zoom);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });*/

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(_context, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<Polyline>();
        originMarkers = new ArrayList<Marker>();
        destinationMarkers = new ArrayList<Marker>();

        for (Route route : routes) {
            mMapNavigation.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) getActivity().findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) getActivity().findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMapNavigation.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMapNavigation.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(8);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMapNavigation.addPolyline(polylineOptions));
        }
    }

    public void route()
    {
        if(start==null || end==null)
        {
            if(start==null)
            {
                if(etOrigin.getText().length()>0)
                {
                    etOrigin.setError("Choose location from dropdown.");
                }
                else
                {
                    Toast.makeText(_context,"Please choose a starting point.",Toast.LENGTH_SHORT).show();
                }
            }
            if(end==null)
            {
                if(etDestination.getText().length()>0)
                {
                    etDestination.setError("Choose location from dropdown.");
                }
                else
                {
                    Toast.makeText(_context,"Please choose a destination.",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            progressDialog = ProgressDialog.show(_context, "Please wait.",
                    "Fetching route information.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }
    }

    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        progressDialog.dismiss();
        if(e != null) {
            Toast.makeText(_context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(_context, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRoutingStart() {
        // The Routing Request starts
    }

    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex)
    {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMapNavigation.moveCamera(center);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            Polyline polyline = mMapNavigation.addPolyline(polyOptions);
            polylines.add(polyline);

        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        mMapNavigation.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end));
        mMapNavigation.addMarker(options);

    }

    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG,connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}