package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.EventNotificationsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;
import jp.shts.android.library.TriangleLabelView;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class EventNotificationAdapter extends RecyclerView.Adapter<EventNotificationAdapter.MyViewHolder> {

    private List<EventNotificationsModel.ResultBean> resultArrayList;
    private Context context;
    private PrettyTime p;

    public EventNotificationAdapter(Context context, List<EventNotificationsModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        p = new PrettyTime();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_groupnotification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final EventNotificationsModel.ResultBean notificationDetails = resultArrayList.get(position);

        holder.tv_title.setText(notificationDetails.getSubject().trim());
        holder.tv_message.setText(notificationDetails.getMessage().trim());

        if (notificationDetails.getIs_fav().equalsIgnoreCase("1"))
            holder.cb_like.setChecked(true);
        else
            holder.cb_like.setChecked(false);


        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            holder.tv_time.setText(notificationDetails.getSender_name());
        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                holder.tv_time.setText(notificationDetails.getSender_name() + " | " + p.format(formatter.parse(notificationDetails.getCreated_at())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (notificationDetails.getIs_read().equals("0")) {
            holder.ll_outimage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_message.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_new.setVisibility(View.VISIBLE);
        } else {
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.mediumGray));
            holder.tv_message.setTextColor(context.getResources().getColor(R.color.mediumGray));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.mediumGray));
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    holder.ll_outimage.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.mediumGray));
                    holder.tv_message.setTextColor(context.getResources().getColor(R.color.mediumGray));
                    holder.tv_time.setTextColor(context.getResources().getColor(R.color.mediumGray));
                    holder.tv_new.setVisibility(View.GONE);

                    showNotification(notificationDetails);
                } else {
                    Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        holder.cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav = holder.cb_like.isChecked() ? "1" : "0";

                if (Utilities.isNetworkAvailable(context))
                    new MarkFavourite().execute(notificationDetails.getEvent_not_details_id(), isFav);
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private ImageView imv_notificationimg;
        private TextView tv_title, tv_message, tv_time;
        private TriangleLabelView tv_new;
        private LinearLayout ll_outimage;
        private CheckBox cb_like;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_notificationimg = view.findViewById(R.id.imv_notificationimg);
            tv_title = view.findViewById(R.id.tv_title);
            tv_message = view.findViewById(R.id.tv_message);
            tv_time = view.findViewById(R.id.tv_time);
            tv_new = view.findViewById(R.id.tv_new);
            ll_outimage = view.findViewById(R.id.ll_outimage);
            cb_like = view.findViewById(R.id.cb_like);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showNotification(final EventNotificationsModel.ResultBean notificationDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_notification, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_notificationimg = promptView.findViewById(R.id.imv_notificationimg);
        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final TextView tv_message = promptView.findViewById(R.id.tv_message);
        final TextView tv_time = promptView.findViewById(R.id.tv_time);
        final Button btn_download = promptView.findViewById(R.id.btn_download);
        final Button btn_delete = promptView.findViewById(R.id.btn_delete);
        final Button btn_share = promptView.findViewById(R.id.btn_share);
        final Button btn_close = promptView.findViewById(R.id.btn_close);
        final ImageButton imb_close = promptView.findViewById(R.id.imb_close);
        final TextView tv_viewdocs = promptView.findViewById(R.id.tv_viewdocs);

        imv_notificationimg.setVisibility(View.GONE);
        btn_download.setVisibility(View.GONE);

        tv_title.setText(notificationDetails.getSubject().trim());
        tv_message.setText(notificationDetails.getMessage().trim());
        Linkify.addLinks(tv_message, Linkify.ALL);

        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            tv_time.setText(notificationDetails.getSender_name());
        } else {
            tv_time.setText(notificationDetails.getSender_name() + " | " + changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", notificationDetails.getCreated_at()));
        }

        if (notificationDetails.getIs_read().equals("0")) {
            if (Utilities.isNetworkAvailable(context))
                new ReadNotification().execute(notificationDetails.getEvent_not_details_id());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        }

        if (notificationDetails.getCan_share().equals("1")) {
            btn_share.setVisibility(View.VISIBLE);
        } else
            btn_share.setVisibility(View.GONE);

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertD.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this notification?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertD.dismiss();
                        if (Utilities.isNetworkAvailable(context))
                            new DeleteNotification().execute(notificationDetails.getEvent_not_details_id());
                        else
                            Utilities.showMessage("Please check your internet connection", context, 2);
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
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = notificationDetails.getSubject();
                String description = notificationDetails.getMessage();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/html");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title + "\n" + description+ "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK);
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        imb_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertD.dismiss();
            }
        });

        alertD.show();
    }

    private class DeleteNotification extends AsyncTask<String, Void, String> {

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
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteEventNotification");
            obj.addProperty("event_not_details_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsNotifications_Activity"));
                        Utilities.showMessage("Notification deleted successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadNotification extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "markReadEventNotification");
            obj.addProperty("event_not_details_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsNotifications_Activity"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    private class MarkFavourite extends AsyncTask<String, Void, String> {

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
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "markFavEventNotification");
            obj.addProperty("event_not_details_id", params[0]);
            obj.addProperty("is_fav", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupNotifications_Activity"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh(List<EventNotificationsModel.ResultBean> resultArrayList) {
        this.resultArrayList = resultArrayList;
        notifyDataSetChanged();
    }

}
