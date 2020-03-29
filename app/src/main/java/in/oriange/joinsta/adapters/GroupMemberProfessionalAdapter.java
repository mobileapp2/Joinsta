package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewGroupMemberProfDetails_Activity;
import in.oriange.joinsta.models.GetProfessionalModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMemberProfessionalAdapter extends RecyclerView.Adapter<GroupMemberProfessionalAdapter.MyViewHolder> {

    private Context context;
    private String userId, name, mobile, countryCode;
    private List<GetProfessionalModel.ResultBean> resultArrayList;
    private JSONArray emailJsonArray;

    public GroupMemberProfessionalAdapter(Context context, List<GetProfessionalModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        emailJsonArray = new JSONArray();

        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            name = json.getString("first_name");
            mobile = json.getString("mobile");
            emailJsonArray = new JSONArray(json.getString("email"));
            try {
                countryCode = json.getString("country_code");
            } catch (Exception e) {
                countryCode = "91";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GetProfessionalModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getProfessional_code() + " - " + searchDetails.getFirm_name());

        if (searchDetails.getTag().get(0) != null) {
            if (searchDetails.getTag().get(0).size() > 0) {
                StringBuilder tag = new StringBuilder();
                try {
                    for (int i = 0; i < 4; i++) {
                        tag.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                    }
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                } catch (IndexOutOfBoundsException e) {
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                }

            } else {
                if (!searchDetails.getSubtype_description().isEmpty())
                    holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
                else
                    holder.tv_subheading.setText(searchDetails.getType_description());
            }
        } else {
            if (!searchDetails.getType_description().isEmpty() && !searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
            } else if (searchDetails.getType_description().isEmpty() && searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setVisibility(View.GONE);
            } else if (!searchDetails.getType_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getType_description());
            } else if (!searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getSubtype_description());
            }

        }

        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewGroupMemberProfDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails));
            }
        });

        holder.btn_caldist.setVisibility(View.GONE);

        if (searchDetails.getOrder_online().trim().equals("")) {
            holder.btn_order_online.setVisibility(View.INVISIBLE);
        }

        if (searchDetails.getTotal_number_review().equals("0")) {
            holder.rl_rating.setVisibility(View.GONE);
        } else {
            holder.rl_rating.setVisibility(View.VISIBLE);
            float averageRating = Float.parseFloat(searchDetails.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));
            holder.tv_total_rating.setText(String.valueOf(averageRating));
            holder.tv_total_reviews.setText("(" + searchDetails.getTotal_number_review() + ")");
            holder.rb_feedback_stars.setRating(averageRating);
        }

        holder.btn_enquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_enquiry, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Enquiry");
                alertDialogBuilder.setView(promptView);

                final MaterialEditText edt_name = promptView.findViewById(R.id.edt_name);
                final RadioGroup rg_communicationmode = promptView.findViewById(R.id.rg_communicationmode);
                final RadioButton rb_mobile = promptView.findViewById(R.id.rb_mobile);
                final RadioButton rb_email = promptView.findViewById(R.id.rb_email);
                final MaterialEditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
                final MaterialEditText edt_email = promptView.findViewById(R.id.edt_email);
                final MaterialEditText edt_subject = promptView.findViewById(R.id.edt_subject);
                final EditText edt_details = promptView.findViewById(R.id.edt_details);
                final Button btn_save = promptView.findViewById(R.id.btn_save);

                edt_name.setText(name);

                final AlertDialog alertD = alertDialogBuilder.create();

                rb_mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edt_mobile.setVisibility(View.VISIBLE);
                        edt_email.setVisibility(View.GONE);

                        edt_email.setText("");

                        edt_mobile.setText("+" + countryCode + mobile);
                    }
                });

                rb_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        edt_mobile.setVisibility(View.GONE);
                        edt_mobile.setText("");

                        if (emailJsonArray == null) {
                            Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                            rg_communicationmode.clearCheck();
                            return;
                        }

                        if (emailJsonArray.length() == 0) {
                            Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                            rg_communicationmode.clearCheck();
                            return;

                        }

                        try {
                            for (int i = 0; i < emailJsonArray.length(); i++) {
                                JSONObject emailObj = emailJsonArray.getJSONObject(0);
                                if (emailObj.getString("is_primary").equals("1")) {
                                    if (emailObj.getString("email_verification").equals("1")) {
                                        if (emailObj.getString("email").equals("")) {
                                            Utilities.showAlertDialogNormal(context, "Please update your primary email from Basic Information");
                                            rg_communicationmode.clearCheck();
                                            return;
                                        }

                                        edt_email.setText(emailObj.getString("email"));
                                    } else {
                                        Utilities.showAlertDialogNormal(context, "Please verify your primary email from Basic Information");
                                        rg_communicationmode.clearCheck();
                                        return;
                                    }
                                } else {
                                    Utilities.showAlertDialogNormal(context, "Please set a primary email from Basic Information");
                                    rg_communicationmode.clearCheck();
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertDialogNormal(context, "We have made some changes related to user email, please kindly logout and login again to refresh your session");
                            rg_communicationmode.clearCheck();
                            return;

                        }

                        edt_email.setVisibility(View.VISIBLE);

                    }
                });

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edt_name.getText().toString().trim().isEmpty()) {
                            edt_name.setError("Please enter name");
                            edt_name.requestFocus();
                            return;
                        }

                        if (rb_mobile.isChecked()) {
                            if (edt_mobile.getText().toString().trim().isEmpty()) {
                                edt_mobile.setError("Please enter valid mobile");
                                edt_mobile.requestFocus();
                                return;
                            }
                            edt_email.setText("");
                        } else if (rb_email.isChecked()) {
                            if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
                                edt_email.setError("Please enter valid email");
                                edt_email.requestFocus();
                                return;
                            }
                            edt_mobile.setText("");
                        } else {
                            Utilities.showMessage("Please select communication mode", context, 2);
                            return;
                        }

                        if (edt_subject.getText().toString().trim().isEmpty()) {
                            edt_subject.setError("Please enter subject");
                            edt_subject.requestFocus();
                            return;
                        }

                        if (edt_details.getText().toString().trim().isEmpty()) {
                            edt_details.setError("Please enter details");
                            edt_details.requestFocus();
                            return;
                        }

                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();
                            new SendEnquiryDetails().execute(
                                    userId,
                                    edt_name.getText().toString().trim(),
                                    edt_mobile.getText().toString().trim(),
                                    edt_email.getText().toString().trim(),
                                    edt_subject.getText().toString().trim(),
                                    edt_details.getText().toString().trim(),
                                    "3",
                                    searchDetails.getId()

                            );
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                });

                alertD.show();
            }
        });

        holder.btn_order_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = searchDetails.getOrder_online();

                if (!url.trim().equals("")) {
                    if (!url.startsWith("https://") || !url.startsWith("http://")) {
                        url = "http://" + url;
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } else {
                    Utilities.showMessage("Order online not added", context, 2);
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
        private RelativeLayout rl_rating;
        private RatingBar rb_feedback_stars;
        private Button btn_enquire, btn_caldist, btn_order_online;
        private TextView tv_heading, tv_subheading, tv_subsubheading, tv_mutual_groups, tv_total_rating, tv_total_reviews;

        public MyViewHolder(View view) {
            super(view);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            btn_enquire = view.findViewById(R.id.btn_enquire);
            btn_caldist = view.findViewById(R.id.btn_caldist);
            btn_order_online = view.findViewById(R.id.btn_order_online);
            rl_rating = view.findViewById(R.id.rl_rating);
            rb_feedback_stars = view.findViewById(R.id.rb_feedback_stars);
            tv_total_rating = view.findViewById(R.id.tv_total_rating);
            tv_total_reviews = view.findViewById(R.id.tv_total_reviews);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class SendEnquiryDetails extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "createenquiry");
            obj.addProperty("userid", params[0]);
            obj.addProperty("name", params[1]);
            obj.addProperty("mobile", params[2]);
            obj.addProperty("email", params[3]);
            obj.addProperty("subject", params[4]);
            obj.addProperty("message", params[5]);
            obj.addProperty("category_type_id", params[6]);
            obj.addProperty("record_id", params[7]);
            obj.addProperty("msg_type", "notification");
            obj.addProperty("msg_type", "notification");
            res = APICall.JSONAPICall(ApplicationConstants.ENQUIRYAPI, obj.toString());
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
                        Utilities.showMessage("Enquiry sent successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
