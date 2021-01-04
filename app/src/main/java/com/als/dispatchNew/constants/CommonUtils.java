package com.als.dispatchNew.constants;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CommonUtils {

	
	public static int setWidth(Context c) {
		Display display = ((Activity) c).getWindowManager().getDefaultDisplay();

		int w = display.getWidth();

		int width = ((w / 2) - 100);

		return width;

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




}
