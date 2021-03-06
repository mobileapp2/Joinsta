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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditGroupMemberSupervisor_Activity;
import in.oriange.joinsta.models.GroupSupervisorsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyViewHolder> {

    private Context context;
    private List<GroupSupervisorsListModel.ResultBean> groupmembers;
    private String groupId;

    public GroupMembersAdapter(Context context, List<GroupSupervisorsListModel.ResultBean> groupmembers, String groupId) {
        this.context = context;
        this.groupmembers = groupmembers;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_group_membersuperviosr, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final GroupSupervisorsListModel.ResultBean memberDetails = groupmembers.get(position);

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

        if (memberDetails.getIs_active().equals("1"))
            holder.sw_active.setChecked(true);
        else
            holder.sw_active.setChecked(false);

        if (memberDetails.getCan_post().equals("1"))
            holder.sw_can_post.setChecked(true);
        else
            holder.sw_can_post.setChecked(false);

        if (memberDetails.getIs_hidden().equals("1"))
            holder.ib_ishidden.setVisibility(View.VISIBLE);
        else
            holder.ib_ishidden.setVisibility(View.GONE);

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
                context.startActivity(new Intent(context, EditGroupMemberSupervisor_Activity.class)
                        .putExtra("memberDetails", memberDetails)
                        .putExtra("groupId", groupId)
                        .putExtra("role", "group_member"));
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    }

    @Override
    public int getItemCount() {
        return groupmembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private LinearLayout ll_buttons;
        private View view_divider;
        private TextView tv_name, tv_mobile_email;
        private Button btn_edit, btn_delete;
        private Switch sw_active, sw_can_post;
        private ImageButton ib_call, ib_ishidden;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            view_divider = view.findViewById(R.id.view_divider);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile_email = view.findViewById(R.id.tv_mobile_email);
            btn_edit = view.findViewById(R.id.btn_edit);
            btn_delete = view.findViewById(R.id.btn_delete);
            sw_active = view.findViewById(R.id.sw_active);
            sw_can_post = view.findViewById(R.id.sw_can_post);
            ib_call = view.findViewById(R.id.ib_call);
            ib_ishidden = view.findViewById(R.id.ib_ishidden);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupMembers_Fragment"));
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

}
