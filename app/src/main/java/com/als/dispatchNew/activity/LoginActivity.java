package com.als.dispatchNew.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.fcm.Config;
import com.als.dispatchNew.fcm.SharedPrefManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements OnClickListener {

    EditText userNameText, passwordText;
    TextView appVersionLogin, buildTypeTV;
    RelativeLayout mainLoginLayout, loginLayout;
    Button loginBtn;
    ProgressDialog p;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TelephonyManager mTelephonyManager;
    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 4;
	Constants constants;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

		constants		= new Constants();
		appVersionLogin = (TextView)findViewById(R.id.appVersionLogin);
		buildTypeTV	= (TextView)findViewById(R.id.buildTypeTV);

        userNameText = (EditText) findViewById(R.id.userNameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        mainLoginLayout = (RelativeLayout) findViewById(R.id.mainLoginLayout);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mainLoginLayout.setOnClickListener(this);
        loginLayout.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        appVersionLogin.setText("Version - " + constants.GetAppVersion(this, getResources().getString(R.string.VersionName)));
		if(APIs.DOMAIN_URL.contains("192") || APIs.DOMAIN_URL.contains("182")) {
			buildTypeTV.setVisibility(View.VISIBLE);
		}

        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    // =============== Check storage permission =====================
                    isStoragePermissionGranted();
                }
                return false;
            }
        });


        try {
            Globally.registrationId = SharedPrefManager.getInstance(this).getDeviceToken();
            Log.d("token", "token: " + Globally.registrationId);
        } catch (Exception e) {
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    Globally.registrationId = SharedPrefManager.getInstance(LoginActivity.this).getDeviceToken();
                }
            }
        };

/*        if (Build.VERSION.SDK_INT >= 23) {
            getDeviceImeiWithPermission();
        } else {
            getDeviceImei();
        }*/
    }


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		onCreate(savedInstanceState);
	}


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                requestPermission();

                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            login();
            return true;
        }

    }


    private void getDeviceImei() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String deviceid = mTelephonyManager.getDeviceId();
		Log.d("DeviceImei", "DeviceImei " + deviceid);
	}


/*
	@TargetApi(Build.VERSION_CODES.M)
	private void getDeviceImeiWithPermission(){
		if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
		} else {
			getDeviceImei();
		}
	}
*/


	public boolean requestPermissionForCamera(){

		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.CAMERA)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v("TAG","Permission is granted");
				login();

				return true;
			} else {
				Log.v("TAG","Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
				return false;
			}
		}
		else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG","Permission is granted");
			login();
			return true;
		}


	}

	private boolean requestPermission(){


		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				requestPermissionForCamera();

				return true;
			} else {
				Log.v("TAG","Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
				return false;
			}
		}
		else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG","Permission is granted");
			login();
			return true;
		}

	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);


		switch (requestCode) {

			case 1:
				if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
					Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
					//resume tasks needing this permission
					requestPermission();
				//	checkPermissionForCamera();
				}
				break;


			case 2:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
			//		checkPermissionForCamera();
					requestPermissionForCamera();
				}
				break;

			case 3:
				Log.v("TAG","Permission Granted: ");

				login();

				break;

			case 4:
				if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					getDeviceImei();
				}
				break;
		}

	}



	void login (){

		Globally.checkConnection = Globally.isInternetOn(LoginActivity.this);

		try {
			Globally.registrationId = SharedPrefManager.getInstance(this).getDeviceToken();
			//Log.d("token", "token: " + Globally.registrationId);
		}catch (Exception e){
			e.printStackTrace();
		}

		if(Globally.registrationId == null){
			Globally.registrationId = "";
		}


		if (Globally.checkConnection) {
			if (userNameText.getText().toString().trim().length() > 0 && passwordText.getText().toString().trim().length() > 0) {
				if (userNameText.getText().toString().trim().length() > 0){
					if (passwordText.getText().toString().length() > 0) {
						loginUser(Globally.registrationId, userNameText.getText().toString(), passwordText.getText().toString());
					} else {
						Globally.showToast(mainLoginLayout, "Please enter your Username and Password");
					}
				}else {
					Globally.showToast(mainLoginLayout, "Please enter the UserName");
				}
			} else {
				Globally.showToast(mainLoginLayout, "Please enter your Username and Password");
			}
		} else {
			Globally.showToast(mainLoginLayout, Globally.INTERNET_MSG);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		Globally.checkConnection = Globally.isInternetOn(LoginActivity.this);
	}


	void loginUser(final String number, final String username, final String pass){

		RequestQueue queue = Volley.newRequestQueue(this);

		p = new ProgressDialog(LoginActivity.this);
		p.setMessage("Loading ...");
		p.show();

		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.LOGIN_USER,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response) {

						if(p != null) {
							p.dismiss();
						}

						// {"Status":false,"Message":"Invalid user! please contact to your company.","Data":null}

						// response
						Log.d("Response", ">>>response: " + response);
						String status = "", message = "";
						JSONObject resultJson;

						try {
							JSONObject obj = new JSONObject(response);
							status = obj.getString("Status");
							message = obj.getString("Message");

							if(status.equalsIgnoreCase("true")){

								Globally.showToast(mainLoginLayout, message );

								try {
										resultJson = new JSONObject(obj.getString("Data"));

										Globally.saveDriverLoginData(resultJson.toString(), LoginActivity.this);
										Globally.setDriverId(resultJson.getString("DriverId") , LoginActivity.this);
										Globally.setUserName(resultJson.getString("DriverName"), LoginActivity.this);
										Globally.setDeviceId(resultJson.getString("DeviceId") , LoginActivity.this);
										Globally.setProfileImage(resultJson.getString("ImagePath") , LoginActivity.this);
										Globally.setPassword( passwordText.getText().toString() , LoginActivity.this);
										Globally.setSecureLoad(resultJson.getBoolean("IsSecureLoad"), LoginActivity.this);

										if(resultJson.has("GpsIntervalTime") && !resultJson.isNull("GpsIntervalTime")){
											Globally.setGpsIntervalTime(resultJson.getInt("GpsIntervalTime"), LoginActivity.this);
										}
								}catch (Exception e){
									e.printStackTrace();
								}

								Intent i = new Intent(LoginActivity.this, DispatchMainActivity.class);
								startActivity(i);
								finish();

							}else if (status.equalsIgnoreCase("false")) {

								Globally.showToast(mainLoginLayout, message);

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
						if(p != null) {
							p.dismiss();
						}
                        Log.d("error", "error: " +error);
						if(error.toString().contains("TimeoutError")){
							Globally.showToast(mainLoginLayout, "Time out error. Server not responding");
						}else{
							Globally.showToast(mainLoginLayout, getString(R.string.error_desc) );
						}

					}
				}
		) {
			@Override
			protected Map<String, String> getParams()
			{
				Map<String,String> params = new HashMap<String, String>();

				params.put("DeviceId", number );
				params.put("Password", pass);
				params.put("Username", username );

				return params;
			}
		};
		//queue.add(postRequest);



		int socketTimeout = 30000;   //150 seconds - change to what you want
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);




	}


	@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.mainLoginLayout:
				Globally.hideSoftKeyboard(LoginActivity.this);
				break;

			case R.id.loginLayout:
				Globally.hideSoftKeyboard(LoginActivity.this);
				break;

			case R.id.loginBtn:
				Globally.hideSoftKeyboard(LoginActivity.this);

		// =============== Check storage permission =====================
				isStoragePermissionGranted();
				break;


		}
	}
}
