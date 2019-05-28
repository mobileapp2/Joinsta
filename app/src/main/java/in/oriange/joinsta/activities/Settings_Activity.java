package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.UserSessionManager;

public class Settings_Activity extends AppCompatActivity {

    private Context context;
    private CardView cv_logout;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = Settings_Activity.this;
        session = new UserSessionManager(context);
        cv_logout = findViewById(R.id.cv_logout);
    }

    private void setDefault() {
    }

    private void getSessionData() {
    }

    private void setEventHandler() {
        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
