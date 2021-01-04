package com.als.dispatchNew.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.activity.MediaActivity;
import com.als.dispatchNew.adapter.UploadedImagesAdapter;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.constants.VolleyRequest;
import com.als.dispatchNew.models.UploadedImageModel;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripDetailFragment extends Fragment implements View.OnClickListener {




    View rootView;
    Constants constants;
    ScrollView loadDetailHistoryScroll;
    TextView typeConView, quantityView, tripTitleTV, wieghtView, DescriptionView;
    TextView congineeNameView, consigneeAddressView, shipperNameConView, shipperAddressConView;
    TextView shipperNameConTV, consineeNameTV, LoadNoTV, poNumberTV, pickupCommentsTV, BillNoView;
    TextView deliveryTimeDetailTV, pickTimeDetailTV, trailerNumberTV,truckNumberTV;
    Button confirmLoadBtn;
    RecyclerView recycleView;
    ImageView menuImageView, addExpenseIV;
    RelativeLayout tripHisDLay, backMenuLay, searchTripBtn, addExpenseBtn, uploadedDocLay;
    FragmentManager fragManager;
    LinearLayoutManager layoutManager;
    UploadedImagesAdapter uploadeddImageAdapter;
    String TripId = "", tripType = "", loadArray = "", LoadId = "", loadNumber = "", trailerNumber  = "", truckNo = "", JobId = "";
    String Description = "", Wheight = "", LoadNumber = "",  ShipperAddress = "",ShipperName = "", ShipperCity = "",
            ShipperStateCode = "", ConsigneeName = "", ConsigneeCity = "", ConsigneeAddress = "",ShipperPostal = "",
            ConsigneeStateCode = "", Type = "", QTY = "", LBS = "",
            ShipperCountryCode="",ConsigneeCountryCode = "",ConsigneePostal="",LoadShipperComment="";
    String shipperAddress = "", consigneeAddress = "";
    String TrailerNumber = "", SealNumber = "", TrailerImage = "", SealImage = "";
    String DriverId, DeviceId, ImageType;
    RequestQueue queue;
    StringRequest postRequest;
    List<UploadedImageModel> imageList = new ArrayList<>();
    boolean IsPickAndDrop, IsArrivedToShipper,IsArrivedToConsignee, IsPickedUp = false;
    int LoadStatusId = 0;
    int CAMERA_FILE = 501;
    int GALLERY_FILE = 502;
    ImageView sealImageView, trailerImageView;
    ProgressDialog progressBarDetail;

    VolleyRequest SaveLocationDetails, GetLoadDetails, UpdateLoadStatus,  ConfirmLoadStatus, ConfirmedPickDropLoad;
    Map<String, String> params;
    final int GetLoadDetail = 101, SaveLocation = 102, UpdateLoad = 103, ConfirmLoad = 104, ConfirmPickDrop = 105;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_trip_details, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        GetLoadDetails              = new VolleyRequest(getActivity());
        SaveLocationDetails         = new VolleyRequest(getActivity());
        UpdateLoadStatus            = new VolleyRequest(getActivity());
        ConfirmLoadStatus           = new VolleyRequest(getActivity());
        ConfirmedPickDropLoad       = new VolleyRequest(getActivity());
        constants = new Constants();
        shipperNameConTV = (TextView)view.findViewById(R.id.shipperNameConTV);
        consineeNameTV = (TextView)view.findViewById(R.id.consineeNameTV);
        tripTitleTV = (TextView)view.findViewById(R.id.tripTitleTV);

        recycleView = (RecyclerView)view.findViewById(R.id.uploadedImgRecycleView);
        loadDetailHistoryScroll = (ScrollView) view.findViewById(R.id.loadDetailHistoryScroll);
        tripHisDLay = (RelativeLayout)view.findViewById(R.id.tripHisDLay);
        backMenuLay = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        searchTripBtn = (RelativeLayout)view.findViewById(R.id.searchTripBtn);
        addExpenseBtn = (RelativeLayout)view.findViewById(R.id.localTripBtn);
        uploadedDocLay= (RelativeLayout)view.findViewById(R.id.uploadedDocLay);

        congineeNameView = (TextView)view.findViewById(R.id.congineeNameView);
        consigneeAddressView = (TextView)view.findViewById(R.id.consigneeAddressView);
        shipperNameConView = (TextView)view.findViewById(R.id.shipperNameConView);
        shipperAddressConView = (TextView)view.findViewById(R.id.shipperAddressConView);
        typeConView = (TextView)view.findViewById(R.id.typeConView);
        quantityView = (TextView)view.findViewById(R.id.quantityView);
        wieghtView = (TextView)view.findViewById(R.id.wieghtView);
        DescriptionView = (TextView)view.findViewById(R.id.DescriptionView);
        LoadNoTV = (TextView)view.findViewById(R.id.LoadNoTV);
        poNumberTV = (TextView)view.findViewById(R.id.poNumberTV);
        pickupCommentsTV = (TextView)view.findViewById(R.id.pickupCommentsTV);
        BillNoView = (TextView)view.findViewById(R.id.BillNoView);
        deliveryTimeDetailTV = (TextView)view.findViewById(R.id.deliveryTimeDetailTV);
        pickTimeDetailTV = (TextView)view.findViewById(R.id.pickTimeDetailTV);
        trailerNumberTV  = (TextView)view.findViewById(R.id.trailerNumberTV);
        truckNumberTV    = (TextView)view.findViewById(R.id.truckNumberTV);

        confirmLoadBtn = (Button)view.findViewById(R.id.confirmLoadBtn);
        menuImageView = (ImageView)view.findViewById(R.id.menuImageView);
        addExpenseIV = (ImageView)view.findViewById(R.id.localTripIV);

        recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        progressBarDetail = new ProgressDialog(getActivity());
        progressBarDetail.setMessage("Loading ...");

        addExpenseIV.setImageResource(R.drawable.ic_add_expense);
        menuImageView.setImageResource(R.drawable.nback);
        tripTitleTV.setText(getResources().getString(R.string.trip_details));
        searchTripBtn.setVisibility(View.GONE);
        addExpenseBtn.setVisibility(View.GONE);
        backMenuLay.setPadding(4, 25, 35, 25);

        confirmLoadBtn.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);

        DriverId = Globally.getDriverId( getActivity());
        DeviceId = Globally.getDeviceId( getActivity());

        Globally.getBundle = this.getArguments();
        tripType = Globally.getBundle.getString("tripType");
        loadArray = Globally.getBundle.getString("array");

        constants.IsFragmentDetail = true;
        constants.isHomeFragment = false;

        getData();

        if(Globally.isInternetOn(getActivity())) {
            GetLoadDetail(DeviceId, DriverId, LoadId);
        }


    }



    private void getData(){
        if(Constants.temperoryList.size() > 0) {

            if(tripType.equals( getResources().getString(R.string.local_trip))){

               // consineeNameTV.setText("Yard");
                consigneeAddress = Constants.temperoryList.get(0).getConsigneeAddress();

                shipperAddress = Constants.temperoryList.get(0).getShipperAddress() + ", " +
                        Constants.temperoryList.get(0).getShipperStateCode() + ", " +
                        Constants.temperoryList.get(0).getShipperCountryCode();

            }else{
                consigneeAddress = Constants.temperoryList.get(0).getConsigneeAddress() + ", " +
                        Constants.temperoryList.get(0).getConsigneeStateCode() + ", " +
                        Constants.temperoryList.get(0).getConsigneeCity() + ", " +
                        Constants.temperoryList.get(0).getConsigneePostal() + ", " +
                        Constants.temperoryList.get(0).getConsigneeCountryCode();

                shipperAddress = Constants.temperoryList.get(0).getShipperAddress() + ", " +
                        Constants.temperoryList.get(0).getShipperStateCode() + ", " +
                        Constants.temperoryList.get(0).getShipperCity() + ", " +
                        Constants.temperoryList.get(0).getShipperPostal() + ", " +
                        Constants.temperoryList.get(0).getShipperCountryCode();

            }


            if (!Constants.temperoryList.get(0).getConsigneeName().equals("") && !Constants.temperoryList.get(0).getConsigneeName().equals("null") && Constants.temperoryList.get(0).getConsigneeName() != null) {
                ConsigneeName = Constants.temperoryList.get(0).getConsigneeName();
                congineeNameView.setText(ConsigneeName);
            }
            if (!Constants.temperoryList.get(0).getConsigneeAddress().equals("") && !Constants.temperoryList.get(0).getConsigneeAddress().equals("null") && Constants.temperoryList.get(0).getConsigneeAddress() != null)
                consigneeAddressView.setText(consigneeAddress.toUpperCase());

            if (!Constants.temperoryList.get(0).getShipperName().equals("") && !Constants.temperoryList.get(0).getShipperName().equals("null") && Constants.temperoryList.get(0).getShipperName() != null){
                ShipperName = Constants.temperoryList.get(0).getShipperName();
                shipperNameConView.setText(ShipperName);
            }

            if (!Constants.temperoryList.get(0).getShipperAddress().equals("") && !Constants.temperoryList.get(0).getShipperAddress().equals("null") && Constants.temperoryList.get(0).getShipperAddress() != null)
                shipperAddressConView.setText(shipperAddress.toUpperCase());

            if (!Constants.temperoryList.get(0).getLoadType().equals("") && !Constants.temperoryList.get(0).getLoadType().equals("null") && Constants.temperoryList.get(0).getLoadType() != null)
                typeConView.setText(Constants.temperoryList.get(0).getLoadType());

            if (!Constants.temperoryList.get(0).getQTY().equals("") && !Constants.temperoryList.get(0).getQTY().equals("null") && Constants.temperoryList.get(0).getQTY() != null)
                quantityView.setText(Constants.temperoryList.get(0).getQTY().split("\\.")[0]);

            if (!Constants.temperoryList.get(0).getLoadStatusId().equals("") && !Constants.temperoryList.get(0).getLoadStatusId().equals("null") && Constants.temperoryList.get(0).getLoadStatusId() != null)
                LoadStatusId = Integer.valueOf(Constants.temperoryList.get(0).getLoadStatusId());


            IsPickedUp = Boolean.parseBoolean(Constants.temperoryList.get(0).isProrityShipment());
            TripId = Constants.temperoryList.get(0).getTripId();
            LoadId = Constants.temperoryList.get(0).getLoadId();
            loadNumber = Constants.temperoryList.get(0).getLoadNumber();
            trailerNumber = Constants.temperoryList.get(0).getTrailorNo();
            truckNo = Constants.temperoryList.get(0).getTruckNo();

            LoadNoTV.setText("Load : #" + loadNumber);
            trailerNumberTV.setText(trailerNumber);
            truckNumberTV.setText(truckNo);

            if(tripType.equals("history")){
                uploadedDocLay.setVisibility(View.VISIBLE);
                confirmLoadBtn.setVisibility(View.GONE);
                tripTitleTV.setText(getResources().getString(R.string.trip_his_details));
                imageList = new ArrayList<>();

                try {
                    JSONArray imageArray = new JSONArray(Constants.temperoryList.get(0).getLoadDeliveryDocuments());
                    for(int i = 0; i < imageArray.length() ; i++){
                        JSONObject obj = (JSONObject)imageArray.get(i);
                        UploadedImageModel imageModel = new UploadedImageModel(i, obj.getString("DocumentPath"));
                        imageList.add(imageModel);

                    }

                    if (imageList.size() > 0 & recycleView != null) {
                        uploadeddImageAdapter = new UploadedImagesAdapter(getActivity(), imageList);
                        recycleView.setAdapter(uploadeddImageAdapter);
                    }
                    recycleView.setLayoutManager(layoutManager);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(tripType.equals(getResources().getString(R.string.local_trip))){

                if(LoadStatusId == constants.LocalDeliveryPickedUp){
                    confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                }else{
                    if(IsPickedUp){
                        confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                    }else{
                        confirmLoadBtn.setText( getResources().getString(R.string.PickupLoad));
                    }
                }

            }
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.localTripBtn:

                break;


            case R.id.backMenuLay:
                getFragmentManager().popBackStack();
                break;

            case R.id.confirmLoadBtn:

                if(tripType.equals(getResources().getString(R.string.local_trip))){
                    if( IsPickedUp == false){

                        LoadStatusId = constants.LocalDeliveryPickedUp;
                        // Call API to make load status as pick up
                        ConfirmLoadStatus(APIs.LOAD_PICKED_UP, DeviceId, DriverId, LoadId,
                                Globally.LATITUDE , Globally.LONGITUDE);
                    }else{
                        moveToConfirmDeliverFragment(tripType, LoadStatusId);
                    }
                }else{
                    if( IsPickAndDrop){
                        if(IsArrivedToShipper || IsArrivedToConsignee){
                            confirmPickDropDialog();
                        }else{
                            // Call API to make load status as arrived
                            if(LoadStatusId == Constants.InternationalLoadPickedUp || LoadStatusId == Constants.DropBeforeTripDelivered ){

                                if(Globally.isInternetOn(getActivity())){
                                    CallAPiOnStatus(LoadStatusId);
                                }else{
                                    Globally.showToast(tripHisDLay, Globally.INTERNET_MSG);
                                }

                            }else if (LoadStatusId == Constants.DroppedLoadPickedUp ){
                                moveToConfirmDeliverFragment(tripType, LoadStatusId);
                            }else{

                                if(Globally.isInternetOn(getActivity())){
                                    ConfirmLoadStatus(APIs.MAKE_LOAD_ARRIVED, DeviceId, DriverId, LoadId,
                                            Globally.LATITUDE , Globally.LONGITUDE);
                                }else{
                                    Globally.showToast(tripHisDLay, Globally.INTERNET_MSG);
                                }




                            }

                        }
                    }else {
                        if(LoadStatusId == Constants.InternationalLoadPickedUp || LoadStatusId == Constants.DropBeforeTripDelivered ){
                            if(Globally.isInternetOn(getActivity())){
                                CallAPiOnStatus(LoadStatusId);
                            }else{
                                Globally.showToast(tripHisDLay, Globally.INTERNET_MSG);
                            }


                        }else{
                            moveToConfirmDeliverFragment(tripType, LoadStatusId);
                        }


                    }

                }




                break;



        }
    }







    void moveToConfirmDeliverFragment(String tripType, int LoadStatusId){
        try {
            ConfirmDeliveryFragment confirmDeliveryFragment = new ConfirmDeliveryFragment();
            Globally.bundle.putString("load_Id", Constants.temperoryList.get(0).getLoadId());
            Globally.bundle.putString("TripId", TripId);
            Globally.bundle.putString("TripType", tripType);
            Globally.bundle.putString("JobId", JobId);
            Globally.bundle.putString("array", loadArray);
            Globally.bundle.putInt("LoadStatusId", LoadStatusId);

            confirmDeliveryFragment.setArguments(Globally.bundle);


            fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTran.replace(R.id.frame_layout_fragment, confirmDeliveryFragment);
            fragmentTran.addToBackStack(null);
            fragmentTran.commit();


        }catch (Exception e){
            e.printStackTrace();
        }
    }




    //*================== Get Load details ===================*//*
    void GetLoadDetail(final String number,final String DriverId,final String LoadId){

        params = new HashMap<String, String>();
        params.put("DeviceId", number);
        params.put("DriverId", DriverId);
        params.put("LoadId", LoadId);

        GetLoadDetails.executeRequest(Request.Method.POST, APIs.GET_LOAD_DETAIL , params, GetLoadDetail,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }



    //*================== Confirm load Status ===================*//*
    void ConfirmLoadStatus(final String API, final String number,final String DriverId,final String LoadId,
                           final String Latitude, final String Longitude){

        params = new HashMap<String, String>();
        params.put("DeviceId", number);
        params.put("DriverId", DriverId);
        params.put("LoadId", LoadId);
        params.put("Latitude", Latitude);
        params.put("Longitude", Longitude);

        ConfirmLoadStatus.executeRequest(Request.Method.POST, API , params, ConfirmLoad,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }





    //*================== Confirm Pick and drop load ===================*//*
    void ConfirmedPickDropLoad(final String number,final String DriverId,final String LoadId,
                                  final String Latitude, final String Longitude){

        final String DriverName = Globally.getUserName(getActivity());
        final String currentEntryTime = Globally.GetCurrentDateTimeString();

        progressBarDetail.show();

        params = new HashMap<String, String>();
        params.put("DeviceID", number);
        params.put("DriverId", DriverId);
        params.put("LoadId", LoadId);
        params.put("Latitude", Latitude);
        params.put("Longitude", Longitude);
        params.put("EntryTime", currentEntryTime);
        params.put("DriverName", DriverName);
        params.put("LoadNumber", LoadNumber);

        if(Globally.isSecureLoad(getActivity())){
            params.put("TrailerNo", TrailerNumber);
            params.put("SealNo", SealNumber);
            params.put("TrailerImage", TrailerImage);
            params.put("SealImage", SealImage);

        }
        ConfirmedPickDropLoad.executeRequest(Request.Method.POST, APIs.CONFIRMED_PICK_DROP_LOAD , params, ConfirmPickDrop,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }




    //*================== update load status ===================*//*
    void UpdateLoadStatus(final String number,final String DriverId,final String LoadId, final String LoadStatusId){

        progressBarDetail.show();

        params = new HashMap<String, String>();
        params.put("DeviceID", number);
        params.put("DriverId", DriverId);
        params.put("LoadId", LoadId);
        params.put("LoadStatusId", LoadStatusId);

        UpdateLoadStatus.executeRequest(Request.Method.POST, APIs.UPDATE_LOAD_STATUS , params, UpdateLoad,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== Save current location ===================*//*
    void UpdateDriverLocation(final String DriverId, final String DeviceId, final String LoadIds,
                            final String CurrentLatitude , final String CurrentLongitude, final String CurrentTime ){

        params = new HashMap<String, String>();

        params.put("DriverId", DriverId );
        params.put("DeviceId", DeviceId );
        params.put("LoadIDs", LoadIds);

        params.put("CurrentLatitude", CurrentLatitude );
        params.put("CurrentLongitude", CurrentLongitude);

        params.put("CurrentDateTime", CurrentTime );

        SaveLocationDetails.executeRequest(Request.Method.POST, APIs.GET_LOAD_AND_GPS , params, SaveLocation,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;
            String status = "", message = "";
            String currentTimeHHmm      = Globally.GetCurrentDateTimeString();

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                message = obj.getString("Message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {

                    case GetLoadDetail:

                        try{
                            JSONArray JobLoadDocumentsArray = new JSONArray();
                            String data = obj.getString("Data");
                            JSONObject dataJson = new JSONObject(data);

                            String poNumber = "", PickupComments = "";

                            ShipperAddress = dataJson.getString("ShipperAddress");
                            ShipperName = dataJson.getString("ShipperName");
                            ShipperCity = dataJson.getString("ShipperCity");
                            ShipperStateCode = dataJson.getString("ShipperStateCode");
                            ConsigneeName = dataJson.getString("ConsigneeName");
                            ConsigneeCity = dataJson.getString("ConsigneeCity");
                            ConsigneeAddress = dataJson.getString("ConsigneeAddress");
                            ConsigneeStateCode = dataJson.getString("ConsigneeStateCode");
                            ConsigneePostal = dataJson.getString("ConsigneePostal");
                            ShipperPostal = dataJson.getString("ShipperPostal");
                            ConsigneeCountryCode = dataJson.getString("ConsigneeCountryCode");
                            ShipperCountryCode = dataJson.getString("ShipperCountryCode");
                            LoadShipperComment = dataJson.getString("LoadShipperComment");
                            LoadNumber = dataJson.getString("LoadNumber");
                            Description = dataJson.getString("Description");
                            LoadStatusId = dataJson.getInt("LoadStatusId");
                            JobId = dataJson.getString("JobId");
                            poNumber = dataJson.getString("PONumber");
                            PickupComments = dataJson.getString("PickupComments");
                            String BLNumber = dataJson.getString("BLNumber");

                            String pickTime = Constants.tripPickedDateTime(dataJson);
                            String delTime = Constants.tripDeliveredDateTime(dataJson);

                            deliveryTimeDetailTV.setText(delTime);
                            pickTimeDetailTV.setText(pickTime);

                            if(!dataJson.isNull("KGS") ){
                                Wheight = String.format("%.2f", dataJson.getDouble("KGS"));
                            }

                            Type = dataJson.getString("Type");
                            QTY = dataJson.getString("QTY");
                            LBS = dataJson.getString("LBS");


                            if(dataJson.has("IsPickAndDrop")) {
                                IsPickAndDrop = dataJson.getBoolean("IsPickAndDrop");
                                IsArrivedToShipper = dataJson.getBoolean("IsArrivedToShipper");
                                IsArrivedToConsignee = dataJson.getBoolean("IsArrivedToConsignee");
                            }

                            consigneeAddress = ConsigneeAddress+", " +ConsigneeStateCode+", " +
                                    ConsigneeCity+ ", " +ConsigneePostal+", " +ConsigneeCountryCode;

                            shipperAddress = ShipperAddress+", " + ShipperCity+", " +
                                    ShipperStateCode + ", " +ShipperPostal;


                            if(tripType.equals( getResources().getString(R.string.local_trip)) && LoadStatusId == constants.LocalDeliveryPickedUp){
                                confirmLoadBtn.setText( getResources().getString(R.string.Deliver) );
                            }else {

                                if (IsPickAndDrop) {
                                    if (IsArrivedToShipper || IsArrivedToConsignee) {
                                        confirmLoadBtn.setText( getResources().getString(R.string.PickupLoad) );
                                    } else {
                                        confirmLoadBtn.setText( getResources().getString(R.string.Arrived));
                                    }
                                } else {
                                    confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                                }
                            }
                            if(QTY != null && !QTY.equals("null") && QTY.length() > 1){
                                QTY = QTY.split("\\.")[0];
                            }
                            quantityView.setText(QTY.toUpperCase());
                            typeConView.setText(Type.toUpperCase());

                            wieghtView.setText(Wheight + " KGS");
                            DescriptionView.setText(Description);

                            BillNoView.setText(BLNumber);
                            if(QTY != null && !QTY.equalsIgnoreCase("null")){
                                quantityView.setText(QTY.split("\\.")[0]);
                            }else{
                                quantityView.setText(getResources().getString(R.string.NOT_AVAILABLE));
                            }


                            if(!poNumber.equalsIgnoreCase("null") && poNumber.length() > 0 ){
                                poNumberTV.setText(poNumber);
                            }else{
                                poNumberTV.setText(getResources().getString(R.string.NOT_AVAILABLE));
                            }

                            if(!PickupComments.equalsIgnoreCase("null") && PickupComments.length() > 0){
                                pickupCommentsTV.setText(PickupComments);
                            }else{
                                pickupCommentsTV.setText(getResources().getString(R.string.NOT_AVAILABLE));
                            }

                            JobLoadDocumentsArray = (JSONArray)  dataJson.get("JobLoadDocumentsApi");
                            Constants.documentListID.clear();

                            for(int i = 0 ; i < JobLoadDocumentsArray.length(); i ++){
                                JSONObject jsonObjj = (JSONObject)JobLoadDocumentsArray.get(i);

                                String id = "";
                                id = String.valueOf(jsonObjj.getString("JobLoadDocTypeId"));

                                Constants.documentListID.add(id);
                            }

                            if(tripType.equals(getResources().getString(R.string.local_trip)) ){

                                if(LoadStatusId == constants.LocalDeliveryPickedUp){
                                    confirmLoadBtn.setText(getResources().getString(R.string.Deliver));
                                }else{
                                    if(IsPickedUp){
                                        confirmLoadBtn.setText(getResources().getString(R.string.Deliver) );
                                    }else{
                                        confirmLoadBtn.setText(getResources().getString(R.string.PickupLoad) );
                                    }
                                }

                            }else{

                                setBtnNameOnStatus(LoadStatusId);

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case SaveLocation:
                        try {
                            // save API call Current Time
                            String currentTime          = Globally.getCurrentDateTime();
                            Globally.setSavedLocTimeStamp(currentTime, getActivity());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                    case ConfirmPickDrop:
                    case UpdateLoad:
                    case ConfirmLoad:
                        try{

                            if(progressBarDetail != null){
                                progressBarDetail.dismiss();
                            }
                            if(flag == UpdateLoad){
                                Globally.showToast(tripHisDLay, message);
                            }else if(flag == ConfirmPickDrop) {
                                Globally.showToast(tripHisDLay, "Load Pickup Successfully");
                            }else{
                                if (tripType.equals(getResources().getString(R.string.local_trip))) {
                                    IsPickedUp = true;
                                    Constants.temperoryList.get(0).setProrityShipment("true");

                                    Globally.showToast(tripHisDLay, "Picked up successfully");
                                } else {
                                    Globally.showToast(tripHisDLay, "Arrived Successfully");
                                }
                            }

                            // call Api to get Load detail after load changes
                            GetLoadDetail( DeviceId, DriverId, LoadId);

                            if(Globally.getGpsIntervalTime(getActivity()) > 1 ) {
                                UpdateDriverLocation(DriverId, DeviceId, LoadId, Globally.LATITUDE, Globally.LONGITUDE, currentTimeHHmm);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;



                }
            }else{

                Globally.showToast(tripHisDLay, message);

                if (message.equalsIgnoreCase("Device Logout")) {
                    Globally.StopService(getActivity());

                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }

            }
        }
    };




    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("Driver", "error" + error.toString());

            switch (flag){
                case UpdateLoad:
                case ConfirmPickDrop:
                case GetLoadDetail:

                    if(progressBarDetail != null){
                        progressBarDetail.dismiss();
                    }
                    Globally.showToast(tripHisDLay,  getString(R.string.error_desc) );

                    break;



            }
        }
    };



    void confirmPickDropDialog(){

        final Dialog picker = new Dialog(getActivity());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_edit_delete_lay);


        TextView confirmPopupButton = (TextView)picker.findViewById(R.id.confirmPopupButton);
        TextView cancelPopupButton = (TextView)picker.findViewById(R.id.cancelPopupButton);

        TextView titleDescView = (TextView)picker.findViewById(R.id.titleDescView);

        confirmPopupButton.setTypeface(null, Typeface.BOLD);
        cancelPopupButton.setTypeface(null, Typeface.NORMAL);

        cancelPopupButton.setText(getResources().getString(R.string.CANCEL));
        confirmPopupButton.setText(getResources().getString(R.string.CONFIRM));

        titleDescView.setText(getResources().getString(R.string.confirmLoadPickUp));

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

                if(Globally.isInternetOn(getActivity())){
                    CallAPiOnStatus(LoadStatusId);
                }else{
                    Globally.showToast(tripHisDLay, Globally.INTERNET_MSG);
                }


            }
        });



        picker.show();
    }


    void confirmSecureLoadDialog(){

        TrailerImage = ""; SealImage = "";

        final Dialog picker = new Dialog(getActivity());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_secureload_items);


        final EditText trailerNumberEditText = (EditText)picker.findViewById(R.id.trailerNumberEditText);
        final EditText sealNumberEditText = (EditText)picker.findViewById(R.id.sealNumberEditText);

        sealImageView  = (ImageView)picker.findViewById(R.id.sealImageView);
        trailerImageView  = (ImageView)picker.findViewById(R.id.trailerImageView);

        final Button confirmSecureLoadBtn = (Button)picker.findViewById(R.id.confirmSecureLoadBtn);
        Button cancelSecureLoadBtn = (Button)picker.findViewById(R.id.cancelSecureLoadBtn);

        sealImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageType = "seal";
                mediaDialog();
            }
        });

        trailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageType = "trailer";
                mediaDialog();
            }
        });

        cancelSecureLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });

        confirmSecureLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TrailerNumber = trailerNumberEditText.getText().toString().trim();
                SealNumber = sealNumberEditText.getText().toString().trim();

                if(TrailerNumber.length() > 0){
                    if(SealNumber.length() > 0){
                        if(TrailerImage.length() > 0){
                            if(SealImage.length() > 0){
                                ConfirmedPickDropLoad( DeviceId , DriverId, LoadId, Globally.LATITUDE, Globally.LONGITUDE);
                                picker.dismiss();
                            }else{
                                Globally.showToast(confirmSecureLoadBtn, "Add Seal Image");
                            }
                        }else{
                            Globally.showToast(confirmSecureLoadBtn, "Add Trailer Image");
                        }
                    }else{
                        Globally.showToast(confirmSecureLoadBtn, "Enter Seal Number");
                    }
                }else{
                    Globally.showToast(confirmSecureLoadBtn, "Enter Trailer Number");
                }

            }
        });



        picker.show();
    }


    /*----------------- Media Dialog For Camera/Gallery---------------- */

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

        TextView liveLoadingView = (TextView)mediaPicker.findViewById(R.id.liveLoadingView);

        liveLoadingView.setVisibility(View.VISIBLE);





        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("camera", CAMERA_FILE);
            }
        });


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("gallery", GALLERY_FILE);
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


    // ----------------- Call Media Activity ----------------
    void mediaIntent(String from, int MEDIA_FILE){
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra("type", from);
        startActivityForResult(i, MEDIA_FILE );
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK && (requestCode == CAMERA_FILE || requestCode == GALLERY_FILE)) {

            int bitmapQuality, base64Quality = 70;

            if(requestCode == CAMERA_FILE){
                bitmapQuality = 80;
            }else{
                bitmapQuality = 90;
            }
           String ImagePath = data.getStringExtra("result");

            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePath, options);

            if(sealImageView!= null) {
                if (ImageType.equals("seal")) {
                    SealImage = Globally.ConvertImageToByteAsString(ImagePath, bitmapQuality, base64Quality);
                    sealImageView.setImageBitmap(bitmap);
                } else if (ImageType.equals("trailer")) {
                    TrailerImage = Globally.ConvertImageToByteAsString(ImagePath, bitmapQuality, base64Quality);
                    trailerImageView.setImageBitmap(bitmap);
                }
            }




        }
    }



    void setBtnNameOnStatus(int status){

        switch (status){

            case Constants.InternationalLoadPickedUp:
                confirmLoadBtn.setText( getResources().getString(R.string.OnRoute));
                break;

            case Constants.DropBeforeTripDelivered:
                confirmLoadBtn.setText( getResources().getString(R.string.Pickup));
                break;

            case Constants.Dispatched:
                confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                break;

                case Constants.DroppedLoadPickedUp:
                    confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                    break;


        }
    }


    void CallAPiOnStatus(int status){

        switch (status){

            case Constants.InternationalLoadPickedUp:

                UpdateLoadStatus(DeviceId, DriverId, LoadId, String.valueOf(constants.Dispatched));

                break;

            case Constants.DropBeforeTripDelivered:
                    UpdateLoadStatus(DeviceId, DriverId, LoadId, String.valueOf(constants.DroppedLoadPickedUp));

                break;


                default:

                        if (Globally.isSecureLoad(getActivity())) {
                            confirmSecureLoadDialog();
                        } else {
                            TrailerNumber = "";
                            SealNumber = "";
                            TrailerImage = "";
                            SealImage = "";
                            ConfirmedPickDropLoad(DeviceId, DriverId, LoadId, Globally.LATITUDE, Globally.LONGITUDE);
                        }


                    break;


        /*    case Constants.Dispatched:
                confirmLoadBtn.setText( getResources().getString(R.string.Deliver));
                break;

            case Constants.DroppedLoadPickedUp:
                confirmLoadBtn.setText( getResources().getString(R.string.DeliverAtConsignee));
                break;
*/

        }
    }

}
