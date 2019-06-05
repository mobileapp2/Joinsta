package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

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

        context = MainDrawer_Activity.this;

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
                    startActivity(new Intent(context, MainDrawer_Activity.class));
                    finish();
                }
            });

            alertD.show();
            return;
        }


        init();
        getSessionDetails();
        setEventHandler();
        setUpBottomNavigation();


    }

    private void init() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        view_pager = findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
    }

    private void getSessionDetails() {

    }

    private void setEventHandler() {

    }

    private void setUpBottomNavigation() {
        // Create items

        AHBottomNavigationItem botHome = new AHBottomNavigationItem("HOME", R.drawable.icon_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem botSearch = new AHBottomNavigationItem("SEARCH", R.drawable.icon_search, R.color.colorPrimaryDark);
        AHBottomNavigationItem botFavourite = new AHBottomNavigationItem("FAVOURITE", R.drawable.icon_favourite, R.color.colorPrimaryDark);
        AHBottomNavigationItem botRequest = new AHBottomNavigationItem("REQUIREMENTS", R.drawable.icon_request, R.color.colorPrimaryDark);
        AHBottomNavigationItem botProfile = new AHBottomNavigationItem("PROFILE", R.drawable.icon_profile, R.color.colorPrimaryDark);

        // Add items

        bottomNavigation.addItem(botHome);
        bottomNavigation.addItem(botSearch);
        bottomNavigation.addItem(botFavourite);
        bottomNavigation.addItem(botRequest);
        bottomNavigation.addItem(botProfile);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));
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
