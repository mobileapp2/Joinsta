package in.oriange.joinsta.activities;

import android.content.Context;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.BotNavViewPagerAdapter;
import in.oriange.joinsta.utilities.Utilities;

public class MainDrawer_Activity extends AppCompatActivity {

    private Context context;
    private AHBottomNavigation bottomNavigation;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        init();
        getSessionData();
        setEventHandler();
        setUpBottomNavigation();

        if (Utilities.isNetworkAvailable(context)) {

        } else {
            SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            alertDialog.setCancelable(false);
            alertDialog.setTitleText("Oops...");
            alertDialog.setContentText("Please check your internet connection");
            alertDialog.show();
        }


    }

    private void init() {
        context = MainDrawer_Activity.this;
        bottomNavigation = findViewById(R.id.bottom_navigation);
        view_pager = findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
    }

    private void getSessionData() {

    }

    private void setEventHandler() {

    }

    private void setUpBottomNavigation() {
        // Create items

        AHBottomNavigationItem botHome = new AHBottomNavigationItem("HOME", R.drawable.icon_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem botSearch = new AHBottomNavigationItem("SEARCH", R.drawable.icon_search, R.color.colorPrimaryDark);
        AHBottomNavigationItem botFavourite = new AHBottomNavigationItem("FAVOURITE", R.drawable.icon_favourite, R.color.colorPrimaryDark);
        AHBottomNavigationItem botRequest = new AHBottomNavigationItem("REQUEST", R.drawable.icon_request, R.color.colorPrimaryDark);
        AHBottomNavigationItem botProfile = new AHBottomNavigationItem("PROFILE", R.drawable.icon_profile, R.color.colorPrimaryDark);

        // Add items

        bottomNavigation.addItem(botHome);
        bottomNavigation.addItem(botSearch);
        bottomNavigation.addItem(botFavourite);
        bottomNavigation.addItem(botRequest);
        bottomNavigation.addItem(botProfile);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#424242"));
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
    }
}
