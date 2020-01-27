package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.oriange.joinsta.R;

public class ViewEventsFree_Activity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_free);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewEventsFree_Activity.this;
    }

    private void getSessionDetails() {

    }

    private void setDefault() {

    }

    private void setEventHandler() {

    }

    private void setUpToolbar() {

    }
}
