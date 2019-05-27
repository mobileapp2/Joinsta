package in.oriange.joinsta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.utilities.UserSessionManager;

public class ViewRequirements_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;


    private RequirementsListModel reqDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requirements);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewRequirements_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
    }

    private void setDefault() {
        reqDetails = (RequirementsListModel) getIntent().getSerializableExtra("reqDetails");
    }

    private void getSessionData() {

    }

    private void setEventHandler() {

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
