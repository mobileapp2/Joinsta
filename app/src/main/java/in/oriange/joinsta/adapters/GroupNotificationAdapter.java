package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupNotifications_Activity;
import in.oriange.joinsta.models.GroupNotificationListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class GroupNotificationAdapter extends RecyclerView.Adapter<GroupNotificationAdapter.MyViewHolder> {

    private List<GroupNotificationListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;
    private PrettyTime p;

    public GroupNotificationAdapter(Context context, List<GroupNotificationListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        p = new PrettyTime();
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        final GroupNotificationListModel.ResultBean notificationDetails = resultArrayList.get(position);

        holder.tv_title.setText(notificationDetails.getSubject().trim());
        holder.tv_message.setText(notificationDetails.getMessage().trim());
        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            holder.tv_time.setText("");
        } else {
            holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", notificationDetails.getCreated_at()));
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification(notificationDetails);
            }
        });

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this notification?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context))
                            new DeleteNotification().execute(notificationDetails.getMsg_details_id());
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


    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private ImageButton ib_delete;
        private TextView tv_title, tv_message, tv_time;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_title = view.findViewById(R.id.tv_title);
            tv_message = view.findViewById(R.id.tv_message);
            ib_delete = view.findViewById(R.id.ib_delete);
            tv_time = view.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showNotification(GroupNotificationListModel.ResultBean notificationDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_notification, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_notificationimg = promptView.findViewById(R.id.imv_notificationimg);
        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final TextView tv_message = promptView.findViewById(R.id.tv_message);
        final TextView tv_time = promptView.findViewById(R.id.tv_time);
        imv_notificationimg.setVisibility(View.GONE);

        tv_title.setText(notificationDetails.getSubject().trim());
        tv_message.setText(notificationDetails.getMessage().trim());
        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            tv_time.setText("");
        } else {
            tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", notificationDetails.getCreated_at()));
        }

        final AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public class DeleteNotification extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteGroupNotification");
            obj.addProperty("msg_details_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
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
                        new GroupNotifications_Activity.GetGroupNotification().execute();
                        Utilities.showMessage("Notification deleted successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
