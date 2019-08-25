package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.UserFeedback_Activity;
import in.oriange.joinsta.models.UserFeedbackListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class UserFeedbackAdapter extends RecyclerView.Adapter<UserFeedbackAdapter.MyViewHolder> {

    private List<UserFeedbackListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;

    public UserFeedbackAdapter(Context context, List<UserFeedbackListModel.ResultBean> resultArrayList) {
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
        View view = inflater.inflate(R.layout.list_row_userfeedback, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final UserFeedbackListModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_feedback.setText(searchDetails.getFeedback_text());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this feedback?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteUserFeedback().execute(searchDetails.getUser_feedback_id());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
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
        private RatingBar rb_feedbackstars;
        private TextView tv_feedback;


        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_feedback = view.findViewById(R.id.tv_feedback);
            rb_feedbackstars = view.findViewById(R.id.rb_feedbackstars);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class DeleteUserFeedback extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deleteuserfeedback");
            obj.addProperty("userid", userId);
            obj.addProperty("user_feedback_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.FEEDBACKAPI, obj.toString());
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
                        new UserFeedback_Activity.GetUserFeedback().execute(userId);
                        Utilities.showMessage("Feedback deleted successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
