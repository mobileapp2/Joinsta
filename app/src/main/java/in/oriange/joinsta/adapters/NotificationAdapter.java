package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.Notification_Activity;
import in.oriange.joinsta.models.NotificationListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;

    public NotificationAdapter(Context context, List<NotificationListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
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
        View view = inflater.inflate(R.layout.list_row_notification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final NotificationListModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_title.setText(searchDetails.getTitle());
        holder.tv_message.setText(searchDetails.getDescription());

        if (!searchDetails.getImage().equals("")) {
            String url = IMAGE_LINK + "notifications/" + searchDetails.getImage();
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_notificationimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imv_notificationimg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.imv_notificationimg.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.imv_notificationimg.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this notification?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteNotification().execute(searchDetails.getUsernotification_id());
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
        private ImageView imv_notificationimg;
        private TextView tv_title, tv_message;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_notificationimg = view.findViewById(R.id.imv_notificationimg);
            tv_title = view.findViewById(R.id.tv_title);
            tv_message = view.findViewById(R.id.tv_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
            obj.addProperty("type", "deleteusernotification");
            obj.addProperty("usernotification_id", params[0]);
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
                        new Notification_Activity.GetNotification().execute();
                        Utilities.showMessage("User feedback deleted successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
