package com.als.dispatchNew.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.adapter.NavMenuItemAdapter;
import com.als.dispatchNew.background.UpdateLocationService;
import com.als.dispatchNew.constants.Constants;
import com.als.dispatchNew.fragments.HelpFragment;
import com.als.dispatchNew.fragments.LocalTripFragment;
import com.als.dispatchNew.fragments.ProfileFragment;
import com.als.dispatchNew.fragments.TripExpListFragment;
import com.als.dispatchNew.fragments.TripFragment;
import com.als.dispatchNew.fragments.TripHistoryPagerFragment;
import com.als.dispatchNew.models.NavigationMenuModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class DispatchMainActivity extends FragmentActivity implements View.OnClickListener {

    Constants constants;
    ListView listViewMenuItems;
    FragmentManager fragManager;
    NavigationView navigationView;
    public static DrawerLayout drawer;
    public static ImageView userProfileIV;
    TextView headerUsernameTV, appVersionHome;
    Button hiddenBackClickBtn;
    LinearLayout navHeaderLay;
    TripFragment tripFragment;
    LocalTripFragment localJobFragment;
    TripExpListFragment tripExpenseFragment;
    TripHistoryPagerFragment tripHistoryFragment;
    ProfileFragment profileFragment;
    HelpFragment helpFragment;
    private DisplayImageOptions options;
    ImageLoader imageLoader;
    String ImageUrl = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_main);

        constants           = new Constants();
        navigationView      = (NavigationView) findViewById(R.id.nav_view);
        drawer              = (DrawerLayout) findViewById(R.id.drawer_layout);

        userProfileIV       = (ImageView) navigationView.findViewById(R.id.userProfileIV);
        headerUsernameTV    = (TextView) navigationView.findViewById(R.id.usernameTV);
        appVersionHome      = (TextView) navigationView.findViewById(R.id.appVersionHome);

        listViewMenuItems   = (ListView)navigationView.findViewById(R.id.listViewMenuItems);
        hiddenBackClickBtn  = (Button)findViewById(R.id.hiddenBackClickBtn);
        navHeaderLay        = (LinearLayout) findViewById(R.id.navHeaderLay);

        tripFragment        = new TripFragment();
        localJobFragment    = new LocalTripFragment();
        tripExpenseFragment = new TripExpListFragment();
        tripHistoryFragment = new TripHistoryPagerFragment();
        profileFragment     = new ProfileFragment();
        helpFragment        = new HelpFragment();

        appVersionHome.setText("Version - " + constants.GetAppVersion(this, getResources().getString(R.string.VersionName)));
        headerUsernameTV.setText(Globally.getUserName(this));
        userProfileIV.setImageResource(R.drawable.user_profile_dummy);
        loadFragment(tripFragment, "trips");

        /*========= Start Service =============*/
        Intent serviceIntent = new Intent(getApplicationContext(), UpdateLocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        startService(serviceIntent);


        ImageUrl = Globally.getProfileImage(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_profile_dummy)
                .showImageForEmptyUri(R.drawable.user_profile_dummy)
                .showImageOnFail(R.drawable.user_profile_dummy)
              //  .cacheInMemory(true)
              //  .cacheOnDisk(true)
             //   .considerExifParams(true)
              //  .displayer(new RoundedBitmapDisplayer(100))
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(ImageUrl, userProfileIV, options);


        hiddenBackClickBtn.setOnClickListener(this);
        navHeaderLay.setOnClickListener(this);

        setItemOnListView();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }


    private void setItemOnListView(){
        List<NavigationMenuModel> itemList = new ArrayList<>();
        itemList.add(new NavigationMenuModel(0, R.drawable.trip_home, getResources().getString(R.string.trips)) );
        itemList.add(new NavigationMenuModel(1, R.drawable.trip_home, getResources().getString(R.string.local_trips)) );
        itemList.add(new NavigationMenuModel(2, R.drawable.ic_expense_dollor, getResources().getString(R.string.expense)) );
        itemList.add(new NavigationMenuModel(3, R.drawable.ic_history, getResources().getString(R.string.trip_history)) );
        itemList.add(new NavigationMenuModel(4, R.drawable.ic_help, getResources().getString(R.string.help)) );
       // itemList.add(new NavigationMenuModel(5, R.drawable.transperent, getResources().getString(R.string.blank)) );
        itemList.add(new NavigationMenuModel(5, R.drawable.ic_logout, getResources().getString(R.string.logout)) );

        //itemList.add(new NavigationMenuModel(5, R.drawable.ic_settings, getResources().getString(R.string.action_settings)) );
        //  itemList.add(new NavigationMenuModel(6, R.drawable.ic_help, getResources().getString(R.string.help)) );
        //  itemList.add(new NavigationMenuModel(3, R.drawable.ic_message, getResources().getString(R.string.messages)) );

        NavMenuItemAdapter adapter = new NavMenuItemAdapter(this, itemList);
        listViewMenuItems.setAdapter(adapter);

        listViewMenuItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                drawer.closeDrawer(GravityCompat.START);

                switch (position){
                    case 0:
                        loadFragment(tripFragment, "trips");
                        break;

                    case 1:
                        loadFragment(localJobFragment, "local_trips");
                        break;


                    case 2:
                        loadFragment(tripExpenseFragment, "expenses");
                        break;


                    case 3:
                        loadFragment(tripHistoryFragment, "history");

                        break;


                    case 4:
                        loadFragment(helpFragment, "help");

                        break;


                    case 5:
                        logoutUserDialog();
                        //  Globally.showToast(listViewMenuItems, "MESSAGES screen not available yet");
                        //Globally.showToast(listViewMenuItems, "HELP screen not available yet");
                        break;


                    case 8:
                        Globally.showToast(listViewMenuItems, "SETTINGS screen not available yet");
                        break;

                }

            }
        });
    }


    void loadFragment(Fragment fragment, String key){
        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.frame_layout_fragment, fragment);
        fragmentTran.addToBackStack(key);
        fragmentTran.commit();


    }




    void logoutUserDialog(){
        final Dialog picker = new Dialog(DispatchMainActivity.this);
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_edit_delete_lay);
        //picker.setTitle("Select Date and Time");


        final TextView changeTitleView, titleDescView;
        RelativeLayout cancelBtn, confirmBtn;
        changeTitleView = (TextView) picker.findViewById(R.id.changeTitleView);
        titleDescView = (TextView) picker.findViewById(R.id.titleDescView);
        cancelBtn = (RelativeLayout) picker.findViewById(R.id.cancelPopupBtn);
        confirmBtn = (RelativeLayout) picker.findViewById(R.id.confirmPopupBtn);
        TextView confirmPopupButton = (TextView) picker.findViewById(R.id.confirmPopupButton);
        TextView cancelPopupButton = (TextView) picker.findViewById(R.id.cancelPopupButton);

        changeTitleView.setText("Confirmation");
        titleDescView.setText("Do you really want to logout ?");
        confirmPopupButton.setText("Logout");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });

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

                try{
                    constants.ClearLogoutData(getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globally.setLoadId("", getApplicationContext());
                Globally.setDriverId( "", getApplicationContext());
                Globally.setPassword( "", getApplicationContext());
                Globally.setUserName( "", getApplicationContext());
                picker.dismiss();

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });


        picker.show();
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = fragManager.getBackStackEntryCount() ;
            if ( count > 1) {
                if(constants.isHomeFragment){
                    finish();
                }else{
                    if(constants.IsFragmentDetail) {
                        getSupportFragmentManager().popBackStack();
                    }else{
                        hiddenBackClickBtn.performClick();
                    }
                }
            } else {
                finish();
            }

        }




    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.navHeaderLay:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                loadFragment(profileFragment, "profile");
                break;

            case R.id.hiddenBackClickBtn:
                loadFragment(tripFragment, "trips");
                break;
        }
    }
}

