package com.als.dispatchNew.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.activity.MediaActivity;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.models.ExpenseAndCurrencyModel;
import com.als.dispatchNew.models.ExpenseTripModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddExpenseFragment extends Fragment implements View.OnClickListener, WheelPicker.OnItemSelectedListener {


    View rootView;
    Constants constants;
    int MEDIA_FILE = 515;
    String WHEEL_SELECTION_MODE = "";
    EditText expenseReasonEditTxt, remarksEditTxt, expenseAmountEditText;
    ImageView menuImageView, addMoreExpnsIV;
    TextView expenseTripIdTxtVw, expenseValueTxtVw, expenseReasonTxtVw, tripTitleTV, wheelSelectedBtn, wheelTitleTxtVw;
    RelativeLayout expenseReasonLay, backMenuLay, searchTripBtn, localTripBtn, wheelPickerLay;
    LinearLayout addMoreExpnsLay;
    Button addExpenseBtn;
    WheelPicker wheelPicker;
    private List<String> expenseReasonList = new ArrayList<>();
    private List<String> currencyList = new ArrayList<>();
    private List<String> tripList = new ArrayList<>();

    List<ExpenseAndCurrencyModel> currencyModelList = new ArrayList<ExpenseAndCurrencyModel>();
    List<ExpenseAndCurrencyModel> expenseModelList = new ArrayList<ExpenseAndCurrencyModel>();
    List<ExpenseTripModel> tripModelList = new ArrayList<>();

    RequestQueue queue;
    StringRequest postRequest;
    String DriverId = "", DeviceId = "", CurrencyName = "", tripId = "", CurrencyId = "", ExpenseReasonId = "", imagePath = "";
    String Remarks = "", Amount = "", Reason = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{

            rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }catch (Exception e){
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }


    private void initView(View view) {

        constants = new Constants();
        expenseReasonEditTxt = (EditText)view.findViewById(R.id.expenseReasonEditTxt);
        expenseAmountEditText= (EditText)view.findViewById(R.id.expenseAmountEditText);
        remarksEditTxt       = (EditText)view.findViewById(R.id.remarksEditTxt);

        expenseTripIdTxtVw   = (TextView) view.findViewById(R.id.expenseTripIdTxtVw);
        expenseValueTxtVw    = (TextView)view.findViewById(R.id.expenseValueTxtVw);
        expenseReasonTxtVw   = (TextView)view.findViewById(R.id.expenseReasonTxtVw);
        tripTitleTV          = (TextView)view.findViewById(R.id.tripTitleTV);
        wheelSelectedBtn     = (TextView)view.findViewById(R.id.wheelSelectedBtn);
        wheelTitleTxtVw      = (TextView)view.findViewById(R.id.wheelTitleTxtVw);

        expenseReasonLay     = (RelativeLayout)view.findViewById(R.id.expenseReasonLay);
        searchTripBtn        = (RelativeLayout)view.findViewById(R.id.searchTripBtn);
        localTripBtn         = (RelativeLayout)view.findViewById(R.id.localTripBtn);
        backMenuLay          = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        wheelPickerLay       = (RelativeLayout)view.findViewById(R.id.wheelPickerLay);

        addMoreExpnsLay      = (LinearLayout)view.findViewById(R.id.addMoreExpnsLay);

        menuImageView        = (ImageView)view.findViewById(R.id.menuImageView);
        addMoreExpnsIV       = (ImageView)view.findViewById(R.id.addMoreExpnsIV);

        addExpenseBtn        = (Button)view.findViewById(R.id.addExpenseBtn);
        wheelPicker          = (WheelPicker)view.findViewById(R.id.wheel_picker);

        wheelPicker.setOnItemSelectedListener(this);

        DeviceId = Globally.getDeviceId(getActivity());
        DriverId =  Globally.getDriverId(getActivity());

        menuImageView.setImageResource(R.drawable.nback);
        tripTitleTV.setText(getResources().getString(R.string.add_Expense));
        backMenuLay.setPadding(4, 25, 35, 25);
        searchTripBtn.setVisibility(View.GONE);
        localTripBtn.setVisibility(View.GONE);

        if(Globally.isInternetOn(getActivity())){
            GetCurrenyAndDriverExpense(DeviceId, DriverId);
            GetTripList(DeviceId, DriverId);
        }

        constants.IsFragmentDetail = true;
        constants.isHomeFragment = false;


        expenseTripIdTxtVw.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);
        expenseReasonLay.setOnClickListener(this);
        expenseValueTxtVw.setOnClickListener(this);
        addExpenseBtn.setOnClickListener(this);
        wheelSelectedBtn.setOnClickListener(this);
        addMoreExpnsIV.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.addMoreExpnsIV:

                // =============== Check storage permission =====================
                wheelSelectedBtn.performClick();
                isStoragePermissionGranted();
                Globally.hideSoftKeyboard(getActivity());
                break;


            case R.id.expenseTripIdTxtVw:
                Globally.hideSoftKeyboard(getActivity());
                WHEEL_SELECTION_MODE = "trip_number";
                wheelTitleTxtVw.setText(getResources().getString(R.string.select_trip_n));

                if(tripList.size() > 0) {
                    wheelPicker.setData(tripList);
                    wheelPickerLay.setVisibility(View.VISIBLE);
                    wheelPicker.setVisibility(View.VISIBLE);
                }else{
                    Globally.showToast( addExpenseBtn, "No Trip found.");
                    if(Globally.isInternetOn(getActivity())){
                        GetTripList(DeviceId, DriverId);
                    }else{
                        Globally.showToast( addExpenseBtn, Globally.INTERNET_MSG);
                    }
                }


                break;


            case R.id.expenseReasonLay:
                Globally.hideSoftKeyboard(getActivity());
                WHEEL_SELECTION_MODE = "trip_expense";
                wheelTitleTxtVw.setText(getResources().getString(R.string.select_expense_reasonnn));

                if(expenseReasonList.size() > 0) {
                    wheelPicker.setData(expenseReasonList);
                    wheelPickerLay.setVisibility(View.VISIBLE);
                    wheelPicker.setVisibility(View.VISIBLE);
                }else{
                    if(Globally.isInternetOn(getActivity())){
                        GetCurrenyAndDriverExpense(DeviceId, DriverId);
                    }else{
                        Globally.showToast( addExpenseBtn, Globally.INTERNET_MSG);
                    }
                }

                break;


            case R.id.expenseValueTxtVw:
                Globally.hideSoftKeyboard(getActivity());
                WHEEL_SELECTION_MODE = "currency";
                wheelTitleTxtVw.setText(getResources().getString(R.string.select_expense_val));

                if(currencyList.size() > 0) {
                    wheelPicker.setData(currencyList);
                    wheelPickerLay.setVisibility(View.VISIBLE);
                    wheelPicker.setVisibility(View.VISIBLE);
                }else{
                    if(Globally.isInternetOn(getActivity())){
                        GetCurrenyAndDriverExpense(DeviceId, DriverId);
                    }else{
                        Globally.showToast( addExpenseBtn, Globally.INTERNET_MSG);
                    }
                }
                break;

            case R.id.backMenuLay:

                getFragmentManager().popBackStack();
                break;

            case R.id.addExpenseBtn:

                Globally.hideSoftKeyboard(getActivity());
                wheelSelectedBtn.performClick();

                Amount = expenseAmountEditText.getText().toString();
                Remarks = remarksEditTxt.getText().toString();
                Reason = expenseReasonTxtVw.getText().toString();

                if(Reason.equalsIgnoreCase("Others")){
                    Reason = expenseReasonEditTxt.getText().toString();
                }

                if(Globally.isInternetOn(getActivity())) {

                        if(Reason.length() > 0) {
                            if(Amount.length() > 0) {
                                if(tripId.length() > 0) {
                                    new AddExpense().execute();
                                }else{
                                    Globally.showToast( addExpenseBtn, "You can't add expense without Trip Number" );
                                }
                            }else{
                                expenseAmountEditText.requestFocus();
                                Globally.showToast( addExpenseBtn, "Enter amount first" );
                            }
                        }else {
                            Globally.showToast( addExpenseBtn, "Enter expense reason" );
                        }
                }else{
                    Globally.showToast( addExpenseBtn, Globally.INTERNET_MSG);
                }

                break;

            case R.id.wheelSelectedBtn:
                wheelPickerLay.setVisibility(View.GONE);
                wheelPicker.setVisibility(View.GONE);

                break;
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                requestPermissionForCamera();

                return true;
            } else {


                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            mediaDialog();
            return true;
        }

    }


    public boolean requestPermissionForCamera(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                mediaDialog();

                return true;
            } else {
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            mediaDialog();
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
                    requestPermissionForCamera();
                    //	checkPermissionForCamera();
                }
                break;


            case 2:
                Log.v("TAG","Permission Granted: ");
                mediaDialog();

                break;

        }

    }




    /* ----------------- Media Dialog For Camera/Gallery---------------- */
    private void mediaDialog(){

        final Dialog mediaPicker = new Dialog(getActivity());
        mediaPicker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = mediaPicker.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        mediaPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mediaPicker.setContentView(R.layout.popup_loading_status);

        Button cameraBtn, galleryBtn, popupCancelSBtn;
        cameraBtn = (Button)mediaPicker.findViewById(R.id.cameraBtn);
        galleryBtn = (Button)mediaPicker.findViewById(R.id.galleryBtn);
        popupCancelSBtn = (Button)mediaPicker.findViewById(R.id.popupCancelSBtn);



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


    /* ----------------- Call Media Activity ---------------- */
    private void mediaIntent(String from){
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra("type", from);
        startActivityForResult(i, MEDIA_FILE );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK && requestCode == MEDIA_FILE) {


            imagePath = data.getStringExtra("result");    //data.getExtras().getString("crop_image");
            try {

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);  // MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Log.d("--imagePath: ", "---imagePath: " + imagePath);

                addMoreExpnsIV.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if(WHEEL_SELECTION_MODE.equals("trip_expense")){
            expenseReasonTxtVw.setText(expenseReasonList.get(position));
            ExpenseReasonId = expenseModelList.get(position).getId();

            if(expenseReasonList.get(position).equalsIgnoreCase("others")){
                expenseReasonEditTxt.setVisibility(View.VISIBLE);
            }else{
                expenseReasonEditTxt.setVisibility(View.GONE);
                expenseReasonEditTxt.setText("");
            }

        } else if (WHEEL_SELECTION_MODE.equals("currency")){
            expenseValueTxtVw.setText(currencyList.get(position));
            CurrencyName = currencyList.get(position);
            CurrencyId = currencyModelList.get(position).getId();
        }else if(WHEEL_SELECTION_MODE.equals("trip_number")){
            expenseTripIdTxtVw.setText(tripModelList.get(position).getTripNumber());
            tripId = tripModelList.get(position).getTripId();
        }
    }


    // ================== Get Curreny And Driver Expense ===================
    private void GetCurrenyAndDriverExpense(final String DeviceId,final String DriverId){
        queue = Volley.newRequestQueue(getActivity());

        postRequest = new StringRequest(Request.Method.POST, APIs.GET_CURRENY_EXPENSE , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("response","response" +response);
                String status = "", message = "", data = "";

                try {
                    JSONObject expenseJsn, expenseInnerJsn, currencyInnerJsn;
                    JSONArray expenseArray, currencyArray;
                    ExpenseAndCurrencyModel curExpenseModel;
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");

                    if(status.equalsIgnoreCase("true")){

                        currencyModelList = new ArrayList<ExpenseAndCurrencyModel>();
                        expenseModelList = new ArrayList<ExpenseAndCurrencyModel>();
                        expenseReasonList = new ArrayList();
                        currencyList = new ArrayList();

                        data = Globally.obj.getString("Data");
                        expenseJsn = new JSONObject(data);

                        expenseArray = (JSONArray) expenseJsn.get("DriverExpenseReasonType");
                        currencyArray = (JSONArray) expenseJsn.get("CurrencyType");

                       // expenseReasonList.add("Select Expense");
                        for(int i = 0 ; i < expenseArray.length() ; i++) {
                            expenseInnerJsn = (JSONObject) expenseArray.get(i);
                            curExpenseModel = new ExpenseAndCurrencyModel(
                                    expenseInnerJsn.getString("DriverExpenseReasonMasterId"),
                                    expenseInnerJsn.getString("Reason"),
                                    expenseInnerJsn.getString("Description")
                            );
                            expenseModelList.add(curExpenseModel);
                            expenseReasonList.add(expenseInnerJsn.getString("Reason"));
                        }

                       // currencyList.add("Currency");
                        for(int j = 0 ; j < currencyArray.length() ; j++) {
                            currencyInnerJsn = (JSONObject) currencyArray.get(j);
                            curExpenseModel = new ExpenseAndCurrencyModel(
                                    currencyInnerJsn.getString("CurrencyId"),
                                    currencyInnerJsn.getString("CurrencyName"),""
                            );
                            currencyModelList.add(curExpenseModel);
                            currencyList.add(currencyInnerJsn.getString("CurrencyName"));
                        }

                        if(WHEEL_SELECTION_MODE.equals("currency")){
                            if(currencyList.size() > 0) {
                                expenseValueTxtVw.performClick();
                            }
                        }else if(WHEEL_SELECTION_MODE.equals("trip_expense")){
                            if(expenseReasonList.size() > 0) {
                                expenseReasonLay.performClick();
                            }
                        }

                    }else {

                        Globally.showToast(addExpenseBtn , message);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.StopService(getActivity());

                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(i);
                            getActivity().finish();
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
                        Log.e("error", "error" + error);
                        Globally.showToast(addExpenseBtn, getString(R.string.error_desc) );
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
        queue.add(postRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class AddExpense extends AsyncTask<String, String, String> {

        ProgressDialog p;
        String strResponse = "";
        com.squareup.okhttp.Response response;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(getActivity());
            p.setMessage("Loading ...");
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {


                com.squareup.okhttp.Request request = null;


                com.squareup.okhttp.MultipartBuilder builderNew = new com.squareup.okhttp.MultipartBuilder().type(com.squareup.okhttp.MultipartBuilder.FORM)
                        .addFormDataPart("DeviceId",DeviceId)
                        .addFormDataPart("DriverId", DriverId)
                        .addFormDataPart("TripId", tripId)
                        .addFormDataPart("DriverExpenseReasonMasterId", ExpenseReasonId)
                        .addFormDataPart("CurrencyId", CurrencyId)
                        .addFormDataPart("Amount", Amount)
                        .addFormDataPart("Remarks", Remarks)
                        .addFormDataPart("CurrencyName", CurrencyName)
                        .addFormDataPart("Reason", Reason);

                File f = new File(imagePath);
                if (f.exists()) {
                    // Log.i("", "---Add File: " + f.toString());
                    builderNew.addFormDataPart("myFile", "file.jpeg", com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("image/jpeg"), new File(f.toString())));
                }

                com.squareup.okhttp.RequestBody requestBody = builderNew.build();
                request = new com.squareup.okhttp.Request.Builder()
                        .url(APIs.UPDATE_DRIVER_TRIP_EXPENSES_DETAIL)
                        .post(requestBody)
                        .build();

                com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
                response = client.newCall(request).execute();
                strResponse = response.body().string();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            if(p != null){
                p.dismiss();
            }

            Log.e("String Response", ">>>>   " + result);

            try {

                JSONObject obj = new JSONObject(result);
                String response = "";
                response = obj.getString("Status");
                if (response.equalsIgnoreCase("true")) {
                    Globally.showToast(addExpenseBtn , "Expense added successfully");
                    getFragmentManager().popBackStack();
                }else{
                    Globally.showToast(addExpenseBtn , "failed");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Globally.showToast(addExpenseBtn , "Error");
            }
        }

    }




    private void GetTripList(final String DeviceId, final String DriverId){

        /**/
        queue = Volley.newRequestQueue(getActivity());

        postRequest = new StringRequest(Request.Method.POST, APIs.TRIP , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response: ", "--response: " +response);
                String status = "", data = "";
                JSONObject dataJson = null;
                JSONArray TripDetailArray;
                try {

                    JSONObject obj = new JSONObject(response);
                    status = obj.getString("Status");
                    data = obj.getString("Data");

                    if(status.equalsIgnoreCase("true")) {

                        dataJson = new JSONObject(data);
                        tripList = new ArrayList<>();
                        tripModelList = new ArrayList<>();

                        TripDetailArray = (JSONArray) dataJson.get("TripDetail");

                        for (int i = 0; i < TripDetailArray.length(); i++) {
                            JSONObject tripDetailJson = (JSONObject) TripDetailArray.get(i);
                            tripList.add(tripDetailJson.getString("TripNumber"));
                            tripModelList.add(new ExpenseTripModel(tripDetailJson.getString("TripId"), tripDetailJson.getString("TripNumber")));
                        }

                        if (tripList.size() > 0) {
                            if (WHEEL_SELECTION_MODE.equals("trip_number")) {
                                expenseTripIdTxtVw.performClick();
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {  }
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
        queue.add(postRequest);

    }




}
