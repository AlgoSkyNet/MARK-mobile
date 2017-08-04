package com.journeytech.mark.mark;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.journeytech.mark.mark.fragment.SnailTrailFragment;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import static com.journeytech.mark.mark.activity.MainActivity.manager;

public class BottomSheetModalFragment extends BottomSheetDialogFragment {

    public static String dateFrom, dateTo;

    CustomDateTimePicker custom, custom2;

    Activity activity;

    public BottomSheetModalFragment(Activity a) {
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
        ImageButton snailtrail = (ImageButton) contentView.findViewById(R.id.snailtrail);
        ImageButton proximity = (ImageButton) contentView.findViewById(R.id.proximity);
        ImageButton navigation = (ImageButton) contentView.findViewById(R.id.navigation);
        snailtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getContext());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.date_time);
                dialog.setCancelable(false);

                final TextView tv7 = (TextView) dialog.findViewById(R.id.textView7);
                Button btn3 = (Button) dialog.findViewById(R.id.button3);
                btn3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        if (getActivity() == null) {
                            System.out.println("yes");
                        }
                        final FragmentManager manager = getFragmentManager();
                        custom = new
                                CustomDateTimePicker(activity,
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

                                        tv7.setText(dateFrom);
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

                final TextView tv9 = (TextView) dialog.findViewById(R.id.textView9);
                Button b4 = (Button) dialog.findViewById(R.id.button4);
                b4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final FragmentManager manager = getFragmentManager();
                        custom2 = new
                                CustomDateTimePicker(activity,
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

                                        dateTo = (i_monthNumber)
                                                + "/" + (i_calendar_day) + "/" + year
                                                + " " + i_hr + ":" + i_min
                                                + ":" + i_sec;

                                        tv9.setText(dateTo);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                        /**
                         * Pass Directly current time format it will return AM and PM if you set
                         * false
                         */
                        custom2.set24HourFormat(false);
                        /**
                         * Pass Directly current data and time to show when it pop up
                         */
                        custom2.setDate(Calendar.getInstance());
                        custom2.showDialog();

                        dismiss();
                    }
                });


                Button b5 = (Button) dialog.findViewById(R.id.button5);
                b5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dateFrom == null || dateFrom.equals("null") &&dateTo == null || dateTo.equals("null")) {
                            Toast.makeText(getContext(), "Date From and Date To is 'empty'.", Toast.LENGTH_LONG).show();
                        } else if (dateFrom == null || dateFrom.equals("null")) {
                            Toast.makeText(getContext(), "Please select Date From.", Toast.LENGTH_LONG).show();
                        } else if(dateTo == null || dateTo.equals("null")) {
                            Toast.makeText(getContext(), "Please select Date To.", Toast.LENGTH_LONG).show();
                        } else {
                            SnailTrailFragment stf = new SnailTrailFragment(getContext(), getActivity());
                            manager.beginTransaction().replace(R.id.mainLayout, stf).commit();
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
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
