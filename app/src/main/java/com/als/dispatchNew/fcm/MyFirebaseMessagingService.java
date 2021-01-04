package com.als.dispatchNew.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.activity.Globally;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService ";
    String title = "";
    String message = "";
    String imageUrl = "";
    String StartOBDDeviceDataId = "";
    String EndOBDDeviceDataId = "";
    String DrivingStartTime = "";
    String IsContinueDriving = "0";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String msg =  remoteMessage.getData().toString();
        Log.e(TAG, "---msg: " + msg);

        String[] msgDetail;
        NotificationModel msgModel;
        List<NotificationModel> msgList = new ArrayList<NotificationModel>();

        title = "";
        message = "";
        imageUrl = "";
        StartOBDDeviceDataId = "";
        EndOBDDeviceDataId = "";
        DrivingStartTime = "";

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            msg = msg.substring(1, msg.length()-1);
            msgDetail = msg.split(",");

            for(int i = 0 ; i < msgDetail.length ; i++){
                String[] msgData = msgDetail[i].split("=");
                msgModel = new NotificationModel(msgData[0].trim(), msgData[1]);
                Log.e(TAG, "---msgData: " + msgData[0].trim());

                msgList.add(msgModel);
            }
            Log.d("pending json", "before: " + remoteMessage.getData().toString());
            String jsonStr = remoteMessage.getData().toString();
            jsonStr = jsonStr.replaceAll("=", "\":\"");
            jsonStr = jsonStr.replaceAll(", ", "\",\"");
            jsonStr = jsonStr.replaceAll("[{]", "{\"");
            jsonStr = jsonStr.replaceAll("[}]", "\"}");

            Log.d("pending json", "After: " + jsonStr);
            try {
                JSONObject json = new JSONObject(jsonStr);
                ParseJson(json);
               // sendPushNotification(json);
                setPendingIntent(StartOBDDeviceDataId , EndOBDDeviceDataId, DrivingStartTime, IsContinueDriving);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }else{
            Log.e(TAG, "Remote Message size is 0" );
        }


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject jsonPre) {
        //optionally we can display the json into log
        Log.e(TAG, "----Notification JSON " + jsonPre.toString());

        try {
            ParseJson(jsonPre);

            //creating MyNotificationManager object
            NotificationManagerSmart mNotificationManager = new NotificationManagerSmart(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), DispatchMainActivity.class);

            //if there is no image
            if(imageUrl.equals("") || imageUrl == null){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image, displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
    }

    private void sendPushNotificationString(List<NotificationModel> messageList) {
        //optionally we can display the json into log
        try {


            Log.d("msgList", "msgList: " + messageList.size());


            for(int i = 0 ; i <messageList.size() ; i++){
                if(messageList.get(i).getKeyName().equals("title"))    {
                    title = messageList.get(i).getValue();
                    Log.e(TAG, "message: " + title );
                }else if(messageList.get(i).getKeyName().equals("message")){
                    message = messageList.get(i).getValue();
                    Log.e(TAG, "message: " + message );
                }else if(messageList.get(i).getKeyName().equals("image")){
                    imageUrl = messageList.get(i).getValue();
                }
            }




            //creating MyNotificationManager object
            NotificationManagerSmart mNotificationManager = new NotificationManagerSmart(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), DispatchMainActivity.class);

            //if there is no image
            if(imageUrl.equals("") || imageUrl == null){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }



    void setPendingIntent(String StartOBDDeviceDataId, String EndOBDDeviceDataId,
                          String DrivingStartTime, String IsContinueDriving){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.app_icon_notification);

        Intent intent = new Intent(getApplicationContext(), DispatchMainActivity.class);
        Intent intent1 = new Intent(getApplicationContext(), DispatchMainActivity.class);

        addDataInIntent(intent, "yes", StartOBDDeviceDataId, EndOBDDeviceDataId, DrivingStartTime, IsContinueDriving );
        addDataInIntent(intent1, "no", StartOBDDeviceDataId, EndOBDDeviceDataId, DrivingStartTime, IsContinueDriving);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, 0);
        // builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon));
        builder.setContentTitle(title);
        builder.setContentText(message);
       // builder.setSubText("Tap to view the website.");
        builder.addAction(R.drawable.app_icon_notification, "Yes", pendingIntent);
        builder.addAction(R.drawable.app_icon_notification, "No", pendingIntent1);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(Globally.NOTIFICATIONS_ID[1], builder.build());

    }


    void ParseJson(JSONObject jsonPre){

        try {
            //getting the json data
            JSONObject json = new JSONObject(jsonPre.toString());
            title = json.getString("title");
            message = json.getString("message");

            if(json.has("image"))
                imageUrl = json.get("image").toString();

            if(json.has("StartOBDDeviceDataId"))
                StartOBDDeviceDataId = json.get("StartOBDDeviceDataId").toString();

            if(json.has("EndOBDDeviceDataId"))
                EndOBDDeviceDataId = json.get("EndOBDDeviceDataId").toString();

            if(json.has("DrivingStartTime"))
                DrivingStartTime = json.get("DrivingStartTime").toString();

            if(json.has("IsContinueDriving"))
                IsContinueDriving = json.get("IsContinueDriving").toString();


        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    void addDataInIntent(Intent intent, String button, String StartOBDDeviceDataId,
                         String EndOBDDeviceDataId, String DrivingStartTime, String IsContinueDriving){
        intent.putExtra("keyDriverJob", button );
        intent.putExtra("StartOBDDeviceDataId", StartOBDDeviceDataId );
        intent.putExtra("EndOBDDeviceDataId", EndOBDDeviceDataId );
        intent.putExtra("DrivingStartTime", DrivingStartTime);
        intent.putExtra("IsContinueDriving", IsContinueDriving );
    }
}










// Format

/*    {
        "to" : "fnr7Uszzl0g:APA91bF-ICU1nzj700Rz6C4qAk3hY5CPf-Z8aGtUWdHYPHO8Nh1Y5TPkknuCfDMACOxUspj6UAYkwEfqXMIdydL-RfHA-pqAm1eI4ogs-uepJXj2pRUxQrdjV9AyszJVuF60JkGZnOQD",
            "notification": {
                "body": "Cool offers. Get them before expiring!",
                "title": "Flat 80% discount",
                "icon": "https://sportsroundtablerak.files.wordpress.com/2011/04/katiedrake1.jpg"
            },
        "data" : {
                "title" : "Mario",
                "message" : "great match",
                "image" : "https://sportsroundtablerak.files.wordpress.com/2011/04/katiedrake1.jpg"



        }
    }
 */