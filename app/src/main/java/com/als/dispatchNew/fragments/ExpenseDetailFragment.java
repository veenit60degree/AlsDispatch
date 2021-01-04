package com.als.dispatchNew.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExpenseDetailFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Constants constants;
    TextView noDataTextView, tripTitleTV, tripNoTV, expReasonTV, expValueTV, remarksTxtView;
    RelativeLayout backMenuLay, newTripBtn, searchTripBtn;
    ImageView menuImageView, uploadedExpenseImgVw;
    Button deleteExpenseBtn;
    JSONObject expenseJsonData;
    ImageLoader imageLoader;
    private DisplayImageOptions options;
    String imagePath = "",  DriverId = "", DeviceId = "", DriverTripExpenseId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {

            rootView = inflater.inflate(R.layout.fragment_expence_detail_view_, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        } catch (Exception e) {
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }

    void initView(View view) {

        constants = new Constants();
        noDataTextView  = (TextView)view.findViewById(R.id.noDataTextView);
        tripTitleTV     = (TextView)view.findViewById(R.id.tripTitleTV);
        tripNoTV        = (TextView)view.findViewById(R.id.tripNoTV);
        expReasonTV     = (TextView)view.findViewById(R.id.expReasonTV);
        expValueTV      = (TextView)view.findViewById(R.id.expValueTV);
        remarksTxtView  = (TextView)view.findViewById(R.id.remarksTxtView);

        backMenuLay     = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        newTripBtn      = (RelativeLayout)view.findViewById(R.id.localTripBtn);
        searchTripBtn   = (RelativeLayout)view.findViewById(R.id.searchTripBtn);

        deleteExpenseBtn= (Button)view.findViewById(R.id.deleteExpenseBtn);

        menuImageView       = (ImageView)view.findViewById(R.id.menuImageView);
        uploadedExpenseImgVw= (ImageView)view.findViewById(R.id.uploadedExpenseImgVw);

        DeviceId = Globally.getDeviceId(getActivity());
        DriverId =  Globally.getDriverId(getActivity());

        backMenuLay.setPadding(4, 25, 35, 25);
        menuImageView.setImageResource(R.drawable.nback);
        newTripBtn.setVisibility(View.GONE);
        searchTripBtn.setVisibility(View.GONE);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.dummy_doc)
                .showImageForEmptyUri(R.drawable.dummy_doc)
                .showImageOnFail(R.drawable.dummy_doc)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        // .displayer(new RoundedBitmapDisplayer(100))

        imageLoader = ImageLoader.getInstance();

        constants.IsFragmentDetail = true;
        constants.isHomeFragment = false;

        parseData();

        uploadedExpenseImgVw.setOnClickListener(this);
        backMenuLay.setOnClickListener(this);
        deleteExpenseBtn.setOnClickListener(this);

    }


    private void parseData(){

        tripTitleTV.setText(getResources().getString(R.string.view_Expense));
        try{
            Globally.getBundle = this.getArguments();
            expenseJsonData = new JSONObject(Globally.getBundle.getString("expense_data"));

            DriverTripExpenseId = expenseJsonData.getString("DriverTripExpenseTempId");

            tripNoTV.setText( expenseJsonData.getString("TripNumber") );
            expReasonTV.setText(expenseJsonData.getString("Reason") );
            expValueTV.setText("$"+expenseJsonData.getString("Amount") + " " +expenseJsonData.getString("CurrencyName") );
            remarksTxtView.setText(expenseJsonData.getString("Remarks"));

            if(!expenseJsonData.isNull("ImagePath")) {
                imagePath = expenseJsonData.getString("ImagePath");
                imageLoader.displayImage(imagePath, uploadedExpenseImgVw, options);
                uploadedExpenseImgVw.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backMenuLay:
                getFragmentManager().popBackStack();
                break;

            case R.id.uploadedExpenseImgVw:
                constants.showFullScreenImage(getActivity(), imagePath, imageLoader, options);
                break;

            case R.id.deleteExpenseBtn:
                if(Globally.isInternetOn(getActivity())) {
                    confirmDeletePopUp();
                }else{
                    Globally.showToast(deleteExpenseBtn, Globally.CHECK_INTERNET_MSG);
                }
                break;

        }
    }



    void confirmDeletePopUp(){

        final Dialog picker = new Dialog(getActivity());
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_edit_delete_lay);



        TextView confirmPopupButton = (TextView)picker.findViewById(R.id.confirmPopupButton);
        TextView cancelPopupButton  = (TextView)picker.findViewById(R.id.cancelPopupButton);
        TextView titleDescView      = (TextView)picker.findViewById(R.id.titleDescView);
        TextView changeTitleView    = (TextView)picker.findViewById(R.id.changeTitleView);

        changeTitleView.setText(getResources().getString(R.string.DeleteExpense));
        titleDescView.setText(getResources().getString(R.string.wantToDeleteExpense));
        confirmPopupButton.setTypeface(Typeface.DEFAULT_BOLD);
        cancelPopupButton.setTypeface(Typeface.DEFAULT);

        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });


        confirmPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();

                DeleteExpense( DeviceId, DriverId, DriverTripExpenseId );
            }
        });



        picker.show();
    }




    // ================== Get Confirm Delivery ===================

    void DeleteExpense(final String DeviceId,final String DriverId, final  String DriverTripExpenseTempId){

        final ProgressDialog p = new ProgressDialog(getActivity());
        p.setMessage("Loading ...");
        p.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.DELETE_TRIP_EXPENSE , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(p != null){
                    p.dismiss();
                }

                Log.d("response: ", "--response: " +response);
                String status = "", message = "", data = "";
                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");

                    if(status.equalsIgnoreCase("true")){

                        Globally.showToast(deleteExpenseBtn, "Expense deleted successfully");
                        getFragmentManager().popBackStack();

                    }else if (status.equalsIgnoreCase("false")) {

                        Globally.showToast(deleteExpenseBtn, message);

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
                        if(p != null){
                            p.dismiss();
                        }

                        Log.e("error", "error "+error);
                        Globally.showToast(deleteExpenseBtn, getString(R.string.error_desc));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("DeviceId", DeviceId );
                params.put("DriverId", DriverId);
                params.put("DriverTripExpenseTempId", DriverTripExpenseTempId);

                return params;
            }
        };
        queue.add(postRequest);



    }



}
