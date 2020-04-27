package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.PublicOfficeApprovalRequestModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class PublicOfficeApprovalRequestAdapter extends RecyclerView.Adapter<PublicOfficeApprovalRequestAdapter.MyViewHolder> {

    private Context context;
    private List<PublicOfficeApprovalRequestModel.ResultBean> approvalList;
    private String userId;

    public PublicOfficeApprovalRequestAdapter(Context context, List<PublicOfficeApprovalRequestModel.ResultBean> approvalList) {
        this.context = context;
        this.approvalList = approvalList;

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
        View view = inflater.inflate(R.layout.list_row_assign_request, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        PublicOfficeApprovalRequestModel.ResultBean resultBean = approvalList.get(position);

        holder.tv_name.setText(resultBean.getSender_details().get(0).getFirst_name());
        holder.tv_office.setText("Office - " + resultBean.getOffice_name() + " (" + resultBean.getCity() + ")");

        holder.ib_call.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to make a call?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_call);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", (dialog, id) -> context.startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + resultBean.getSender_details().get(0).getCountry_code() + resultBean.getSender_details().get(0).getMobile()))));
                builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });


        holder.btn_accept.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new PublicOfficeRequestAction().execute(userId, "1", resultBean.getOffice_id());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });

        holder.btn_reject.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new PublicOfficeRequestAction().execute(userId, "0", resultBean.getOffice_id());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });


        holder.btn_accept.setText("Approve");
    }

    @Override
    public int getItemCount() {
        return approvalList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_office, tv_request_status;
        private ImageButton ib_call;
        private LinearLayout ll_buttons;
        private Button btn_accept, btn_reject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_office = itemView.findViewById(R.id.tv_office);
            tv_request_status = itemView.findViewById(R.id.tv_request_status);
            ib_call = itemView.findViewById(R.id.ib_call);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            ll_buttons = itemView.findViewById(R.id.ll_buttons);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class PublicOfficeRequestAction extends AsyncTask<String, Void, String> {

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
            Type = params[1];
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "approvedRequest");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("is_approved", params[1]);
            obj.addProperty("office_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("PublicOfficeApprovalRequestList_Activity"));

                        if (Type.equals("1")) {
                            Utilities.showMessage("Public office approved", context, 1);
                        } else if (Type.equals("0")) {
                            Utilities.showMessage("Public office rejected", context, 1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
