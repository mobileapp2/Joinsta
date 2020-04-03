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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.UserProfileDetailsModel;
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
    UserProfileDetailsModel userDetailsPojo;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList;

    private String userId, imageUrl = "", countryCode, genderId;

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

        UserProfileDetailsModel.ResultBean userDetails = userDetailsPojo.getResult().get(0);

        userId = userDetails.getUserid();
        imageUrl = userDetails.getImage_url();
        genderId = userDetails.getGender_id();
        edt_bloodgroup.setText(userDetails.getBlood_group_description());
        edt_education.setText(userDetails.getEducation_description());
        edt_specify.setText(userDetails.getSpecific_education());
        edt_nativeplace.setText(userDetails.getNative_place());
        edt_fname.setText(userDetails.getFirst_name());
        edt_mname.setText(userDetails.getMiddle_name());
        edt_lname.setText(userDetails.getLast_name());
        edt_about.setText(userDetails.getAbout());
        edt_reg_mobile.setText(userDetails.getCountry_code() + userDetails.getMobile());
        edt_referral_code.setText(userDetails.getReferral_code());

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

        List<UserProfileDetailsModel.ResultBean.MobileNumbersBean> mobileList = userDetails.getMobile_numbers();
        List<UserProfileDetailsModel.ResultBean.LandlineNumbersBean> landlineList = userDetails.getLandline_numbers();
        List<UserProfileDetailsModel.ResultBean.EmailBean> emailList = userDetails.getEmail();

        if (mobileList != null)
            if (mobileList.size() > 0) {
                for (int i = 0; i < mobileList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_view_mobile, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    mobileLayoutsList.add(ll);
                    ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);

                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileList.get(i).getMobile());
                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobileList.get(i).getCountry_code());
                }
            }

        if (landlineList != null)
            if (landlineList.size() > 0) {
                for (int i = 0; i < landlineList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_view_landline, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    landlineLayoutsList.add(ll);
                    ll_landline.addView(rowView, ll_landline.getChildCount() - 1);

                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number());
                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineList.get(i).getCountry_code());
                }
            }

        if (emailList != null)
            if (emailList.size() > 0) {
                for (int i = 0; i < emailList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_view_email, null);
                    emailLayoutsList.add((LinearLayout) rowView);
                    ll_email.addView(rowView, ll_email.getChildCount());
                    ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setText(emailList.get(i).getEmail());
                    if (emailList.get(i).getEmail_verification().equals("0")) {
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.VISIBLE);
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.GONE);
                    } else {
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.VISIBLE);
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.GONE);
                    }
                }
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

                    userDetailsPojo = new Gson().fromJson(result, UserProfileDetailsModel.class);
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
