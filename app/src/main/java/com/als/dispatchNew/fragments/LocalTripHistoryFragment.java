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

public class LocalTripHistoryFragment extends Fragment {

        View rootView;
        ListView tripHistoryListView;
        TripAdapter tripHistoryAdapter;
        List<TripHistoryModel> localHistoryList = new ArrayList<>();

        public static Button tripHistoryBtn;
        RelativeLayout tripHisLay;
        TextView noTripHistoryTV;
        RequestQueue queue;
        StringRequest postRequest;
        FragmentManager fragManager;
        ProgressBar progressBarHistory;
        boolean isFirst = true;
        String DriverId = "", DeviceId = "";
        Constants constants;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.trip_history_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            initView(rootView);
            return rootView;

        }


        void initView(View container) {

            constants           = new Constants();
            progressBarHistory  = (ProgressBar)container.findViewById(R.id.progressBarHistory);
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
                    TripHistoryModel selectedModel = localHistoryList.get(position);
                    Constants.temperoryList.add(selectedModel);

                    moveToTripDetailFragment(selectedModel, "history");

                }
            });

            tripHistoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetLocalTripHistory( DeviceId, DriverId );
                }
            });


        }

        @Override
        public void onResume() {
            super.onResume();

            setAdapter();
            GetLocalTripHistory( DeviceId, DriverId );

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

        void GetLocalTripHistory(final String number, final String DriverId){

            queue = Volley.newRequestQueue(getActivity());
            postRequest = new StringRequest(Request.Method.POST, APIs.GET_DRIVER_JOB , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("response: ", "--response: " +response);
                    progressBarHistory.setVisibility(View.GONE);
                    String status = "", message = "", data = "";
                    JSONObject dataJson = null;
                    JSONArray DeliveredTripArray;

                    //{"Status":true,"Message":"Success","Data":{"NewLoad":[],"PickupLoad":null,"DeliveredLoad":[]}}
                    try {
                        Globally.obj = new JSONObject(response);
                        status = Globally.obj.getString("Status");
                        message = Globally.obj.getString("Message");
                        data = Globally.obj.getString("Data");

                        if(status.equalsIgnoreCase("true")){

                            localHistoryList = new ArrayList<>();
                            dataJson = new JSONObject(data);

                            DeliveredTripArray =  (JSONArray)  dataJson.get("DeliveredLoad");

                            parseDataFromJsonArray(DeliveredTripArray);

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


    void parseDataFromJsonArray(JSONArray newJobArray){
        try{
            for(int i = 0 ; i < newJobArray.length() ; i++){
                JSONObject itemsObject = (JSONObject)newJobArray.get(i);
                TripHistoryModel tripLoadModel = constants.getLocalTripsModel("", "", itemsObject);
                localHistoryList.add(tripLoadModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        setAdapter();
    }




    void setAdapter(){

            try{
                if(localHistoryList.size() > 0) {
                    tripHistoryAdapter = new TripAdapter(getActivity(), localHistoryList, "history");
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
