package com.als.dispatchNew.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.adapter.TripAdapter;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.models.TripHistoryModel;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripHistoryFragment extends Fragment {


    View rootView;
    ListView tripHistoryListView;
    TripAdapter tripHistoryAdapter;
    List<TripHistoryModel> historyList = new ArrayList<>();

    TripHistoryModel tripHistoryModel;
    public static Button tripHistoryBtn;
    RelativeLayout tripHisLay;
    TextView noTripHistoryTV;
    RequestQueue queue;
    StringRequest postRequest;
    FragmentManager fragManager;
    ProgressBar progressBarHistory;
    boolean isFirst = true;
    String DriverId = "", DeviceId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.trip_history_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);
        return rootView;

    }


    void initView(View container) {

        progressBarHistory = (ProgressBar)container.findViewById(R.id.progressBarHistory);
        tripHistoryListView = (ListView) container.findViewById(R.id.tripHistoryListView);

        tripHistoryBtn = (Button)container.findViewById(R.id.tripHistoryBtn);
        tripHisLay = (RelativeLayout) container.findViewById(R.id.tripHisLay);
        noTripHistoryTV = (TextView)container.findViewById(R.id.noTripHistoryTV);

        DeviceId = Globally.getDeviceId( getActivity());
        DriverId = Globally.getDriverId( getActivity());

        // Listview item click listener
        tripHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Constants.temperoryList.clear();
                TripHistoryModel selectedModel = historyList.get(position);
                Constants.temperoryList.add(selectedModel);

                moveToTripDetailFragment(selectedModel, "history");

            }
        });




        tripHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDriverHistory(DeviceId, DriverId, false );
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        setAdapter();
        GetDriverHistory( DeviceId, DriverId, isFirst );

        isFirst = false;
    }




        void moveToTripDetailFragment(  TripHistoryModel tempModel , String tripType){

            try{

                TripDetailFragment detailFragment = new TripDetailFragment();
                Globally.bundle.putString("tripType", tripType);

                detailFragment.setArguments(Globally.bundle);

                Constants.temperoryList.clear();
                Constants.temperoryList.add(tempModel);

                fragManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTran = fragManager.beginTransaction();
                fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                        android.R.anim.fade_in,android.R.anim.fade_out);
                fragmentTran.replace(R.id.frame_layout_fragment, detailFragment);
                fragmentTran.addToBackStack("history");
                fragmentTran.commit();

            }catch (Exception e){
                e.printStackTrace();
            }

        }







    // ================== Get Driver Trip History ===================

    void GetDriverHistory(final String number, final String DriverId, final boolean isLoad){

        if(isLoad){
            progressBarHistory.setVisibility(View.VISIBLE);
        }

        queue = Volley.newRequestQueue(getActivity());
        postRequest = new StringRequest(Request.Method.POST, APIs.TRIP_HISTORY , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("response: ", "--response: " +response);
                progressBarHistory.setVisibility(View.GONE);
                String status = "", message = "", data = "";
                JSONObject dataJson = null, itemsObject, deliveredJson;
                JSONArray TripDetailArray, DeliveredLoadArray;
                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");
                    data = Globally.obj.getString("Data");

                    if(status.equalsIgnoreCase("true")){

                        historyList = new ArrayList<>();
                        dataJson = new JSONObject(data);

                        TripDetailArray = (JSONArray)  dataJson.get("TripDetail");


                        for(int i = 0 ; i < TripDetailArray.length() ; i++){
                            itemsObject = (JSONObject)TripDetailArray.get(i);

                           String TripId = itemsObject.getString("TripId");
                            String TripNumber = itemsObject.getString("TripNumber");

                            List<TripHistoryModel> childList = new ArrayList<TripHistoryModel>();
                            DeliveredLoadArray = (JSONArray)  itemsObject.get("DeliveredLoad");
                            for(int j = 0 ; j < DeliveredLoadArray.length() ; j++){
                                deliveredJson = (JSONObject)DeliveredLoadArray.get(j);

                                String DeliveryDocumentStr = deliveredJson.getString("LoadDeliveryDocuments");
                                String truckNo = "", trailerNo = "";
                                truckNo = Constants.checkNullStr(deliveredJson,"LoadTruckNumber");

                                if(deliveredJson.has("LoadTrailorNumber")){
                                    trailerNo = Constants.checkNullStr(deliveredJson,"LoadTrailorNumber");
                                }else{
                                    trailerNo = Constants.getTrailerNumber(deliveredJson);
                                }


                                tripHistoryModel = new TripHistoryModel(TripId, TripNumber, trailerNo, truckNo,
                                        deliveredJson.getString("ShipperName"),deliveredJson.getString("ShipperAddress"),deliveredJson.getString("ShipperStateCode"),
                                        deliveredJson.getString("ShipperCity"),deliveredJson.getString("ShipperPostal"),deliveredJson.getString("ShipperCountryCode"),
                                        deliveredJson.getString("LoadId"),deliveredJson.getString("LoadNumber"),deliveredJson.getString("IsRead"),
                                        deliveredJson.getString("LoadStatusId"),deliveredJson.getString("LoadStatusName"),deliveredJson.getString("ConsigneeName"),
                                        deliveredJson.getString("ConsigneeAddress"),deliveredJson.getString("ConsigneeStateCode"),deliveredJson.getString("ConsigneeCity"),

                                        deliveredJson.getString("ConsigneePostal"),deliveredJson.getString("ConsigneeCountryCode"),deliveredJson.getString("ConsigneeLattitude"),
                                        deliveredJson.getString("ConsigneeLongitude"),deliveredJson.getString("IsProrityShipment"), "", //deliveredJson.getString("Remarks"),

                                        deliveredJson.getString("Description"),deliveredJson.getString("LoadShipperComment"),deliveredJson.getString("EntryTime"),
                                        deliveredJson.getString("ExitTime"),deliveredJson.getString("DeliveryWaitingTime"),deliveredJson.getString("DeliverywaitingTimeReason"),

                                        deliveredJson.getString("LoadType"),deliveredJson.getString("QTY"),deliveredJson.getString("LBS"),
                                        deliveredJson.getString("KGS"),deliveredJson.getString("JobId"),deliveredJson.getString("IsCustomeBrokerCleared")  ,
                                        DeliveryDocumentStr, Constants.tripPickedDateTime(deliveredJson) , Constants.tripDeliveredDateTime(deliveredJson) );

                                historyList.add(tripHistoryModel);
                            }


                        }


                        setAdapter();

                    }else if (status.equalsIgnoreCase("false")) {

                        Globally.showToast(tripHisLay, message);
                        noTripHistoryTV.setVisibility(View.VISIBLE);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.setDriverId( "", getActivity());
                            Globally.setPassword("", getActivity());
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

                        Log.d("error", "error: " + error);


                        try {
                            Globally.showToast(tripHisLay, getString(R.string.error_desc));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        progressBarHistory.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                params.put("DeviceId", number );
                params.put("DriverId", DriverId);

                return params;
            }
        };

        int socketTimeout = 30000;   //150 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);



    }


    void setAdapter(){

        try{
            if(historyList.size() > 0) {
                tripHistoryAdapter = new TripAdapter(getActivity(), historyList, "history");
                tripHistoryListView.setAdapter(tripHistoryAdapter);
                noTripHistoryTV.setVisibility(View.GONE);
            }else{
                noTripHistoryTV.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
