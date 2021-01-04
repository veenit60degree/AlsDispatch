package com.als.dispatchNew.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.constants.APIs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CancelJobFragment extends Fragment implements OnClickListener{
	
	View rootView;
	RelativeLayout backCancelLay;
	Button doneBtn;
	EditText cancellationEditText;
	LinearLayout jobCancelLay;
	String loadId = "";
	ProgressDialog p;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.job_cancel_frag, container, false);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

	    initView(rootView);
		
		return rootView;
	}

	


	void initView(View v){
		
		backCancelLay = (RelativeLayout)v.findViewById(R.id.backCancelLay);
		doneBtn = (Button)v.findViewById(R.id.doneBtn);
		cancellationEditText = (EditText)v.findViewById(R.id.cancellationEditText);
		jobCancelLay = (LinearLayout)v.findViewById(R.id.jobCancelLay);

		doneBtn.setOnClickListener(this);   
		backCancelLay.setOnClickListener(this);
		
		
		 Globally.getBundle = this.getArguments();
		    try {
		 	    loadId = Globally.getBundle.getString("load_Id");
		 	    
		 	    Log.e("",  "loadId: " + loadId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		   
		    
		
	}


	@Override
	public void onResume() {
		super.onResume();
		
		Globally.checkConnection = Globally.isInternetOn(getActivity());
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.backCancelLay:
			getFragmentManager().popBackStack();
			break;

		case R.id.doneBtn:
			if(Globally.checkConnection){
				if(cancellationEditText.getText().toString().length() > 0){
					CancelJob( Globally.getDeviceId( getActivity()), Globally.getDriverId( getActivity()),
							loadId, cancellationEditText.getText().toString());
				}else {
					Globally.showToast(jobCancelLay, "Reason field should not be blank");
				}
			}else {
				Globally.showToast(jobCancelLay, Globally.INTERNET_MSG);
			}
			
			
			break;

			
		}
		
	}



	// ================== GetDriverJob ===================

	void CancelJob(final String number, final String DriverId, final String LoadId, final String reason){
		RequestQueue queue = Volley.newRequestQueue(getActivity());

		p = new ProgressDialog(getActivity());
		p.setMessage("Loading ...");
		p.show();

		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.LOAD_CANCELLED_PARAMETERS , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				p.dismiss();
				String status = "", message = "";
				//JSONObject dataJson = null;

				try {
					Globally.obj = new JSONObject(response);
					status = Globally.obj.getString("Status");
					message = Globally.obj.getString("Message");

					if(status.equalsIgnoreCase("true")){
						Globally.showToast(jobCancelLay, message);


						Intent i = new Intent(getActivity(), DispatchMainActivity.class);
						getActivity().startActivity(i);
						getActivity().finish();




					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				p.dismiss();
				Globally.showToast(jobCancelLay,  getString(R.string.error_desc) );
			}
		}){

			@Override
			protected Map<String, String> getParams()
			{
				Map<String,String> params = new HashMap<String, String>();

				params.put("CancelReason", reason);
				params.put("DeviceId", number );
				params.put("DriverId", DriverId);
				params.put("LoadId", LoadId );

				return params;
			}
		};
		queue.add(postRequest);

	}
}
