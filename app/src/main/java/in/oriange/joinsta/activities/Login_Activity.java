package in.oriange.joinsta.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.goodiebag.pinview.Pinview;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
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
    private TextView tv_forgotpass;
    private MaterialEditText edt_username, edt_password, edt_mobile;
    private Button btn_login, btn_register, btn_sendotp;
    private String MOBILE;

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
        tv_forgotpass = findViewById(R.id.tv_forgotpass);
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
                    new SendOTP().execute(edt_mobile.getText().toString().trim(), "1");
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

        tv_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMobileForPassword();
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

    private void createDialogForOTP(final String otp, final String TYPE) {
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
                        alertD.dismiss();
                        if (TYPE.equals("1")) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new LoginUserWithOtp().execute(edt_mobile.getText().toString().trim());
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        } else if (TYPE.equals("2")) {
                            changePasswordAlert();
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
                        } else {
                            Utilities.showMessage("Mobile number is verified. You can proceed.", context, 1);
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
        private String TYPE = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            TYPE = params[1];
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
                        createDialogForOTP(OTP, TYPE);
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
                                saveRegistrationID();
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
                            saveRegistrationID();
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

    private void createMobileForPassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_change_mobile, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Enter Mobile");
        alertDialogBuilder.setView(promptView);

        final MaterialEditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
        final Button btn_save = promptView.findViewById(R.id.btn_save);

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                    edt_mobile.setError("Please enter valid mobile number");
                    edt_mobile.requestFocus();
                    return;
                }

                MOBILE = edt_mobile.getText().toString().trim();

                if (Utilities.isNetworkAvailable(context)) {
                    alertD.dismiss();
                    new SendOTP().execute(edt_mobile.getText().toString().trim(), "2");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        alertD.show();

    }

    private void changePasswordAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setView(promptView);

        final MaterialEditText edt_oldpassword = promptView.findViewById(R.id.edt_oldpassword);
        final MaterialEditText edt_newpassword = promptView.findViewById(R.id.edt_newpassword);
        final Button btn_save = promptView.findViewById(R.id.btn_save);

        edt_oldpassword.setVisibility(View.GONE);

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_newpassword.getText().toString().trim().isEmpty()) {
                    edt_newpassword.setError("Please enter new password");
                    edt_newpassword.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    alertD.dismiss();
                    new ChangePassword().execute(
                            "",
                            edt_newpassword.getText().toString().trim()
                    );
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        alertD.show();

    }

    public class ChangePassword extends AsyncTask<String, Void, String> {

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
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "changepassword");
                obj.put("userId", "");
                obj.put("mobile", MOBILE);
                obj.put("oldpassword", "");
                obj.put("newpasssword", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
            return res;
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
                        tv_title.setText("Password changed successfully. Please login again with new password.");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                session.logoutUser();
                            }
                        });

                        alertD.show();
                    } else if (type.equalsIgnoreCase("failure")) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveRegistrationID() {
        String user_id = "", regToken = "";
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));

            for (int j = 0; j < user_info.length(); j++) {
                JSONObject json = user_info.getJSONObject(j);
                user_id = json.getString("userid");
            }

            regToken = session.getAndroidToken().get(ApplicationConstants.KEY_ANDROIDTOKETID);

            if (regToken != null && !regToken.isEmpty() && !regToken.equals("null"))
                new SendRegistrationToken().execute(user_id, regToken);
        } catch (Exception e) {
            e.printStackTrace();
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

    public class SendRegistrationToken extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;

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
            String s = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "registerDevice");
                obj.put("device_type", "Android");
                obj.put("ram", totalRAMSize());
                obj.put("processor", Build.CPU_ABI);
                obj.put("device_os", Build.VERSION.RELEASE);
                obj.put("location", "0.0, 0.0");
                obj.put("device_model", Build.MODEL);
                obj.put("manufacturer", Build.MANUFACTURER);
                obj.put("customers_id", params[0]);
                obj.put("device_id", params[1]);
                s = obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = APICall.JSONAPICall(ApplicationConstants.DEVICEREGAPI, s);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result != null && result.length() > 0 && !result.equalsIgnoreCase("[]")) {
                try {
                    int c = 0;
                    JSONObject obj1 = new JSONObject(result);
                    String success = obj1.getString("success");
                    String message = obj1.getString("message");
                    if (success.equalsIgnoreCase("1")) {
                        if (session.isLocationSet())
                            startActivity(new Intent(context, MainDrawer_Activity.class));
                        else
                            startActivity(new Intent(context, SelectLocation_Activity.class));

                        finish();
                    } else {
                        Utilities.showMessage("Please Try After Sometime", context, 3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String totalRAMSize() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        double totalRAM = memoryInfo.totalMem / 1048576.0;
        return String.valueOf(totalRAM);
    }

}
