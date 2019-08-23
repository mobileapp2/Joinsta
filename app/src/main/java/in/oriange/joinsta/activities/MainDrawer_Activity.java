package in.oriange.joinsta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.BotNavViewPagerAdapter;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.joinsta.utilities.Utilities.isLocationEnabled;

public class MainDrawer_Activity extends AppCompatActivity {

    private static Context context;
    private AHBottomNavigation bottomNavigation;
    //    private ReadableBottomBar bottomNavigation;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;

    private int startOrigin;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        context = MainDrawer_Activity.this;
        session = new UserSessionManager(context);
        if (!Utilities.isNetworkAvailable(context)) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_layout_error, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setView(promptView);

            LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
            TextView tv_title = promptView.findViewById(R.id.tv_title);
            Button btn_ok = promptView.findViewById(R.id.btn_ok);

            animation_view.playAnimation();
            tv_title.setText("Please check your internet connection");
            btn_ok.setText("Retry");
            alertDialogBuilder.setCancelable(false);
            final AlertDialog alertD = alertDialogBuilder.create();

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertD.dismiss();
                    startActivity(new Intent(context, MainDrawer_Activity.class)
                            .putExtra("startOrigin", 0));
                    finish();
                }
            });

            alertD.show();
            return;
        }


        init();
        setUpBottomNavigation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!session.isLocationSet()) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(MainDrawer_Activity.this), 0);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        }
    }

    private void init() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        view_pager = findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);

        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            return;
        }

        if (!isLocationEnabled(context)) {
            return;
        }

        startLocationUpdates();
        startOrigin = getIntent().getIntExtra("startOrigin", 0);

    }

    private void setUpBottomNavigation() {
        //Create items

        AHBottomNavigationItem botHome = new AHBottomNavigationItem("Home", R.drawable.icon_home_1, R.color.colorPrimaryDark);
        AHBottomNavigationItem botSearch = new AHBottomNavigationItem("Search", R.drawable.icon_search_1, R.color.colorPrimaryDark);
        AHBottomNavigationItem botFavourite = new AHBottomNavigationItem("Favourite", R.drawable.icon_favourite_1, R.color.colorPrimaryDark);
        AHBottomNavigationItem botRequest = new AHBottomNavigationItem("Requirements", R.drawable.icon_request_1, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botProfile = new AHBottomNavigationItem("Profile", R.drawable.icon_profile_1, R.color.colorPrimaryDark);
        AHBottomNavigationItem botProfile = new AHBottomNavigationItem("More", R.drawable.icon_more_1, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botHome = new AHBottomNavigationItem("Home", R.drawable.icon_nav_home, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botSearch = new AHBottomNavigationItem("Search", R.drawable.icon_nav_search, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botFavourite = new AHBottomNavigationItem("Favourite", R.drawable.icon_nav_favourite, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botRequest = new AHBottomNavigationItem("Requirements", R.drawable.icon_nav_request, R.color.colorPrimaryDark);
//        AHBottomNavigationItem botProfile = new AHBottomNavigationItem("Profile", R.drawable.icon_nav_profile, R.color.colorPrimaryDark);

        //Add items

        bottomNavigation.addItem(botHome);
        bottomNavigation.addItem(botSearch);
        bottomNavigation.addItem(botFavourite);
        bottomNavigation.addItem(botRequest);
        bottomNavigation.addItem(botProfile);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#EEEEEE"));
        bottomNavigation.setAccentColor(Color.parseColor("#FFA000"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                view_pager.setCurrentItem(position, true);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                return true;
            }
        });

        bottomNavigation.setCurrentItem(startOrigin);

//        bottomNavigation.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
//            @Override
//            public void onItemSelected(int i) {
//                if (currentFragment == null) {
//                    currentFragment = adapter.getCurrentFragment();
//                }
//
//                view_pager.setCurrentItem(i, true);
//                currentFragment = adapter.getCurrentFragment();
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Place place = PlacePicker.getPlace(this, data);
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                if (addresses.size() != 0) {
                    session.setLocation(addresses.get(0).getLocality());
                    finishAffinity();
                    startActivity(new Intent(context, MainDrawer_Activity.class)
                            .putExtra("startOrigin", 0));


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("Alert");
                    builder.setMessage("City not found, please try again");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                            try {
                                startActivityForResult(builder.build(MainDrawer_Activity.this), 0);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Alert");
                builder.setMessage("City not found, please try again");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                        try {
                            startActivityForResult(builder.build(MainDrawer_Activity.this), 0);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.create();
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }
    }

    private static LocationRequest mLocationRequest;

    private static long UPDATE_INTERVAL = 10 * 10000000;  /* 10 secs */
    private static long FASTEST_INTERVAL = 20000; /* 2 sec */
    public static LatLng latLng;

    @SuppressLint("RestrictedApi")
    public static void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public static void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

}
