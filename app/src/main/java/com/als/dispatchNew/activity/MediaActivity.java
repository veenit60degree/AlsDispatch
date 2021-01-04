package com.als.dispatchNew.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.als.dispatchNew.BuildConfig;
import com.als.dispatchNew.R;

import java.io.File;


public class MediaActivity extends Activity {

    int REQ_PICK_IMAGE = 1, REQ_CAPTURE_IMAGE = 2;
    Uri fileUri;
    File capturedFile;
    String imageType = "", imagePath = "";
    LinearLayout mediaLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_media);

        Intent i = getIntent();
        imageType = i.getStringExtra("type");

        isStoragePermissionGranted();


        mediaLay = (LinearLayout)findViewById(R.id.mediaLay);
        mediaLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

    }




    void openGallery(){
        Intent galIntent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galIntent, REQ_PICK_IMAGE);

    }

    void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturedFile = Globally.getOutputMediaFile(Globally.PICK_FROM_CAMERA);
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", capturedFile) );
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQ_CAPTURE_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    void openMediaCamera(){
        if(imageType.equals("camera")){
            openCamera();
        }else{
            openGallery();
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted Storage");
                requestPermissionForCamera();

                return true;
            } else {
                Log.v("TAG","Permission is revoked Storage");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted Storage");
            openMediaCamera();
            return true;
        }

    }

    public boolean requestPermissionForCamera(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted Camera");
                openMediaCamera();

                return true;
            } else {
                Log.v("TAG","Permission is revoked Camera");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted Camera");
            openMediaCamera();
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {

            case 1:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("TAG","Permission onRequest: "+permissions[0]+ "grantResults was: "+grantResults[0]);
                    //resume tasks needing this permission
                    requestPermissionForCamera();
                    //	checkPermissionForCamera();
                }else{
                    finish();
                }
                break;


            case 2:
                Log.v("TAG","Permission Granted onRequest ");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG","Permission: "+permissions[0]+ "grantResults was "+grantResults[0]);
                    //		checkPermissionForCamera();
                    openMediaCamera();
                }else{
                    finish();
                }


                break;

        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQ_PICK_IMAGE) {

            BitmapFactory.Options options = new BitmapFactory.Options();

          //   down sizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 2;

            fileUri = data.getData();

            if(fileUri != null){
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(fileUri,  filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);

                if (cursor != null) {
                    cursor.close();
                }

                int rotation_degree = Globally.getExifOrientation(imagePath);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation_degree);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                imagePath = Globally.SaveRotatedImage(bitmap);
                Log.e("", "===fileUri REQ_PICK_IMAGE: " + imagePath);

                // delete actual catured image
                Globally.DeleteFile(new File(fileUri.getPath()));

                FinishActivity(imagePath);



            }else {
                Globally.showToast(mediaLay, "Image not found in your Sd card");
                finish();
            }

        } else if (resultCode == RESULT_OK && requestCode == REQ_CAPTURE_IMAGE) {


            if(capturedFile != null){
                try {
                    imagePath = capturedFile.getPath();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // down sizing image as it throws OutOfMemory Exception for larger images
                   options.inSampleSize = 2;
                   Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                    int rotation_degree = Globally.getExifOrientation(imagePath);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotation_degree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    imagePath = Globally.SaveRotatedImage(bitmap);
                    Log.e("", "===fileUri Rotated_IMAGE: " + imagePath );

                    // delete actual catured image
                    Globally.DeleteFile(capturedFile);

                    // pass rotated final image
                    FinishActivity(imagePath);


                } catch (Exception e) {
                    Globally.showToast(mediaLay, "" + e.toString());
                    e.printStackTrace();
                    finish();
                }
            }
        }else{
            finish();
        }


    }



    void FinishActivity(String image){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", image);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


}
