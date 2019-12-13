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

import com.airbnb.lottie.LottieAnimationView;
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

        holder.tv_heading.setText(groupDetails.getGroup_code() + " - " + groupDetails.getGroup_name());

        if (groupDetails.getStatus().equalsIgnoreCase("accepted")) {
            holder.ll_buttons.setVisibility(View.VISIBLE);
            holder.tv_status.setVisibility(View.GONE);
            holder.btn_rejoin.setVisibility(View.GONE);
            holder.tv_role.setVisibility(View.INVISIBLE);
        } else if (groupDetails.getStatus().equalsIgnoreCase("left")) {
            holder.ll_buttons.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.GONE);
            holder.btn_rejoin.setVisibility(View.VISIBLE);
            holder.tv_role.setVisibility(View.GONE);
        } else if (groupDetails.getStatus().equalsIgnoreCase("requested")) {
            holder.ll_buttons.setVisibility(View.GONE);
            holder.btn_rejoin.setVisibility(View.VISIBLE);
            holder.tv_role.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("Requested to join this group");
            holder.btn_rejoin.setText("Cancel");
        } else if (groupDetails.getStatus().equalsIgnoreCase("rejected")) {
            holder.ll_buttons.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.btn_rejoin.setVisibility(View.VISIBLE);
            holder.tv_role.setVisibility(View.GONE);
            holder.tv_status.setText("Request is rejected");
            holder.btn_rejoin.setText("Cancel");
        }

        if (groupDetails.getIs_admin().equals("1")) {
            holder.tv_role.setVisibility(View.VISIBLE);
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
                context.startActivity(new Intent(context, GroupNotifications_Activity.class)
                        .putExtra("groupId", groupDetails.getId())
                        .putExtra("groupName", groupDetails.getGroup_name()));
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
                builder.setPositiveButton("EXIT GROUP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ExitGroup().execute(
                                userId,
                                groupDetails.getId(),
                                "left"
                        );
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        holder.btn_rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupDetails.getStatus().equalsIgnoreCase("left")) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new JoinGroup().execute(groupDetails.getId());
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else if (groupDetails.getStatus().equalsIgnoreCase("requested") || groupDetails.getStatus().equalsIgnoreCase("rejected")) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new CancelRequest().execute(userId, groupDetails.getId());
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_heading, tv_role;
        private LinearLayout ll_buttons;
        private TextView tv_status;
        private Button btn_rejoin;
        private ImageButton ib_settings, ib_notifications, ib_exit;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_role = view.findViewById(R.id.tv_role);
            ib_settings = view.findViewById(R.id.ib_settings);
            ib_notifications = view.findViewById(R.id.ib_notifications);
            ib_exit = view.findViewById(R.id.ib_exit);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            tv_status = view.findViewById(R.id.tv_status);
            btn_rejoin = view.findViewById(R.id.btn_rejoin);
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

    private class JoinGroup extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "joingroup");
            obj.addProperty("status", "requested");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("user_id", userId);
            obj.addProperty("role", "group_member");
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

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Your request to join this group is submitted successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CancelRequest extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "cancelRequest");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("group_id", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
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
                        new Groups_Fragment.GetMyGroupsList().execute();

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Request canceled successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                            }
                        });

                        alertD.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
