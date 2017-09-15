package com.journeytech.mark.mark.list_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeytech.mark.mark.R;

import static com.journeytech.mark.mark.activity.LogIn.typeface;
import static com.journeytech.mark.mark.activity.MainActivity.manager;

public class BottomSheetModalListFragment extends BottomSheetDialogFragment {

    Activity activity;

    public BottomSheetModalListFragment(Activity a) {
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
        final Button proximity = (Button) contentView.findViewById(R.id.proximity);
        final Button navigation = (Button) contentView.findViewById(R.id.navigation);

        snailtrail.setTypeface(typeface);
        proximity.setTypeface(typeface);
        navigation.setTypeface(typeface);

        snailtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SnailTrailTwoHrsFragment stf = new SnailTrailTwoHrsFragment(getContext(), getActivity());
                manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, stf).commit();

                dismiss();

            }
        });

        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

                ProximityListMapFragment plmf = new ProximityListMapFragment();
                manager.beginTransaction().replace(R.id.mainLayout, plmf).commit();

            }
        });

        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

                NavigationListMapFragment nlmf = new NavigationListMapFragment();
                manager.beginTransaction().addToBackStack("sd").replace(R.id.mainLayout, nlmf).commit();
            }
        });
    }

    public void showToast(String msg) {
        if (msg != null && activity != null)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
