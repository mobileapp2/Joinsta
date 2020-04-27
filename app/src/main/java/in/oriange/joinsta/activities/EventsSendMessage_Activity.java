package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

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
    @BindView(R.id.edt_subject)
    MaterialEditText edtSubject;
    @BindView(R.id.cb_canshare)
    CheckBox cbCanshare;
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
                startActivityForResult(new Intent(context, FullScreenTextEdit_Activity.class)
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


        if (rbAll.isChecked()) {
            userType = "all";
        } else if (rbPaid.isChecked()) {
            userType = "paid";
        } else if (rbUnpaid.isChecked()) {
            userType = "unpaid";
        } else if (rbPayAtCounter.isChecked()) {
            userType = "pay_at_counter";
        } else {
            Utilities.showMessage("Please select user type", context, 2);
            return;
        }

        if (edtSubject.getText().toString().trim().isEmpty()) {
            edtSubject.setError("Please enter subject");
            edtSubject.requestFocus();
            return;
        }

        if (edtMessage.getText().toString().trim().isEmpty()) {
            edtMessage.setError("Please enter message");
            edtMessage.requestFocus();
            return;
        }

        String canShare = cbCanshare.isChecked() ? "1" : "0";

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("type", "sendEventNotification");
        mainObject.addProperty("event_id", eventId);
        mainObject.addProperty("subject", edtSubject.getText().toString().trim());
        mainObject.addProperty("message", edtMessage.getText().toString().trim());
        mainObject.addProperty("user_id", userId);
        mainObject.addProperty("user_type", userType);
        mainObject.addProperty("can_share", canShare);

        if (Utilities.isNetworkAvailable(context)) {
            new SendMessage().execute(mainObject.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class SendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.EVENTSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Notification sent successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
