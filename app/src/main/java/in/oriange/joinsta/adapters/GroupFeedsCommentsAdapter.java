package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupFeedsCommentsReplys_Activity;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupFeedsCommentsAdapter extends RecyclerView.Adapter<GroupFeedsCommentsAdapter.MyViewHolder> {

    private String userId;
    private Context context;
    private List<GroupFeedsModel.ResultBean.FeedCommentsBean> feedCommentsList;
    private PrettyTime p;

    private PowerMenu powerMenu;
    public static int itemClickedPosition = 0;
    public static boolean isCommentClicked = false;

    public GroupFeedsCommentsAdapter(Context context, List<GroupFeedsModel.ResultBean.FeedCommentsBean> feedCommentsList) {
        this.context = context;
        this.feedCommentsList = feedCommentsList;
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
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_feeds_comments, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final GroupFeedsModel.ResultBean.FeedCommentsBean commentsDetails = feedCommentsList.get(position);

        if (commentsDetails.getCreated_by().equals(userId)) {
            holder.imv_more.setVisibility(View.VISIBLE);
        } else {
            holder.imv_more.setVisibility(View.GONE);
        }

        if (!commentsDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(commentsDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)
                    .centerCrop()
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(commentsDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(commentsDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tv_comment.setText(commentsDetails.getMessage());

        if (commentsDetails.getComment_reply().size() != 0) {
            holder.tv_replies.setVisibility(View.VISIBLE);
            if (commentsDetails.getComment_reply().size() == 1) {
                holder.tv_replies.setText("1 Reply");
            } else {
                holder.tv_replies.setText(commentsDetails.getComment_reply().size() + " Replies");
            }
        } else {
            holder.tv_replies.setVisibility(View.GONE);
        }

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                isCommentClicked = true;
                context.startActivity(new Intent(context, GroupFeedsCommentsReplys_Activity.class)
                        .putExtra("commentsDetails", commentsDetails));
            }
        });

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                powerMenu = getHamburgerPowerMenu(context, onHamburgerItemClickListener, onHamburgerMenuDismissedListener);
                powerMenu.showAsDropDown(v);

            }
        });

    }

    @Override
    public int getItemCount() {
        return feedCommentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_mainlayout;
        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_comment;
        private TextView tv_replies;
        private ImageButton imv_more;

        public MyViewHolder(@NonNull View view) {
            super(view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_time = view.findViewById(R.id.tv_time);
            tv_comment = view.findViewById(R.id.tv_comment);
            tv_replies = view.findViewById(R.id.tv_replies);
            imv_more = view.findViewById(R.id.imv_more);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    switch (position) {
                        case 0:
                            LayoutInflater layoutInflater = LayoutInflater.from(context);
                            View promptView = layoutInflater.inflate(R.layout.dialog_layout_edit_comment, null);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            alertDialogBuilder.setView(promptView);

                            final EditText edt_comment = promptView.findViewById(R.id.edt_comment);
                            final Button btn_save = promptView.findViewById(R.id.btn_save);

                            edt_comment.setText(feedCommentsList.get(itemClickedPosition).getMessage());

                            final AlertDialog editAlertD = alertDialogBuilder.create();

                            btn_save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!edt_comment.getText().toString().trim().isEmpty()) {
                                        if (Utilities.isNetworkAvailable(context)) {
                                            editAlertD.dismiss();
                                            JsonObject mainObj = new JsonObject();
                                            mainObj.addProperty("type", "updateFeedCommentDetails");
                                            mainObj.addProperty("user_id", userId);
                                            mainObj.addProperty("feed_comment_id", feedCommentsList.get(itemClickedPosition).getId());
                                            mainObj.addProperty("edit_message", edt_comment.getText().toString().trim());
                                            mainObj.addProperty("edit_is_private", "0");

                                            if (Utilities.isNetworkAvailable(context)) {
                                                new EditComment().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
                                            } else {
                                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                                            }
                                        } else {
                                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                                        }
                                    }

                                }
                            });

                            editAlertD.show();


                            break;

                        case 1:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builder.setMessage("Are you sure you want to delete this comment?");
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.icon_alertred);
                            builder.setCancelable(false);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Utilities.isNetworkAvailable(context)) {
                                        new DeleteComment().execute(String.valueOf(itemClickedPosition));
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
                .addItem(new PowerMenuItem("Edit"))
                .addItem(new PowerMenuItem("Delete"))
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

    private class DeleteComment extends AsyncTask<String, Void, String> {
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
            param.add(new ParamsPojo("type", "deleteFeedCommentDetails"));
            param.add(new ParamsPojo("feed_comment_id", feedCommentsList.get(itemClickedPosition).getId()));
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
                        Utilities.showMessage("Comment deleted successfully", context, 1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class EditComment extends AsyncTask<String, Void, String> {
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
                        Utilities.showMessage("Comment updated successfully", context, 1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void swap(List<GroupFeedsModel.ResultBean.FeedCommentsBean> datas) {
        feedCommentsList.clear();
        feedCommentsList.addAll(datas);
        notifyDataSetChanged();
    }
}
