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
import in.oriange.joinsta.models.GetProfessionalModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class ViewMyProfDetails_Activity extends AppCompatActivity {
    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private LinearLayout ll_nopreview;
    private MaterialEditText edt_name, edt_nature, edt_subtype, edt_mobile, edt_landline, edt_designation, edt_website,
            edt_select_area, edt_address, edt_pincode, edt_city, edt_email, edt_district, edt_state, edt_country,
            edt_tax_alias, edt_pan, edt_gst, edt_accholdername, edt_bank_alias, edt_bank_name, edt_ifsc, edt_account_no;
    private CardView cv_tabs;
    private ImageView imv_share;
    private LinearLayout ll_mobile, ll_landline;
    private TextView tv_countrycode_mobile, tv_countrycode_landline;
    private TagContainerLayout tag_container;

    private GetProfessionalModel.ResultBean searchDetails;
    private String userId;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmy_profdetails);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewMyProfDetails_Activity.this;
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
        edt_tax_alias = findViewById(R.id.edt_tax_alias);
        edt_pan = findViewById(R.id.edt_pan);
        edt_gst = findViewById(R.id.edt_gst);
        edt_accholdername = findViewById(R.id.edt_accholdername);
        edt_bank_alias = findViewById(R.id.edt_bank_alias);
        edt_bank_name = findViewById(R.id.edt_bank_name);
        edt_ifsc = findViewById(R.id.edt_ifsc);
        edt_account_no = findViewById(R.id.edt_account_no);
        imv_share = findViewById(R.id.imv_share);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);

        tag_container = findViewById(R.id.tag_container);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        tv_countrycode_landline = findViewById(R.id.tv_countrycode_landline);
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
    }

    private void setDefault() {
        searchDetails = (GetProfessionalModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        edt_name.setText(searchDetails.getProfessional_code() + " - " + searchDetails.getFirm_name());
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
        edt_tax_alias.setText(searchDetails.getTax_alias());
        edt_pan.setText(searchDetails.getPan_number());
        edt_gst.setText(searchDetails.getGst_number());
        edt_accholdername.setText(searchDetails.getAccount_holder_name());
        edt_bank_alias.setText(searchDetails.getBank_alias());
        edt_bank_name.setText(searchDetails.getBank_name());
        edt_ifsc.setText(searchDetails.getIfsc_code());
        edt_account_no.setText(searchDetails.getAccount_no());

        ArrayList<GetProfessionalModel.ResultBean.TagBean> tagsList = new ArrayList<>();
        tagsList = searchDetails.getTag().get(0);

        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++)
                    tag_container.addTag(tagsList.get(i).getTag_name());
            else
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

        ArrayList<GetProfessionalModel.ResultBean.MobilesBean> mobilesList = new ArrayList<>();
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
                                tv_countrycode_mobile.setText("+91");
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
                                ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText("+91");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


        ArrayList<GetProfessionalModel.ResultBean.LandlineBean> landlineList = new ArrayList<>();
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
        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getFirm_name().equals("")) {
                    sb.append("Firm Name - " + searchDetails.getFirm_name() + "\n");
                }

                if (!searchDetails.getSubtype_description().equals("")) {
                    sb.append("Name of the profession - " + searchDetails.getType_description() + "/" + searchDetails.getSubtype_description() + "\n");
                } else {
                    sb.append("Name of the profession - " + searchDetails.getType_description() + "\n");
                }

                if (searchDetails.getTag().get(0) != null)
                    if (searchDetails.getTag().get(0).size() != 0) {
                        StringBuilder tags = new StringBuilder();
                        for (int i = 0; i < searchDetails.getTag().get(0).size(); i++) {
                            tags.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                        }

                        sb.append("Services Offered - " + tags.toString().substring(0, tags.toString().length() - 2) + "\n");
                    }

                if (!searchDetails.getAddress().equals("")) {
                    sb.append("Address - " + searchDetails.getAddress() + "\n");
                }

                if (searchDetails.getMobiles().get(0) != null)
                    if (searchDetails.getMobiles().get(0).size() != 0) {
                        StringBuilder mobile = new StringBuilder();
                        for (int i = 0; i < searchDetails.getMobiles().get(0).size(); i++) {
                            mobile.append(searchDetails.getMobiles().get(0).get(i).getMobile_number() + ", ");
                        }

                        sb.append("Mobile - " + mobile.toString().substring(0, mobile.toString().length() - 2) + "\n");
                    }

                if (!searchDetails.getLatitude().equals("") || !searchDetails.getLongitude().equals("")) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + searchDetails.getLatitude() + "," + searchDetails.getLongitude() + "\n");

                }

                if (!searchDetails.getEmail().equals("")) {
                    sb.append("Email - " + searchDetails.getEmail() + "\n");
                }

                if (!searchDetails.getWebsite().equals("")) {
                    sb.append("Website - " + searchDetails.getWebsite() + "\n");
                }

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        new DeleteProfessional().execute();
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
                startActivity(new Intent(context, EditProfessional_Activity.class)
                        .putExtra("searchDetails", searchDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class DeleteProfessional extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deleteprofessional");
            obj.addProperty("professional_id", searchDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.PROFESSIONALAPI, obj.toString());
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
                        new ProfileDetails_Activity.GetProfessional().execute();
                        Utilities.showMessage("Professional details deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
