package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.library.banner.BannerLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private CardView cv_url, cv_promo_code;
    private ImageButton imb_share;

    private MyOffersListModel.ResultBean offerDetails;
    private String userId, isFromMyOfferOrFromParticularOffer, isRecordAddedByCurrentUserId;

    private String shareMessage;
    private ArrayList<Uri> downloadedImagesUriList;
    private int numOfDocuments = 0;
    private int numOfFilesDownloaded = 0;
    private File file, downloadedDocsfolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offer_details);

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
        imb_share = findViewById(R.id.imb_share);

        cv_url = findViewById(R.id.cv_url);
        cv_promo_code = findViewById(R.id.cv_promo_code);

        downloadedImagesUriList = new ArrayList<>();

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Offer Images");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
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
            tv_validity.setVisibility(View.GONE);
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

        imb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offerDetails.getDocuments().size() != 0) {
                    numOfDocuments = offerDetails.getDocuments().size();
                    shareMessage = getShareMessage(offerDetails);
                    downloadedImagesUriList = new ArrayList<>();
                    numOfFilesDownloaded = 0;
                    for (int i = 0; i < offerDetails.getDocuments().size(); i++) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DownloadDocumentForShare().execute(IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(i).getDocument());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                } else {
                    String shareMessage = getShareMessage(offerDetails);
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });
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

    private class DownloadDocumentForShare extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        ProgressDialog pd;
        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Document");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocsfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            pd.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            super.onPostExecute(aBoolean);
            Uri uri = Uri.parse("file:///" + file);
            downloadedImagesUriList.add(uri);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            numOfFilesDownloaded = numOfFilesDownloaded + 1;

            if (numOfFilesDownloaded == numOfDocuments) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, downloadedImagesUriList);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }

    private String getShareMessage(MyOffersListModel.ResultBean offerDetails) {
        StringBuilder sb = new StringBuilder();

        if (!offerDetails.getRecord_name().equals("")) {
            sb.append(offerDetails.getRecord_name() + "\n");
        }

        sb.append(offerDetails.getTitle() + " - " + offerDetails.getDescription() + "\n");

        if (!offerDetails.getStart_date().equals("") && !offerDetails.getEnd_date().equals("")) {
            sb.append("Offer valid from " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getStart_date()) + " to " +
                    changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()) + "\n");
        }

        if (!offerDetails.getPromo_code().equals("")) {
            sb.append("Promo Code - " + offerDetails.getPromo_code() + "\n");
        }

        if (!offerDetails.getUrl().equals("")) {
            sb.append("Click Here - " + offerDetails.getUrl() + "\n");
        }

        return sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;
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
