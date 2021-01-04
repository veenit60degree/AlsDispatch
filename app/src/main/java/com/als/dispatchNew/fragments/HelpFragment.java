package com.als.dispatchNew.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.adapter.SupportAdapter;
import com.als.dispatchNew.constants.SupportEnum;
import com.als.dispatchNew.models.SupportModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HelpFragment extends Fragment implements View.OnClickListener {


        View rootView;
        TextView tripTitleTV;
        RelativeLayout backMenuLay, localTripBtn, searchTripBtn;
        ListView supportListView;
        SupportAdapter supportAdapter;



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
                    rootView = inflater.inflate(R.layout.fragment_help, container, false);
                    rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } catch (InflateException e) {
                    e.printStackTrace();
                }


                initView(rootView);


                return rootView;

        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            void initView(View container){

            supportListView = (ListView)container.findViewById(R.id.supportListView);
            backMenuLay     = (RelativeLayout)container.findViewById(R.id.backMenuLay);
            localTripBtn     = (RelativeLayout)container.findViewById(R.id.localTripBtn);
            searchTripBtn     = (RelativeLayout)container.findViewById(R.id.searchTripBtn);
            tripTitleTV = (TextView)container.findViewById(R.id.tripTitleTV);

            tripTitleTV.setText(getResources().getString(R.string.als_support));
            localTripBtn.setVisibility(View.GONE);
            searchTripBtn.setVisibility(View.GONE);

            try {
                JSONArray supportArray = new JSONArray(SupportEnum.supportData);
                ParseSupportDetails(supportArray);
            }catch (Exception e){
                e.printStackTrace();
            }

            backMenuLay.setOnClickListener(this);
        }




    private void ParseSupportDetails(JSONArray supportArray){

        List<SupportModel> supportList = new ArrayList<SupportModel>();
        try {
            for(int i = 0 ; i < supportArray.length() ; i++){
                JSONObject supportObj = (JSONObject)supportArray.get(i);
                SupportModel sModel = new SupportModel(
                        supportObj.getString(SupportEnum.SupportDetailId),
                        supportObj.getString(SupportEnum.SupportKey),
                        supportObj.getString(SupportEnum.SupportValue),
                        supportObj.getInt(SupportEnum.SupportKeyType),
                        supportObj.getBoolean(SupportEnum.SupportIsActive),
                        supportObj.getString(SupportEnum.SupportCreateDate)
                );
                supportList.add(sModel);

            }

            if(supportList.size() > 0) {
                supportAdapter = new SupportAdapter(getActivity(), supportList);
                supportListView.setAdapter(supportAdapter);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View view) {

            switch (view.getId()){
                case R.id.backMenuLay:
                    DispatchMainActivity.drawer.openDrawer(GravityCompat.START);
                    break;
            }

    }


}
