package com.als.dispatchNew.background;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.CommonUtils;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.constants.VolleyRequest;
import com.als.dispatchNew.fcm.NotificationManagerSmart;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class UpdateLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    String packageName = "com.als.dispatchNew";
    boolean isStopService = false;
    private static final long MIN_TIME_LOCATION_UPDATES = 10 * 1000;   // 10 sec
    private static final long TIME_LOCATION_UPDATES = TimeUnit.MINUTES.toMillis(1);
    public static LocationRequest locationRequest;
    public static GoogleApiClient mGoogleApiClient;

    protected LocationManager locationManager;
    CommonUtils commonUtils;
    String LATITUDE = "" , LONGITUDE = "", DeviceId = "", DriverId =  "", LoadIds = "";

    int GpsVehicleSpeed = -1;
    final int TripListing = 101, SaveLocation = 103;
    int ApiCallInterval = 1;
    VolleyRequest GetTripListing, SaveLocationDetails;
    Map<String, String> params;
    JSONArray DispatchedLoadArray;
    NotificationManagerSmart mNotificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        mTimer                  = new Timer();
        commonUtils             = new CommonUtils();
        GetTripListing          = new VolleyRequest(getApplicationContext());
        SaveLocationDetails     = new VolleyRequest(getApplicationContext());
        mNotificationManager    = new NotificationManagerSmart(getApplicationContext());
        ApiCallInterval         = Globally.getGpsIntervalTime(getApplicationContext());

        mTimer.schedule(timerTask, TIME_LOCATION_UPDATES, TIME_LOCATION_UPDATES);

        createLocationRequest(MIN_TIME_LOCATION_UPDATES);

        // check availability of play services
        if (commonUtils.checkPlayServices(getApplicationContext())) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            requestLocationWithoutPlayServices();
        }


    }



    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {

            LATITUDE = "" +location.getLatitude();
            LONGITUDE = "" +location.getLongitude();

            GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;

         //   showNotification("Location Changed", "Speed is " + GpsVehicleSpeed, 101);

         //   Log.d("Location", "---GPS Location: " + LATITUDE + "," + LONGITUDE );

            if(Globally.isInternetOn(getApplicationContext())) {
                if(LoadIds.length() > 0 && GpsVehicleSpeed > 3 ) {
                    CheckLocationToUpdate();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!Globally.getUserName( getApplicationContext() ).equals("") &&
                !Globally.getPassword( getApplicationContext() ).equals("")) {
            try {

            } catch (Exception e) {
            }


        }

        DeviceId = Globally.getDeviceId(getApplicationContext());
        DriverId =  Globally.getDriverId(getApplicationContext());

        if(Globally.isInternetOn(getApplicationContext())) {
            GetTripListing(DriverId, DeviceId);
        }
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }


    private Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "-- Timer Task ");


            if (!Globally.getUserName( getApplicationContext() ).equals("") &&
                    !Globally.getPassword( getApplicationContext() ).equals("")) {

                if(Globally.isInternetOn(getApplicationContext())) {
                    GetTripListing(DriverId, DeviceId);
                }
            } else {
                Log.e("Log", "--stop");
                StopService();

            }

        }
    };



    @SuppressLint("RestrictedApi")
    protected void createLocationRequest(long time) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(time);
        locationRequest.setFastestInterval(time);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    private void requestLocationWithoutPlayServices(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME_LOCATION_UPDATES,10, locationListenerGPS);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(loc != null){
            LATITUDE = "" +loc.getLatitude();
            LONGITUDE = "" +loc.getLongitude();
        }else{
            LATITUDE = "0.0" ;
            LONGITUDE = "0.0" ;
        }
    }


    protected void StopLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected()) {
                stopForeground(true);
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected", "onConnected");
        try {
            requestLocationUpdates();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "onConnectionFailed");
    }


    @Override
    public void onLocationChanged(Location location) {
        LATITUDE = "" +location.getLatitude();
        LONGITUDE = "" +location.getLongitude();

        Globally.LATITUDE = LATITUDE;
        Globally.LONGITUDE = LONGITUDE;

        GpsVehicleSpeed = (int) location.getSpeed() * 18 / 5;
       //   Log.d("Location speed", "Current speed: " + GpsVehicleSpeed );
      //  showNotification("Location Changed", "Speed is " + GpsVehicleSpeed, 101);

        if(Globally.isInternetOn(getApplicationContext())) {
            if(LoadIds.length() > 0 && GpsVehicleSpeed > 3) {
                CheckLocationToUpdate();
            }
        }

    }


    private void CheckLocationToUpdate(){
        String currentTimeHHmm      = Globally.GetCurrentDateTimeString();
        String currentTime          = Globally.getCurrentDateTime();
        String savedUpdatedLocTime  = Globally.getSavedLocTimeStamp(getApplicationContext());

        if(savedUpdatedLocTime.length() > 0){

            DateTime savedDateTime       = Globally.getDateTimeObj(savedUpdatedLocTime, false);
            DateTime currentDateTime        = Globally.getDateTimeObj(currentTime, false);

            if (savedDateTime.toString().substring(0, 10).equals(currentDateTime.toString().substring(0, 10))) {
                int MinDiff = currentDateTime.getMinuteOfDay() - savedDateTime.getMinuteOfDay();

                if(MinDiff >= ApiCallInterval) {   // 5 min.
                    SaveDriverLocation(DriverId, DeviceId, LoadIds, LATITUDE, LONGITUDE, currentTimeHHmm) ;
                }
            }else{
                // clear time to update instant
                Globally.setSavedLocTimeStamp("", getApplicationContext());
            }

        }else{
            SaveDriverLocation(DriverId, DeviceId, LoadIds, LATITUDE, LONGITUDE, currentTimeHHmm) ;

        }

    }



    void StopService(){
        isStopService = true;
        try {
            mTimer.cancel();
            mTimer = null;
        } catch (Exception e) {
        }
        stopForeground(true);
        stopSelf();
    }


    public void onDestroy() {

        if(!isStopService) {
            Intent intent = new Intent(packageName);
            intent.putExtra("location", "torestore");
            sendBroadcast(intent);
        }
        Log.i("tag", "---------onDestroy Service ");
    }




    class GetAddressTask extends AsyncTask<String, String, List<Address>>{

        @Override
        protected List<Address> doInBackground(String... params) {
            String myString = params[0];
            double latitude = Double.parseDouble(myString.split(",")[0]);
            double longitude = Double.parseDouble(myString.split(",")[1]);

            Geocoder geocoder;
            List<Address> addresses = null;

            try {
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            }catch (Exception e){
                e.printStackTrace();
            }

            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);

            if(addresses != null){
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            }

        }
    }



    //*================== Get all assigned trip list ===================*//*
    void GetTripListing(final String DriverId, final String DeviceId){

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );

        GetTripListing.executeRequest(Request.Method.POST, APIs.TRIP , params, TripListing,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);

    }


    //*================== update driver current position ===================*//*
    void SaveDriverLocation(final String DriverId, final String DeviceId, final String LoadIds,
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
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {

                    case TripListing:

                        try {
                            JSONObject dataJson = new JSONObject(obj.getString("Data"));
                            JSONArray TripDetailArray = (JSONArray)  dataJson.get("TripDetail");
                            DispatchedLoadArray = new JSONArray();
                            LoadIds = "";

                            for(int tripCount = 0 ; tripCount < TripDetailArray.length() ; tripCount++) {

                                JSONObject TripDetailJson = (JSONObject) TripDetailArray.get(tripCount);

                                DispatchedLoadArray = (JSONArray) TripDetailJson.get("DispatchedLoad");

                                for(int i = 0 ; i < DispatchedLoadArray.length() ; i++){
                                    JSONObject objItem = (JSONObject)DispatchedLoadArray.get(i);
                                    int LoadStatusId = objItem.getInt("LoadStatusId");

                                    if(LoadStatusId == Constants.Dispatched || LoadStatusId == Constants.PickedUp ||
                                            LoadStatusId == Constants.InternationalLoadPickedUp || LoadStatusId == Constants.LocalDeliveryPickedUp ||
                                            LoadStatusId == Constants.DroppedLoadPickedUp ){

                                        String LoadId = objItem.getString("LoadId");

                                        if(LoadIds.length() > 0){
                                            LoadIds = LoadIds + "," + LoadId;
                                        }else{
                                            LoadIds = LoadId;
                                        }

                                    }
                                }

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        break;


                    case SaveLocation:
                        try {
                            // save API call Current Time
                            String currentTime          = Globally.getCurrentDateTime();
                            Globally.setSavedLocTimeStamp(currentTime, getApplicationContext());

                           // showNotification("API call", "Location updated" , 102);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    };




    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("Driver", "error" + error.toString());
        }
    };


    void showNotification(String title, String message, int id){
        Intent intent = new Intent(getApplicationContext(), DispatchMainActivity.class);   //
        mNotificationManager.showLocalNotification(title, message, id, intent);

    }


}
