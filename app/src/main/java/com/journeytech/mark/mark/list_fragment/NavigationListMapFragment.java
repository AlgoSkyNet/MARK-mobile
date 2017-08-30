package com.journeytech.mark.mark.list_fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.journeytech.mark.mark.AlarmSheetModalFragment;
import com.journeytech.mark.mark.GPSTracker;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;
import com.journeytech.mark.mark.drawroute.DataParser;
import com.journeytech.mark.mark.modules.DirectionFinder;
import com.journeytech.mark.mark.modules.DirectionFinderListener;
import com.journeytech.mark.mark.modules.Route;

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

import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.latitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.longitudeListMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationListMapFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    public static GoogleMap mMapNavigation;
    private RelativeLayout rl, durationTimeRL;
    private ImageView btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<Marker>();
    private List<Marker> destinationMarkers = new ArrayList<Marker>();
    private List<Polyline> polylinePaths = new ArrayList<Polyline>();
    private ProgressDialog progressDialog;

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

        rl = (RelativeLayout) view.findViewById(R.id.rl);
        durationTimeRL = (RelativeLayout) view.findViewById(R.id.durationTimeRL);
        rl.setVisibility(View.VISIBLE);
        durationTimeRL.setVisibility(View.VISIBLE);

        btnFindPath = (ImageView) getActivity().findViewById(R.id.btnFindPath);
        etOrigin = (EditText) getActivity().findViewById(R.id.etOrigin);
        etDestination = (EditText) getActivity().findViewById(R.id.etDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
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
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMapNavigation.addPolyline(polylineOptions));
        }
    }

}
