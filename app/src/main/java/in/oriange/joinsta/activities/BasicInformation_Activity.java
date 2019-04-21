package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.pojos.MasterPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class BasicInformation_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private MaterialEditText edt_fname, edt_mname, edt_lname, edt_bloodgroup, edt_education,
            edt_specify, edt_mobile, edt_landline, edt_email, edt_nativeplace;
    private RadioButton rb_male, rb_female;
    private LinearLayout ll_mobile, ll_landline, ll_email;
    private ImageButton ib_add_mobile, ib_add_landline, ib_add_email;
    private String bloodGroupId = "1", educationId = "1", referalCode, isActive, imageUrl;
    private ArrayList<MasterModel> bloodGroupList, educationList;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_information);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BasicInformation_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);

        edt_fname = findViewById(R.id.edt_fname);
        edt_mname = findViewById(R.id.edt_mname);
        edt_lname = findViewById(R.id.edt_lname);
        edt_bloodgroup = findViewById(R.id.edt_bloodgroup);
        edt_education = findViewById(R.id.edt_education);
        edt_specify = findViewById(R.id.edt_specify);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_nativeplace = findViewById(R.id.edt_nativeplace);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

        ib_add_mobile = findViewById(R.id.ib_add_mobile);
        ib_add_landline = findViewById(R.id.ib_add_landline);
        ib_add_email = findViewById(R.id.ib_add_email);

        bloodGroupList = new ArrayList<>();
        educationList = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        emailLayoutsList = new ArrayList<>();
    }

    private void setDefault() {
        mobileLayoutsList.add(ll_mobile);
        landlineLayoutsList.add(ll_landline);
        emailLayoutsList.add(ll_email);
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            referalCode = json.getString("referral_code");
            isActive = json.getString("is_active");
            imageUrl = json.getString("image_url");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        edt_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetBloodGroupList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        edt_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetEducationList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        ib_add_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                LinearLayout ll = (LinearLayout) rowView;
                mobileLayoutsList.add(ll);
                ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
            }
        });

        ib_add_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                LinearLayout ll = (LinearLayout) rowView;
                landlineLayoutsList.add(ll);
                ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
            }
        });

        ib_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_email, null);
                LinearLayout ll = (LinearLayout) rowView;
                emailLayoutsList.add(ll);
                ll_email.addView(rowView, ll_email.getChildCount() - 1);
            }
        });
    }

    private class GetBloodGroupList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllBloodGroup"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        bloodGroupList = pojoDetails.getResult();
                        if (bloodGroupList.size() > 0) {
                            showBloodGroupListDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Fail", message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Please Try Again", "Server Not Responding", false);
            }
        }
    }

    private void showBloodGroupListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Blood Group");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < bloodGroupList.size(); i++) {

            arrayAdapter.add(String.valueOf(bloodGroupList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_bloodgroup.setText(bloodGroupList.get(which).getName());
                bloodGroupId = bloodGroupList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    private class GetEducationList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllEducationList"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        educationList = pojoDetails.getResult();
                        if (educationList.size() > 0) {
                            showEducationDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Fail", message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Please Try Again", "Server Not Responding", false);
            }
        }
    }

    private void showEducationDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Education");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < educationList.size(); i++) {

            arrayAdapter.add(String.valueOf(educationList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_education.setText(educationList.get(which).getName());
                educationId = educationList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    public void removeMobileLayout(View view) {
        ll_mobile.removeView((View) view.getParent());
        mobileLayoutsList.remove(view.getParent());
    }

    public void removeLandlineLayout(View view) {
        ll_landline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void removeEmailLayout(View view) {
        ll_email.removeView((View) view.getParent());
        emailLayoutsList.remove(view.getParent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                submitData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void submitData() {
        if (edt_fname.getText().toString().trim().isEmpty()) {
            edt_fname.setError("Please enter first name");
            edt_fname.requestFocus();
            return;
        }

        if (edt_mname.getText().toString().trim().isEmpty()) {
            edt_mname.setError("Please enter middle name");
            edt_mname.requestFocus();
            return;
        }

        if (edt_lname.getText().toString().trim().isEmpty()) {
            edt_lname.setError("Please enter last name");
            edt_lname.requestFocus();
            return;
        }

        String genderId = "";

        if (rb_male.isChecked()) {
            genderId = "1";
        } else if (rb_female.isChecked()) {
            genderId = "2";
        } else {
            Utilities.showMessage("Please select genderId", context, 2);
            return;
        }

        if (edt_education.getText().toString().trim().isEmpty()) {
            edt_education.setError("Please select education");
            edt_education.requestFocus();
            return;
        }

        if (edt_specify.getText().toString().trim().isEmpty()) {
            edt_specify.setError("Please enter specification");
            edt_specify.requestFocus();
            return;
        }

        if (edt_mobile.getText().toString().trim().isEmpty()) {
            edt_mobile.setError("Please enter mobile number");
            edt_mobile.requestFocus();
            return;
        }

//        if (edt_landline.getText().toString().trim().isEmpty()) {
//            edt_landline.setError("Please enter landline number");
//            edt_landline.requestFocus();
//            return;
//        }

        if (edt_email.getText().toString().trim().isEmpty()) {
            edt_email.setError("Please enter email");
            edt_email.requestFocus();
            return;
        }

        if (edt_nativeplace.getText().toString().trim().isEmpty()) {
            edt_nativeplace.setError("Please enter native place");
            edt_nativeplace.requestFocus();
            return;
        }

        JsonObject mainObj = new JsonObject();

        JsonArray mobileJSONArray = new JsonArray();
        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            JsonObject mobileJSONObj = new JsonObject();
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                mobileJSONObj.addProperty("mobile_number", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                mobileJSONObj.addProperty("is_primary", "0");
                mobileJSONObj.addProperty("is_public", "0");
                mobileJSONArray.add(mobileJSONObj);
            }
        }

        JsonArray landlineJSONArray = new JsonArray();
        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            JsonObject landlineJSONObj = new JsonObject();
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                landlineJSONObj.addProperty("landlinenumber", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                landlineJSONObj.addProperty("is_primary", "0");
                landlineJSONArray.add(landlineJSONObj);
            }
        }

        JsonArray emailJSONArray = new JsonArray();
        for (int i = 0; i < emailLayoutsList.size(); i++) {
            JsonObject emailJSONObj = new JsonObject();
            if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().equals("")) {
                emailJSONObj.addProperty("email_id", ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim());
                emailJSONObj.addProperty("is_primary", "0");
                emailJSONArray.add(emailJSONObj);
            }
        }


        mainObj.addProperty("type", "createusers");
        mainObj.addProperty("first_name", edt_fname.getText().toString().trim());
        mainObj.addProperty("last_name", edt_lname.getText().toString().trim());
        mainObj.addProperty("middle_name", edt_mname.getText().toString().trim());
        mainObj.addProperty("gender_id", genderId);
        mainObj.addProperty("blood_group_id", bloodGroupId);
        mainObj.addProperty("education_id", educationId);
        mainObj.addProperty("specific_education", edt_specify.getText().toString().trim());
        mainObj.addProperty("referral_code", referalCode);
        mainObj.addProperty("is_active", isActive);
        mainObj.addProperty("password", "");
        mainObj.addProperty("image_url", "");
        mainObj.addProperty("native_place", edt_nativeplace.getText().toString().trim());
        mainObj.add("mobile", mobileJSONArray);
        mainObj.add("landline_number", landlineJSONArray);
        mainObj.add("email", emailJSONArray);

        Log.i("BASICINFOJSON", mainObj.toString());

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
