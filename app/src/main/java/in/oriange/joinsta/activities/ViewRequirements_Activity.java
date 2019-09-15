package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Request_Fragment;
import in.oriange.joinsta.models.MainCategoryListModel;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.pojos.MainCategoryListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class ViewRequirements_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private CheckBox cb_like;
    private CircleImageView imv_user;
    private ImageView imv_mobile, imv_email;
    private TextView tv_reqby_name;
    private TextView tv_categoryname, tv_reqmtitle, tv_reqmdesc, tv_city;

    private RequirementsListModel reqDetails;
    private String userId, isFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requirements);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewRequirements_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        cb_like = findViewById(R.id.cb_like);
        imv_user = findViewById(R.id.imv_user);
        imv_mobile = findViewById(R.id.imv_mobile);
        imv_email = findViewById(R.id.imv_email);
        tv_reqby_name = findViewById(R.id.tv_reqby_name);
        tv_categoryname = findViewById(R.id.tv_categoryname);
        tv_reqmtitle = findViewById(R.id.tv_reqmtitle);
        tv_reqmdesc = findViewById(R.id.tv_reqmdesc);
        tv_city = findViewById(R.id.tv_city);
    }

    private void setDefault() {
        reqDetails = (RequirementsListModel) getIntent().getSerializableExtra("reqDetails");


        if (!reqDetails.getImageURL().trim().isEmpty()) {
            Picasso.with(context)
                    .load(reqDetails.getImageURL().trim())
                    .placeholder(R.drawable.icon_userphoto)
                    .into(imv_user, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (reqDetails.getIsStarred().equals("1"))
            cb_like.setChecked(true);


        PrettyTime p = new PrettyTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        tv_reqby_name.setText(reqDetails.getFname() + " " + reqDetails.getMname() + " " + reqDetails.getLname());
        tv_reqmtitle.setText(reqDetails.getTitle());
        tv_reqmdesc.setText(reqDetails.getDescription());
        try {
            tv_city.setText(p.format(formatter.parse(reqDetails.getUpdated_at())) + " | " + reqDetails.getCity());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetMainCategotyList().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
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

    private void setEventHandler() {
        cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFav = reqDetails.getIsStarred();

                if (cb_like.isChecked())
                    isFav = "1";
                else
                    isFav = "0";

                JsonObject mainObj = new JsonObject();

                mainObj.addProperty("type", "starrequirement");
                mainObj.addProperty("IsStarred", isFav);
                mainObj.addProperty("StarredBy", userId);
                mainObj.addProperty("reqId", reqDetails.getId());

                if (Utilities.isNetworkAvailable(context)) {
                    new StarRequirement().execute(mainObj.toString());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });


        imv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    provideCallPremission(context);
                    return;
                }

                if (reqDetails.getMobileNumber().trim().equals("")) {
                    Utilities.showMessage("Mobile number not available", context, 2);
                    return;
                }

                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + reqDetails.getMobileNumber())));
            }
        });

        imv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reqDetails.getEmail().isEmpty()) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{reqDetails.getEmail()});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } else {
                    Utilities.showMessage("Email address not added", context, 2);
                }
            }
        });
    }

    private class GetMainCategotyList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getcategorytypes");
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYTYPEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ArrayList<MainCategoryListModel> mainCategoryList = new ArrayList<>();
                    MainCategoryListPojo pojoDetails = new Gson().fromJson(result, MainCategoryListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        mainCategoryList = pojoDetails.getResult();
                        if (mainCategoryList.size() > 0) {

                            for (MainCategoryListModel catDetails : mainCategoryList) {
                                if (catDetails.getId().equals(reqDetails.getCategory_type_id())) {
                                    tv_categoryname.setText(catDetails.getType_description());
                                }
                            }

                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private class StarRequirement extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.REQUIREMENTAPI, params[0]);
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
                        new Request_Fragment.GetRequirementList().execute();
                    } else {
                        cb_like.setChecked(false);
                    }
                }
            } catch (Exception e) {
                cb_like.setChecked(false);
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
