package in.oriange.joinsta.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    private AHBottomNavigationItem botNavClent, botNavPolicy, botNavCalendar, botNavReminder, botFilter;
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
//            new AwesomeErrorDialog(this)
//                    .setTitle(R.string.msgt_nointernetconnection)
//                    .setMessage("")
//                    .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                    .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
//                    .setCancelable(true).setButtonText("Retry")
//                    .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                    .setButtonText(getString(R.string.dialog_ok_button))
//                    .setCancelable(false)
//                    .setErrorButtonClick(new Closure() {
//                        @Override
//                        public void exec() {
//                            startActivity(new Intent(context, MainDrawer_Activity.class));
//                            finish();
//                        }
//                    })
//                    .show();

            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Oops...");
            sweetAlertDialog.setContentText("Please check your internet connection");
            sweetAlertDialog.setConfirmText("RETRY");
            sweetAlertDialog.setConfirmButton("RETRY", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    startActivity(new Intent(context, MainDrawer_Activity.class));
                    finish();
                }
            });
            sweetAlertDialog.show();
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

    }
}
