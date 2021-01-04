package com.als.dispatchNew.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.Globally;
import com.als.dispatchNew.activity.LoginActivity;
import com.als.dispatchNew.adapter.AddDocumentAdapter;
import com.als.dispatchNew.constants.APIs;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.models.DocumentModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPickDocFragment extends Fragment implements View.OnClickListener{

    View rootView;
    RequestQueue queue;
    StringRequest postRequest;
    public static String loadId = "", JobId = "";
    RelativeLayout billLoadLay, fccLay, toxicLay, certificationLay, exportLay, commercialInvoiceLay, backDetailAddPickLay;
    LinearLayout addPickLay;
    TextView billLoadTV,fccTV,toxicTV,certificationTV,exportTV,commercialInvoiceTV;
    ProgressDialog p;
    DocumentModel dataModel;
    AddDocumentAdapter docAdapter;
    List<DocumentModel> dataList = new ArrayList<DocumentModel>();
    ListView docListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.add_pick_doc_frag, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }



    void initView(View container) {

        addPickLay = (LinearLayout) container.findViewById(R.id.addPickLay);
        billLoadLay = (RelativeLayout)container.findViewById(R.id.billLoadLay);
        fccLay= (RelativeLayout)container.findViewById(R.id.fccLay);
        toxicLay = (RelativeLayout)container.findViewById(R.id.toxicLay);
        certificationLay = (RelativeLayout)container.findViewById(R.id.certificationLay);
        exportLay = (RelativeLayout)container.findViewById(R.id.exportLay);
        commercialInvoiceLay = (RelativeLayout)container.findViewById(R.id.commercialInvoiceLay);
        backDetailAddPickLay = (RelativeLayout)container.findViewById(R.id.backDetailAddPickLay);
        docListView = (ListView)container.findViewById(R.id.docListView);

        billLoadTV = (TextView)container.findViewById(R.id.billLoadTV);
        fccTV = (TextView)container.findViewById(R.id.fccTV);
        toxicTV = (TextView)container.findViewById(R.id.toxicTV);
        certificationTV = (TextView)container.findViewById(R.id.certificationTV);
        exportTV = (TextView)container.findViewById(R.id.exportTV);
        commercialInvoiceTV = (TextView)container.findViewById(R.id.commercialInvoiceTV);

        billLoadLay.setOnClickListener(this);
        fccLay.setOnClickListener(this);
        toxicLay.setOnClickListener(this);
        certificationLay.setOnClickListener(this);
        exportLay.setOnClickListener(this);
        commercialInvoiceLay.setOnClickListener(this);
        backDetailAddPickLay.setOnClickListener(this);


        Globally.getBundle = this.getArguments();
            loadId = Globally.getBundle.getString("load_Id");
            JobId  = Globally.getBundle.getString("JobId");


      //  GET_JOB_LOAD_TYPE(Globally.getDeviceId( getActivity()), Globally.getDriverId( getActivity()), loadId );

    }

    @Override
    public void onResume() {
        super.onResume();
        Globally.checkConnection = Globally.isInternetOn(getActivity());
        if(Globally.checkConnection) {
            GetLoadDetail( Globally.getDeviceId( getActivity()), Globally.getDriverId( getActivity()), loadId);
        }else{
            Globally.showToast(addPickLay, Globally.INTERNET_MSG);
        }

    }


// ================== Get Confirm Delivery ===================

    void GET_JOB_LOAD_TYPE( final String DeviceId, final String DriverId, final String LoadId){


        queue = Volley.newRequestQueue(getActivity());

            p = new ProgressDialog(getActivity());
            p.setMessage("Loading ...");
            p.show();


        postRequest = new StringRequest(Request.Method.POST, APIs.GET_JOB_LOAD_TYPE , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(p != null){
                    p.dismiss();
                }


                Log.d("response: ", "--response: " +response);
                String status = "", message = "", data = "";
                JSONObject dataJson, docJSON;
                JSONArray DocTypeArray;

                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");
                    data = Globally.obj.getString("Data");

                    if(status.equalsIgnoreCase("true")){




                            //Globally.showToast(getActivity(), message );

                            dataJson = new JSONObject(data);

                        DocTypeArray = (JSONArray) dataJson.get("JobDocTypeApi");
                        dataList.clear();

                        for(int i = 0 ; i < DocTypeArray.length() ; i++){
                            docJSON = (JSONObject)DocTypeArray.get(i);

                            boolean isExist = false;


                            if(!isExist){
                                dataModel = new DocumentModel(docJSON.getString("JobDocTypeId"), docJSON.getString("JobDocTypeName"),
                                        docJSON.getString("Description"),"","","","");
                            }
                            dataList.add(dataModel);
                        }

                        if(dataList.size() >= 0){
                            docAdapter = new AddDocumentAdapter(getActivity(), dataList);
                            docListView.setAdapter(docAdapter);
                        }


                    }else if (status.equalsIgnoreCase("false")) {

                        Globally.showToast(addPickLay, message);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.setDriverId( "", getActivity());
                            Globally.setPassword( "", getActivity());
                            Globally.setUserName( "", getActivity());

                            getActivity().stopService(Globally.serviceIntent);

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
                        Log.e("error", "error "+error);
                        if(p != null){
                            p.dismiss();
                        }

                        Globally.showToast(addPickLay, getString(R.string.error_desc) );
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                params.put("DeviceId", DeviceId );
                params.put("DriverId", DriverId);
                params.put("LoadId", LoadId);

                return params;
            }
        };
        queue.add(postRequest);



    }




    void GetLoadDetail(final String number,final String DriverId,final String LoadId){

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest  = new StringRequest(Request.Method.POST, APIs.GET_LOAD_DETAIL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("response", "response: " +response);
                String status = "", message = "", data = "";
                JSONObject dataJson = null;
                JSONArray JobLoadDocumentsArray = new JSONArray();


                try {
                    Globally.obj = new JSONObject(response);
                    status = Globally.obj.getString("Status");
                    message = Globally.obj.getString("Message");
                    data = Globally.obj.getString("Data");

                    if(status.equalsIgnoreCase("true")){


                        dataJson = new JSONObject(data);

                        JobLoadDocumentsArray = (JSONArray)  dataJson.get("JobLoadDocumentsApi");
                        Constants.documentListID.clear();

                        for(int i = 0 ; i < JobLoadDocumentsArray.length(); i ++){
                            JSONObject jsonObjj = (JSONObject)JobLoadDocumentsArray.get(i);

                            String id = String.valueOf(jsonObjj.getString("JobLoadDocTypeId"));

                            Constants.documentListID.add(id);
                        }

                        GET_JOB_LOAD_TYPE(Globally.getDeviceId( getActivity()), Globally.getDriverId( getActivity()), loadId );

                    }else {
                        Globally.showToast(addPickLay, message);

                        if(message.equalsIgnoreCase("Device Logout")){
                            Globally.setDriverId( "", getActivity());
                            Globally.setPassword( "", getActivity());
                            Globally.setUserName( "", getActivity());

                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(i);
                            getActivity().finish();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Globally.showToast(addPickLay, getString(R.string.error_desc) );
            }
        }){


            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                params.put("DeviceId", number);
                params.put("DriverId", DriverId);
                params.put("LoadId", LoadId);

                return params;
            }
        };
        queue.add(postRequest);



    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backDetailAddPickLay:
                 getFragmentManager().popBackStack();
                break;



        }
    }


}
