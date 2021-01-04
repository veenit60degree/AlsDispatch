package com.als.dispatchNew.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aigestudio.wheelpicker.WheelPicker;
import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.activity.MediaActivity;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.models.DocumentModel;
import com.als.dispatchNew.models.OtherLoadModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ConfirmDeliveryFragment extends Fragment implements View.OnClickListener, WheelPicker.OnItemSelectedListener{

    private EditText delvryTimeEditText, timeReasonEditText, entryTimeEditText, exitTimeEditText, delCommentsEditText;
    View rootView;
    Constants constants;

    Button confirmDeliveryBtn,  selectButton, entryTimeBtn, exitTimeBtn;
    TextView tripTitleTV, wheelSelectedDelBtn;
    TextView wareHouseAddTV, yardAddTV, consigneeAddTV;
    DatePicker datePicker;
    Calendar calendar;
    String month = "", dayyy = "";
    ImageView addMoreImages, menuImageView;
    LinearLayout addMoreItemsLay, delAddressLay, yardLay, wareHouseLay;
    RelativeLayout confirmDelMainLay, searchTripBtn, backMenuLay, localTripBtn, wheelPickerDelLay, timeReasonLay;
    Button timeReasonBtn;
    TextInputLayout delTimeInputLay, timeReasonInputLay;
    RadioButton consigneeRadioBtn, yardRadioBtn, wareHouseRadioBtn;
    LinearLayout horizontalContainerLay;
    TextView othetLoadTitleTV;
    HorizontalScrollView othetLoadTitleScroll;

    String imagePath = "" ;
    int layoutHeight, imageTag = 1,  hour,minute, LoadStatusId = 0;
    String date = "", time = "",loadId = "", tripId = "", JobId = "", TripType = "", CompanyId = "", loadArray = "", DeliveryDocumentId = "";
    String startDate = "", endDate = "", waitingTime = "", jobLoadId = "", deliveryAddressTypeLocal = "", deliveryAddressType = "";
    String otherLoadId = "";
    RequestQueue queue;
    StringRequest postRequest;
    Dialog picker;
    File file;
    List<DocumentModel> dataList = new ArrayList<DocumentModel>();
    ArrayList<String> DeliveryDocumentIdArray = new ArrayList<String>();
    ArrayList<String> loadNumberArray = new ArrayList<>();
    ArrayList<String> finalSelectedLoad = new ArrayList<>();
    ProgressDialog p;
    WheelPicker wheelPickerConfirmDel;
    int MEDIA_FILE = 515;
    String DriverId = "", DeviceId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.confirm_delivery_layout, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        constants = new Constants();
        delvryTimeEditText  = (EditText)view.findViewById(R.id.delTimeEditText);
        timeReasonEditText  = (EditText)view.findViewById(R.id.timeReasonEditText);
        delCommentsEditText = (EditText)view.findViewById(R.id.delCommentsEditText);
        entryTimeEditText   = (EditText)view.findViewById(R.id.entryTimeEditText);
        exitTimeEditText    = (EditText)view.findViewById(R.id.exitTimeEditText);

        tripTitleTV         = (TextView)view.findViewById(R.id.tripTitleTV);
        wheelSelectedDelBtn = (TextView)view.findViewById(R.id.wheelSelectedDelBtn);
        wareHouseAddTV      = (TextView)view.findViewById(R.id.wareHouseAddTV);
        yardAddTV           = (TextView)view.findViewById(R.id.yardAddTV);
        consigneeAddTV      = (TextView)view.findViewById(R.id.consigneeAddTV);

        confirmDeliveryBtn  = (Button)view.findViewById(R.id.confirmDeliveryBtn);
        confirmDelMainLay   = (RelativeLayout)view.findViewById(R.id.confirmDelMainLay);
        searchTripBtn       = (RelativeLayout)view.findViewById(R.id.searchTripBtn);
        localTripBtn        = (RelativeLayout)view.findViewById(R.id.localTripBtn);
        backMenuLay         = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        wheelPickerDelLay   = (RelativeLayout)view.findViewById(R.id.wheelPickerDelLay);
        timeReasonLay       = (RelativeLayout)view.findViewById(R.id.timeReasonLay);

        othetLoadTitleTV    = (TextView)view.findViewById(R.id.otherLoadTitleTV);
        othetLoadTitleScroll= (HorizontalScrollView)view.findViewById(R.id.otherLoadScroll);

        timeReasonBtn       = (Button) view.findViewById(R.id.timeReasonBtn);

        timeReasonInputLay  = (TextInputLayout) view.findViewById(R.id.timeReasonInputLay);
        delTimeInputLay     = (TextInputLayout) view.findViewById(R.id.delTimeInputLay);

        delAddressLay       = (LinearLayout)view.findViewById(R.id.delAddressLay);
        addMoreItemsLay     = (LinearLayout)view.findViewById(R.id.addMoreItemsLay);
        yardLay             = (LinearLayout)view.findViewById(R.id.yardLay);
        wareHouseLay        = (LinearLayout)view.findViewById(R.id.wareHouseLay);
        horizontalContainerLay = (LinearLayout)view.findViewById(R.id.horizontalContainerLay);

        addMoreImages       = (ImageView)view.findViewById(R.id.addMoreImages);
        menuImageView       = (ImageView)view.findViewById(R.id.menuImageView);

        entryTimeBtn        = (Button) view.findViewById(R.id.entryTimeBtn);
        exitTimeBtn         = (Button) view.findViewById(R.id.exitTimeBtn);

        wheelPickerConfirmDel = (WheelPicker)view.findViewById(R.id.wheelPickerConfirmDel);

        consigneeRadioBtn   = (RadioButton) view.findViewById(R.id.consigneeRadioBtn);
        yardRadioBtn        = (RadioButton) view.findViewById(R.id.yardRadioBtn);
        wareHouseRadioBtn   = (RadioButton) view.findViewById(R.id.wareHouseRadioBtn);

        othetLoadTitleTV.setVisibility(View.GONE);
        othetLoadTitleScroll.setVisibility(View.GONE);

        wheelPickerConfirmDel.setOnItemSelectedListener(this);

        layoutHeight = addMoreImages.getLayoutParams().height;
        menuImageView.setImageResource(R.drawable.nback);
        backMenuLay.setPadding(4, 25, 35, 25);

        DriverId = Globally.getDriverId(getActivity());
        DeviceId = Globally.getDeviceId( getActivity());

        searchTripBtn.setVisibility(View.GONE);
        localTripBtn.setVisibility(View.GONE);
        tripTitleTV.setText(getResources().getString(R.string.ConfirmDelivery));

        try{
            Globally.getBundle = this.getArguments();
            loadId          = Globally.getBundle.getString("load_Id");
            tripId          = Globally.getBundle.getString("TripId");
            TripType        = Globally.getBundle.getString("TripType");
            JobId           = Globally.getBundle.getString("JobId");
            LoadStatusId    = Globally.getBundle.getInt("LoadStatusId");
            loadArray       = Globally.getBundle.getString("array");

            JSONObject userData = new JSONObject(Globally.getDriverLoginData(getActivity()));
            CompanyId = userData.getString("CompanyId");

            if(TripType.equals( getResources().getString(R.string.local_trip) )){
              //  delAddressLay.setVisibility(View.GONE);
                timeReasonBtn.setVisibility(View.VISIBLE);
                timeReasonEditText.setHint("DOCUMENT TYPE");
                timeReasonInputLay.setHint("");
                GetJobLoadType(DeviceId, DriverId, CompanyId);
            }else{
                if(LoadStatusId == constants.DropBeforeTripDelivered || LoadStatusId == constants.DroppedLoadPickedUp){
                    yardLay.setVisibility(View.GONE);
                    wareHouseLay.setVisibility(View.GONE);
                    consigneeRadioBtn.setChecked(true);
                    deliveryAddressType = String.valueOf(constants.Delivered );

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        addMoreImages.setOnClickListener(this);
        entryTimeBtn.setOnClickListener(this);
        exitTimeBtn.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);
        confirmDeliveryBtn.setOnClickListener(this);
        wheelSelectedDelBtn.setOnClickListener(this);
        timeReasonBtn.setOnClickListener(this);
        delTimeInputLay.setOnClickListener(this);
        consigneeRadioBtn.setOnClickListener(this);
        yardRadioBtn.setOnClickListener(this);
        wareHouseRadioBtn.setOnClickListener(this);
        horizontalContainerLay.setOnClickListener(this);

    }




    void addLoadItems(){
        //create LayoutInflator class
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

        try{
            horizontalContainerLay.removeAllViews();
            loadNumberArray = new ArrayList<>();
            otherLoadId = "";
            JSONArray loadJsonArray = new JSONArray(loadArray);

            for(int tripCount = 0 ; tripCount < loadJsonArray.length() ; tripCount++) {

                JSONObject TripDetailJson = (JSONObject) loadJsonArray.get(tripCount);

                JSONArray DispatchedLoadArray = (JSONArray) TripDetailJson.get("DispatchedLoad");

                for(int i = 0 ; i < DispatchedLoadArray.length() ; i++){
                    JSONObject objItem = (JSONObject)DispatchedLoadArray.get(i);
                    int LoadStatusId = objItem.getInt("LoadStatusId");

                    if(LoadStatusId == Constants.Dispatched || LoadStatusId == Constants.PickedUp ||
                            LoadStatusId == Constants.InternationalLoadPickedUp || LoadStatusId == Constants.LocalDeliveryPickedUp ||
                            LoadStatusId == Constants.DroppedLoadPickedUp ){

                        String LoadId = objItem.getString("LoadId");
                        String LoadNumber = objItem.getString("LoadNumber");

                        if(!LoadId.equals(loadId)) {
                            loadNumberArray.add(LoadId);
                            finalSelectedLoad.add("");

                            LinearLayout clickableColumn = (LinearLayout) inflater.inflate(R.layout.item_checkbox, null);

                            // create dynamic LinearLayout and set Image on it.
                            CheckBox checkBox = (CheckBox) clickableColumn.findViewById(R.id.checkboxLoadId);
                            checkBox.setText(LoadNumber);
                            checkBox.setId(loadNumberArray.size()-1);
                            checkBox.setAllCaps(true);
                            horizontalContainerLay.addView(clickableColumn);


                            if (clickableColumn != null) {
                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                                        int position = compoundButton.getId();
                                        if (checked) {
                                            finalSelectedLoad.set(position, loadNumberArray.get(position));
                                        }else{
                                            finalSelectedLoad.set(position, "");
                                        }
                                       // Log.d("finalSelectedLoad", "finalSelectedLoad: " + finalSelectedLoad);
                                    }
                                });
                            }
                        }

                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }




    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backMenuLay:
                getFragmentManager().popBackStack();
                break;


            case R.id. entryTimeBtn:

                datePickerDialog(entryTimeEditText, "entry");
                break;

            case R.id. exitTimeBtn:

                datePickerDialog(exitTimeEditText, "exit");
                break;


            case R.id.timeReasonBtn:
                if(TripType.equals( getResources().getString(R.string.local_trip) )){
                    wheelPickerConfirmDel.setVisibility(View.VISIBLE);
                    wheelPickerDelLay.setVisibility(View.VISIBLE);
                }
                break;



            case R.id.wheelSelectedDelBtn:
                wheelPickerConfirmDel.setVisibility(View.GONE);
                wheelPickerDelLay.setVisibility(View.GONE);
                break;


            case R.id.addMoreImages:

                if(TripType.equals( getResources().getString(R.string.local_trip) )){
                    if(jobLoadId.length() > 0){
                        mediaDialog();
                    }else {
                        Globally.showToast(confirmDelMainLay, getResources().getString(R.string.del_doc_type));
                    }
                }else{
                    mediaDialog();
                }



                break;



            case R.id.confirmDeliveryBtn:

                if(Globally.isInternetOn(getActivity())) {
                    if(deliveryAddressType.length() > 0){
                        if (startDate.length() > 0) {
                            if (endDate.length() > 0) {
                                if(constants.getDateDifference(startDate, endDate)) {
                                    confirmPopUp();
                                }else{
                                    Globally.showToast(confirmDelMainLay, getResources().getString(R.string.end_time_should_be_greater));
                                }
                            } else {
                                Globally.showToast(confirmDeliveryBtn, getResources().getString(R.string.del_end_time));
                            }
                        } else {
                            Globally.showToast(confirmDeliveryBtn, getResources().getString(R.string.del_start_time));
                        }
                    }else{
                        Globally.showToast(confirmDelMainLay, getResources().getString(R.string.selectDelAdd));
                    }
                }else{
                    Globally.showToast(confirmDeliveryBtn, Globally.CHECK_INTERNET_MSG);
                }


                break;


            case R.id.consigneeRadioBtn:
                deliveryAddressType = String.valueOf(constants.Delivered );
                deliveryAddressTypeLocal = String.valueOf(constants.LocalConsignee);
                yardRadioBtn.setChecked(false);
                wareHouseRadioBtn.setChecked(false);

                othetLoadTitleTV.setVisibility(View.GONE);
                othetLoadTitleScroll.setVisibility(View.GONE);
                addLoadItems();

                break;


            case R.id.yardRadioBtn:
                deliveryAddressType = String.valueOf(constants.DropAtYard);
                deliveryAddressTypeLocal = String.valueOf(constants.LocalYard);
                consigneeRadioBtn.setChecked(false);
                wareHouseRadioBtn.setChecked(false);

                othetLoadTitleTV.setVisibility(View.GONE);
                othetLoadTitleScroll.setVisibility(View.GONE);
                addLoadItems();


                break;


            case R.id.wareHouseRadioBtn:
                deliveryAddressType = String.valueOf(constants.DropAtWarehouse);
                deliveryAddressTypeLocal = String.valueOf(constants.LocalWarehouse);
                yardRadioBtn.setChecked(false);
                consigneeRadioBtn.setChecked(false);

                othetLoadTitleTV.setVisibility(View.VISIBLE);
                othetLoadTitleScroll.setVisibility(View.VISIBLE);
                addLoadItems();

                break;






        }
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    void datePickerDialog(final EditText editText, final String fieldName ){

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

      //  Date dateee = calendar.getTime();
       // long mills = dateee.getTime();

     //   datePicker.setMaxDate(mills);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


       // timePicker.setCurrentMinute(minute);


        selectButton = (Button)picker.findViewById(R.id.setDate);


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
                    editText.setText(String.valueOf(date + " " +time));
                    picker.dismiss();
                }

                if(fieldName.equals("exit")) {
                    endDate = date + " " +time;
                }else {
                    startDate = date + " " +time;
                }

               // Log.d("startDate", "----startDate" +startDate);
              //  Log.d("endDate", "----endDate" +endDate);

                if(selectButton.getText().toString().equals("Set Time")) {
                    if (startDate.length() > 0 && endDate.length() > 0) {
                        if(constants.getDateDifference(startDate, endDate)) {
                            waitingTime = constants.printDifference(startDate, endDate);
                            delvryTimeEditText.setText(waitingTime);
                        }else{
                            Globally.showToast(confirmDelMainLay, getResources().getString(R.string.end_time_should_be_greater));
                        }
                    }
                }

                selectButton.setText("Set Time");


            }
        });

        picker.show();

    }




    // ================== Get Confirm Delivery ===================

    void ConfirmDelivery(final  String API, final String DeliveryWaitingTime,final String DelWaitingTimeReason,final String DeviceId,
                         final String DriverId, final  String EntryTime,final String ExitTime,
                         final String LoadDelDocIds,final String LoadID, final String TripId,
                         final String Latitude, final String Longitude){

        otherLoadId = "";
        try {
            for (int i = 0; i < finalSelectedLoad.size(); i++) {
                if (finalSelectedLoad.get(i).length() > 0) {
                    otherLoadId = otherLoadId + "," + finalSelectedLoad.get(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

       p = new ProgressDialog(getActivity());
        p.setMessage("Loading ...");
        p.show();

        queue = Volley.newRequestQueue(getActivity());
        postRequest = new StringRequest(Request.Method.POST, API , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(p != null){
                    p.dismiss();
                }

                Log.d("response: ", "--response: " +response);
                String status = "", message = "", data = "";
                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");

                    if(status.equalsIgnoreCase("true")){

                        Globally.showToast(confirmDelMainLay, "Delivery Confirmed");

                        if(TripType.equals( getResources().getString(R.string.local_trip) )){
                            getFragmentManager().popBackStackImmediate("local_trips", 0);
                        }else{
                            getFragmentManager().popBackStackImmediate("trips", 0);
                        }

                    }else if (status.equalsIgnoreCase("false")) {

                        Globally.showToast(confirmDelMainLay, message);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.StopService(getActivity());

                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(i);
                            getActivity().finish();
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(p != null){
                            p.dismiss();
                        }

                        Log.e("error", "error "+error);
                        Globally.showToast(confirmDelMainLay,  getString(R.string.error_desc) );
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();


                params.put("DeviceId", DeviceId );
                params.put("DriverId", DriverId);
                params.put("DeliveryWaitingTime", DeliveryWaitingTime );
                params.put("DeliverywaitingTimeReason", DelWaitingTimeReason);

                if(TripType.equals( getResources().getString(R.string.local_trip) )){
                    params.put("JobLoadDocTypeId", LoadDelDocIds);   // for Local Trip
                    params.put("CompanyID", CompanyId);
                    params.put("JobID", jobLoadId);
                    params.put("LoadStatusId", deliveryAddressTypeLocal);
                }else{
                    params.put("LoadStatusId", deliveryAddressType);
                    params.put("LoadDeliveryDocumentIds", LoadDelDocIds );   // for international Trip
                }

                params.put("EntryTime", EntryTime);
                params.put("ExitTime", ExitTime );
                params.put("LoadID", LoadID);
                params.put("OtherLoads", otherLoadId);
                params.put("TripId", TripId);
                params.put("Latitude", Latitude);
                params.put("Longitude", Longitude);


                return params;
            }
        };
        queue.add(postRequest);



    }




// ================== Get Confirm Delivery ===================

    void GetJobLoadType( final String DeviceId, final String DriverId, final String CompanyId){


        queue = Volley.newRequestQueue(getActivity());

        p = new ProgressDialog(getActivity());
        p.setMessage("Loading ...");
        p.show();


        postRequest = new StringRequest(Request.Method.POST, APIs.GET_JOB_LOAD_TYPE , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(p != null){
                    p.dismiss();
                }


                Log.d("response: ", "--response: " +response);
                String status = "", message = "", data = "";
                JSONArray DocTypeArray;

                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");

                    if(status.equalsIgnoreCase("true")){

                        DocTypeArray = new JSONArray(Globally.obj.getString("Data"));

                        dataList.clear();

                        List<String> DocTypeList = new ArrayList<>();
                        for(int i = 0 ; i < DocTypeArray.length() ; i++){
                            JSONObject docJSON = (JSONObject)DocTypeArray.get(i);

                            String JobDocTypeId = docJSON.getString("JobDocTypeId");
                            String JobDocTypeName = docJSON.getString("JobDocTypeName");
                            DocTypeList.add(JobDocTypeName);

                            DocumentModel dataModel = new DocumentModel(JobDocTypeId, JobDocTypeName,
                                        docJSON.getString("Description"),"","","","");
                            dataList.add(dataModel);
                        }

                        if(dataList.size() >= 0){
                            wheelPickerConfirmDel.setData(DocTypeList);
                        }else{
                            timeReasonLay.setVisibility(View.GONE);
                        }


                    }else if (status.equalsIgnoreCase("false")) {

                        Globally.showToast(confirmDeliveryBtn, message);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.setDriverId( "", getActivity());
                            Globally.setPassword( "", getActivity());
                            Globally.setUserName( "", getActivity());

                            getActivity().stopService(Globally.serviceIntent);

                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(i);
                            getActivity().finish();
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "error "+error);
                        if(p != null){
                            p.dismiss();
                        }

                        Globally.showToast(confirmDeliveryBtn,  getString(R.string.error_desc) );
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                params.put("DeviceId", DeviceId );
                params.put("DriverId", DriverId);
                params.put("CompanyId", CompanyId);

                return params;
            }
        };
        queue.add(postRequest);



    }


    void confirmPopUp(){

        final Dialog picker = new Dialog(getActivity());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_edit_delete_lay);



        TextView confirmPopupButton = (TextView)picker.findViewById(R.id.confirmPopupButton);
        TextView cancelPopupButton = (TextView)picker.findViewById(R.id.cancelPopupButton);


        confirmPopupButton.setTypeface(Typeface.DEFAULT_BOLD);
        cancelPopupButton.setTypeface(Typeface.DEFAULT);
        DeliveryDocumentId = "";

        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });


        confirmPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            for(int i = 0; i < DeliveryDocumentIdArray.size();i++){
                if(i == 0) {
                    DeliveryDocumentId = DeliveryDocumentIdArray.get(i);
                }else{
                    DeliveryDocumentId = DeliveryDocumentId + ","+ DeliveryDocumentIdArray.get(i);
                }
            }

            String API = "";
            if(TripType.equals( getResources().getString(R.string.local_trip) )){
                API = APIs.LOCAL_LOAD_DELIVERED;
            }else{
                API = APIs.CONFIRM_DELIVERY;

            }


                ConfirmDelivery(
                        API,
                        delvryTimeEditText.getText().toString().trim(),
                        timeReasonEditText.getText().toString().trim(),
                        Globally.getDeviceId( getActivity()),
                        Globally.getDriverId(getActivity()),
                        entryTimeEditText.getText().toString().trim(),
                        exitTimeEditText.getText().toString().trim() ,
                        DeliveryDocumentId, loadId, tripId, Globally.LATITUDE , Globally.LONGITUDE);


            }
        });



        picker.show();
    }





    /* ----------------- Media Dialog For Camera/Gallery---------------- */
    void mediaDialog(){

        final Dialog mediaPicker = new Dialog(getActivity());
        mediaPicker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = mediaPicker.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        mediaPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mediaPicker.setContentView(R.layout.popup_loading_status);

        Button cameraBtn, galleryBtn, popupCancelSBtn;
        cameraBtn = (Button)mediaPicker.findViewById(R.id.cameraBtn);
        galleryBtn = (Button)mediaPicker.findViewById(R.id.galleryBtn);
        popupCancelSBtn = (Button)mediaPicker.findViewById(R.id.popupCancelSBtn);


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("camera");
            }
        });


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("gallery");
            }
        });




        popupCancelSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
            }
        });
        mediaPicker.show();

    }



    /* ----------------- Call Media Activity ---------------- */
    void mediaIntent(String from){
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra("type", from);
        startActivityForResult(i, MEDIA_FILE );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK && requestCode == MEDIA_FILE) {

            imagePath = data.getStringExtra("result");    //data.getExtras().getString("crop_image");
            try {

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);  // MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                  Log.d("--imagePath: ", "---imagePath: " + imagePath);
                file = new File(imagePath);

                if (file.exists()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutHeight, layoutHeight);
                    final ImageView imageView = new ImageView (getActivity());
                    params.setMargins(0,0,16,0);

                    imageView.setTag(imageTag);
                    imageView.setId(imageTag);
                    imageView.setImageBitmap(bitmap);

                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(params);

                    addMoreItemsLay.addView(imageView);
                    imageTag++;

                    new UploadImage().execute();
                }

                confirmDeliveryBtn.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {

        timeReasonEditText.setText(dataList.get(position).getJobId());
        jobLoadId = dataList.get(position).getJobLoadDocId();

        wheelPickerConfirmDel.setVisibility(View.GONE);
        wheelPickerDelLay.setVisibility(View.GONE);


    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class UploadImage extends AsyncTask<String, String, String> {

        String strResponse = "";
        com.squareup.okhttp.Response response;
        String ApiUploadPic = "";


        @Override
        protected String doInBackground(String... params) {
            // publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                com.squareup.okhttp.Request request = null;
                com.squareup.okhttp.MultipartBuilder builderNew = null;

                if(TripType.equals( getResources().getString(R.string.local_trip)) ){
                    ApiUploadPic = APIs.LOCAL_DOC_UPLOAD;
                    builderNew = new com.squareup.okhttp.MultipartBuilder().type(com.squareup.okhttp.MultipartBuilder.FORM)
                            .addFormDataPart("DeviceId", DeviceId)
                            .addFormDataPart("DriverId", DriverId)
                            .addFormDataPart("LoadId", loadId)
                            .addFormDataPart("JobId", JobId)
                            .addFormDataPart("JobLoadDocTypeId", jobLoadId);
                }else {
                    ApiUploadPic = APIs.PHOTO_UPLOAD;
                    builderNew = new com.squareup.okhttp.MultipartBuilder().type(com.squareup.okhttp.MultipartBuilder.FORM)
                            .addFormDataPart("DeviceId", DeviceId)
                            .addFormDataPart("DriverId", DriverId)
                            .addFormDataPart("LoadID", loadId);
                }


                File f = new File(imagePath);
                if (f.exists()) {
                    // Log.i("", "---Add File: " + f.toString());
                    builderNew.addFormDataPart("myFile", "file.jpeg", com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("image/jpeg"), new File(f.toString())));
                }

                com.squareup.okhttp.RequestBody requestBody = builderNew.build();
                request = new com.squareup.okhttp.Request.Builder()
                        // .header("Content-Type", "multipart/form-data")
                        .url(ApiUploadPic)
                        .post(requestBody)
                        .build();

                int timeOut = 20;
                com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
                client.setConnectTimeout(timeOut, TimeUnit.SECONDS); // connect timeout
                client.setReadTimeout(timeOut, TimeUnit.SECONDS);
                response = client.newCall(request).execute();

                //   response = okk.newCall(request).execute();
                strResponse = response.body().string();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.e("String Response", ">>>>   " + result);

            try {

                if(result.length() > 0) {
                    JSONObject obj = new JSONObject(result);
                    String response = "";
                    response = obj.getString("Status");
                    if (response.equalsIgnoreCase("true")) {
                        JSONObject jsonData = new JSONObject(obj.getString("Data"));

                        timeReasonEditText.setText("");
                        jobLoadId = "";

                        if(TripType.equals( getResources().getString(R.string.local_trip) )){
                            if (jsonData.has("JobLoadDocId")) {
                                DeliveryDocumentIdArray.add(jsonData.getString("JobLoadDocId"));
                            }
                        }else {
                            if (jsonData.has("LoadDeliveryDocumentId")) {
                                DeliveryDocumentIdArray.add(jsonData.getString("LoadDeliveryDocumentId"));
                            }
                        }

                    } else {
                        removeAddedField(imageTag-1);
                    }
                }else{
                    removeAddedField(imageTag-1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                removeAddedField(imageTag-1);
            }
        }

    }


    private void removeAddedField(int layoutId) {
        ImageView view = (ImageView) addMoreItemsLay.findViewById(layoutId);
        addMoreItemsLay.removeView(view);
        Globally.showToast(confirmDelMainLay, getResources().getString(R.string.failedToUploadDoc));

    }


}




