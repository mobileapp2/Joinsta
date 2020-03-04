package in.oriange.joinsta.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.yyyyMMddDate;

public class OfflinePayment_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_transaction_id, edt_date, edt_mobile, edt_quantity, edt_amount;
    private CheckBox cb_is_early_bird_availed;
    private Button btn_save;

    private String userId, date, eventId;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_payment);


        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = OfflinePayment_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_transaction_id = findViewById(R.id.edt_transaction_id);
        edt_date = findViewById(R.id.edt_date);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_quantity = findViewById(R.id.edt_quantity);
        edt_amount = findViewById(R.id.edt_amount);
        cb_is_early_bird_availed = findViewById(R.id.cb_is_early_bird_availed);
        btn_save = findViewById(R.id.btn_save);
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

        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);
    }

    private void setEventHandler() {
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", date));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {
                    dialog.getDatePicker().setCalendarViewShown(false);
                    dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

    }

    private void submitData() {
        if (edt_transaction_id.getText().toString().trim().isEmpty()) {
            edt_transaction_id.setError("Please enter Receipt No./Transaction No.");
            edt_transaction_id.requestFocus();
            return;
        }

        if (edt_date.getText().toString().trim().isEmpty()) {
            edt_date.setError("Please enter Payment/Transaction Date");
            edt_date.requestFocus();
            return;
        }

        if (!edt_mobile.getText().toString().trim().isEmpty()) {
            if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                edt_mobile.setError("Please enter valid mobile number");
                edt_mobile.requestFocus();
                return;
            }
        }

        if (edt_quantity.getText().toString().trim().isEmpty() ||
                (Integer.parseInt(edt_quantity.getText().toString().trim()) == 0)) {
            edt_quantity.setError("Please enter quantity");
            edt_quantity.requestFocus();
            return;
        }


        if (edt_amount.getText().toString().trim().isEmpty() ||
                (Integer.parseInt(edt_amount.getText().toString().trim()) == 0)) {
            edt_amount.setError("Please enter amount");
            edt_amount.requestFocus();
            return;
        }

        String isEarlyBirdAvailed = cb_is_early_bird_availed.isChecked() ? "1" : "0";

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addEventPaymentDetails");
        mainObj.addProperty("payment_mode", "offline");
        mainObj.addProperty("order_status", "success");
        mainObj.addProperty("order_gateway", "");
        mainObj.addProperty("gateway_configuration_id", "0");
        mainObj.addProperty("transaction_id", edt_transaction_id.getText().toString().trim());
        mainObj.addProperty("transaction_date", date);
        mainObj.addProperty("paid_to", edt_mobile.getText().toString().trim());
        mainObj.addProperty("event_id", eventId);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("created_by", userId);
        mainObj.addProperty("quantity", edt_quantity.getText().toString().trim());
        mainObj.addProperty("amount", edt_amount.getText().toString().trim());
        mainObj.addProperty("is_early_bird_availed", isEarlyBirdAvailed);

        if (Utilities.isNetworkAvailable(context)) {
            new AddEventPaymentDetails().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }


    private class AddEventPaymentDetails extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.PAYMENTTRACKAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Offline payment status saved successfully");
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
                        Utilities.showMessage("Failed to submit the details", context, 3);
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
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
