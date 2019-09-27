package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import in.oriange.joinsta.R;

public class Policy_Activity extends AppCompatActivity {

    private Context context;
    private CardView cv_tandc, cv_pp, cv_smp, cv_ipp, cv_g, cv_mad, cv_ua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        init();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = Policy_Activity.this;

        cv_tandc = findViewById(R.id.cv_tandc);
        cv_pp = findViewById(R.id.cv_pp);
        cv_smp = findViewById(R.id.cv_smp);
        cv_ipp = findViewById(R.id.cv_ipp);
        cv_g = findViewById(R.id.cv_g);
        cv_mad = findViewById(R.id.cv_mad);
        cv_ua = findViewById(R.id.cv_ua);
    }

    private void setEventHandler() {

        cv_tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Terms and Conditions")
                        .putExtra("filePath", "termsandconditions.html"));
            }
        });

        cv_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Privacy Policy")
                        .putExtra("filePath", "privacypolicy.html"));
            }
        });

        cv_smp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Social Media Policy")
                        .putExtra("filePath", "socialmedia.html"));
            }
        });

        cv_ipp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "IP Policy")
                        .putExtra("filePath", "ippolicy.html"));
            }
        });

        cv_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Grievance")
                        .putExtra("filePath", "grievance.html"));
            }
        });

        cv_mad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Mobile App Disclaimer")
                        .putExtra("filePath", "appdisclaimer.html"));
            }
        });

        cv_ua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "User Agreement")
                        .putExtra("filePath", "useragreement.html"));
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
