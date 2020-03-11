package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupFeedsCommentsAdapter;
import in.oriange.joinsta.models.DisclaimerModel;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class GroupFeedsComments_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CircleImageView imv_user, imv_current_user;
    private TextView tv_name, tv_time, tv_feed_title, tv_feed_text, tv_viewdocs, tv_disclaimer;
    private CardView cv_feed_image;
    private ImageView imv_feed_image;
    private Button btn_comment, btn_share;
    private RecyclerView rv_feeds_comments;
    private EditText edt_comment;
    private ImageButton imb_post_comment;
    private CheckBox cb_like;

    private GroupFeedsModel.ResultBean feedDetails;
    private String userId;

    private File downloadedDocsfolder, downloadedDocumentfolder, file;
    private String description;

    private LocalBroadcastManager localBroadcastManager;
    private List<GroupFeedsModel.ResultBean.FeedCommentsBean> feedCommentsList;
    private GroupFeedsCommentsAdapter groupFeedsCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feeds_comments);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupFeedsComments_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);
        imv_current_user = findViewById(R.id.imv_current_user);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_feed_title = findViewById(R.id.tv_feed_title);
        tv_feed_text = findViewById(R.id.tv_feed_text);
        tv_viewdocs = findViewById(R.id.tv_viewdocs);
        tv_disclaimer = findViewById(R.id.tv_disclaimer);
        cv_feed_image = findViewById(R.id.cv_feed_image);
        imv_feed_image = findViewById(R.id.imv_feed_image);
        btn_comment = findViewById(R.id.btn_comment);
        btn_share = findViewById(R.id.btn_share);
        rv_feeds_comments = findViewById(R.id.rv_feeds_comments);
        rv_feeds_comments.setLayoutManager(new LinearLayoutManager(context));
        edt_comment = findViewById(R.id.edt_comment);
        imb_post_comment = findViewById(R.id.imb_post_comment);
        cb_like = findViewById(R.id.cb_like);

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Posts");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();


        downloadedDocumentfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Posts Documents");
        if (!downloadedDocumentfolder.exists())
            downloadedDocumentfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        feedCommentsList = new ArrayList<>();
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            String imageUrl = json.getString("image_url");

            if (!imageUrl.equals("")) {
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_user)
                        .resize(250, 250)
                        .centerCrop()
                        .into(imv_current_user);
            } else {
                imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        feedDetails = (GroupFeedsModel.ResultBean) getIntent().getSerializableExtra("feedDetails");

        if (!feedDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(feedDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)
                    .centerCrop()
                    .into(imv_user);
        } else {
            imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        tv_name.setText(feedDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            tv_time.setText(new PrettyTime().format(formatter.parse(feedDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!feedDetails.getFeed_title().trim().isEmpty())
            tv_feed_title.setText(feedDetails.getFeed_title());
        else
            tv_feed_title.setVisibility(View.GONE);

        tv_feed_text.setText(feedDetails.getFeed_text());
        Linkify.addLinks(tv_feed_text, Linkify.ALL);
        Linkify.addLinks(tv_feed_title, Linkify.ALL);

        if (!feedDetails.getFeed_doc().equals("")) {
            String url = IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc();
            Picasso.with(context)
                    .load(url)
                    .into(imv_feed_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            cv_feed_image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            cv_feed_image.setVisibility(View.GONE);
                        }
                    });
        } else {
            cv_feed_image.setVisibility(View.GONE);
        }

        if (feedDetails.getFeed_comments().size() != 0) {
            if (feedDetails.getFeed_comments().size() == 1) {
                btn_comment.setText("1 Comment");
            } else {
                btn_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
            }
        }

        if (feedDetails.getIs_favourite() == 1) {
            cb_like.setChecked(true);
        }

        if (feedDetails.getFeed_documents().size() != 0) {
            tv_viewdocs.setVisibility(View.VISIBLE);
            if (feedDetails.getFeed_documents().size() == 1) {
                tv_viewdocs.setText(feedDetails.getFeed_documents().size() + " Document Attached");
            } else {
                tv_viewdocs.setText(feedDetails.getFeed_documents().size() + " Documents Attached");
            }
            tv_viewdocs.setPaintFlags(tv_viewdocs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        if (feedDetails.getShow_disclaimer().equals("1")) {
            tv_disclaimer.setVisibility(View.VISIBLE);
            tv_disclaimer.setPaintFlags(tv_disclaimer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        feedCommentsList = feedDetails.getFeed_comments();
        groupFeedsCommentsAdapter = new GroupFeedsCommentsAdapter(context, feedCommentsList);
        rv_feeds_comments.setAdapter(groupFeedsCommentsAdapter);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("GroupFeedsComments_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        imb_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_comment.getText().toString().trim().isEmpty()) {

                    JsonObject mainObj = new JsonObject();
                    mainObj.addProperty("type", "addFeedCommentDetails");
                    mainObj.addProperty("user_id", userId);
                    mainObj.addProperty("feed_id", feedDetails.getId());
                    mainObj.addProperty("message", edt_comment.getText().toString().trim());
                    mainObj.addProperty("is_private", "0");

                    if (Utilities.isNetworkAvailable(context)) {
                        new AddFeedComment().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                }
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedDetails.getCan_share().equals("1")) {
                    description = feedDetails.getFeed_text();
                    if (!feedDetails.getFeed_doc().equals("")) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DownloadDocumentForShare().execute(IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, description + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                } else {
                    Utilities.showMessage("You cannot share this feed", context, 2);
                }
            }
        });

        cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav;

                if (cb_like.isChecked())
                    isFav = "1";
                else
                    isFav = "0";

                JsonObject mainObj = new JsonObject();

                mainObj.addProperty("type", "markFavouriteFeedDetails");
                mainObj.addProperty("user_id", userId);
                mainObj.addProperty("feed_id", feedDetails.getId());
                mainObj.addProperty("is_fav", isFav);

                if (Utilities.isNetworkAvailable(context)) {
                    new SetFavourite().execute(mainObj.toString());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        imv_feed_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc());
            }
        });

        tv_viewdocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentsList(feedDetails.getFeed_documents());
            }
        });

        tv_disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new GetDisclaimer().execute();
                else
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

    }

    private class AddFeedComment extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.FEEDSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeeds_Activity"));
                        edt_comment.setText("");
                    } else {
                        Utilities.showMessage("Failed to add comment", context, 3);
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
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
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
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, description + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }
    }

    private void showImageDialog(final String imageUrl) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_image, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_offer = promptView.findViewById(R.id.imv_offer);
        final Button btn_download = promptView.findViewById(R.id.btn_download);
        final Button btn_close = promptView.findViewById(R.id.btn_close);

        Picasso.with(context)
                .load(imageUrl)
                .into(imv_offer);

        final AlertDialog dialog = alertDialogBuilder.create();

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new DownloadDocument().execute(imageUrl, "2");
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });

        dialog.show();
    }

    private void showDocumentsList(final List<GroupFeedsModel.ResultBean.FeedDocumentsBean> documentList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Document List");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row_ellipsize);

        for (int i = 0; i < documentList.size(); i++) {
            arrayAdapter.add(documentList.get(i).getDocuments());
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context))
                    new DownloadDocument().execute(IMAGE_LINK + "feed_doc/" + documentList.get(which).getDocuments(), "1");
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
        builderSingle.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userId.equals(feedDetails.getCreated_by())) {
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
                builder.setMessage("Are you sure you want to delete this post?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteFeed().execute();
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
                context.startActivity(new Intent(context, EditGroupFeeds_Activity.class)
                        .putExtra("feedDetails", feedDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class DeleteFeed extends AsyncTask<String, Void, String> {
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
            param.add(new ParamsPojo("type", "deleteFeedDetails"));
            param.add(new ParamsPojo("feed_id", feedDetails.getId()));
            res = APICall.FORMDATAAPICall(ApplicationConstants.FEEDSAPI, param);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeeds_Activity"));
                        Utilities.showMessage("Post deleted successfully", context, 1);
                        finish();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SetFavourite extends AsyncTask<String, Void, String> {
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
            res = APICall.JSONAPICall(ApplicationConstants.FEEDSAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeeds_Activity"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog pd;

        String TYPE = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Document");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            TYPE = params[1];
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

                file = new File(downloadedDocumentfolder, Uri.parse(params[0]).getLastPathSegment());
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
            if (aBoolean == true) {

                if (TYPE.equals("1")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse("file://" + file);
                    if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                        // Word document
                        intent.setDataAndType(uri, "application/msword");
                    } else if (downloadurl.toString().contains(".pdf")) {
                        // PDF file
                        intent.setDataAndType(uri, "application/pdf");
                    } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                        // Powerpoint file
                        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                        // Excel file
                        intent.setDataAndType(uri, "application/vnd.ms-excel");
                    } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                        // WAV audio file
                        intent.setDataAndType(uri, "application/x-wav");
                    } else if (downloadurl.toString().contains(".rtf")) {
                        // RTF file
                        intent.setDataAndType(uri, "application/rtf");
                    } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                        // WAV audio file
                        intent.setDataAndType(uri, "audio/x-wav");
                    } else if (downloadurl.toString().contains(".gif")) {
                        // GIF file
                        intent.setDataAndType(uri, "image/gif");
                    } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                        // JPG file
                        intent.setDataAndType(uri, "image/jpeg");
                    } else if (downloadurl.toString().contains(".txt")) {
                        // Text file
                        intent.setDataAndType(uri, "text/plain");
                    } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                        // Video files
                        intent.setDataAndType(uri, "video/*");
                    } else {
                        intent.setDataAndType(uri, "*/*");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (TYPE.equals("2")) {
                    Utilities.showMessage("Image successfully downloaded", context, 1);
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            }
        }
    }

    private class GetDisclaimer extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getDisclaimerString"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.FEEDSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<DisclaimerModel.ResultBean> disclaimerList = new ArrayList<>();
                    DisclaimerModel pojoDetails = new Gson().fromJson(result, DisclaimerModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        disclaimerList = pojoDetails.getResult();
                        if (disclaimerList.size() > 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false)
                                    .setTitle("Disclaimer")
                                    .setMessage(disclaimerList.get(0).getString())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.hideSoftKeyboard(GroupFeedsComments_Activity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            feedDetails = (GroupFeedsModel.ResultBean) intent.getSerializableExtra("feedDetails");

            if (feedDetails.getFeed_comments().size() != 0) {
                if (feedDetails.getFeed_comments().size() == 1) {
                    btn_comment.setText("1 Comment");
                } else {
                    btn_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
                }
            }

            feedCommentsList.clear();
            feedCommentsList = feedDetails.getFeed_comments();
            groupFeedsCommentsAdapter.swap(feedCommentsList);

            if (GroupFeedsCommentsAdapter.isCommentClicked) {
                if (feedCommentsList.size() != 0) {
                    GroupFeedsModel.ResultBean.FeedCommentsBean commentsDetails = feedCommentsList.get(GroupFeedsCommentsAdapter.itemClickedPosition);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeedsCommentsReplys_Activity")
                            .putExtra("commentsDetails", commentsDetails));
                }
            }
        }
    };
}
