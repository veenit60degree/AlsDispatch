package com.als.dispatchNew.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.activity.DispatchMainActivity;
import com.als.dispatchNew.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class TripHistoryPagerFragment extends Fragment implements View.OnClickListener {

    RelativeLayout backMenuLay, localTripBtn, searchTripBtn;
    Constants constants;
    public static ViewPager tripHistoryPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_pager,container, false);
        constants = new Constants();

        // Setting ViewPager for each Tabs
        tripHistoryPager = (ViewPager) view.findViewById(R.id.tripHistoryPager);
        setupViewPager(tripHistoryPager);

        // Set Tabs inside Toolbar
        TabLayout historyTabLayout = (TabLayout) view.findViewById(R.id.historyTabLayout);
        historyTabLayout.setupWithViewPager(tripHistoryPager);


        historyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if(tab.getPosition() == 0){
                    TripHistoryFragment.tripHistoryBtn.performClick();
                }else{
                    LocalTripHistoryFragment.tripHistoryBtn.performClick();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        TextView tripTitleTV = (TextView)view.findViewById(R.id.tripTitleTV);
        tripTitleTV.setText(getResources().getString(R.string.trip_history));

        backMenuLay = (RelativeLayout)view.findViewById(R.id.backMenuLay);
        localTripBtn = (RelativeLayout)view.findViewById(R.id.localTripBtn);
        searchTripBtn = (RelativeLayout)view.findViewById(R.id.searchTripBtn);

        localTripBtn.setVisibility(View.GONE);
        searchTripBtn.setVisibility(View.GONE);

        backMenuLay.setOnClickListener(this);
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        constants.isHomeFragment = false;
        constants.IsFragmentDetail = false;

    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TripHistoryFragment(), "Trip History");
        adapter.addFragment(new LocalTripHistoryFragment(), "Local Trip History");
        viewPager.setAdapter(adapter);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.backMenuLay:
                DispatchMainActivity.drawer.openDrawer(GravityCompat.START);
                break;


        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }





}
