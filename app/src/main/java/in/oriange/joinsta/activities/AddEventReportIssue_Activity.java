package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class AddEventReportIssue_Activity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.edt_name)
    MaterialEditText edtName;
    @BindView(R.id.edt_mobile)
    MaterialEditText edtMobile;
    @BindView(R.id.edt_title)
    MaterialEditText edtTitle;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.btn_save)
    Button btnSave;

    private String userId, eventId, eventCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_report_issue);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddEventReportIssue_Activity.this;

        eventId = getIntent().getStringExtra("eventId");
        eventCategoryId = getIntent().getStringExtra("eventCategoryId");
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(new UserSessionManager(context).getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

            edtName.setText(json.getString("first_name"));
            edtMobile.setText(json.getString("mobile"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDate();
            }
        });
    }

    private void submitDate() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter your name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (!Utilities.isValidMobileno(edtMobile.getText().toString().trim())) {
            edtMobile.setError("Please enter valid mobile number");
            edtMobile.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtTitle.getText().toString().trim().isEmpty()) {
            edtTitle.setError("Please enter title");
            edtTitle.requestFocus();
            edtTitle.getParent().requestChildFocus(edtTitle, edtTitle);
            return;
        }

        if (edtDescription.getText().toString().trim().isEmpty()) {
            edtDescription.setError("Please enter description");
            edtDescription.requestFocus();
            edtDescription.getParent().requestChildFocus(edtDescription, edtDescription);
            return;
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addEventReportIssue");
        mainObj.addProperty("name", edtName.getText().toString().trim());
        mainObj.addProperty("mobile", edtMobile.getText().toString().trim());
        mainObj.addProperty("title", edtTitle.getText().toString().trim());
        mainObj.addProperty("description", edtDescription.getText().toString().trim());
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("event_id", eventId);
        mainObj.addProperty("event_category_id", eventCategoryId);


        if (Utilities.isNetworkAvailable(context)) {
            new EventReportIssue().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class EventReportIssue extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = APICall.JSONAPICall(ApplicationConstants.EVENTSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type, message;
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
                        tv_title.setText("Issue submitted successfully");
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
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
