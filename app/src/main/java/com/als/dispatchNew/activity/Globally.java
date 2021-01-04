package com.als.dispatchNew.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.constants.DriverDetailModel;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/*========================= CONSTANT VARIABLES =======================*/
public class Globally {

	public static SharedPreferences preferences, prefs;
	public static String registrationId = "";
	public static String LATITUDE = "", LONGITUDE = "";
	public static JSONObject obj ;
	public static Intent i;
	public static final int PICK_FROM_CAMERA = 1, MEDIA_TYPE_VIDEO = 4575;
	public static int REQ_PICK_IMAGE = 1, REQ_CAPTURE_IMAGE = 3, CROPPER = 4;
	public static boolean checkConnection = false;

	public static int SocketTimeout3Sec     = 3000;   // 3 seconds
	public static int SocketTimeout5Sec     = 5000;   // 5 seconds
	public static int SocketTimeout10Sec    = 10000;   // 10 seconds
	public static int SocketTimeout15Sec    = 15000;   // 15 seconds
	public static int SocketTimeout20Sec    = 20000;   // 20 seconds
	public static int SocketTimeout30Sec    = 30000;   // 30 seconds


	public static String INTERNET_MSG 		= "Not connected to Internet";
	public static String CHECK_INTERNET_MSG = "Please check your internet connection";
	static int[] NETWORK_TYPES = {
			ConnectivityManager.TYPE_WIFI,
			ConnectivityManager.TYPE_MOBILE,
			ConnectivityManager.TYPE_BLUETOOTH,
			ConnectivityManager.TYPE_ETHERNET };


	public static Intent serviceIntent;
	public static String DateFormat 				= "yyyy'-'MM'-'dd'T'HH':'mm':'ss";
	public static String DateTimeFormat 			= "yyMMddHHmm";
	public static String DateFormatMMddyyyy			= "MM/dd/yyyy";
	public static String DateFormatMMddyyyyHH  	    = "MM/dd/yyyy HH:mm";
	public static String DateFormatHalf 			= "yyyy'-'MM'-'dd";
	public static String DateFormatMMddyyyyHyphen 	= "MM'-'dd'-'yyyy";
	static String DateFormatMMddyyyyHHmma			= "MM/dd/yyyy hh:mm a";


	public static Bundle bundle = new Bundle();
	public static Bundle getBundle;


	public static int[] NOTIFICATIONS_ID = {
			0,
			1,		// DRIVER JOB CONFIRMATION
			2		//
	};


	private Globally() {
	}


	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}

	public static boolean isInternetOn(Context context) {
		try {
			ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			for (int networkType : NETWORK_TYPES) {
				NetworkInfo netInfo = connec.getNetworkInfo(networkType);
				if (netInfo != null && netInfo.isConnectedOrConnecting() ) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}






	public static String convertStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    try {
	        while ((line = rd.readLine()) != null) {
	            total.append(line);
	        }
	    } catch (Exception e) {
	    }
	    return total.toString();
	}

	
	/*========================= Show Toast message =====================*/
	public static void showToast(View view, String message)
	{

		try {
			Snackbar snackbar = Snackbar
					.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(Color.parseColor("#2680EB"));
			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();
		}

		//Toast.makeText(cxt, message, Toast.LENGTH_SHORT).show();
	}



	public static String ConvertImageToByteAsString(String file, int bitmapQuality, int base64Quality){
		Bitmap bm = BitmapFactory.decodeFile(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, baos); //bm is the bitmap object
		byte[] byteArray = baos.toByteArray();

		Bitmap bitmap1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap1.compress(Bitmap.CompressFormat.JPEG, base64Quality, stream);
		String byteStr = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);

		return byteStr;
	}

	/*========================= Show Toast message =====================*/
	public static void EldScreenToast(View view, String message, int color) {

		try {
			Snackbar snackbar = Snackbar
					.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(color);
			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();
		}

	}




/* ####################################################################################################### */	
	
/*====================== Save User Data with Shared Preferences =====================*/
	// Set  UserName -------------------
	public static void setUserName( String value, Context context) {
	     prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("username", value);
	    editor.commit();
	}

	// Get  USerName -------------------
	public static String getUserName(Context context) {
	    preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getString("username", "");
	}



	public static String GetCurrentDateTimeString(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(DateFormatMMddyyyyHH);
		return df.format(c.getTime());
	}

	public static String getCurrentDateTime(){
		SimpleDateFormat currentDateFormat = new SimpleDateFormat(DateFormat);
		Calendar c = Calendar.getInstance();
		String StringCurrentDate = "";

		try {
			StringCurrentDate = currentDateFormat.format(c.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		return StringCurrentDate;
	}


	public static String GetCurrentDate_MMddyyyy(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(DateFormatMMddyyyy);
		return df.format(c.getTime());
	}



	public static void setId(String key, String value, Context context) {
	     prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(key, value);
	    editor.commit();
	}

	
	// Get  USerName -------------------
	public static String getId(String key, Context context) {
	    preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getString(key, "");
	}



	// Save Saved Location Time Stamp -------------------
	public static void setSavedLocTimeStamp(String value, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("savedLocTime", value);
		editor.commit();
	}


	// Get Saved Location Time Stamp -------------------
	public static String getSavedLocTimeStamp(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString("savedLocTime", "");
	}





	// Set  Password -------------------
	public static void setPassword( String value, Context context) {
	     prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("password", value);
	    editor.commit();
	}
	
		
		// Get  Password -------------------
		public static String getPassword( Context context) {
		    preferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return preferences.getString("password", "");
		}


	// Save ImEi Number
	public static void setDeviceId(String value, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("DeviceId", value);
		editor.commit();
	}


	// Get  ImEi Number -------------------
	public static String getDeviceId(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString("DeviceId", "");
	}



	// Save Profile Image URL
	public static void setProfileImage(String value, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("ProfileImage", value);
		editor.commit();
	}


	// Get Profile Image -------------------
	public static String getProfileImage(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString("ProfileImage", "");
	}




	// Set  Load Id -------------------
		public static void setLoadId( String value, Context context) {
		     prefs = PreferenceManager.getDefaultSharedPreferences(context);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString("load_id", value);
		    editor.commit();
		}
		
			
			// Get  Load Id -------------------
			public static String getLoadsId(Context context) {
			    preferences = PreferenceManager.getDefaultSharedPreferences(context);
			    return preferences.getString("load_id", "");
			}


	// Set Secure Load status -------------------
	public static void setSecureLoad( boolean value, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("IsSecureLoad", value);
		editor.commit();
	}


	// Get Secure Load status -------------------
	public static boolean isSecureLoad(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean("IsSecureLoad", false);
	}



	// Set Gps Interval Time -------------------
	public static void setGpsIntervalTime( int value, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("intervalTime", value);
		editor.commit();
	}


	// Get Gps Interval Time -------------------
	public static int getGpsIntervalTime(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt("intervalTime", 1);
	}




	/*====================== Hide keyboard =====================*/
	@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
	public static void hideKeyboard(Context cxt, RelativeLayout lay){
		InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(lay.getWindowToken(), 0);
		
		
        
         
	}
	
	
	

	@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
	public static void hideSoftKeyboard(Activity activity) {
		try {
		
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		
		} catch (Exception e) {   }
	}




	// Set RegId -------------------
	public static void setRegId(String key, String value, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	// Get RegId -------------------
	public static String getRegId(String key, Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(key, "");
	}


		// Set Driver Id -------------------
		public static void setDriverId(String value, Context context) {
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString("DriverId", value);
		    editor.commit();
		}
		// Get Driver Id -------------------
		public static String getDriverId(Context context) {
		    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return preferences.getString("DriverId", "");
		}


	// Set Driver Id -------------------
	public static void saveDriverLoginData(String value, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("LoginData", value);
		editor.commit();
	}
	// Get Driver Id -------------------
	public static String getDriverLoginData(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString("LoginData", "");
	}




	/* --- Get Image orientaion ---------*/
	@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				// We only recognise a subset of orientation tag values.
				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						degree = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						degree = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						degree = 270;
						break;
				}

			}
		}

		return degree;
	}



	/*Check Image Orientation status */
	public static String SaveRotatedImage(Bitmap finalBitmap) {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/ALS_Dispatch");
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "IMG_"+ n +".jpeg";
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(file);
	}




	// Create URI file----
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}



	/*Create file directory with file */
	public static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"ALS_Dispatch");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "ALS_Dispatch" + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == PICK_FROM_CAMERA) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpeg");
		}  else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");



		} else {
			return null;
		}
		return mediaFile;
	}


	public static void DeleteDirectory(String directory) {
		try {
			// External sdcard location
			File mediaStorageDir = new File(directory);
			// delete the storage directory if it exists
			if (mediaStorageDir.isDirectory()) {
				String[] children = mediaStorageDir.list();
				for (int i = 0; i < children.length; i++) {
					new File(mediaStorageDir, children[i]).delete();
				}
				//mediaStorageDir.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}



	public static void DeleteFile(File file) {
		try {
			// delete the storage directory if it exists
			if (file.isFile()) {
				file.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}




	public static long DateDifference(Date startDate, Date endDate) {
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

		/*long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		System.out.printf(
				"%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

		return elapsedDays;
	}


	/*---------------- PARSE JSON -----------------*/
	public static DriverDetailModel driverDetailModel(DriverDetailModel model, JSONObject resultJson) throws Exception{

		double StartLatitude = 0.0;
		double StartLongitude = 0.0;
		double EndLatitude = 0.0;
		double EndLongitude = 0.0;

		if( !resultJson.getString("StartLatitude").equalsIgnoreCase("null"))
			StartLatitude = resultJson.getInt("StartLatitude");
		if( !resultJson.getString("StartLongitude").equalsIgnoreCase("null"))
			StartLongitude = resultJson.getInt("StartLongitude");
		if( !resultJson.getString("EndLatitude").equalsIgnoreCase("null"))
			EndLatitude = resultJson.getInt("EndLatitude");
		if( !resultJson.getString("EndLongitude").equalsIgnoreCase("null"))
			EndLongitude = resultJson.getInt("EndLongitude");

		model = new DriverDetailModel(
				resultJson.getInt("DriverLogId"),
				resultJson.getInt("DriverId"),
				resultJson.getInt("ProjectId"),
				resultJson.getInt("DriverStatusId"),
				resultJson.getString("StartDateTime"),
				resultJson.getString("EndDateTime"),
				resultJson.getString("TotalHours"),
				StartLatitude,
				StartLongitude,
				EndLatitude,
				EndLongitude,
				resultJson.getInt("CurrentCycleId"),
				resultJson.getBoolean("IsViolation"),
				resultJson.getString("CreatedDate")
		);

		return model;
	}




	public static void StopService(Context context){
		try {
			setDriverId( "", context);
			setPassword( "", context);
			setProfileImage( "", context);
			setUserName( "", context);
			setGpsIntervalTime(1, context);
			saveDriverLoginData("", context);
			context.stopService(serviceIntent);
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	public static DateTime getDateTimeObj(String date, boolean isInputInUTC) {
		DateTime oDate = null;
		DateTimeFormatter dtf = null;
		boolean requireOnlyTime = false;

		try {
			if (date != null && date != "") {
				if (requireOnlyTime) {
					dtf = org.joda.time.format.DateTimeFormat.forPattern("HH:mm:ss");
					oDate = dtf.parseDateTime(date);
				} else {
					if (isInputInUTC) {
						if (!date.contains(".") && !(date.toLowerCase().contains("z"))) {
							date += ".000Z";
						}
						oDate = new DateTime(date, DateTimeZone.UTC);
						// oDate = DateTime.parse(date);
					} else {
						if (date.contains(".")) {
							oDate = DateTime.parse(date).toDateTime(DateTimeZone.UTC);
						} else {
							oDate = new LocalDateTime(date).toDateTime(DateTimeZone.UTC);
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return oDate;
	}


}
