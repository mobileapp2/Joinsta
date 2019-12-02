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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
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
    private TextView tv_title, tv_description, tv_validity, tv_url, tv_promo_code;
    private CardView cv_validity, cv_url, cv_promo_code, cv_images;
    private ImageView imv_image_one, imv_image_two, imv_image_three;

    private MyOffersListModel.ResultBean offerDetails;
    private String userId, imageOne = "", imageTwo = "", imageThree = "", isFromMyOfferOrFromParticularOffer, isRecordAddedByCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offerdetails);

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

        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_validity = findViewById(R.id.tv_validity);
        tv_url = findViewById(R.id.tv_url);
        tv_promo_code = findViewById(R.id.tv_promo_code);

        cv_validity = findViewById(R.id.cv_validity);
        cv_url = findViewById(R.id.cv_url);
        cv_promo_code = findViewById(R.id.cv_promo_code);
        cv_images = findViewById(R.id.cv_images);

        imv_image_one = findViewById(R.id.imv_image_one);
        imv_image_two = findViewById(R.id.imv_image_two);
        imv_image_three = findViewById(R.id.imv_image_three);

    }

    private void setDefault() {
        offerDetails = (MyOffersListModel.ResultBean) getIntent().getSerializableExtra("offerDetails");
        isFromMyOfferOrFromParticularOffer = getIntent().getStringExtra("isFromMyOfferOrFromParticularOffer");
        isRecordAddedByCurrentUserId = getIntent().getStringExtra("isRecordAddedByCurrentUserId");

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
            tv_promo_code.setText("Promo Code - " + offerDetails.getPromo_code());
        } else {
            cv_promo_code.setVisibility(View.GONE);
        }

        if (offerDetails.getDocuments().size() == 0) {
            cv_images.setVisibility(View.GONE);
        }

        for (int i = 0; i < offerDetails.getDocuments().size(); i++) {

            switch (i) {
                case 0:
                    imageOne = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(0).getDocument();
                    Picasso.with(context)
                            .load(imageOne)
                            .resize(200, 200)
                            .into(imv_image_one);

                    break;
                case 1:
                    imageTwo = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(1).getDocument();
                    Picasso.with(context)
                            .load(imageTwo)
                            .resize(200, 200)
                            .into(imv_image_two);

                    break;
                case 2:
                    imageThree = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(2).getDocument();
                    Picasso.with(context)
                            .load(imageThree)
                            .resize(200, 200)
                            .into(imv_image_three);

                    break;

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
        imv_image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageOne.isEmpty()) {
                    showImageDialog(imageOne);
                }
            }
        });

        imv_image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageTwo.isEmpty()) {
                    showImageDialog(imageTwo);
                }

            }
        });

        imv_image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageThree.isEmpty()) {
                    showImageDialog(imageThree);
                }

            }
        });

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

        tv_promo_code.setOnClickListener(new View.OnClickListener() {
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
//                startActivity(new Intent(context, EditBusiness_Activity.class)
//                        .putExtra("searchDetails", searchDetails));
//                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class DeleteOffer extends AsyncTask<String, Void, String> {

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
