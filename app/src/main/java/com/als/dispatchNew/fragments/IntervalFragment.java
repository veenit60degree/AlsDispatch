package com.als.dispatchNew.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.constants.Constants;

import java.util.Calendar;
import java.util.Date;

public class IntervalFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    View rootView;
    Constants constants;

    TextView dateToTxtView, dateFromTxtView, timeFromTxtView, timeToTxtView;
    Button showHistoryBtn;
    LinearLayout ToDateBtn, FromDateBtn;
    Dialog picker;
    DatePicker datePicker;
    Calendar calendar;
    String month = "", dayyy = "";
    int  hour,minute;
    String date = "", time = "", startDate = "", endDate = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{

            rootView = inflater.inflate(R.layout.interval_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }catch (Exception e){
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }

    void initView(View view) {

        constants = new Constants();
        dateToTxtView = (TextView) view.findViewById(R.id.dateToTxtView);
        dateFromTxtView = (TextView) view.findViewById(R.id.dateFromTxtView);
        timeFromTxtView   = (TextView) view.findViewById(R.id.timeFromTxtView);
        timeToTxtView   = (TextView) view.findViewById(R.id.timeToTxtView);

        showHistoryBtn = (Button) view.findViewById(R.id.showHistoryBtn);

        FromDateBtn    = (LinearLayout) view.findViewById(R.id.FromDateBtn);
        ToDateBtn      = (LinearLayout)view.findViewById(R.id.ToDateBtn);

        FromDateBtn.setOnClickListener(this);
        ToDateBtn.setOnClickListener(this);
        showHistoryBtn.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.FromDateBtn:
                datePickerDialog("start");
            break;


            case R.id.ToDateBtn:

                datePickerDialog("end");
                break;


            case R.id.showHistoryBtn:

                if (startDate.length() > 0 && endDate.length() > 0) {
                    if(constants.getDateDifference(startDate, endDate)) {
                        TripHistoryPagerFragment.tripHistoryPager.setCurrentItem(0);  //selectPage(1);
                    }else{
                        Globally.showToast(showHistoryBtn, getResources().getString(R.string.end_time_should_be_greater));
                    }
                }else{
                    Globally.showToast(showHistoryBtn, getResources().getString(R.string.select_from_to_time));
                }

            break;

        }

    }





    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    void datePickerDialog( final String inputType ){

        if(picker != null && picker.isShowing()){
            picker.dismiss();
        }

        picker = new Dialog(getActivity());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_datepicker);
        //picker.setTitle("Select Date and Time");


        final TimePicker timePicker;
        timePicker = (TimePicker)picker.findViewById(R.id.timePicker);
        datePicker=(DatePicker)picker.findViewById(R.id.datePicker);
        calendar = Calendar.getInstance();
        //  calendar.add(Calendar.YEAR, -18);
        Date dateee = calendar.getTime();
        long mills = dateee.getTime();

        datePicker.setMaxDate(mills);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        // timePicker.setCurrentMinute(minute);


       final Button selectButton = (Button)picker.findViewById(R.id.setDate);


        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int day=datePicker.getDayOfMonth();
                int year=datePicker.getYear();
                int mnth=datePicker.getMonth()+1;

                // set current time into timepicker
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
                String min = String.valueOf(minute);
                String hr = String.valueOf(hour);

                if(min.length() == 1){
                    min = "0"+min;
                }

                if(hr.length() == 1){
                    hr = "0"+hr;
                }

                if(String.valueOf(mnth).length() == 1){
                    month = "0"+String.valueOf(mnth);
                }else {
                    month = String.valueOf(mnth);
                }

                if(String.valueOf(day).length() == 1){
                    dayyy = "0"+String.valueOf(day);
                }else {
                    dayyy = String.valueOf(day);
                }


                if(month.length() == 1){
                    month = "0"+month;
                }

                if(selectButton.getText().toString().equals("Set Date")){
                    timePicker.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    date = month +"/"+ dayyy  +"/"+  String.valueOf(year);

                }else{

                    time = hr + ":" + min ;

                    picker.dismiss();
                }

                if(inputType.equals("end")) {
                    endDate = date + " " +time;

                    dateToTxtView.setText(String.valueOf(date) );
                    timeToTxtView.setText(String.valueOf(time));

                }else {
                    startDate = date + " " +time;

                    dateFromTxtView.setText(String.valueOf(date) );
                    timeFromTxtView.setText(String.valueOf(time));

                }

                // Log.d("startDate", "----startDate" +startDate);
                //  Log.d("endDate", "----endDate" +endDate);

                selectButton.setText("Set Time");


            }
        });

        picker.show();

    }



}
