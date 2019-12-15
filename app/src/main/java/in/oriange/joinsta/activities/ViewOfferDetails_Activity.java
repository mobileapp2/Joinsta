package in.oriange.joinsta.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.banner.BannerLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.OfferRecyclerBannerAdapter;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class ViewOfferDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private BannerLayout rv_offer_images;
    private TextView tv_business_name, tv_title, tv_description, tv_validity, tv_url, tv_promo_code;
    private CardView cv_validity, cv_url, cv_promo_code;

    private MyOffersListModel.ResultBean offerDetails;
    private String userId, isFromMyOfferOrFromParticularOffer, isRecordAddedByCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details_1);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewOfferDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tv_business_name = findViewById(R.id.tv_business_name);
        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_validity = findViewById(R.id.tv_validity);
        tv_url = findViewById(R.id.tv_url);
        tv_promo_code = findViewById(R.id.tv_promo_code);
        rv_offer_images = findViewById(R.id.rv_offer_images);

        cv_validity = findViewById(R.id.cv_validity);
        cv_url = findViewById(R.id.cv_url);
        cv_promo_code = findViewById(R.id.cv_promo_code);

    }

    private void setDefault() {
        offerDetails = (MyOffersListModel.ResultBean) getIntent().getSerializableExtra("offerDetails");
        isFromMyOfferOrFromParticularOffer = getIntent().getStringExtra("isFromMyOfferOrFromParticularOffer");
        isRecordAddedByCurrentUserId = getIntent().getStringExtra("isRecordAddedByCurrentUserId");

//        if (!offerDetails.getRecord_name().isEmpty() && !offerDetails.getSub_category().isEmpty()) {
//            tv_business_name.setText(offerDetails.getRecord_name() + " (" + offerDetails.getSub_category() + ")");
//        } else if (offerDetails.getRecord_name().isEmpty() && offerDetails.getSub_category().isEmpty()) {
//            tv_business_name.setVisibility(View.GONE);
//        } else if (!offerDetails.getRecord_name().isEmpty()) {
//            tv_business_name.setText(offerDetails.getRecord_name());
//        }

        if (!offerDetails.getRecord_name().trim().equals("")) {
            tv_business_name.setText(offerDetails.getRecord_name());
        } else {
            tv_business_name.setVisibility(View.GONE);
        }

        tv_title.setText(offerDetails.getTitle());
        tv_description.setText(offerDetails.getDescription());

        if (!offerDetails.getStart_date().equals("") && !offerDetails.getEnd_date().equals("")) {
            tv_validity.setText("Offer valid from " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getStart_date()) + " to " +
                    changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()));
        } else {
            cv_validity.setVisibility(View.GONE);
        }

        if (!offerDetails.getUrl().equals("")) {
            tv_url.setText(offerDetails.getUrl());
        } else {
            cv_url.setVisibility(View.GONE);
        }

        if (!offerDetails.getPromo_code().equals("")) {
            tv_promo_code.setText(offerDetails.getPromo_code());
        } else {
            cv_promo_code.setVisibility(View.GONE);
        }

        if (offerDetails.getDocuments().size() == 0) {
            rv_offer_images.setVisibility(View.GONE);
        } else {
            final List<String> offerImages = new ArrayList<>();
            for (int i = 0; i < offerDetails.getDocuments().size(); i++) {
                offerImages.add(IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(i).getDocument());
            }
            OfferRecyclerBannerAdapter webBannerAdapter = new OfferRecyclerBannerAdapter(this, offerImages);
            webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    showImageDialog(offerImages.get(position));
                }
            });
            rv_offer_images.setAdapter(webBannerAdapter);
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

        tv_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = offerDetails.getUrl();

                if (!url.startsWith("https://") || !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        cv_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(offerDetails.getPromo_code());
                Utilities.showMessage("Promo code copied to clipboard", context, 1);
            }
        });
    }

    private void showImageDialog(String offerUrl) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_offeriamge, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_offer = promptView.findViewById(R.id.imv_offer);

        Picasso.with(context)
                .load(offerUrl)
                .into(imv_offer);

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isRecordAddedByCurrentUserId.equals("1")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menus_edit_delete, menu);

            if (offerDetails.getIs_approved().equals("1")) {
                menu.getItem(0).setVisible(false);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this offer?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteOffer().execute();
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
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
                break;
            case R.id.action_edit:
                startActivity(new Intent(context, EditOffers_Activity.class)
                        .putExtra("offerDetails", offerDetails)
                        .putExtra("categoryTypeId", "1")
                        .putExtra("categoryTypeName", "business")
                        .putExtra("isFromMyOfferOrFromParticularOffer", isFromMyOfferOrFromParticularOffer));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class DeleteOffer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "deleteOfferDetails"));
            param.add(new ParamsPojo("offer_id", offerDetails.getId()));
            res = APICall.FORMDATAAPICall(ApplicationConstants.OFFERSAPI, param);
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
                        if (isFromMyOfferOrFromParticularOffer.equals("1")) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyAddedOffers_Actvity"));
                        } else if (isFromMyOfferOrFromParticularOffer.equals("2")) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("OffersForParticularRecord_Activity"));
                        }

                        Utilities.showMessage("Offer deleted successfully", context, 1);
                        finish();
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

}
