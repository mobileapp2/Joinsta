package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.oriange.joinsta.R;

public class GroupsSendMessageScroll_Activity extends AppCompatActivity {

    private Context context;
    private EditText edt_text;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_sendmessage_scroll);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupsSendMessageScroll_Activity.this;
        edt_text = findViewById(R.id.edt_text);
        btn_save = findViewById(R.id.btn_save);
    }

    private void setDefault() {
        edt_text.setText(getIntent().getStringExtra("message"));
    }

    private void setEventHandler() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_text.getText().toString().trim().isEmpty()) {
                    edt_text.setError("Please enter message");
                    return;
                }

                Intent intent = getIntent();
                intent.putExtra("message", edt_text.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
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
