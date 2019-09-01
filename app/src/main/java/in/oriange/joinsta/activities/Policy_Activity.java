package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.oriange.joinsta.R;

public class Policy_Activity extends AppCompatActivity {

    private Context context;
    private LinearLayout ll_tandc, ll_pp, ll_smp, ll_ipp, ll_g, ll_mad, ll_ua;

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

        ll_tandc = findViewById(R.id.ll_tandc);
        ll_pp = findViewById(R.id.ll_pp);
        ll_smp = findViewById(R.id.ll_smp);
        ll_ipp = findViewById(R.id.ll_ipp);
        ll_g = findViewById(R.id.ll_g);
        ll_mad = findViewById(R.id.ll_mad);
        ll_ua = findViewById(R.id.ll_ua);
    }

    private void setEventHandler() {

        ll_tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Terms and Conditions")
                        .putExtra("filePath", "termsandconditions.html"));
            }
        });

        ll_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Privacy Policy")
                        .putExtra("filePath", "privacypolicy.html"));
            }
        });

        ll_smp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Social Media Policy")
                        .putExtra("filePath", "socialmedia.html"));
            }
        });

        ll_ipp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "IP Policy")
                        .putExtra("filePath", "ippolicy.html"));
            }
        });

        ll_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Grievance")
                        .putExtra("filePath", "grievance.html"));
            }
        });

        ll_mad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PolicyDetails_Activity.class)
                        .putExtra("title", "Mobile App Disclaimer")
                        .putExtra("filePath", "appdisclaimer.html"));
            }
        });

        ll_ua.setOnClickListener(new View.OnClickListener() {
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
