package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.goodiebag.pinview.Pinview;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.PermissionUtil;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class Login_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private LinearLayout ll_password, ll_otp, ll_loginwithpwd, ll_loginwithotp;
    private View v_password, v_otp;
    private MaterialEditText edt_username, edt_password, edt_mobile;
    private Button btn_login, btn_register, btn_sendotp;

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
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        btn_login = findViewById(R.id.btn_login);
        btn_sendotp = findViewById(R.id.btn_sendotp);
        btn_register = findViewById(R.id.btn_register);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        edt_mobile = findViewById(R.id.edt_mobile);
        ll_password = findViewById(R.id.ll_password);
        ll_otp = findViewById(R.id.ll_otp);
        ll_loginwithpwd = findViewById(R.id.ll_loginwithpwd);
        ll_loginwithotp = findViewById(R.id.ll_loginwithotp);
        v_password = findViewById(R.id.v_password);
        v_otp = findViewById(R.id.v_otp);
    }

    private void setDefault() {
        checkPermissions();
    }

    private void setEventHandlers() {
        ll_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v_password.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                v_otp.setBackgroundColor(getResources().getColor(R.color.white));
                ll_loginwithpwd.setVisibility(View.VISIBLE);
                ll_loginwithotp.setVisibility(View.GONE);
            }
        });

        ll_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v_password.setBackgroundColor(getResources().getColor(R.color.white));
                v_otp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ll_loginwithpwd.setVisibility(View.GONE);
                ll_loginwithotp.setVisibility(View.VISIBLE);

            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        btn_sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                    edt_mobile.setError("Please enter valid mobile number");
                    edt_mobile.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new SendOTP().execute(edt_mobile.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Register_Activity.class));
            }
        });

        edt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_mobile.getText().toString().trim().equals("")) {
                    return;
                }

                if (edt_mobile.getText().toString().trim().length() == 10) {
                    if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                        edt_mobile.setError("Please enter valid mobile number");
                        edt_mobile.requestFocus();
                        return;
                    }

                    if (Utilities.isNetworkAvailable(context)) {
                        new VerifyMobile().execute(edt_mobile.getText().toString().trim());
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void submitData() {
        if (edt_username.getText().toString().isEmpty()) {
            edt_username.setError("Please enter your username");
            return;
        }

        if (edt_password.getText().toString().isEmpty()) {
            edt_password.setError("Please enter your password");
            return;
        }

        if (Utilities.isNetworkAvailable(context)) {
            new LoginUser().execute(edt_username.getText().toString().trim(), edt_password.getText().toString().trim());
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void createDialogForOTP(final String otp) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_otp, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        Pinview pinview_opt = promptView.findViewById(R.id.pinview_opt);
        pinview_opt.setPinLength(otp.length());

        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        pinview_opt.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                if (pinview.getValue().length() == otp.length()) {
                    if (pinview.getValue().equals(otp)) {
                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();
                            new LoginUserWithOtp().execute(edt_mobile.getText().toString().trim());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        Utilities.showMessage("OTP did not match", context, 3);
                    }
                }
            }
        });
        alertD.show();
    }

    private class VerifyMobile extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("mobile", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.LOGINAPI, param);
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
                        JSONObject resultObj = mainObj.getJSONObject("result");

                        String is_registered = resultObj.getString("is_registered");

                        if (is_registered.equals("-1")) {
                            Utilities.showAlertDialog(context, "Mobile number is not registered", false);
                            edt_mobile.setText("");
                        }

                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Failed to verify. Please try again", false);
                        edt_mobile.setText("");
                    }

                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                edt_mobile.setText("");
                e.printStackTrace();
            }
        }
    }

    private class SendOTP extends AsyncTask<String, Void, String> {


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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "send"));
            param.add(new ParamsPojo("mobile", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.OTPAPI, param);
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
                        String OTP = mainObj.getString("otp");
                        createDialogForOTP(OTP);
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Failed to send otp. Please try again", false);
                    }

                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                e.printStackTrace();
            }
        }
    }

    private class LoginUserWithOtp extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("logintype", "loginwithotp")
                    .add("mobile", params[0])
                    .add("is_registered", "1")
                    .build();
            Request request = new Request.Builder()
                    .url(ApplicationConstants.LOGINAPI)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());
                                startActivity(new Intent(context, MainDrawer_Activity.class));
                                if (session.isLocationSet())
                                    startActivity(new Intent(context, MainDrawer_Activity.class));
                                else
                                    startActivity(new Intent(context, SelectLocation_Activity.class));
                                finish();
                            }
                        }
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LoginUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("logintype", "loginwithpassword")
                    .add("mobile", params[0])
                    .add("password", params[1])
                    .add("is_registered", "1")
                    .build();
            Request request = new Request.Builder()
                    .url(ApplicationConstants.LOGINAPI)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            session.createUserLoginSession(jsonarr.toString());
                            startActivity(new Intent(context, MainDrawer_Activity.class));
                            if (session.isLocationSet())
                                startActivity(new Intent(context, MainDrawer_Activity.class));
                            else
                                startActivity(new Intent(context, SelectLocation_Activity.class));

                            finish();
                        }
                    } else {
                        Utilities.showMessage("Username or password is invalid", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void checkPermissions() {
        if (!PermissionUtil.askPermissions(this)) {
            // permision not required or already given
//            startService(new Intent(context, ChecklistSyncServiceHLL.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();

                    for (int i = 0; i < permissions.length; ++i) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if (size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while (i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if (!isPermissionGranted) {
                            new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                                    .setTitle("Permissions mandatory")
                                    .setMessage("All the permissions are required for this app")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkPermissions();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Login_Activity.this);
    }

}
