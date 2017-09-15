package com.journeytech.mark.mark.map_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.journeytech.mark.mark.CustomDateTimePicker;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.list_fragment.SnailTrailDatesFragment;
import com.journeytech.mark.mark.list_fragment.SnailTrailTwoHrsFragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.journeytech.mark.mark.activity.LogIn.typeface;
import static com.journeytech.mark.mark.activity.MainActivity._context;
import static com.journeytech.mark.mark.activity.MainActivity.manager;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.mMapFragment;

public class BottomSheetModalMapFragment extends BottomSheetDialogFragment {

    public static String dateFromMapFragment = null, dateToMapFragment = null;

    private org.joda.time.LocalDateTime mLocalDateTime = new org.joda.time.LocalDateTime();

    CustomDateTimePicker custom, custom2;

    Activity activity;

    Context context;

    public BottomSheetModalMapFragment(Activity a) {
        this.activity = a;
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
        View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet3, null);
        dialog.setContentView(contentView);
        BottomSheetBehavior mBottomSheetBehavior;
        mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight(500);
            contentView.requestLayout();
        }
        Button snailtrail = (Button) contentView.findViewById(R.id.snailtrail);
        Button proximity = (Button) contentView.findViewById(R.id.proximity);
        Button navigation = (Button) contentView.findViewById(R.id.navigation);

        snailtrail.setTypeface(typeface);
        proximity.setTypeface(typeface);
        navigation.setTypeface(typeface);

        snailtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnailTrailMapTwoHrsFragment stf = new SnailTrailMapTwoHrsFragment(getContext(), getActivity());
                manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, stf).commit();

                dismiss();
            }
        });
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapFragment.clear();

//                ProximityListMapFragment.createProximity(MainActivity.getLatitude(), MainActivity.getLongitude());
                dismiss();

                ProximityMapFragment pnf = new ProximityMapFragment(getActivity(), getContext());
                manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, pnf).commit();

            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*tvdist = (TextView) findViewById(R.id.tvDistance);
                tvdura = (TextView) findViewById(R.id.tvDuration);
                Double lat2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLatitude());
                Double long2 = Double.parseDouble(list_location.get(list_location.size() - 1).getLongitude());
                LatLng l1 = new LatLng(p.getLatitude(), p.getLongitude());
                LatLng l2 = new LatLng(lat2, long2);

                Double m = ProximityListMapFragment.DistanceBetween(l1, l2);
                double km = 1000;
                double distanceInMeters = m / km;

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);

                tvdist.setText(df.format(distanceInMeters) + " km");

                //For example spead is 10 meters per minute.
                int speedIs10MetersPerMinute = 10;
                Double estimatedDriveTimeInMinutes = m / speedIs10MetersPerMinute;
                estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

                tvdura.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");*/
/*                ProximityListMapFragment pnf = new ProximityListMapFragment(getActivity(), getContext());
                manager.beginTransaction().replace(R.id.mainLayout, pnf).commit();*/

                //Get Snail Trail Geo Location for plotting


                mMapFragment.clear();

                dismiss();

                NavigationMapFragment nf = new NavigationMapFragment(activity);
                manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, nf).commit();
            }
        });
    }

    public void showToast(String msg) {
        if (msg != null && activity != null)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
