package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

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
import java.util.Calendar;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewOfferDetails_Activity;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;
import jp.shts.android.library.TriangleLabelView;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.diffBetweenTwoDates;

public class MyAddedOffersAdapter extends RecyclerView.Adapter<MyAddedOffersAdapter.MyViewHolder> {

    private Context context;
    private List<MyOffersListModel.ResultBean> myOffersList;
    private String isFromMyOfferOrFromParticularOffer;            //1 = My Offers  2 = Particular record offer
    private String userId;
    private int mYear, mMonth, mDay;
    private String startDate;
    private String shareMessage;
    private ArrayList<Uri> downloadedImagesUriList;
    private int numOfDocuments = 0;
    private int numOfFilesDownloaded = 0;
    private File downloadedDocsfolder;

    public MyAddedOffersAdapter(Context context, List<MyOffersListModel.ResultBean> myOffersList, String isFromMyOfferOrFromParticularOffer) {
        this.context = context;
        this.myOffersList = myOffersList;
        this.isFromMyOfferOrFromParticularOffer = isFromMyOfferOrFromParticularOffer;

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Offer Images");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        startDate = mYear + "-" + mMonth + "-" + mDay;

        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_offers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final MyOffersListModel.ResultBean offerDetails = myOffersList.get(position);

        if (diffBetweenTwoDates(startDate, offerDetails.getCreated_at()) <= 2) {
            holder.tv_new.setVisibility(View.VISIBLE);
        }

        if (!offerDetails.getRecord_name().trim().equals("")) {
            holder.tv_business_name.setText(offerDetails.getRecord_name());
        } else {
            holder.tv_business_name.setVisibility(View.GONE);
        }

//        if (!offerDetails.getRecord_name().isEmpty() && !offerDetails.getSub_category().isEmpty()) {
//            holder.tv_business_name.setText(Html.fromHtml("<font color=\"#EF6C00\"> <b>"+ offerDetails.getRecord_name() +"</b> </font>" + " (" + offerDetails.getSub_category() + ")"));
//        } else if (offerDetails.getRecord_name().isEmpty() && offerDetails.getSub_category().isEmpty()) {
//            holder.tv_business_name.setVisibility(View.GONE);
//        } else if (!offerDetails.getRecord_name().isEmpty()) {
//            holder.tv_business_name.setText("<font color=\"#EF6C00\"> <b>"+ offerDetails.getRecord_name() +"</b> </font>");
//        }

        holder.tv_title.setText(offerDetails.getTitle());
        holder.tv_description.setText(offerDetails.getDescription());

        if (!offerDetails.getUrl().trim().isEmpty()) {
            holder.tv_url.setText(offerDetails.getUrl());
            holder.tv_url.setPaintFlags(holder.tv_url.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            holder.tv_url.setOnClickListener(new View.OnClickListener() {
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

        } else {
            holder.tv_url.setVisibility(View.GONE);
        }

        holder.tv_validity.setText(Html.fromHtml("Valid upto " + "<font color=\"#EF6C00\"> <b>" + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()) + "</b> </font>"));

        if (!offerDetails.getPromo_code().equals("")) {
            holder.tv_promo_code.setText(offerDetails.getPromo_code());
        } else {
            holder.cv_promo_code.setVisibility(View.GONE);
        }

        holder.cv_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(offerDetails.getPromo_code());
                Utilities.showMessage("Promo code copied to clipboard", context, 1);
            }
        });

        if (offerDetails.getDocuments().size() != 0) {

            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();

            for (int i = 0; i < offerDetails.getDocuments().size(); i++) {
                bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(i).getDocument()));
            }

            OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, bannerList);
            holder.imageSlider.setSliderAdapter(adapter);
            holder.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            holder.imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
            holder.imageSlider.setIndicatorSelectedColor(Color.WHITE);
            holder.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
            holder.imageSlider.setAutoCycle(true);
            holder.imageSlider.setScrollTimeInSec(10);
        } else {
            holder.imageSlider.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isRecordAddedByCurrentUserId = "0";            //0 = No  1 = Yes
                if (offerDetails.getCreated_by().equals(userId)) {
                    isRecordAddedByCurrentUserId = "1";
                }

                context.startActivity(new Intent(context, ViewOfferDetails_Activity.class)
                        .putExtra("offerDetails", offerDetails)
                        .putExtra("isFromMyOfferOrFromParticularOffer", isFromMyOfferOrFromParticularOffer)
                        .putExtra("isRecordAddedByCurrentUserId", isRecordAddedByCurrentUserId));

            }
        });

        holder.imb_share.setOnClickListener(new View.OnClickListener() {
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
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/html");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return myOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout, cv_promo_code;
        private SliderView imageSlider;
        private TriangleLabelView tv_new;
        private TextView tv_business_name, tv_title, tv_description, tv_url, tv_validity, tv_promo_code;
        private ImageButton imb_share;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            cv_promo_code = view.findViewById(R.id.cv_promo_code);
            imageSlider = view.findViewById(R.id.imageSlider);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_url = view.findViewById(R.id.tv_url);
            tv_validity = view.findViewById(R.id.tv_validity);
            tv_promo_code = view.findViewById(R.id.tv_promo_code);
            tv_new = view.findViewById(R.id.tv_new);
            imb_share = view.findViewById(R.id.imb_share);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("text/html");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, downloadedImagesUriList);
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
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

}
