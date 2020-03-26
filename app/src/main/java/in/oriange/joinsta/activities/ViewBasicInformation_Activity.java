package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.JOINSTA_PLAYSTORELINK;

public class ViewBasicInformation_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView imv_user;
    private MaterialEditText edt_fname, edt_mname, edt_lname, edt_bloodgroup, edt_education,
            edt_specify, edt_mobile, edt_landline, edt_email, edt_nativeplace, edt_reg_mobile, edt_about, edt_referral_code;
    private TextView tv_verify, tv_verified, tv_countrycode_mobile, tv_countrycode_landline;
    private RadioButton rb_male, rb_female;
    private LinearLayout ll_mobile, ll_landline, ll_email;
    private ImageButton imv_share;

    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList;

    private String userId, imageUrl = "", countryCode, genderId;

    private JSONArray mobileJsonArray, landlineJsonArray, emailJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_basic_information);

        init();
        setDefault();
        setEventHandlers();
        setUpToolbar();
    }

    private void init() {
        context = ViewBasicInformation_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        edt_fname = findViewById(R.id.edt_fname);
        edt_mname = findViewById(R.id.edt_mname);
        edt_lname = findViewById(R.id.edt_lname);
        edt_bloodgroup = findViewById(R.id.edt_bloodgroup);
        edt_education = findViewById(R.id.edt_education);
        edt_specify = findViewById(R.id.edt_specify);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_reg_mobile = findViewById(R.id.edt_reg_mobile);
        edt_nativeplace = findViewById(R.id.edt_nativeplace);
        edt_about = findViewById(R.id.edt_about);
        edt_referral_code = findViewById(R.id.edt_referral_code);
        imv_share = findViewById(R.id.imv_share);

        tv_verify = findViewById(R.id.tv_verify);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        tv_countrycode_landline = findViewById(R.id.tv_countrycode_landline);
        tv_verified = findViewById(R.id.tv_verified);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

    }

    private void setDefault() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new RefreshSession().execute(userId);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            getSessionDetails();
        }
    }

    private void getSessionDetails() {
        ArrayList<LinearLayout> mobileLayoutsList = new ArrayList<>();
        ArrayList<LinearLayout> landlineLayoutsList = new ArrayList<>();
        ArrayList<LinearLayout> emailLayoutsList = new ArrayList<>();

        ll_mobile.removeAllViews();
        ll_landline.removeAllViews();
        ll_email.removeAllViews();

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            edt_bloodgroup.setText(json.getString("blood_group_description"));
            edt_education.setText(json.getString("education_description"));
            edt_specify.setText(json.getString("specific_education"));
            edt_nativeplace.setText(json.getString("native_place"));
            edt_fname.setText(json.getString("first_name"));
            edt_mname.setText(json.getString("middle_name"));
            edt_lname.setText(json.getString("last_name"));
            imageUrl = json.getString("image_url");
            genderId = json.getString("gender_id");

            try {
                edt_about.setText(json.getString("about"));

                if (edt_about.getText().toString().trim().equals("null")) {
                    edt_about.setText("");
                }
            } catch (Exception e) {
                edt_about.setText("");
            }

            try {
                countryCode = json.getString("country_code");
            } catch (Exception e) {
                countryCode = "91";
            }

            try {
                String referral_code;
                referral_code = json.getString("referral_code");
                edt_referral_code.setText(referral_code);
            } catch (Exception e) {
                edt_referral_code.setText("");
            }

            edt_reg_mobile.setText("+" + countryCode + " " + json.getString("mobile"));

            if (edt_bloodgroup.getText().toString().trim().equals("null"))
                edt_bloodgroup.setText("");

            if (edt_education.getText().toString().trim().equals("null"))
                edt_education.setText("");

            if (!imageUrl.equals("")) {
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_userphoto)
                        .into(imv_user);
            }

            if (genderId.equals("1")) {
                rb_male.setChecked(true);
            } else if (genderId.equals("2")) {
                rb_female.setChecked(true);
            }

            try {
                mobileJsonArray = new JSONArray(json.getString("mobile_numbers"));
            } catch (Exception e) {
                mobileJsonArray = new JSONArray();
            }

            try {
                landlineJsonArray = new JSONArray(json.getString("landline_numbers"));
            } catch (Exception e) {
                landlineJsonArray = new JSONArray();
            }

            try {
                emailJsonArray = new JSONArray(json.getString("email"));
            } catch (Exception e) {
                emailJsonArray = new JSONArray();
            }

            if (mobileJsonArray != null)
                if (mobileJsonArray.length() > 0) {
                    for (int i = 0; i < mobileJsonArray.length(); i++) {

                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_view_mobile, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            mobileLayoutsList.add(ll);
                            ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                            if (mobileJsonArray.getJSONObject(i).getString("mobile").length() > 10) {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileJsonArray.getJSONObject(i).getString("mobile").substring(mobileJsonArray.getJSONObject(i).getString("mobile").length() - 10));
                                String code = mobileJsonArray.getJSONObject(i).getString("mobile").substring(0, mobileJsonArray.getJSONObject(i).getString("mobile").length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(code);
                            } else {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileJsonArray.getJSONObject(i).getString("mobile"));
                                ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText("+91");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            if (landlineJsonArray != null)
                if (landlineJsonArray.length() > 0) {
                    for (int i = 0; i < landlineJsonArray.length(); i++) {
                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_view_landline, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            landlineLayoutsList.add(ll);
                            ll_landline.addView(rowView, ll_landline.getChildCount() - 1);

                            if (landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", "").length() > 10) {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", "").substring(landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", "").length() - 10));
                                String code = landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", "").substring(0, landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", "").length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(code);
                            } else {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineJsonArray.getJSONObject(i).getString("landline_number").replace("-", ""));
                                ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText("+91");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            if (emailJsonArray != null)
                if (emailJsonArray.length() > 0) {
                    for (int i = 0; i < emailJsonArray.length(); i++) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.layout_view_email, null);
                        emailLayoutsList.add((LinearLayout) rowView);
                        ll_email.addView(rowView, ll_email.getChildCount());
                        ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setText(emailJsonArray.getJSONObject(i).getString("email"));
                        if (emailJsonArray.getJSONObject(i).getString("email_verification").equals("0")) {
                            (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.VISIBLE);
                            (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.GONE);
                        } else {
                            (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.VISIBLE);
                            (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.GONE);
                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new RefreshSession().execute(userId);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_referral_code.getText().toString().trim().isEmpty()) {
                    String salutation = "";
                    if (genderId.equals("1")) {
                        salutation = "Mr. ";
                    } else if (genderId.equals("2")) {
                        salutation = "Ms. ";
                    }

                    String shareMessage = "Welcome to Joinsta\n\n" +
                            "Connect with businesses, employees and professionals all over the world to collaborate and grow together.\n" +
                            "Enter referral code of " + salutation + edt_fname.getText().toString().trim() + " - " + edt_referral_code.getText().toString().trim() + "\n" +
                            "Below is the link to download the app.\n" +
                            "Google play store: " + JOINSTA_PLAYSTORELINK + "\n\n" +
                            "Joinsta - Team";

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
                }
            }
        });

        tv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", "SendVerificationLink");
                obj.addProperty("email_id", edt_email.getText().toString().trim());
                obj.addProperty("user_id", userId);

                if (Utilities.isNetworkAvailable(context)) {
                    new SendVerificationLink().execute(obj.toString());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });
    }

    public void verifyEmail(View view) {
        LinearLayout ll_email = (LinearLayout) view.getParent();
        MaterialEditText edt_email = ll_email.findViewById(R.id.edt_email);

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "SendVerificationLink");
        obj.addProperty("email_id", edt_email.getText().toString().trim());
        obj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new SendVerificationLink().execute(obj.toString());
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class SendVerificationLink extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.EMAILVERIFYAPI, params[0]);
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
                        Utilities.showAlertDialog(context, "Verification link is sent on your email, " +
                                "when you have successfully verified your email through that link please kindly swipe down " +
                                "to refresh your email verification status", true);
                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RefreshSession extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getUserDetails");
            obj.addProperty("user_id", userId);
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
                    if (type.equalsIgnoreCase("success")) {

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());
                            }
                        }
                        getSessionDetails();
                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_edit_delete, menu);

        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            startActivity(new Intent(context, EditBasicInformation_Activity.class));
            finish();
        }
        return true;
    }

}
