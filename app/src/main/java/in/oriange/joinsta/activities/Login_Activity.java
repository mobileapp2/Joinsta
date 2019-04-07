package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import in.oriange.joinsta.R;

public class Login_Activity extends AppCompatActivity {

    private Context context;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setDefault();
        setEventHandlers();
    }

    private void init() {
        context = Login_Activity.this;

        btn_login = findViewById(R.id.btn_login);
    }

    private void setDefault() {

    }

    private void setEventHandlers() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainDrawer_Activity.class));
            }
        });
    }
}
