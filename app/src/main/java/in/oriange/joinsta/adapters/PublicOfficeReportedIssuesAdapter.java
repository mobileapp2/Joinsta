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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.PublicOfficeReportedIssuesModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class PublicOfficeReportedIssuesAdapter extends RecyclerView.Adapter<PublicOfficeReportedIssuesAdapter.MyViewHolder> {

    private Context context;
    private List<PublicOfficeReportedIssuesModel.ResultBean> issueList;
    private String userId;

    public PublicOfficeReportedIssuesAdapter(Context context, List<PublicOfficeReportedIssuesModel.ResultBean> issueList) {
        this.context = context;
        this.issueList = issueList;

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
        View view = inflater.inflate(R.layout.list_row_enquiry, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final PublicOfficeReportedIssuesModel.ResultBean issueDetails = issueList.get(position);

        holder.tv_record_name.setText(issueDetails.getTitle());
        holder.tv_message.setText(issueDetails.getDescription());
        holder.tv_createdby.setText(issueDetails.getName() + " (" + issueDetails.getMobile() + ")");
        holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", issueDetails.getCreated_at()));
        if (issueDetails.getIs_attended().equals("1")) {
            holder.sw_attend.setChecked(true);
        } else {
            holder.sw_attend.setChecked(false);
        }


        holder.tv_subject.setVisibility(View.GONE);
        holder.btn_delete.setVisibility(View.GONE);

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
                                    Uri.parse("tel:" + issueDetails.getMobile())));
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

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ll_buttons.getVisibility() == View.GONE) {
                    holder.ll_buttons.setVisibility(View.VISIBLE);
                } else if (holder.ll_buttons.getVisibility() == View.VISIBLE) {
                    holder.ll_buttons.setVisibility(View.GONE);
                }
            }
        });

        holder.sw_attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String is_attended = "0";

                if (holder.sw_attend.isChecked()) {
                    is_attended = "1";
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new AttendIssue().execute(issueDetails.getId(), is_attended, userId);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                sb.append(issueDetails.getTitle() + " - " + issueDetails.getDescription() + "\n");

                sb.append("By - " + issueDetails.getName() + "\n");

                sb.append("Mobile - " + issueDetails.getMobile() + "\n");

                String details = sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cv_mainlayout;
        private ImageButton ib_call;
        private LinearLayout ll_buttons;
        private TextView tv_record_name, tv_subject, tv_message, tv_createdby, tv_time;
        private Switch sw_attend;
        private Button btn_delete, btn_share;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ib_call = view.findViewById(R.id.ib_call);
            tv_record_name = view.findViewById(R.id.tv_record_name);
            tv_subject = view.findViewById(R.id.tv_subject);
            tv_message = view.findViewById(R.id.tv_message);
            tv_createdby = view.findViewById(R.id.tv_createdby);
            tv_time = view.findViewById(R.id.tv_time);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            sw_attend = view.findViewById(R.id.sw_attend);
            btn_delete = view.findViewById(R.id.btn_delete);
            btn_share = view.findViewById(R.id.btn_share);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class AttendIssue extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "officeReportIssuesAction");
            obj.addProperty("office_report_id", params[0]);
            obj.addProperty("is_attended", params[1]);
            obj.addProperty("user_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEREPORTISSUEAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
//            String type = "", message = "";
//            try {
//                if (!result.equals("")) {
//                    JSONObject mainObj = new JSONObject(result);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
