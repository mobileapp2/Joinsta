package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;

public class EventsSendMessage_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    @BindView(R.id.rg_to)
    RadioGroup rgTo;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_paid)
    RadioButton rbPaid;
    @BindView(R.id.rb_unpaid)
    RadioButton rbUnpaid;
    @BindView(R.id.rb_pay_at_counter)
    RadioButton rbPayAtCounter;
    @BindView(R.id.edt_message)
    EditText edtMessage;
    @BindView(R.id.btn_save)
    Button btnSave;

    private final int MESSAGE_REQUEST = 300;
    private String userId, eventId, messageForPaid, messageForUnPaid, userType;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_send_message);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EventsSendMessage_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
    }

    private void getSessionDetails() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        eventId = getIntent().getStringExtra("eventId");
        messageForPaid = getIntent().getStringExtra("messageForPaid");
        messageForUnPaid = getIntent().getStringExtra("messageForUnPaid");
        position = getIntent().getIntExtra("position", 0);

        switch (position) {
            case 0:
                userType = "1";
                rbAll.setChecked(true);
                edtMessage.setText("");
                break;
            case 1:
                rbPaid.setChecked(true);
                userType = "2";
                edtMessage.setText(messageForPaid);
                break;
            case 2:
                userType = "3";
                rbUnpaid.setChecked(true);
                edtMessage.setText(messageForUnPaid);
                break;
            case 3:
                userType = "4";
                rbPayAtCounter.setChecked(true);
                edtMessage.setText("");
                break;
        }
    }

    private void setEventHandler() {
        rgTo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        userType = "1";
                        edtMessage.setText("");
                        break;

                    case R.id.rb_paid:
                        userType = "2";
                        edtMessage.setText(messageForPaid);
                        break;

                    case R.id.rb_unpaid:
                        userType = "3";
                        edtMessage.setText(messageForUnPaid);
                        break;

                    case R.id.rb_pay_at_counter:
                        userType = "4";
                        edtMessage.setText("");
                        break;
                }
            }
        });

        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, GroupsSendMessageScroll_Activity.class)
                        .putExtra("message", edtMessage.getText().toString().trim()), MESSAGE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void submitData() {
        if (edtMessage.getText().toString().trim().isEmpty()) {
            edtMessage.setError("Please enter message");
            edtMessage.requestFocus();
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MESSAGE_REQUEST) {
                edtMessage.setText(data.getStringExtra("message"));
            }
        }
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
