package com.journeytech.mark.mark;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.journeytech.mark.mark.fragment_unused.VehicleDetailsFragment;

public class BottomSheetModalFragment extends BottomSheetDialogFragment {

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
        ImageButton snailtrail = (ImageButton) contentView.findViewById(R.id.snailtrail);
        ImageButton proximity = (ImageButton) contentView.findViewById(R.id.proximity);
        ImageButton navigation = (ImageButton) contentView.findViewById(R.id.navigation);
        snailtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Snail Trail", Toast.LENGTH_SHORT).show();
                VehicleDetailsFragment mapFragment;
                FragmentManager manager;
                mapFragment = new VehicleDetailsFragment();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();

//                new GetVehicleValue().execute();
                dismiss();
            }
        });
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Proximity ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Navigation", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

}
