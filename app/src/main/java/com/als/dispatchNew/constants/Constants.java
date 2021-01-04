package com.als.dispatchNew.constants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.models.JobGetSet;
import com.als.dispatchNew.models.TripHistoryModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Constants {

    public Constants(){
        super();
    }
    public static boolean isHomeFragment = true;
    public static boolean IsFragmentDetail = false;
    public static List<TripHistoryModel> temperoryList = new ArrayList<>();
    public static ArrayList<String> documentListID = new ArrayList<String>();

    public static int SocketTimeout3Sec     = 3000;   // 3 seconds
    public static int SocketTimeout5Sec     = 5000;   // 5 seconds
    public static int SocketTimeout10Sec    = 10000;   // 10 seconds
    public static int SocketTimeout15Sec    = 15000;   // 15 seconds
    public static int SocketTimeout20Sec    = 20000;   // 20 seconds
    public static int SocketTimeout30Sec    = 30000;   // 30 seconds


    public static final int New                       = 1;    //Load is new but not confirmed yet
    public static final int Confirmed                 = 2;    // Load is confirmed and ready for trip.
    public static final int LocalAssigned             = 3;    //Load assigned to driver from local collection from shipper to warehouse
    public static final int PickAndDrop               = 4;    //Load is ready to pick from shipper and drop to consignee
    public static final int Line                      = 5;    //Load dropped at warehouse from shipper address(local collection) and ready for delivery to consignee
    public static final int TripCreated               = 6;    //Load is assigned to trip and ready for delivery
    public static final int ReadyToDispatch           = 7;    //Load is ready for dispatchNew towards consignee
    public static final int Dispatched                = 8;    //Load is dispatched and on route towards consignee
    public static final int Delivered                 = 9;    //Load is delivered successfully to consignee
    public static final int LocalLoadPickedup         = 10;   //Load is ready from local collection from shipper to yard/warehouse
    public static final int PickedUp                  = 11;   //Load picked up from shipper (local collection) and ready for drop to yard/warehouse
    public static final int Hold                      = 12;   //Load is on hold by dispatcher
    public static final int Cancelled                 = 13;   //Load cancelled by dispatcher
    public static final int InternationalLoadPickedUp = 14;   //Load picked from shipper and ready to drop at consignee
    public static final int DropBeforeTripDelivered   = 15;   //Load Dropped but not delivered to consignee
    public static final int DropAtYard                = 16;   //Load Dropped at yard but not delivered to consignee yet
    public static final int PostTripLocalDelivery     = 17;   //Load is assigned to driver and ready for delivery to consignee after trip completion
    public static final int DropAtWarehouse           = 18;   //Load Dropped at warehouse but not delivered to consignee yet
    public static final int LocalDeliveryPickedUp     = 19;   //Load picked up from yard/warehouse and ready for delivery to consignee. local delivery
    public static final int DroppedLoadPickedUp       = 20;   // Load which were dropped but not delivered, has been picked up for delivery.


    /* ----- LoadStatusId const ----- */
    public static final int LocalConsignee  = 9;
    public static final int LocalYard       = 21;
    public static final int LocalWarehouse  = 5;


    public TripHistoryModel getHistoryModel(String TripId, String tripNumber, JSONObject deliveredJson, String DeliveredLoadNumber){
        TripHistoryModel tripLoadModel = null;


        try {
            String truckNo = "", trailerNo = "";
                truckNo = checkNullStr(deliveredJson,"LoadTruckNumber");

             if(deliveredJson.has("LoadTrailorNumber")){
                 trailerNo = checkNullStr(deliveredJson,"LoadTrailorNumber");
            }else{
                 trailerNo = getTrailerNumber(deliveredJson);
            }

            tripLoadModel=  new TripHistoryModel(
                    TripId, tripNumber, trailerNo, truckNo,
                    deliveredJson.getString("ShipperName"),deliveredJson.getString("ShipperAddress"),deliveredJson.getString("ShipperStateCode"),
                    deliveredJson.getString("ShipperCity"),deliveredJson.getString("ShipperPostal"),deliveredJson.getString("ShipperCountryCode"),
                    deliveredJson.getString("LoadId"), DeliveredLoadNumber,deliveredJson.getString("IsRead"),
                    deliveredJson.getString("LoadStatusId"),deliveredJson.getString("LoadStatusName"),deliveredJson.getString("ConsigneeName"),
                    deliveredJson.getString("ConsigneeAddress"),deliveredJson.getString("ConsigneeStateCode"),deliveredJson.getString("ConsigneeCity"),

                    deliveredJson.getString("ConsigneePostal"),deliveredJson.getString("ConsigneeCountryCode"),deliveredJson.getString("ConsigneeLattitude"),
                    deliveredJson.getString("ConsigneeLongitude"),deliveredJson.getString("IsProrityShipment"),deliveredJson.getString("Remarks"),

                    deliveredJson.getString("Description"),deliveredJson.getString("LoadShipperComment"),deliveredJson.getString("EntryTime"),
                    deliveredJson.getString("ExitTime"),deliveredJson.getString("DeliveryWaitingTime"),deliveredJson.getString("DeliverywaitingTimeReason"),

                    deliveredJson.getString("LoadType"),deliveredJson.getString("QTY"),deliveredJson.getString("LBS"),
                    deliveredJson.getString("KGS"),deliveredJson.getString("JobId") , "",
                    deliveredJson.getString("LoadDeliveryDocuments"),
                    tripPickedDateTime(deliveredJson), tripDeliveredDateTime(deliveredJson)
            );
        }catch (Exception e){
            e.printStackTrace();
        }

        return tripLoadModel;
    }


    public TripHistoryModel getLocalTripsModel(String TripId, String tripNumber, JSONObject tripJson){
        TripHistoryModel tripLoadModel = null;



        try {

            String truckNo = "", trailerNo = "";
            truckNo = checkNullStr(tripJson,"LoadTruckNumber");

            if(tripJson.has("LoadTrailorNumber")){
                trailerNo = checkNullStr(tripJson,"LoadTrailorNumber");
            }else{
                trailerNo = getTrailerNumber(tripJson);
            }

            tripLoadModel=  new TripHistoryModel(
                    TripId, tripNumber, trailerNo, truckNo,
                    tripJson.getString("ShipperName"),tripJson.getString("ShipperAddress"),tripJson.getString("ShipperStateCode"),
                    "","",tripJson.getString("ShipperCountryCode"),
                    tripJson.getString("LoadId"),  tripJson.getString("LoadNumber"), tripJson.getString("IsRead"),
                    tripJson.getString("LoadStatusId"),tripJson.getString("LoadStatusName"),tripJson.getString("ConsigneeName"),
                    tripJson.getString("ConsigneeAddress"), "","",
                    "","",tripJson.getString("ShipperLattitude"),
                    tripJson.getString("ShipperLongitude"),tripJson.getString("IsPickedUp"),tripJson.getString("IsDelivered"),

                    "", "", "", "", "", "",
                    "", "", "","", "" , "","",
                    tripPickedDateTime(tripJson), tripDeliveredDateTime(tripJson)

            );
        }catch (Exception e){
            e.printStackTrace();
        }

        return tripLoadModel;
    }


    public static String getTrailerNumber(JSONObject json){
        String TrailerNumber = "NOT AVAILABLE";
        try{
            if(!json.isNull("TrailerNo")){
                TrailerNumber = json.getString("TrailerNo");

                if(TrailerNumber.trim().length() == 0){
                    TrailerNumber = "NOT AVAILABLE";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return TrailerNumber;
    }


    public static String checkNullStr(JSONObject json, String parameter){
        String value = "NOT AVAILABLE";
        try{
            if(json.has(parameter) && !json.isNull(parameter)){
                value = json.getString(parameter);

                if(value.trim().length() == 0){
                    value = "NOT AVAILABLE";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }


    public static String tripPickedDateTime(JSONObject json){

        String dateTime = "";
        try{
            if(json.has("PickupDate"))
                dateTime = json.getString("PickupDate");

            if(json.has("PickupTime"))
                dateTime = dateTime + " " + json.getString("PickupTime");

            dateTime = dateTime.replaceAll("null","");

        }catch (Exception e){
            dateTime = "";
        }

        return dateTime;

    }



    public static String tripDeliveredDateTime(JSONObject json){

        String dateTime = "";
        try{
            if(json.has("DeliveryDate"))
                dateTime = json.getString("DeliveryDate");

            if(json.has("DeliveryTime"))
                dateTime = dateTime + " " + json.getString("DeliveryTime");

            dateTime = dateTime.replaceAll("null","");

        }catch (Exception e){
            dateTime = "";
        }

        return dateTime;

    }



    public JobGetSet getLocalTripJob(JSONObject itemsObject) {

        JobGetSet localTripJobData = null;
        try {
            localTripJobData = new JobGetSet(
                    itemsObject.getString("ShipperName"),
                    itemsObject.getString("ShipperAddress"),
                    itemsObject.getString("ShipperStateCode"),
                    itemsObject.getString("ShipperCountryCode"),
                    itemsObject.getString("LoadId"),
                    itemsObject.getString("LoadNumber"),
                    itemsObject.getString("IsRead"),
                    itemsObject.getString("JobId"),
                    itemsObject.getString("IsCustomeBrokerCleared"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return localTripJobData;

    }

    public int getTabWidth(Activity context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
       // int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels/2;
    }




   public boolean getDateDifference(String entryDate, String exitDate){

        boolean isGreater = false;
       SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Globally.DateFormatMMddyyyyHH);

        try {

            Date EntryDate = simpleDateFormat.parse(entryDate);
            Date ExitDate = simpleDateFormat.parse(exitDate);

            if(EntryDate.before(ExitDate)) {
                isGreater = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isGreater;
    }



    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public String printDifference(String startDateStr, String endDateStr ){

        String waitingTime = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Globally.DateFormatMMddyyyyHH);

        try {

            Date startDate = simpleDateFormat.parse(startDateStr);
            Date endDate = simpleDateFormat.parse(endDateStr);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();


            System.out.println("startDate : " + startDate);
            System.out.println("endDate : "+ endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays,
                    elapsedHours, elapsedMinutes, elapsedSeconds);

            if(elapsedDays >  0){
                waitingTime = String.valueOf(elapsedDays) + "day " + elapsedHours+"hr " + elapsedMinutes+"mins";
            }else{
                waitingTime =  elapsedHours+"hr " + elapsedMinutes+"mins";
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        return waitingTime;


    }


    public String GetAppVersion(Context context, String type){
        String AppVersion = "";
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            if(type.equals("VersionName")) {
                AppVersion = info.versionName;
            }else{
                AppVersion = String.valueOf(info.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return AppVersion;
    }


    public boolean checkPlayServices(Context context) {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                Log.d("GooglePlayServices", "UserResolvableError"  );
            } else {
                Log.d("GooglePlayServices", "This device is not supported." );
            }
            return false;
        }
        return true;
    }


    public boolean CheckGpsStatus(Context context){
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static void ClearAllFields(Context c){

        try {
            Globally.setLoadId( "", c);
            Globally.setDriverId("", c);
            Globally.setPassword( "", c);
            Globally.setUserName( "", c);

            c.stopService(Globally.serviceIntent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void ClearLogoutData(Context c){
        ClearAllFields(c);

        Globally.i = new Intent(c, LoginActivity.class);
        Globally.i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(Globally.i);
        ((Activity) c).finish();

    }



    public void showFullScreenImage(Context context, String imagePath, ImageLoader imageLoader, DisplayImageOptions options ){
        final Dialog picker = new Dialog(context);
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_touch_imageview);

        ImageView touchImageView = (ImageView) picker.findViewById(R.id.touchImageView);
        Button closeBtn = (Button) picker.findViewById(R.id.closeBtn);

        imageLoader.displayImage(imagePath, touchImageView, options);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });

        picker.show();
    }


}
