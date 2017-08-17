package com.journeytech.mark.mark.map_fragment;

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

import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.latitudeG;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.longitudeG;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.vm;

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



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.proximity_bottomsheet3, null);

        TextView plate_num = (TextView) contentView.findViewById(R.id.plate_num);
        TextView location = (TextView) contentView.findViewById(R.id.tvLocation);
        TextView distance = (TextView) contentView.findViewById(R.id.tvDistance);
        TextView time = (TextView) contentView.findViewById(R.id.tvTime);
        plate_num.setText(vm.getPlate_num());
        location.setText(VehicleMapFragment.location);

        LatLng l1 = new LatLng(latitudeG, longitudeG);
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

        time.setText(df.format(estimatedDriveTimeInMinutes).toString() + " min.");

        dialog.setContentView(contentView);
    }
}
