package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.models.GroupRequestsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupRequestsAdapter extends RecyclerView.Adapter<GroupRequestsAdapter.MyViewHolder> {

    private Context context;
    private List<GroupRequestsListModel.ResultBean> resultList;
    private String userId;

    public GroupRequestsAdapter(Context context, List<GroupRequestsListModel.ResultBean> resultList) {
        this.context = context;
        this.resultList = resultList;

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
        View view = inflater.inflate(R.layout.list_row_group_requests, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final GroupRequestsListModel.ResultBean memberDetails = resultList.get(position);

        holder.tv_name.setText(memberDetails.getGroup_code() + " " + memberDetails.getGroup_name());
        holder.tv_mobile_email.setText("From - " + memberDetails.getSender_name());

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new AcceptRejectUserRequest().execute(userId, "accepted", memberDetails.getId());
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new AcceptRejectUserRequest().execute(userId, "rejected", memberDetails.getId());
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private LinearLayout ll_buttons;
        private View view_divider;
        private TextView tv_name, tv_mobile_email;
        private Button btn_accept, btn_reject;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            view_divider = view.findViewById(R.id.view_divider);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile_email = view.findViewById(R.id.tv_mobile_email);
            btn_accept = view.findViewById(R.id.btn_accept);
            btn_reject = view.findViewById(R.id.btn_reject);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class AcceptRejectUserRequest extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;
        private String Type;

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
            Type = params[2];
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "AddGroupMembers");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("status", params[1]);
            obj.addProperty("group_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupRequests_Activity"));
                        new Groups_Fragment.GetMyGroupsList().execute();
                        new Groups_Fragment.GetRequestedGroups().execute();
                        if (Type.equals("accepted")) {
                            Utilities.showMessage("Group request accepted", context, 1);
                        } else if (Type.equals("rejected")) {
                            Utilities.showMessage("Group request rejected", context, 1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
