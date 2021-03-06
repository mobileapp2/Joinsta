package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditGroupFeeds_Activity;
import in.oriange.joinsta.activities.GroupFeedsComments_Activity;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class GroupFeedsAdapter extends RecyclerView.Adapter<GroupFeedsAdapter.MyViewHolder> {

    private String userId;
    private Context context;
    private List<GroupFeedsModel.ResultBean> feedsList;
    private PrettyTime p;

    private ArrayList<Uri> downloadedImagesUriList;
    private int numOfDocuments = 0;
    private int numOfFilesDownloaded = 0;
    private File downloadedDocsfolder;
    private String title, description, isAdmin;

    private PowerMenu powerMenu;
    public static int itemClickedPosition = 0;

    public GroupFeedsAdapter(Context context, List<GroupFeedsModel.ResultBean> feedsList, String isAdmin) {
        this.context = context;
        this.feedsList = feedsList;
        this.isAdmin = isAdmin;
        p = new PrettyTime();

        UserSessionManager session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Feeds");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_feeds, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final GroupFeedsModel.ResultBean feedDetails = feedsList.get(position);

        if (feedDetails.getCreated_by().equals(userId) || isAdmin.equals("1")) {
            holder.imv_more.setVisibility(View.VISIBLE);
        } else {
            holder.imv_more.setVisibility(View.GONE);
        }

        if (!feedDetails.getImage_url().trim().isEmpty()) {
            String url = IMAGE_LINK + "" + feedDetails.getCreated_by() + "/" + feedDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)
                    .centerCrop()
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(feedDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(feedDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!feedDetails.getFeed_title().trim().isEmpty()) {
            holder.tv_feed_title.setText(feedDetails.getFeed_title());
            holder.tv_feed_title.setVisibility(View.VISIBLE);
        } else {
            holder.tv_feed_title.setVisibility(View.GONE);
        }

        if (!feedDetails.getFeed_text().trim().isEmpty()) {
            holder.tv_feed_text.setText(feedDetails.getFeed_text());
            holder.tv_feed_text.setVisibility(View.VISIBLE);
        } else {
            holder.tv_feed_text.setVisibility(View.GONE);
        }

        if (feedDetails.getFeed_comments().size() != 0) {
            if (feedDetails.getFeed_comments().size() == 1) {
                holder.tv_comment.setText("1 Comment");
            } else {
                holder.tv_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
            }
        }

        if (feedDetails.getIs_favourite() == 1) {
            holder.imv_favourite.setImageResource(R.drawable.icon_like_red);
        } else {
            holder.imv_favourite.setImageResource(R.drawable.icon_like_grey);
        }

        if (feedDetails.getIs_hidden().equals("1")) {
            holder.ib_ishidden.setVisibility(View.VISIBLE);
        } else {
            holder.ib_ishidden.setVisibility(View.GONE);
        }

        List<BannerListModel.ResultBean> imagesList = new ArrayList<>();
        List<String> documentsList = new ArrayList<>();

        for (int i = 0; i < feedDetails.getFeed_documents().size(); i++) {
            if (feedDetails.getFeed_documents().get(i).getDocument_type().equalsIgnoreCase("invitationimage")) {
                imagesList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_documents().get(i).getDocuments()));
            } else if (feedDetails.getFeed_documents().get(i).getDocument_type().equalsIgnoreCase("invitationdocument")) {
                documentsList.add(feedDetails.getFeed_documents().get(i).getDocuments());
            }
        }

        if (imagesList.size() != 0) {
            holder.imageSlider.setVisibility(View.VISIBLE);
            OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, imagesList);
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

        if (documentsList.size() != 0) {
            holder.tv_viewdocs.setVisibility(View.VISIBLE);
            if (documentsList.size() == 1) {
                holder.tv_viewdocs.setText(documentsList.size() + " Document Attached");
            } else {
                holder.tv_viewdocs.setText(documentsList.size() + " Documents Attached");
            }
            holder.tv_viewdocs.setPaintFlags(holder.tv_viewdocs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.tv_viewdocs.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });

        holder.ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });

        holder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedDetails.getCan_share().equals("1")) {
                    title = feedDetails.getFeed_title();
                    description = feedDetails.getFeed_text();
                    if (imagesList.size() != 0) {
                        numOfDocuments = imagesList.size();
                        downloadedImagesUriList = new ArrayList<>();
                        numOfFilesDownloaded = 0;
                        for (int i = 0; i < imagesList.size(); i++) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new DownloadDocumentForShare().execute(imagesList.get(i).getBanners_image());
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        }

                    } else {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/html");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title + "\n" + description + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK);
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    }
                } else {
                    Utilities.showMessage("You cannot share this feed", context, 2);
                }
            }
        });

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                powerMenu = getHamburgerPowerMenu(context, onHamburgerItemClickListener, onHamburgerMenuDismissedListener, feedDetails.getIs_hidden());
                powerMenu.showAsDropDown(v);
            }
        });

        holder.ib_ishidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showMessage("Post is marked hidden and is visible to you and group admin only", context, 2);
            }
        });

        holder.ll_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav = "0";

                if (feedDetails.getIs_favourite() == 0)
                    isFav = "1";
                else if (feedDetails.getIs_favourite() == 1)
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

        holder.tv_viewdocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentsList(documentsList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_feed_title, tv_feed_text, tv_comment, tv_viewdocs;
        private CardView cv_mainlayout;
        private ImageView imv_favourite;
        private LinearLayout ll_favourites, ll_comments, ll_share;
        private ImageButton imv_more, ib_ishidden;
        private SliderView imageSlider;

        public MyViewHolder(@NonNull View view) {
            super(view);

            imv_more = view.findViewById(R.id.imv_more);
            ib_ishidden = view.findViewById(R.id.ib_ishidden);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_feed_title = view.findViewById(R.id.tv_feed_title);
            tv_feed_text = view.findViewById(R.id.tv_feed_text);
            tv_time = view.findViewById(R.id.tv_time);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ib_ishidden = view.findViewById(R.id.ib_ishidden);
            tv_comment = view.findViewById(R.id.tv_comment);
            tv_viewdocs = view.findViewById(R.id.tv_viewdocs);
            ll_favourites = view.findViewById(R.id.ll_favourites);
            ll_comments = view.findViewById(R.id.ll_comments);
            ll_share = view.findViewById(R.id.ll_share);
            imv_favourite = view.findViewById(R.id.imv_favourite);
            imageSlider = view.findViewById(R.id.imageSlider);
        }
    }

    private class DownloadDocumentForShare extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        ProgressDialog pd;
        File file;

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
            downloadedImagesUriList.add(uri);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            numOfFilesDownloaded = numOfFilesDownloaded + 1;

            if (numOfFilesDownloaded == numOfDocuments) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("text/html");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title + "\n" + description + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, downloadedImagesUriList);
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        }
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    switch (item.getTitle()) {
                        case "Edit":
                            context.startActivity(new Intent(context, EditGroupFeeds_Activity.class)
                                    .putExtra("feedDetails", feedsList.get(itemClickedPosition)));
                            break;

                        case "Delete":
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builder.setMessage("Are you sure you want to delete this post?");
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.icon_alertred);
                            builder.setCancelable(false);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Utilities.isNetworkAvailable(context)) {
                                        new DeleteFeed().execute(String.valueOf(itemClickedPosition));
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
                        case "Unhide":
                            JsonObject mainObj = new JsonObject();
                            mainObj.addProperty("type", "updateFeedVisiblity");
                            mainObj.addProperty("user_id", userId);
                            mainObj.addProperty("feed_id", feedsList.get(itemClickedPosition).getId());
                            mainObj.addProperty("is_hide", "0");
                            if (Utilities.isNetworkAvailable(context)) {
                                new HideUnhidePost().execute(mainObj.toString());
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                            break;
                        case "Hide":
                            JsonObject mainObj1 = new JsonObject();
                            mainObj1.addProperty("type", "updateFeedVisiblity");
                            mainObj1.addProperty("user_id", userId);
                            mainObj1.addProperty("feed_id", feedsList.get(itemClickedPosition).getId());
                            mainObj1.addProperty("is_hide", "1");
                            if (Utilities.isNetworkAvailable(context)) {
                                new HideUnhidePost().execute(mainObj1.toString());
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                            break;
                    }
                }
            };

    private OnDismissedListener onHamburgerMenuDismissedListener =
            new OnDismissedListener() {
                @Override
                public void onDismissed() {

                }
            };

    private PowerMenu getHamburgerPowerMenu(Context context,
                                            OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener,
                                            OnDismissedListener onDismissedListener,
                                            String isHidden) {
        PowerMenu.Builder powerMenuBuilder = new PowerMenu.Builder(context);
        if (feedsList.get(itemClickedPosition).getCreated_by().equals(userId)) {
            powerMenuBuilder.addItem(new PowerMenuItem("Edit"));
        }
        powerMenuBuilder.addItem(new PowerMenuItem("Delete"));
        if (isHidden.equals("1")) {
            powerMenuBuilder.addItem(new PowerMenuItem("Unhide"));
        } else {
            powerMenuBuilder.addItem(new PowerMenuItem("Hide"));
        }
        powerMenuBuilder.setAutoDismiss(true);
        powerMenuBuilder.setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT);
        powerMenuBuilder.setMenuEffect(MenuEffect.BODY);
        powerMenuBuilder.setMenuRadius(10f);
        powerMenuBuilder.setMenuShadow(10f);
        powerMenuBuilder.setTextColor(context.getResources().getColor(R.color.black));
        powerMenuBuilder.setSelectedTextColor(Color.WHITE);
        powerMenuBuilder.setMenuColor(Color.WHITE);
        powerMenuBuilder.setSelectedMenuColor(context.getResources().getColor(R.color.colorPrimary));
        powerMenuBuilder.setOnMenuItemClickListener(onMenuItemClickListener);
        powerMenuBuilder.setOnDismissListener(onDismissedListener);
        powerMenuBuilder.setPreferenceName("HamburgerPowerMenu");
        powerMenuBuilder.setInitializeRule(Lifecycle.Event.ON_CREATE, 0);
        return powerMenuBuilder.build();
    }

    private void showDocumentsList(List<String> postDocuments) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Document List");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row_ellipsize);

        for (int i = 0; i < postDocuments.size(); i++) {
            arrayAdapter.add(postDocuments.get(i));
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
                    new DownloadDocument().execute(IMAGE_LINK + "feed_doc/" + postDocuments.get(which));
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
        builderSingle.show();
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
            param.add(new ParamsPojo("feed_id", feedsList.get(itemClickedPosition).getId()));
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

    private class HideUnhidePost extends AsyncTask<String, Void, String> {
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
        File file;

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
            if (aBoolean) {
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
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void swap(List<GroupFeedsModel.ResultBean> datas) {
        feedsList.clear();
        feedsList.addAll(datas);
        notifyDataSetChanged();
    }


}
