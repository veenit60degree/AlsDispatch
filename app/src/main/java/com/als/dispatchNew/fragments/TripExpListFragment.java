package com.als.dispatchNew.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.adapter.ExpenseHistoryAdapter;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripExpListFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View rootView;
    Constants constants;
    ListView expenseListView;
    RelativeLayout backMenuLay, addExpenseBtn, searchTripBtn;
    RequestQueue queue;
    StringRequest postRequest;
    TextView noDataTextView, tripTitleTV;
    ImageView addExpenseIV;
    SwipeRefreshLayout tripSwipeLayout;
    ArrayList<String> expenseList = new ArrayList<String>();
    ExpenseHistoryAdapter expenseHistoryAdapter;
    String  DeviceId = "", DriverId = "", tripId = "";
    JSONArray expenseArray = new JSONArray();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {

            rootView = inflater.inflate(R.layout.trip_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        } catch (Exception e) {
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }

    void initView(View view) {

        constants = new Constants();
        expenseListView = (ListView) view.findViewById(R.id.tripListView);
        noDataTextView  = (TextView)view.findViewById(R.id.noDataTextView);
        tripTitleTV = (TextView)view.findViewById(R.id.tripTitleTV);

        backMenuLay = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        addExpenseBtn  = (RelativeLayout) view.findViewById(R.id.localTripBtn);
        searchTripBtn   = (RelativeLayout) view.findViewById(R.id.searchTripBtn);

        addExpenseIV = (ImageView)view.findViewById(R.id.localTripIV);
        tripSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.tripSwipeLayout);

        tripTitleTV.setText(getResources().getString(R.string.expense));

        DeviceId = Globally.getDeviceId( getActivity());
        DriverId = Globally.getDriverId( getActivity());

        searchTripBtn.setVisibility(View.GONE);
        addExpenseIV.setImageResource(R.drawable.ic_add_expense);

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveToExpenseDetailFragment(position);
            }
        });

        // Setup refresh listener which triggers new data loading
        tripSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Globally.isInternetOn(getActivity())) {
                    GetTripExpenseList(DeviceId, DriverId);
                }else{
                    Globally.showToast(backMenuLay, Globally.INTERNET_MSG);
                }

            }
        });




        addExpenseBtn.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        constants.isHomeFragment = false;
        constants.IsFragmentDetail = false;

        setAdapter();

        if(Globally.isInternetOn(getActivity())) {
            GetTripExpenseList(DeviceId, DriverId);
        }else{
            Globally.showToast(expenseListView, Globally.INTERNET_MSG);
        }


    }




    void moveToExpenseDetailFragment(int pos){

        try{

            ExpenseDetailFragment detailFragment = new ExpenseDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putString("expense_data", expenseArray.get(pos).toString());
            detailFragment.setArguments(bundle);

            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTran = fragManager.beginTransaction();
            fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                    android.R.anim.fade_in,android.R.anim.fade_out);
            fragmentTran.replace(R.id.frame_layout_fragment, detailFragment);
            fragmentTran.addToBackStack("expense");
            fragmentTran.commit();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //  ================== Get Driver Trip History ===================

    void GetTripExpenseList(final String DeviceId, final String DriverId){

        /**/
        queue = Volley.newRequestQueue(getActivity());

        postRequest = new StringRequest(Request.Method.POST, APIs.GET_DRIVER_TRIP_EXPENSES_DETAIL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response: ", "--response: " +response);
                String status = "", data = "";
                JSONObject dataJson = null, expenseHistoryJson;
                tripSwipeLayout.setRefreshing(false);

                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    data = Globally.obj.getString("Data");

                    if(status.equalsIgnoreCase("true")){

                        dataJson = new JSONObject(data);
                        expenseList = new ArrayList<String>();

                        expenseArray = (JSONArray)  dataJson.get("DriverTripExpenses");

                        for(int i = 0 ; i < expenseArray.length() ; i++) {
                            expenseHistoryJson = (JSONObject) expenseArray.get(i);
                            String expense = expenseHistoryJson.getString("TripNumber") ;
                            expenseList.add(expense);
                        }

                        setAdapter();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", "error: " + error);
                        tripSwipeLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                params.put("DeviceId", DeviceId );
                params.put("DriverId", DriverId);
                params.put("TripId", tripId);

                return params;
            }
        };
        queue.add(postRequest);

    }


    void setAdapter() {

        try {
            if (expenseList.size() > 0) {
                expenseHistoryAdapter = new ExpenseHistoryAdapter(getActivity(), expenseList);
                expenseListView.setAdapter(expenseHistoryAdapter);
                noDataTextView.setVisibility(View.GONE);
            } else {
                expenseHistoryAdapter.notifyDataSetChanged();
                noDataTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void moveToAddExpenseFragment(){
        AddExpenseFragment tripFragment = new AddExpenseFragment();
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.frame_layout_fragment, tripFragment);
        fragmentTran.addToBackStack("add_expense");
        fragmentTran.commit();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backMenuLay:
                DispatchMainActivity.drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.localTripBtn:
                moveToAddExpenseFragment();
                break;
        }
    }
}
