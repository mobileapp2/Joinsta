package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupNotifications_Activity;
import in.oriange.joinsta.activities.MyGroupDetails_Activity;
import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.models.MyGroupsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.MyViewHolder> {

    private List<MyGroupsListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;

    public MyGroupsAdapter(Context context, List<MyGroupsListModel.ResultBean> resultArrayList) {
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
        View view = inflater.inflate(R.layout.list_row_mygroups, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final MyGroupsListModel.ResultBean groupDetails = resultArrayList.get(position);

        holder.tv_heading.setText(groupDetails.getGroup_code() + "-" + groupDetails.getGroup_name());

        if (groupDetails.getStatus().equals("accepted")) {
            holder.ll_buttons.setVisibility(View.VISIBLE);
            holder.tv_status.setVisibility(View.GONE);
        } else if (groupDetails.getStatus().equals("left")) {
            holder.ll_buttons.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("Left");
        } else if (groupDetails.getStatus().equals("requested")) {
            holder.ll_buttons.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("Requested");
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MyGroupDetails_Activity.class)
                        .putExtra("groupDetails", groupDetails)
                        .putExtra("type", "2"));
            }
        });


        holder.ib_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_groupsettings, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Group Settings");
                alertDialogBuilder.setView(promptView);

                final SwitchCompat sw_notifications = promptView.findViewById(R.id.sw_notifications);
                final SwitchCompat sw_hidden = promptView.findViewById(R.id.sw_hidden);
                final Button btn_save = promptView.findViewById(R.id.btn_save);

                if (groupDetails.getAllow_notifiication().equals("1")) {
                    sw_notifications.setChecked(true);
                }

                if (groupDetails.getIs_hidden().equals("1")) {
                    sw_hidden.setChecked(true);
                }

                final AlertDialog alertD = alertDialogBuilder.create();

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();

                            String allow_notification = "0";
                            String is_hidden = "0";

                            if (sw_notifications.isChecked()) {
                                allow_notification = "1";
                            }

                            if (sw_hidden.isChecked()) {
                                is_hidden = "1";
                            }

                            if (Utilities.isNetworkAvailable(context)) {
                                new UpdateGroupSettings().execute(
                                        userId,
                                        groupDetails.getId(),
                                        allow_notification,
                                        is_hidden);
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }

                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                });

                alertD.show();
            }
        });

        holder.ib_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.ib_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupNotifications_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });

        holder.ib_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to exit this group?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ExitGroup().execute(
                                userId,
                                groupDetails.getId(),
                                "left"
                        );
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
        private TextView tv_heading;
        private LinearLayout ll_buttons;
        private TextView tv_status;
        private ImageButton ib_settings, ib_notifications, ib_exit;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_heading = view.findViewById(R.id.tv_heading);
            ib_settings = view.findViewById(R.id.ib_settings);
            ib_notifications = view.findViewById(R.id.ib_notifications);
            ib_exit = view.findViewById(R.id.ib_exit);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            tv_status = view.findViewById(R.id.tv_status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class UpdateGroupSettings extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "updatesetting");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("group_id", params[1]);
            obj.addProperty("allow_notification", params[2]);
            obj.addProperty("is_hidden", params[3]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
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
                        new Groups_Fragment.GetMyGroupsList().execute();
                        Utilities.showMessage("Group settings updated successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ExitGroup extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "exitgroup");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("group_id", params[1]);
            obj.addProperty("status", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
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
                        new Groups_Fragment.GetMyGroupsList().execute();
                        Utilities.showMessage("Group left successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
