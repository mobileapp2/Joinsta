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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupMembersProfileDetails_Activity;
import in.oriange.joinsta.models.GroupMemebersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersListAdapter extends RecyclerView.Adapter<GroupMembersListAdapter.MyViewHolder> {

    private List<GroupMemebersListModel.ResultBean> resultArrayList;
    private Context context;
    private String userId;

    public GroupMembersListAdapter(Context context, List<GroupMemebersListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

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
        View view = inflater.inflate(R.layout.list_row_grpmembers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GroupMemebersListModel.ResultBean memberDetails = resultArrayList.get(position);

        holder.tv_name.setText(memberDetails.getFirst_name().trim());
        holder.tv_mobile.setText(memberDetails.getMobile());

        if (memberDetails.getIs_joinsta_member().equals("0")) {
            holder.btn_invite.setVisibility(View.VISIBLE);
        }

        if (memberDetails.getIs_hidden().equals("1")) {
            holder.ib_ishidden.setVisibility(View.VISIBLE);
        }

        if (!memberDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(memberDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .into(holder.imv_user, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.imv_user.setVisibility(View.VISIBLE);
        }

        holder.ib_ishidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showMessage("User has marked himself/herself as hidden member", context, 2);
            }
        });

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (memberDetails.getIs_joinsta_member().equals("1")) {
                    context.startActivity(new Intent(context, GroupMembersProfileDetails_Activity.class)
                            .putExtra("userId", memberDetails.getId())
                            .putExtra("name", memberDetails.getFirst_name().trim())
                            .putExtra("imageUrl", memberDetails.getImage_url().trim()));
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
                            new SendInviteSMS().execute(memberDetails.getGroup_id(), userId, memberDetails.getId());
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
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private CircleImageView imv_user;
        private ProgressBar progressBar;
        private Button btn_invite;
        private TextView tv_name, tv_mobile;
        private ImageButton ib_ishidden;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_user = view.findViewById(R.id.imv_user);
            progressBar = view.findViewById(R.id.progressBar);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile = view.findViewById(R.id.tv_mobile);
            btn_invite = view.findViewById(R.id.btn_invite);
            ib_ishidden = view.findViewById(R.id.ib_ishidden);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SendInviteSMS extends AsyncTask<String, Void, String> {

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
