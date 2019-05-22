package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Search_Fragment;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class ViewSearchEmpDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private CheckBox cb_like;
    private LinearLayout ll_direction, ll_mobile, ll_landline, ll_email;
    private MaterialEditText edt_nature, edt_subtype, edt_designation, edt_website, edt_select_area, edt_address, edt_pincode, edt_city,
            edt_district, edt_state, edt_country;
    private CardView cv_tabs;
    private TagContainerLayout tag_container;

    private SearchDetailsModel.ResultBean.EmployeesBean searchDetails;
    private String userId;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsearch_empdetails);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewSearchEmpDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);

        ll_direction = findViewById(R.id.ll_direction);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

        cb_like = findViewById(R.id.cb_like);
        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);

        edt_nature = findViewById(R.id.edt_nature);
        edt_subtype = findViewById(R.id.edt_subtype);
        edt_designation = findViewById(R.id.edt_designation);
        edt_website = findViewById(R.id.edt_website);
        edt_select_area = findViewById(R.id.edt_select_area);
        edt_address = findViewById(R.id.edt_address);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_city = findViewById(R.id.edt_city);
        edt_district = findViewById(R.id.edt_district);
        edt_state = findViewById(R.id.edt_state);
        edt_country = findViewById(R.id.edt_country);

        tag_container = findViewById(R.id.tag_container);
    }

    private void setDefault() {
        searchDetails = (SearchDetailsModel.ResultBean.EmployeesBean) getIntent().getSerializableExtra("searchDetails");

        edt_nature.setText(searchDetails.getType_description());
        edt_subtype.setText(searchDetails.getSubtype_description());
        edt_designation.setText(searchDetails.getDesignation());
        edt_website.setText(searchDetails.getWebsite());
        edt_select_area.setText(searchDetails.getLandmark());
        edt_address.setText(searchDetails.getAddress());
        edt_pincode.setText(searchDetails.getPincode());
        edt_city.setText(searchDetails.getCity());
        edt_district.setText(searchDetails.getDistrict());
        edt_state.setText(searchDetails.getState());
        edt_country.setText(searchDetails.getCountry());

        if (searchDetails.getIsFavourite().equals("1"))
            cb_like.setChecked(true);

        ArrayList<SearchDetailsModel.ResultBean.EmployeesBean.TagBean> tagsList = new ArrayList<>();
        tagsList = searchDetails.getTag().get(0);

        if (tagsList != null)
            if (tagsList.size() > 0) {
                for (int i = 0; i < tagsList.size(); i++) {
                    tag_container.addTag(tagsList.get(i).getTag_name());
                }
            } else
                cv_tabs.setVisibility(View.GONE);
        else
            cv_tabs.setVisibility(View.GONE);


        if (!searchDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(searchDetails.getImage_url().trim())
                    .into(imv_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            imv_image.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imv_image.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            imv_image.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getSessionData() {

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
                String isFav = searchDetails.getIsFavourite();

                if (cb_like.isChecked())
                    isFav = "1";
                else
                    isFav = "0";

                JsonObject mainObj = new JsonObject();

                mainObj.addProperty("type", "createfav");
                mainObj.addProperty("info_id", searchDetails.getId());
                mainObj.addProperty("info_type", "3");
                mainObj.addProperty("user_id", userId);
                mainObj.addProperty("record_status_id", isFav);

                if (Utilities.isNetworkAvailable(context)) {
                    new SetFavourite().execute(mainObj.toString());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });


        ll_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + searchDetails.getLatitude() + "&daddr=" + searchDetails.getLocality() + ""));
                startActivity(intent);
            }
        });

        ll_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDetails.getMobiles().get(0) != null)
                    if (searchDetails.getMobiles().get(0).size() > 0) {
                        if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            provideCallPremission(context);
                        } else {
                            showMobileListDialog(searchDetails.getMobiles().get(0));
                        }
                    } else
                        Utilities.showMessage("Mobile number not added", context, 2);

                else
                    Utilities.showMessage("Mobile number not added", context, 2);

            }
        });

        ll_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDetails.getLandline().get(0) != null)
                    if (searchDetails.getLandline().get(0).size() > 0) {
                        if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            provideCallPremission(context);
                        } else {
                            showLandlineListDialog(searchDetails.getLandline().get(0));
                        }
                    } else
                        Utilities.showMessage("Landline number not added", context, 2);
                else
                    Utilities.showMessage("Mobile number not added", context, 2);

            }
        });

        ll_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchDetails.getEmail().isEmpty()) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{searchDetails.getEmail()});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } else {
                    Utilities.showMessage("Email address not added", context, 2);
                }
            }
        });
    }

    private void showMobileListDialog(final ArrayList<SearchDetailsModel.ResultBean.EmployeesBean.MobilesBean> mobileList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select mobile number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < mobileList.size(); i++) {
            arrayAdapter.add(String.valueOf(mobileList.get(i).getMobile_number()));
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
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + mobileList.get(which).getMobile_number())));
            }
        });
        builderSingle.show();
    }

    private void showLandlineListDialog(final ArrayList<SearchDetailsModel.ResultBean.EmployeesBean.LandlineBean> landlineList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select landline number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < landlineList.size(); i++) {
            arrayAdapter.add(String.valueOf(landlineList.get(i).getLandline_numbers()));
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
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + landlineList.get(which).getLandline_numbers())));
            }
        });
        builderSingle.show();
    }

    private class SetFavourite extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.FAVOURITEAPI, params[0]);
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
                        Search_Fragment.employeeList.get(position).setIsFavourite("1");
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle("");
    }
}
