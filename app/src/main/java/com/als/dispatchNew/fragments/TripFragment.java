package com.als.dispatchNew.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
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

public class TripFragment extends Fragment implements View.OnClickListener{


    View rootView;
    TextView noDataTextView, tripTitleTV;
    RelativeLayout  tripFragMLay,  backMenuLay;
    public static RelativeLayout mapTripLayout;
    public static Button tripHiddenBtn;
    RelativeLayout newTripTripBtn, searchTripBtn, backActionBar, actionbarLay, searchBarLayout;
    ListView tripListView;
    ImageView menuImageView, clearTextBtn;
    TextView menuTextView;
    EditText searchEditText;
    SwipeRefreshLayout tripSwipeLayout;
    RequestQueue queue;
    StringRequest postRequest;
    TripHistoryModel tripLoadModel;
    List<TripHistoryModel> tripList = new ArrayList<TripHistoryModel>();
    List<TripHistoryModel> listClone = new ArrayList<>();
    JSONArray TripDetailArray;
    TripAdapter tripAdapter;
    FragmentManager fragManager;
    ProgressDialog p;
    ProgressBar progressBar;
    Constants constants;
    Animation inAnim, outAnim;
    String DriverId = "", DeviceId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{

            rootView = inflater.inflate(R.layout.trip_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }catch (Exception e){
            e.printStackTrace();
        }
        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        constants = new Constants();

        noDataTextView = (TextView)view.findViewById(R.id.noDataTextView);
        tripTitleTV = (TextView)view.findViewById(R.id.tripTitleTV);

         menuTextView = (TextView)view.findViewById(R.id.menuTextView);

        mapTripLayout   = (RelativeLayout)view.findViewById(R.id.mapTripLayout);
        tripFragMLay    = (RelativeLayout)view.findViewById(R.id.tripFragMLay);
        backMenuLay     = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        newTripTripBtn  = (RelativeLayout) view.findViewById(R.id.localTripBtn);
        searchTripBtn   = (RelativeLayout) view.findViewById(R.id.searchTripBtn);
        searchBarLayout = (RelativeLayout) view.findViewById(R.id.searchBarLayout);
        backActionBar   = (RelativeLayout) view.findViewById(R.id.backActionBar);
        actionbarLay    = (RelativeLayout) view.findViewById(R.id.actionbarLay);

        clearTextBtn    = (ImageView)view.findViewById(R.id.clearTextBtn);
        menuImageView = (ImageView)view.findViewById(R.id.menuImageView);
        tripHiddenBtn = (Button)view.findViewById(R.id.tripHiddenBtn);


        searchEditText  = (EditText)view.findViewById(R.id.searchEditText);
        tripListView = (ListView)view.findViewById(R.id.tripListView);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        tripSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.tripSwipeLayout);


        ScrollView loadDetailHistoryScroll = (ScrollView) view.findViewById(R.id.loadDetailHistoryScroll);
        ScrollView deliveryTimeHistoryScroll = (ScrollView)view.findViewById(R.id.deliveryTimeHistoryScroll);

        inAnim                      = AnimationUtils.loadAnimation( getActivity(), R.anim.in_animation);
        outAnim                     = AnimationUtils.loadAnimation( getActivity(), R.anim.out_animation);

        DeviceId = Globally.getDeviceId(getActivity());
        DriverId =  Globally.getDriverId(getActivity());

        tripTitleTV.setText(getResources().getString(R.string.trips));

        tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Globally.hideSoftKeyboard(getActivity());

                int positionItem = position;
                String trip_id = "";
                if (listClone.size() > 0) {
                    String tripId = listClone.get(position).getTripId();
                    for (int i = 0; i < tripList.size(); i++) {
                        trip_id = tripList.get(i).getTripId();
                        if (tripId.equals(trip_id)) {
                            positionItem = i;
                            break;
                        }
                    }
                }


                Constants.temperoryList.clear();
                TripHistoryModel selectedModel = tripList.get(positionItem);
                Constants.temperoryList.add(selectedModel);

                moveTOTripDetailFragment(selectedModel, "trip" );

            }
        });


        // Setup refresh listener which triggers new data loading
        tripSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Globally.isInternetOn(getActivity())) {
                    GetDriverTrip(DeviceId, DriverId);
                }else{
                    Globally.showToast(backMenuLay, Globally.INTERNET_MSG);
                }

            }
        });



        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    listClone = new ArrayList<TripHistoryModel>();
                    String tripNumber = "";

                    if(s.length()>0){
                        clearTextBtn.setVisibility(View.VISIBLE);
                        tripNumber = searchEditText.getText().toString();
                        tripNumber = tripNumber.toLowerCase().trim();
                        for (TripHistoryModel string : tripList) {
                            if(string.getTripNumber().toLowerCase().trim().contains(tripNumber)){
                                listClone.add(string);
                            }
                        }
                        tripListView.setAdapter( new TripAdapter(getActivity(), listClone, "trip"));
                    }else{
                        clearTextBtn.setVisibility(View.GONE);
                        tripListView.setAdapter(new TripAdapter(getActivity(), tripList, "trip"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        deliveryTimeHistoryScroll.setVisibility(View.GONE);
        loadDetailHistoryScroll.setVisibility(View.GONE);
        tripHiddenBtn.setOnClickListener(this);
        newTripTripBtn.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);
        searchTripBtn.setOnClickListener(this);
        backActionBar.setOnClickListener(this);
        clearTextBtn.setOnClickListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.backMenuLay:
                DispatchMainActivity.drawer.openDrawer(GravityCompat.START);
                break;


            case R.id.localTripBtn:
              //  TabAct.host.setCurrentTab(1);
                break;

            case R.id.searchTripBtn:

                actionbarLay.setVisibility(View.GONE);
                searchBarLayout.setVisibility(View.VISIBLE);
                searchBarLayout.startAnimation( inAnim );
                searchEditText.setText("");
                searchEditText.setFocusable(true);
                searchEditText.requestFocus();
                searchEditText.setFocusableInTouchMode(true);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_FORCED);
                break;

            case R.id.backActionBar:
                Globally.hideSoftKeyboard(getActivity());
                searchBarLayout.setVisibility(View.GONE);
                actionbarLay.setVisibility(View.VISIBLE);
                searchBarLayout.startAnimation( outAnim );
                searchEditText.setText("");
                clearTextBtn.setVisibility(View.GONE);

                break;


            case R.id.clearTextBtn:
                searchEditText.setText("");
                clearTextBtn.setVisibility(View.GONE);
                break;

            case R.id.tripHiddenBtn:
                GetDriverTrip(Globally.getDeviceId( getActivity()), Globally.getDriverId( getActivity()));
                break;


        }
    }


    void noRecordView(){
        if(tripList.size() == 0){
            noDataTextView.setVisibility(View.VISIBLE);
        }else {
            noDataTextView.setVisibility(View.GONE);
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        constants.isHomeFragment = true;
        constants.IsFragmentDetail = false;
        noRecordView();

        if(Globally.isInternetOn(getActivity())) {
            GetDriverTrip(DeviceId, DriverId);
        }else{
            Globally.showToast(backMenuLay, Globally.INTERNET_MSG);
        }

    }





   void moveTOTripDetailFragment(  TripHistoryModel tempModel , String tripType){

       try{

            TripDetailFragment detailFragment = new TripDetailFragment();
            Globally.bundle.putString("tripType", tripType);
            Globally.bundle.putString("array", TripDetailArray.toString());
            detailFragment.setArguments(Globally.bundle);

            Constants.temperoryList.clear();
            Constants.temperoryList.add(tempModel);

            fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
            fragmentTran.replace(R.id.frame_layout_fragment, detailFragment);
            fragmentTran.addToBackStack("job");
            fragmentTran.commit();

       }catch (Exception e){
           e.printStackTrace();
       }

    }




// ================== Get Driver Trip History ===================

    void GetDriverTrip(final String number, final String DriverId){

            try{
              p = new ProgressDialog(getActivity());
              p.setMessage("Loading ...");
              p.show();
            }catch (Exception e){}

        queue = Volley.newRequestQueue(getActivity());

        postRequest = new StringRequest(Request.Method.POST, APIs.TRIP , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                   if(p != null) {
                       p.dismiss();
                   }
                }catch (Exception e){}

                Log.d("response: ", "--response: " +response);
                tripSwipeLayout.setRefreshing(false);

                String status = "", message = "", data = "";
                JSONObject dataJson = null, TripDetailJson, dispatchedJson;

                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");
                    data = Globally.obj.getString("Data");
                    tripList.clear();

                    if(status.equalsIgnoreCase("true")){

                        String PickedLoadNumber = "", TripId = "", TripNumber = "";
                        dataJson = new JSONObject(data);
                        TripDetailArray = (JSONArray)  dataJson.get("TripDetail");

                        for(int tripCount = 0 ; tripCount < TripDetailArray.length() ; tripCount++) {

                            TripDetailJson = (JSONObject) TripDetailArray.get(tripCount);
                            TripId = TripDetailJson.getString("TripId");
                            TripNumber = TripDetailJson.getString("TripNumber");


                            JSONArray DispatchedLoadArray =  (JSONArray)  TripDetailJson.get("DispatchedLoad");

                            for(int i = 0 ; i < DispatchedLoadArray.length() ; i++){
                                dispatchedJson = (JSONObject)DispatchedLoadArray.get(i);

                                PickedLoadNumber = dispatchedJson.getString("LoadNumber");


                                tripLoadModel = constants.getHistoryModel(TripId, TripNumber, dispatchedJson, PickedLoadNumber);
                                tripList.add(tripLoadModel);

                            }

                        }

                        setAdapter();


                    }else if (status.equalsIgnoreCase("false")) {

                        tripSwipeLayout.setRefreshing(false);
                        Globally.showToast(tripFragMLay, message);
                        noRecordView();

                        if(message.equalsIgnoreCase("Device Logout")){

                            Globally.StopService(getActivity());

                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(i);
                            getActivity().finish();
                        }else {
                            setAdapter();
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

                        Log.d("error", "error" + error);
                        Globally.showToast(menuImageView, getString(R.string.error_desc) );
                        try{
                            if(p != null) {
                                p.dismiss();
                            }
                        }catch (Exception e){}
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
            if(tripList.size() > 0){
                tripAdapter = new TripAdapter(getActivity(), tripList, "trip");
                tripListView.setAdapter(tripAdapter);
            }else {
                tripAdapter.notifyDataSetChanged();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        noRecordView();

    }








}
