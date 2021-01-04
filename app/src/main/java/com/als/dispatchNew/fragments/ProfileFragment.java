package com.als.dispatchNew.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.MediaActivity;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    View rootView;
    TextView userNameTV,companyNameTV, tripTitleTV;
    TextView profileNameTV, scanCodeTV, carrierCodeTV , profileDlNoTV, profileDobTV, profileGenderTV, profileEmailTV;
    ImageView uploadImgBtn,editImgVw;
    CardView profileCardView;
    Constants constants;
    int MEDIA_FILE = 515;
    RelativeLayout menuActionBar, localTripBtn, searchTripBtn, editImageView;
    String DriverId = "", DeviceId = "", DriverName = "", DOB = "", ImagePath = "", ImageUrl = "",
    Gender = "", Email = "", DrivingLicenseNumber = "", ScacCode = "", CarrierCode = "", CompanyName = "";

    private DisplayImageOptions options;
    ImageLoader imageLoader;


    String tempImgPath = ""; //https://image.flaticon.com/icons/png/128/149/149071.png



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);


        return rootView;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void initView(View container) {

        constants            = new Constants();
        userNameTV           = (TextView) container.findViewById(R.id.userNameProfileTV);
        companyNameTV        = (TextView) container.findViewById(R.id.companyNameTV);
        profileNameTV        = (TextView) container.findViewById(R.id.profileNameTV);
        scanCodeTV           = (TextView) container.findViewById(R.id.scanCodeTV);
        carrierCodeTV        = (TextView) container.findViewById(R.id.carrierCodeTV);
        profileDlNoTV        = (TextView) container.findViewById(R.id.profileDlNoTV);
        profileDobTV         = (TextView) container.findViewById(R.id.profileDobTV);
        profileGenderTV      = (TextView) container.findViewById(R.id.profileGenderTV);
        profileEmailTV       = (TextView) container.findViewById(R.id.profileEmailTV);
        tripTitleTV          = (TextView) container.findViewById(R.id.tripTitleTV);

        uploadImgBtn         = (ImageView) container.findViewById(R.id.uploadImgBtn);
        editImgVw            = (ImageView) container.findViewById(R.id.editImgVw);

        menuActionBar        = (RelativeLayout) container.findViewById(R.id.backMenuLay);
        localTripBtn         = (RelativeLayout)container.findViewById(R.id.localTripBtn);
        searchTripBtn        = (RelativeLayout)container.findViewById(R.id.searchTripBtn);
        editImageView        = (RelativeLayout)container.findViewById(R.id.editImageView);
        profileCardView      = (CardView) container.findViewById(R.id.profileCardView);


        localTripBtn.setVisibility(View.GONE);
        searchTripBtn.setVisibility(View.GONE);

        editImageView.setElevation(10);
        profileCardView.setElevation(5);

        tripTitleTV.setText(getResources().getString(R.string.profile));

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_profile_dummy)
                .showImageForEmptyUri(R.drawable.user_profile_dummy)
                .showImageOnFail(R.drawable.user_profile_dummy)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
              //  .displayer(new RoundedBitmapDisplayer(100))
                .build();

        imageLoader = ImageLoader.getInstance();

        showDriverDetails();

        editImageView.setOnClickListener(this);
        menuActionBar.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();

        constants.isHomeFragment = false;
        constants.IsFragmentDetail = false;


    }


    private void showDriverDetails(){

        try {
            JSONObject userData = new JSONObject(Globally.getDriverLoginData(getActivity()));
            DriverId = userData.getString("DriverId");
            DeviceId = userData.getString("DeviceId");
            DriverName = userData.getString("DriverName");
            CompanyName = userData.getString("CompanyName");
            DOB = userData.getString("DOB");
            Gender = userData.getString("Gender");
            Email = userData.getString("Email");
            ScacCode = userData.getString("ScacCode");
            CarrierCode = userData.getString("CarrierCode");
            DrivingLicenseNumber = userData.getString("DrivingLicenseNumber");

            userNameTV.setText(DriverName);
            companyNameTV.setText(CompanyName);
            profileNameTV.setText(DriverName);
            scanCodeTV.setText(ScacCode);
            carrierCodeTV.setText(CarrierCode);
            profileDlNoTV.setText(DrivingLicenseNumber);
            profileDobTV.setText(DOB);
            profileGenderTV.setText(Gender);
            profileEmailTV.setText(Email);

            ImageUrl = Globally.getProfileImage(getActivity());
            imageLoader.displayImage(ImageUrl, uploadImgBtn, options);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.editImageView:

                if(Globally.isInternetOn(getActivity())) {
                    mediaDialog();
                }else{
                    Globally.showToast(uploadImgBtn, Globally.CHECK_INTERNET_MSG);
                }

                break;


            case R.id.backMenuLay:
                DispatchMainActivity.drawer.openDrawer(GravityCompat.START);
                break;


        }
    }



     /*----------------- Media Dialog For Camera/Gallery---------------- */

    void mediaDialog(){

        final Dialog mediaPicker = new Dialog(getActivity());
        mediaPicker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = mediaPicker.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        mediaPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mediaPicker.setContentView(R.layout.popup_loading_status);

        Button cameraBtn, galleryBtn, popupCancelSBtn, ViewFullImageBtn;
        cameraBtn = (Button)mediaPicker.findViewById(R.id.cameraBtn);
        galleryBtn = (Button)mediaPicker.findViewById(R.id.galleryBtn);
        popupCancelSBtn = (Button)mediaPicker.findViewById(R.id.popupCancelSBtn);
        ViewFullImageBtn = (Button)mediaPicker.findViewById(R.id.ViewFullImageBtn);

        TextView liveLoadingView = (TextView)mediaPicker.findViewById(R.id.liveLoadingView);

        ViewFullImageBtn.setVisibility(View.VISIBLE);
        liveLoadingView.setVisibility(View.VISIBLE);

        ViewFullImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                constants.showFullScreenImage(getActivity(), ImageUrl, imageLoader, options);

            }
        });




        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("camera");
            }
        });


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
                mediaIntent("gallery");
            }
        });




        popupCancelSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPicker.dismiss();
            }
        });
        mediaPicker.show();

    }



     // ----------------- Call Media Activity ----------------
    void mediaIntent(String from){
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra("type", from);
        startActivityForResult(i, MEDIA_FILE );
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK && requestCode == MEDIA_FILE) {

            ImagePath = data.getStringExtra("result");

           /* BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePath, options);
            uploadImgBtn.setImageBitmap(bitmap);
*/

            new UploadProfileImage().execute();

        }
    }




    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class UploadProfileImage extends AsyncTask<String, String, String> {

        String strResponse = "";
        com.squareup.okhttp.Response response;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading ...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                com.squareup.okhttp.Request request = null;
                com.squareup.okhttp.MultipartBuilder builderNew = new com.squareup.okhttp.MultipartBuilder().type(com.squareup.okhttp.MultipartBuilder.FORM)
                        .addFormDataPart("DeviceId", DeviceId)
                        .addFormDataPart("DriverId", DriverId);

                File f = new File(ImagePath);
                if (f.exists()) {
                    // Log.i("", "---Add File: " + f.toString());
                    builderNew.addFormDataPart("ImageFile", "file.jpeg", com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("image/jpeg"), new File(f.toString())));
                }

                com.squareup.okhttp.RequestBody requestBody = builderNew.build();
                request = new com.squareup.okhttp.Request.Builder()
                        // .header("Content-Type", "multipart/form-data")
                        .url(APIs.UPLOAD_DRIVER_IMAGE)
                        .post(requestBody)
                        .build();

                com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
                response = client.newCall(request).execute();

                //   response = okk.newCall(request).execute();
                strResponse = response.body().string();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.e("String Response", ">>>>   " + result);
            progressDialog.dismiss();

            try {

                if(result.length() > 0) {
                    JSONObject obj = new JSONObject(result);
                    String response = "";
                    response = obj.getString("Status");
                    if (response.equalsIgnoreCase("true")) {
                        JSONObject jsonData = new JSONObject(obj.getString("Data"));

                        Globally.showToast(uploadImgBtn, getResources().getString(R.string.ProfileImageChanged));
                        ImageUrl = jsonData.getString("ImagePath");
                        Globally.setProfileImage(ImageUrl , getActivity());

                        imageLoader.clearDiskCache();
                        imageLoader.clearMemoryCache();

                        imageLoader.displayImage(ImageUrl, uploadImgBtn, options);
                        imageLoader.displayImage(ImageUrl, DispatchMainActivity.userProfileIV, options);
                    } else {
                        Globally.showToast(uploadImgBtn, getResources().getString(R.string.failedToSaveImg));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
