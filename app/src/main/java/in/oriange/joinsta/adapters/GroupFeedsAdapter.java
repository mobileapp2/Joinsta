package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditGroupFeeds_Activity;
import in.oriange.joinsta.activities.GroupFeedsComments_Activity;
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

    private File downloadedDocsfolder, file;
    private String description;

    private PowerMenu powerMenu;
    public static int itemClickedPosition = 0;

    public GroupFeedsAdapter(Context context, List<GroupFeedsModel.ResultBean> feedsList) {
        this.context = context;
        this.feedsList = feedsList;
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

        if (feedDetails.getCreated_by().equals(userId)) {
            holder.imv_more.setVisibility(View.VISIBLE);
        } else {
            holder.imv_more.setVisibility(View.GONE);
        }

        if (!feedDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(feedDetails.getImage_url().trim())
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

        holder.tv_feed_text.setText(feedDetails.getFeed_text());

        if (!feedDetails.getFeed_doc().equals("")) {
            String url = IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc();
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_feed_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.cv_feed_image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.cv_feed_image.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.cv_feed_image.setVisibility(View.GONE);
        }

        if (feedDetails.getFeed_comments().size() != 0) {
            if (feedDetails.getFeed_comments().size() == 1) {
                holder.btn_comment.setText("1 Comment");
            } else {
                holder.btn_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
            }
        }

        if (feedDetails.getIs_favourite() == 1) {
            holder.cb_like.setChecked(true);
        } else {
            holder.cb_like.setChecked(false);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });

        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });

        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = feedDetails.getFeed_text();
                if (!feedDetails.getFeed_doc().equals("")) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new DownloadDocumentForShare().execute(IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc());
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/html");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, description);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            }
        });

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                powerMenu = getHamburgerPowerMenu(context,
                        onHamburgerItemClickListener, onHamburgerMenuDismissedListener);
                powerMenu.showAsDropDown(v);
            }
        });

        holder.cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav;

                if (holder.cb_like.isChecked())
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
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_feed_text;
        private CardView cv_mainlayout, cv_feed_image;
        private ImageView imv_feed_image;
        private Button btn_comment, btn_share;
        private ImageButton imv_more;
        private CheckBox cb_like;

        public MyViewHolder(@NonNull View view) {
            super(view);

            imv_more = view.findViewById(R.id.imv_more);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_feed_text = view.findViewById(R.id.tv_feed_text);
            tv_time = view.findViewById(R.id.tv_time);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            cv_feed_image = view.findViewById(R.id.cv_feed_image);
            imv_feed_image = view.findViewById(R.id.imv_feed_image);
            btn_comment = view.findViewById(R.id.btn_comment);
            btn_share = view.findViewById(R.id.btn_share);
            cb_like = view.findViewById(R.id.cb_like);

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
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/html");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, description);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));

        }
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    switch (position) {
                        case 0:
                            context.startActivity(new Intent(context, EditGroupFeeds_Activity.class)
                                    .putExtra("feedDetails", feedsList.get(itemClickedPosition)));
                            break;

                        case 1:
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

                    }
                }
            };

    private OnDismissedListener onHamburgerMenuDismissedListener =
            new OnDismissedListener() {
                @Override
                public void onDismissed() {

                }
            };


    public static PowerMenu getHamburgerPowerMenu(
            Context context,
            OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener,
            OnDismissedListener onDismissedListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("EDIT"))
                .addItem(new PowerMenuItem("DELETE"))
                .setAutoDismiss(true)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuEffect(MenuEffect.BODY)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(context.getResources().getColor(R.color.black))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(context.getResources().getColor(R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .setOnDismissListener(onDismissedListener)
                .setPreferenceName("HamburgerPowerMenu")
                .setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
                .build();
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
