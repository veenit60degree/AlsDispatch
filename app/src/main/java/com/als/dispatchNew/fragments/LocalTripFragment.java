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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

public class LocalTripFragment extends Fragment implements OnClickListener {


	View rootView;
	TextView noDataTextView, tripTitleTV;
	RelativeLayout  tripFragMLay,  backMenuLay;
	public static RelativeLayout mapTripLayout;
	RelativeLayout searchNewTripBtn, searchTripBtn, backActionBar, actionbarLay, searchBarLayout;
	ListView tripListView;
	ImageView menuImageView, searchTripImgVw, clearTextBtn;
	TextView menuTextView;
	EditText searchEditText;
	SwipeRefreshLayout tripSwipeLayout;
	RequestQueue queue;
	StringRequest postRequest;
	TripHistoryModel tripLoadModel;
	List<TripHistoryModel> tripList = new ArrayList<TripHistoryModel>();
	List<TripHistoryModel> listClone = new ArrayList<>();

	public static String tripNumber = "";
	TripAdapter tripAdapter;
	FragmentManager fragManager;
	ProgressDialog p;
	String TripId = "", TripNumber = "";
	ProgressBar progressBar;
	Constants constants;
	Animation inAnim, outAnim;
	String DriverId = "", DeviceId = "";




	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		}
		try {
			rootView = inflater.inflate(R.layout.trip_fragment, container, false);
			rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		} catch (InflateException e) {
			e.printStackTrace();
        /* map is already there, just return view as it is */
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
		searchNewTripBtn = (RelativeLayout) view.findViewById(R.id.localTripBtn);
		searchTripBtn   = (RelativeLayout) view.findViewById(R.id.searchTripBtn);
		searchBarLayout = (RelativeLayout) view.findViewById(R.id.searchBarLayout);
		backActionBar   = (RelativeLayout) view.findViewById(R.id.backActionBar);
		actionbarLay    = (RelativeLayout) view.findViewById(R.id.actionbarLay);

		clearTextBtn    = (ImageView)view.findViewById(R.id.clearTextBtn);
		searchTripImgVw = (ImageView)view.findViewById(R.id.localTripIV);
		menuImageView 	= (ImageView)view.findViewById(R.id.menuImageView);


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

		tripTitleTV.setText(getResources().getString(R.string.local_collections));
		searchTripImgVw.setImageResource(R.drawable.search);

		searchTripBtn.setVisibility(View.GONE);

		tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Constants.temperoryList.clear();
				TripHistoryModel selectedModel = tripList.get(position);
				Constants.temperoryList.add(selectedModel);

				moveToTripDetailFragment(selectedModel, "local_trip" );
			}
		});


		// Setup refresh listener which triggers new data loading
		tripSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {

				if(Globally.isInternetOn(getActivity())) {
					GetDriverJob(DeviceId, DriverId);
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
						tripListView.setAdapter( new TripAdapter(getActivity(), listClone, ""));
					}else{
						clearTextBtn.setVisibility(View.GONE);
						tripListView.setAdapter(new TripAdapter(getActivity(), tripList, "local_trip"));
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
		backMenuLay.setOnClickListener(this);
		searchNewTripBtn.setOnClickListener(this);
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

		constants.isHomeFragment = false;
		constants.IsFragmentDetail = false;

		noRecordView();

		GetDriverJob(DeviceId, DriverId);


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
			fragmentTran.addToBackStack("job");
			fragmentTran.commit();

		}catch (Exception e){
			e.printStackTrace();
		}

	}





	
	// ================== GetDriverJob ===================


	void GetDriverJob(final String DeviceId, final String DriverId){

			p = new ProgressDialog(getActivity());
			p.setMessage("Loading ...");
			p.show();


		queue = Volley.newRequestQueue(getActivity());

		postRequest = new StringRequest(Request.Method.POST, APIs.GET_DRIVER_JOB , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				p.dismiss();

				Log.d("response", "response: " + response);

				tripSwipeLayout.setRefreshing(false);
				String status = "", message = "", data = "";
				JSONObject dataJson = null;
				JSONArray newJobArray; // pickUpJsonArray, deliveredJsonArray;
				try {
					JSONObject obj = new JSONObject(response);
					status = obj.getString("Status");
					message = obj.getString("Message");
					data = obj.getString("Data");


					tripList.clear();

					if(status.equalsIgnoreCase("true")){

						dataJson = new JSONObject(data);

						newJobArray = (JSONArray)  dataJson.get("NewLoad");
						parseDataFromJsonArray(newJobArray);

						setAdapter();


					}else if (status.equalsIgnoreCase("false")) {

						Globally.showToast(menuImageView, message);

						if(message.equalsIgnoreCase("Device Logout")){
							Globally.setDriverId( "", getActivity());
							Globally.setPassword("", getActivity());
							Globally.setUserName( "", getActivity());


							getActivity().stopService(Globally.serviceIntent);

							Intent i = new Intent(getActivity(), LoginActivity.class);
							getActivity().startActivity(i);
							getActivity().finish();
						}else{
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
						Log.d("error", "error: " + error);
						p.dismiss();

						tripSwipeLayout.setRefreshing(false);
						setAdapter();
						Globally.showToast(menuImageView, getString(R.string.error_desc) );


					}
				}
		) {
			@Override
			protected Map<String, String> getParams()
			{
				Map<String,String> params = new HashMap<String, String>();

				params.put("DeviceId", DeviceId );
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
				tripLoadModel = constants.getLocalTripsModel(TripId, TripNumber,  itemsObject);
				tripList.add(tripLoadModel);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}



	void setAdapter(){

		try{
			if(tripList.size() > 0){
				tripAdapter = new TripAdapter(getActivity(), tripList, "local_trip" );
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
