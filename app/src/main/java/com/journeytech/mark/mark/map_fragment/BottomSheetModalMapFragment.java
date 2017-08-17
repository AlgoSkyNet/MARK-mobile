package com.journeytech.mark.mark.map_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.journeytech.mark.mark.CustomDateTimePicker;
import com.journeytech.mark.mark.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import static com.journeytech.mark.mark.activity.LogIn.typeface;
import static com.journeytech.mark.mark.activity.MainActivity.manager;
import static com.journeytech.mark.mark.map_fragment.VehicleMapFragment.mMapFragment;

public class BottomSheetModalMapFragment extends BottomSheetDialogFragment {

    public static String dateFromMapFragment = null, dateToMapFragment = null;

    CustomDateTimePicker custom, custom2;

    Activity activity;

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

                final Dialog dialog = new Dialog(getContext());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.date_time);
                dialog.setCancelable(false);

                ImageView close = (ImageView) dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

//                Button btn3 = (Button) dialog.findViewById(R.id.button3);
                final EditText et2 = (EditText) dialog.findViewById(R.id.editText2);
                et2.setOnClickListener(new View.OnClickListener() {
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
                                                      int hour24, int hour12, int min, int sec,
                                                      String AM_PM) {

                                        DecimalFormat df = new DecimalFormat("00");
                                        String i_hr = df.format(hour24);
                                        String i_min = df.format(min);
                                        String i_sec = df.format(sec);
                                        String i_monthNumber = df.format(monthNumber+1);
                                        String i_calendar_day = df.format(calendarSelected
                                                .get(Calendar.DAY_OF_MONTH));

                                        dateFromMapFragment = (i_monthNumber)
                                                + "/" + i_calendar_day + "/" + year
                                                + " " + i_hr + ":" + i_min
                                                + ":" + i_sec;

                                        et2.setText(dateFromMapFragment);
                                    }

                                    /*@Override
                                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                                      Date dateSelected, int year, String monthFullName,
                                                      String monthShortName, int monthNumber, int date,
                                                      String weekDayFullName, String weekDayShortName,
                                                      int hour24, int hour12, int min, int sec, String AM_PM) {
                                        min = min - 1;
                                            DecimalFormat df = new DecimalFormat("00");
                                            String i_hr = df.format(hour24);
                                            String i_min = df.format(min);
                                            String i_sec = df.format(sec);
                                            String i_monthNumber = df.format(dateSelected.getMonth() + 1);
                                            String i_calendar_day = df.format(dateSelected.getDay() - 1);

                                            dateFrom = (i_monthNumber)
                                                    + "/" + (i_calendar_day) + "/" + year
                                                    + " " + i_hr + ":" + min
                                                    + ":" + i_sec;

                                            tv7.setText(dateFrom);

                                    }*/

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

//                Button b4 = (Button) dialog.findViewById(R.id.button4);
                final EditText et3 = (EditText) dialog.findViewById(R.id.editText3);
                et3.setOnClickListener(new View.OnClickListener() {
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
                                        String i_monthNumber = df.format(monthNumber+1);
                                        String i_calendar_day = df.format(calendarSelected
                                                .get(Calendar.DAY_OF_MONTH));

                                        dateToMapFragment = (i_monthNumber)
                                                + "/" + i_calendar_day + "/" + year
                                                + " " + i_hr + ":" + i_min
                                                + ":" + i_sec;

                                        et3.setText(dateToMapFragment);
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


                ImageView b5 = (ImageView) dialog.findViewById(R.id.button5);
                b5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dateFromMapFragment == null || dateFromMapFragment.equals("null")) {
                            showToast("Please select Date From.");
//                            Toast.makeText(activity, "Please select Date From.", Toast.LENGTH_SHORT).show();
                        }
                        else if(dateToMapFragment == null || dateToMapFragment.equals("null")) {
                            showToast("Please select Date To.");
//                            Toast.makeText(activity, "Please select Date To.", Toast.LENGTH_SHORT).show();
                        } else if (dateFromMapFragment == null || dateFromMapFragment.equals("null") && dateToMapFragment == null || dateToMapFragment.equals("null")) {
                            showToast("Date From and Date To is 'empty'.");
//                            Toast.makeText(activity, "Date From and Date To is 'empty'.", Toast.LENGTH_SHORT).show();
                        }
                         else if (dateToMapFragment == dateFromMapFragment) {
                            showToast("Invalid Date.");

                        }
                        if (dateFromMapFragment != null && dateToMapFragment != null) {
                            SnailTrailMapFragment stf = new SnailTrailMapFragment(getContext(), getActivity());
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
                mMapFragment.clear();

//                ProximityListMapFragment.createProximity(MainActivity.getLatitude(), MainActivity.getLongitude());
                dismiss();

                ProximityMapFragment pnf = new ProximityMapFragment(getActivity(), getContext());
                manager.beginTransaction().replace(R.id.mainLayout, pnf).commit();

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
                manager.beginTransaction().replace(R.id.mainLayout, nf).commit();
            }
        });
    }

    public void showToast(String msg) {
        if (msg != null && activity != null)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
