package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.PermissionUtil;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Login_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private LinearLayout ll_password, ll_otp, ll_loginwithpwd, ll_loginwithotp;
    private View v_password, v_otp;
    private MaterialEditText edt_username, edt_password;
    private Button btn_login, btn_register;

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
        btn_register = findViewById(R.id.btn_register);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
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

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Register_Activity.class));
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
            JsonObject obj = new JsonObject();
            obj.addProperty("logintype", "loginwithpassword");
            obj.addProperty("mobile", params[0]);
            obj.addProperty("password", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.LOGINAPI, obj.toString());
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
}
