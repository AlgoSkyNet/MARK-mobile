package com.journeytech.mark.mark;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.journeytech.mark.mark.fragment.SnailTrailFragment;
import com.journeytech.mark.mark.model.DateTime;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class BottomSheetModalFragment extends BottomSheetDialogFragment {

    public static DateTime dt;
    FragmentManager fm;

    public static String dateFrom;

    DateFormat formatDateTime = DateFormat.getDateTimeInstance();
    Calendar dateTime = Calendar.getInstance();

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
                final FragmentManager manager = getFragmentManager();

                custom = new CustomDateTimePicker(getActivity(),
                        new CustomDateTimePicker.ICustomDateTimeListener() {

                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                              Date dateSelected, int year, String monthFullName,
                                              String monthShortName, int monthNumber, int date,
                                              String weekDayFullName, String weekDayShortName,
                                              int hour24, int hour12, int min, int sec, String AM_PM) {
                                DecimalFormat df = new DecimalFormat("00");
                                String i_hr = df.format(hour24);
                                String i_min = df.format(min);
                                String i_sec = df.format(sec);
                                String i_monthNumber = df.format(dateSelected.getMonth() + 1);
                                String i_calendar_day = df.format(dateSelected.getDay() - 1);

                                dateFrom = (i_monthNumber)
                                        + "/" + (i_calendar_day) + "/" + year
                                        + " " + i_hr + ":" + i_min
                                        + ":" + i_sec;

                                SnailTrailFragment stf = new SnailTrailFragment(getContext(), getActivity());
                                manager.beginTransaction().replace(R.id.mainLayout, stf).commit();

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                /**
                 * Pass Directly current time format it will return AM and PM if you set
                 * false
                 */
                custom.set24HourFormat(false);
                /**
                 * Pass Directly current data and time to show when it pop up
                 */
                custom.setDate(Calendar.getInstance());
                custom.showDialog();

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


    CustomDateTimePicker custom;
}
