package com.journeytech.mark.mark.list_fragment;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.journeytech.mark.mark.DistanceBetween;
import com.journeytech.mark.mark.R;
import com.journeytech.mark.mark.activity.MainActivity;

import java.text.DecimalFormat;

import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.date;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.engine;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.latitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.location;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.longitudeListMap;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.plate_num;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.remarks;
import static com.journeytech.mark.mark.list_fragment.VehicleListMapFragment.time;

public class ProximityBottomSheetModalMapFragment extends BottomSheetDialogFragment {
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

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.proximity_bottomsheet3, null);

        TextView plate_n = (TextView) contentView.findViewById(R.id.plate_num);
        TextView distance = (TextView) contentView.findViewById(R.id.TVDistance);
        TextView tim = (TextView) contentView.findViewById(R.id.TVTime);
        plate_n.setText("PLATE NO.: " +plate_num+ "\n"+
                        "LAST SEEN: "+date+ " "+time + "\n" +
                        "LOCATION: "+location+"\n"+
                        "ENGINE: " + engine + "\n"+
                        "REMARKS: "+ remarks);

        LatLng l1 = new LatLng(latitudeListMap, longitudeListMap);
        LatLng l2 = new LatLng(MainActivity.getLatitude(), MainActivity.getLongitude());

        Double m = DistanceBetween.distanceBetween(l1, l2);
        double km = 1000;
        double distanceInMeters = m / km;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        distance.setText(df.format(distanceInMeters) + " km");

        //For example spead is 10 meters per minute.
        int speedIs10MetersPerMinute = 10;
        Double estimatedDriveTimeInMinutes = m / speedIs10MetersPerMinute;
        estimatedDriveTimeInMinutes = estimatedDriveTimeInMinutes / 60;

        tim.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

        dialog.setContentView(contentView);
    }
}
