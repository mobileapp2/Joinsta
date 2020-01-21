package in.oriange.joinsta.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.oriange.joinsta.R;

public class SelectCity_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {

    }

    private void setDefault() {

    }

    private void getSessionDetails() {

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
