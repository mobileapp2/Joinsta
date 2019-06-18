package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Profile_Fragment;
import in.oriange.joinsta.models.GetBusinessModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class ViewMyBizDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private LinearLayout ll_nopreview;
    private MaterialEditText edt_name, edt_nature, edt_subtype, edt_designation, edt_website, edt_mobile, edt_landline,
            edt_email, edt_select_area, edt_address, edt_pincode, edt_city,
            edt_district, edt_state, edt_country;
    private LinearLayout ll_mobile, ll_landline;
    private TextView tv_countrycode_mobile, tv_countrycode_landline;
    private CardView cv_tabs;
    private TagContainerLayout tag_container;
    private GetBusinessModel.ResultBean searchDetails;
    private String userId;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmy_bizdetails);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewMyBizDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        ll_nopreview = findViewById(R.id.ll_nopreview);
        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);

        edt_name = findViewById(R.id.edt_name);
        edt_nature = findViewById(R.id.edt_nature);
        edt_subtype = findViewById(R.id.edt_subtype);
        edt_designation = findViewById(R.id.edt_designation);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);
        edt_select_area = findViewById(R.id.edt_select_area);
        edt_address = findViewById(R.id.edt_address);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_city = findViewById(R.id.edt_city);
        edt_district = findViewById(R.id.edt_district);
        edt_state = findViewById(R.id.edt_state);
        edt_country = findViewById(R.id.edt_country);

        tag_container = findViewById(R.id.tag_container);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        tv_countrycode_landline = findViewById(R.id.tv_countrycode_landline);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);


        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
    }

    private void setDefault() {
        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        edt_name.setText(searchDetails.getBusiness_name());
        edt_nature.setText(searchDetails.getType_description());
        edt_subtype.setText(searchDetails.getSubtype_description());
        edt_designation.setText(searchDetails.getDesignation());
        edt_email.setText(searchDetails.getEmail());
        edt_website.setText(searchDetails.getWebsite());
        edt_select_area.setText(searchDetails.getLandmark());
        edt_address.setText(searchDetails.getAddress());
        edt_pincode.setText(searchDetails.getPincode());
        edt_city.setText(searchDetails.getCity());
        edt_district.setText(searchDetails.getDistrict());
        edt_state.setText(searchDetails.getState());
        edt_country.setText(searchDetails.getCountry());

        ArrayList<GetBusinessModel.ResultBean.TagBean> tagsList = new ArrayList<>();
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
            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .into(imv_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            ll_nopreview.setVisibility(View.GONE);
                            imv_image.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imv_image.setVisibility(View.GONE);
                            ll_nopreview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            imv_image.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        ArrayList<GetBusinessModel.ResultBean.MobilesBean> mobilesList = new ArrayList<>();
        mobilesList = searchDetails.getMobiles().get(0);

        if (mobilesList != null)
            if (mobilesList.size() > 0)
                for (int i = 0; i < mobilesList.size(); i++) {
                    if (i == mobilesList.size() - 1) {
                        try {
                            if (mobilesList.get(i).getMobile_number().length() > 10) {
                                edt_mobile.setText(mobilesList.get(i).getMobile_number().substring(mobilesList.get(i).getMobile_number().length() - 10));
                                String code = mobilesList.get(i).getMobile_number().substring(0, mobilesList.get(i).getMobile_number().length() - 10);
                                if (!code.isEmpty())
                                    tv_countrycode_mobile.setText(code);
                            } else {
                                edt_mobile.setText(mobilesList.get(i).getMobile_number());
                                String code = "";
                                if (!code.isEmpty())
                                    tv_countrycode_mobile.setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_mobile4, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            mobileLayoutsList.add(ll);
                            ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                            if (mobilesList.get(i).getMobile_number().length() > 10) {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number().substring(mobilesList.get(i).getMobile_number().length() - 10));
                                String code = mobilesList.get(i).getMobile_number().substring(0, mobilesList.get(i).getMobile_number().length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(code);
                            } else {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number());
                                String code = "";
                                if (!code.isEmpty())
                                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


        ArrayList<GetBusinessModel.ResultBean.LandlineBean> landlineList = new ArrayList<>();
        landlineList = searchDetails.getLandline().get(0);


        if (landlineList != null)
            if (landlineList.size() > 0)
                for (int i = 0; i < landlineList.size(); i++) {
                    if (i == landlineList.size() - 1) {
                        try {

                            if (landlineList.get(i).getLandline_number().length() > 10) {
                                edt_landline.setText(landlineList.get(i).getLandline_number().substring(landlineList.get(i).getLandline_number().length() - 10));
                                String code = landlineList.get(i).getLandline_number().substring(0, landlineList.get(i).getLandline_number().length() - 10);
                                if (!code.isEmpty())
                                    tv_countrycode_landline.setText(code);
                            } else {
                                edt_landline.setText(landlineList.get(i).getLandline_number());
                                String code = "";
                                if (!code.isEmpty())
                                    tv_countrycode_landline.setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_landline4, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            landlineLayoutsList.add(ll);
                            ll_landline.addView(rowView, ll_landline.getChildCount() - 1);

                            if (landlineList.get(i).getLandline_number().length() > 10) {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number().substring(landlineList.get(i).getLandline_number().length() - 10));
                                String code = landlineList.get(i).getLandline_number().substring(0, landlineList.get(i).getLandline_number().length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(code);
                            } else {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number());
                                String code = "";
                                if (!code.isEmpty())
                                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

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
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteBusiness().execute();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
                break;
            case R.id.action_edit:
                startActivity(new Intent(context, EditBusiness_Activity.class)
                        .putExtra("searchDetails", searchDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class DeleteBusiness extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deletebusiness");
            obj.addProperty("business_id", searchDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
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
                        new Profile_Fragment.GetBusiness().execute();
                        Utilities.showMessage("Business details deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
