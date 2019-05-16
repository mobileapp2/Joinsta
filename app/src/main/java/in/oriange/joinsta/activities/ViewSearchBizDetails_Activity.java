package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;
import static in.oriange.joinsta.utilities.Utilities.provideLocationAccess;

public class ViewSearchBizDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private LinearLayout ll_direction, ll_mobile, ll_landline, ll_email, ll_nopreview;
    private MaterialEditText edt_name, edt_nature, edt_subtype, edt_designation, edt_website, edt_select_area, edt_address, edt_pincode, edt_city,
            edt_district, edt_state, edt_country;
    private CardView cv_tabs;
    private TagContainerLayout tag_container;

    private SearchDetailsModel.ResultBean.BusinessesBean searchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsearch_bizdetails);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewSearchBizDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);

        ll_nopreview = findViewById(R.id.ll_nopreview);
        ll_direction = findViewById(R.id.ll_direction);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);

        edt_name = findViewById(R.id.edt_name);
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
        searchDetails = (SearchDetailsModel.ResultBean.BusinessesBean) getIntent().getSerializableExtra("searchDetails");

        edt_name.setText(searchDetails.getBusiness_name());
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

        ArrayList<SearchDetailsModel.ResultBean.BusinessesBean.TagBeanXX> tagsList = new ArrayList<>();
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
    }

    private void getSessionData() {

    }

    private void setEventHandler() {
        ll_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
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

    private void showMobileListDialog(final ArrayList<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> mobileList) {
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

    private void showLandlineListDialog(final ArrayList<SearchDetailsModel.ResultBean.BusinessesBean.LandlineBeanXX> landlineList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select landline number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < landlineList.size(); i++) {
            arrayAdapter.add(String.valueOf(landlineList.get(i).getLandline_number()));
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
                        Uri.parse("tel:" + landlineList.get(which).getLandline_number())));
            }
        });
        builderSingle.show();
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
