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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.GroupMembersRequestsModel;
import in.oriange.joinsta.models.GroupMembersRequestsModel.ResultBean;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersRequestsAdapter extends RecyclerView.Adapter<GroupMembersRequestsAdapter.MyViewHolder> {

    private Context context;
    private List<GroupMembersRequestsModel.ResultBean> resultList;

    public GroupMembersRequestsAdapter(Context context, List<GroupMembersRequestsModel.ResultBean> resultList) {
        this.context = context;
        this.resultList = resultList;
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
        final GroupMembersRequestsModel.ResultBean memberDetails = resultList.get(position);

        holder.tv_name.setText(memberDetails.getFirst_name());

        if (!memberDetails.getMobile().isEmpty() && !memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText("+" + memberDetails.getCountry_code() + " " + memberDetails.getMobile() + " | " + memberDetails.getEmail());
        } else if (!memberDetails.getMobile().isEmpty() && memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText("+" + memberDetails.getCountry_code() + " " + memberDetails.getMobile());
        } else if (memberDetails.getMobile().isEmpty() && !memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(memberDetails.getEmail());
        } else if (memberDetails.getMobile().isEmpty() && memberDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setVisibility(View.GONE);
        }

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

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new AcceptRejectUserRequest().execute(memberDetails.getId(), "accept", "1");
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new AcceptRejectUserRequest().execute(memberDetails.getId(), "reject", "2");
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
            String res = "[]";
            Type = params[2];
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "requestAction");
            obj.addProperty("request_id", params[0]);
            obj.addProperty("status", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINUSERREQUESTSAPI, obj.toString());
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
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupsMembersRequests_Activity"));
                        if (Type.equals("1")) {
                            Utilities.showMessage("Member request accepted", context, 1);
                        } else if (Type.equals("2")) {
                            Utilities.showMessage("Member request rejected", context, 1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
