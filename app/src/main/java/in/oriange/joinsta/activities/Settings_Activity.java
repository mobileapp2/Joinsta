package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.JOINSTA_PLAYSTORELINK;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class Settings_Activity extends AppCompatActivity {

    private Context context;
    private ProgressDialog pd;
    private CardView cv_logout, cv_feedback, cv_invite, cv_password, cv_report_issue;
    private UserSessionManager session;
    private String userId, password, mobile, referral_code, country_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = Settings_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        cv_password = findViewById(R.id.cv_password);
        cv_invite = findViewById(R.id.cv_invite);
        cv_feedback = findViewById(R.id.cv_feedback);
        cv_logout = findViewById(R.id.cv_logout);
        cv_report_issue = findViewById(R.id.cv_report_issue);
    }

    private void setDefault() {
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            password = json.getString("password");
            mobile = json.getString("mobile");
            referral_code = json.getString("referral_code");
            country_code = json.getString("country_code");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        cv_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordAlert();
            }
        });

        cv_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareMessage;
                if (!referral_code.trim().equals("")) {
                    shareMessage = "Welcome to Joinsta\n\n" +
                            "Connect with businesses, employees and professionals all over the world to collaborate and grow together.\n" +
                            "Enter my referral code - " + referral_code + "\n" +
                            "Below is the link to download the app.\n" +
                            "Google play store: " + JOINSTA_PLAYSTORELINK + "\n\n" +
                            "Joinsta - Team";
                } else {
                    shareMessage = "Welcome to Joinsta\n\n" +
                            "Connect with businesses, employees and professionals all over the world to collaborate and grow together.\n" +
                            "Below is the link to download the app.\n" +
                            "Google play store: " + JOINSTA_PLAYSTORELINK + "\n\n" +
                            "Joinsta - Team";
                }

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
            }
        });

        cv_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, UserFeedback_Activity.class));
            }
        });

        cv_report_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ReportIssue_Activity.class));
            }
        });

        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

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

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edt_oldpassword.getText().toString().trim().equals(password)) {
                    edt_oldpassword.setError("Please enter correct old password");
                    edt_oldpassword.requestFocus();
                    return;
                }

                if (edt_newpassword.getText().toString().trim().isEmpty()) {
                    edt_newpassword.setError("Please enter new password");
                    edt_newpassword.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    alertD.dismiss();
                    new ChangePassword().execute(
                            edt_oldpassword.getText().toString().trim(),
                            edt_newpassword.getText().toString().trim()
                    );
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        alertD.show();

    }

    private class ChangePassword extends AsyncTask<String, Void, String> {

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
                obj.put("userId", userId);
                obj.put("mobile", mobile);
                obj.put("country_code", country_code);
                obj.put("oldpassword", params[0]);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Settings_Activity.this);
    }


}
