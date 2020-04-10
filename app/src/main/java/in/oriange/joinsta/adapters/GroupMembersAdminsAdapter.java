package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditGroupMembersAdmin_Activity;
import in.oriange.joinsta.models.GroupMembersAdminsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class GroupMembersAdminsAdapter extends RecyclerView.Adapter<GroupMembersAdminsAdapter.MyViewHolder> {

    private Context context;
    private List<GroupMembersAdminsListModel.ResultBean> participantsList;
    private String groupId, userId;

    public GroupMembersAdminsAdapter(Context context, List<GroupMembersAdminsListModel.ResultBean> participantsList, String groupId) {
        this.context = context;
        this.participantsList = participantsList;
        this.groupId = groupId;

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
        View view = inflater.inflate(R.layout.list_row_group_membersuperviosr, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final GroupMembersAdminsListModel.ResultBean memberDetails = participantsList.get(position);

        holder.tv_name.setText(memberDetails.getFirst_name());

        if (!memberDetails.getMobile().isEmpty() && !memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(memberDetails.getCountry_code() + " " + memberDetails.getMobile() + " | " + memberDetails.getEmail());
        } else if (!memberDetails.getMobile().isEmpty() && memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(memberDetails.getCountry_code() + " " + memberDetails.getMobile());
        } else if (memberDetails.getMobile().isEmpty() && !memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(memberDetails.getEmail());
        } else if (memberDetails.getMobile().isEmpty() && memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setVisibility(View.GONE);
        }

        if (memberDetails.getRole().equals("group_admin"))
            holder.tv_role.setText("Group Admin");
        else if (memberDetails.getRole().equals("group_member"))
            holder.tv_role.setText("Group Member");

        if (memberDetails.getIs_active().equals("1"))
            holder.sw_active.setChecked(true);
        else
            holder.sw_active.setChecked(false);

        if (memberDetails.getCan_pos().equals("1"))
            holder.sw_can_post.setChecked(true);
        else
            holder.sw_can_post.setChecked(false);

        if (memberDetails.getIs_hidden().equals("1"))
            holder.ib_ishidden.setVisibility(View.VISIBLE);
        else
            holder.ib_ishidden.setVisibility(View.GONE);

        if (memberDetails.getIs_joinsta_member().equals("1"))
            holder.btn_invite.setVisibility(View.GONE);
        else
            holder.btn_invite.setVisibility(View.VISIBLE);

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ll_buttons.getVisibility() == View.GONE) {
                    holder.ll_buttons.setVisibility(View.VISIBLE);
                    holder.view_divider.setVisibility(View.VISIBLE);
                } else if (holder.ll_buttons.getVisibility() == View.VISIBLE) {
                    holder.ll_buttons.setVisibility(View.GONE);
                    holder.view_divider.setVisibility(View.GONE);
                }

            }
        });

        holder.ib_ishidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showMessage("User has marked himself/herself as hidden member", context, 2);
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditGroupMembersAdmin_Activity.class)
                        .putExtra("memberDetails", memberDetails)
                        .putExtra("groupId", groupId));
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numOfAdmins = 0;
                if (memberDetails.getRole().equals("group_admin")) {
                    for (GroupMembersAdminsListModel.ResultBean resultBean : participantsList)
                        if (resultBean.getRole().equals("group_admin") && resultBean.getIs_joinsta_member().equals("1"))
                            numOfAdmins = numOfAdmins + 1;

                    if (numOfAdmins == 1)
                        if (memberDetails.getIs_joinsta_member().equals("1"))
                            Utilities.showAlertDialogNormal(context, "You cannot delete this group admin");
                        else
                            deleteDialog(memberDetails);
                    else
                        deleteDialog(memberDetails);

                } else if (memberDetails.getRole().equals("group_member")) {
                    deleteDialog(memberDetails);
                } else {
                    deleteDialog(memberDetails);
                }
            }
        });

        holder.sw_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Utilities.isNetworkAvailable(context))
                        new SetIsActive().execute(memberDetails.getId(), "1");
                    else
                        Utilities.showMessage("Please check your internet connection", context, 2);
                } else {
                    if (Utilities.isNetworkAvailable(context))
                        new SetIsActive().execute(memberDetails.getId(), "0");
                    else
                        Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        holder.sw_can_post.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Utilities.isNetworkAvailable(context))
                        new CanPost().execute(memberDetails.getId(), "1");
                    else
                        Utilities.showMessage("Please check your internet connection", context, 2);
                } else {
                    if (Utilities.isNetworkAvailable(context))
                        new CanPost().execute(memberDetails.getId(), "0");
                    else
                        Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        holder.ib_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    provideCallPremission(context);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to make a call?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.icon_call);
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + memberDetails.getCountry_code() + "" + memberDetails.getMobile())));
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
                }
            }
        });

        holder.btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to send an invitation?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isNetworkAvailable(context)) {
                            new SendInviteSMS().execute(groupId, userId, memberDetails.getId());
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private LinearLayout ll_buttons;
        private View view_divider;
        private TextView tv_name, tv_mobile_email, tv_role;
        private Button btn_edit, btn_delete;
        private Switch sw_active, sw_can_post;
        private ImageButton ib_call, ib_ishidden;
        private Button btn_invite;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            view_divider = view.findViewById(R.id.view_divider);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile_email = view.findViewById(R.id.tv_mobile_email);
            tv_role = view.findViewById(R.id.tv_role);
            btn_edit = view.findViewById(R.id.btn_edit);
            btn_delete = view.findViewById(R.id.btn_delete);
            sw_active = view.findViewById(R.id.sw_active);
            sw_can_post = view.findViewById(R.id.sw_can_post);
            ib_call = view.findViewById(R.id.ib_call);
            ib_ishidden = view.findViewById(R.id.ib_ishidden);
            btn_invite = view.findViewById(R.id.btn_invite);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void deleteDialog(GroupMembersAdminsListModel.ResultBean memberDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setMessage("Are you sure you want to delete this member?");
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.icon_alertred);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Utilities.isNetworkAvailable(context))
                    new DeleteGroupMember().execute(memberDetails.getId());
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

    private class DeleteGroupMember extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "deleteGroupMemberDetails"));
            param.add(new ParamsPojo("member_id", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
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
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupMembersAdmins_Activity"));
                        Utilities.showMessage("Group member deleted successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SetIsActive extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "setactive");
            obj.addProperty("id", params[0]);
            obj.addProperty("is_active", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMISACTIVEAPI, obj.toString());
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
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Active status changed successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CanPost extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "postFeedAction");
            obj.addProperty("member_id", params[0]);
            obj.addProperty("is_post", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.FEEDSAPI, obj.toString());
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
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Can post status changed successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendInviteSMS extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "sendInviteSMS");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("sender_id", params[1]);
            obj.addProperty("receiver_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Invitation sent successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
