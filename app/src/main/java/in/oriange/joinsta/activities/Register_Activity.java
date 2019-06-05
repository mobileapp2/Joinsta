package in.oriange.joinsta.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.goodiebag.pinview.Pinview;
import com.google.gson.JsonObject;
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
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class Register_Activity extends AppCompatActivity {

    private Context context;
    ProgressDialog pd;
    private MaterialEditText edt_name, edt_mobile, edt_password;
    private Button btn_register;
    private UserSessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = Register_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        btn_register = findViewById(R.id.btn_register);
    }

    private void setDefault() {

    }

    private void setEventHandler() {
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


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError("Please enter name");
                    return;
                }

                if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                    edt_mobile.setError("Please enter valid mobile number");
                    edt_mobile.requestFocus();
                    return;
                }

                if (edt_password.getText().toString().trim().isEmpty()) {
                    edt_password.setError("Please enter password");
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new SendOTP().execute(edt_mobile.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });
    }

    public class SendOTP extends AsyncTask<String, Void, String> {


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
                            new RegisterUser().execute(
                                    edt_name.getText().toString().trim(),
                                    edt_mobile.getText().toString().trim(),
                                    edt_password.getText().toString().trim());
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

                        if (is_registered.equals("1")) {
                            Utilities.showAlertDialog(context, "Mobile number is already registered", false);
                            edt_mobile.setText("");
                        } else {
                            Utilities.showMessage("You can proceed.", context, 1);
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

    public class RegisterUser extends AsyncTask<String, Void, String> {


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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "registeruser");
            obj.addProperty("name", params[0]);
            obj.addProperty("mobile", params[1]);
            obj.addProperty("password", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
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
                        new LoginUser().execute(edt_mobile.getText().toString().trim(), edt_password.getText().toString().trim());
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Failed to register. Please try again", false);
                    }

                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
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
                obj.put("device_id", params[2]);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Register_Activity.this);
    }
}
