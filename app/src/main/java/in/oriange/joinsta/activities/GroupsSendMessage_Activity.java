package in.oriange.joinsta.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.GroupAdminsGroupsListModel;
import in.oriange.joinsta.models.GroupMessagesCountsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupsSendMessage_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private TextView tv_sms_count, tv_whatsapp_count, tv_email_count, tv_notifications_count;
    private MaterialEditText edt_groups, edt_subject;
    private EditText edt_message;
    private RadioButton rb_supervisor, rb_all;
    private CheckBox cb_sms, cb_whatsapp, cb_email, cb_notification;
    private Button btn_save;

    private List<GroupAdminsGroupsListModel.ResultBean> groupsList;

    private JsonArray selectedGroups;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_sendmessage);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupsSendMessage_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tv_sms_count = findViewById(R.id.tv_sms_count);
        tv_whatsapp_count = findViewById(R.id.tv_whatsapp_count);
        tv_email_count = findViewById(R.id.tv_email_count);
        tv_notifications_count = findViewById(R.id.tv_notifications_count);
        edt_groups = findViewById(R.id.edt_groups);
        edt_subject = findViewById(R.id.edt_subject);
        edt_message = findViewById(R.id.edt_message);
        rb_supervisor = findViewById(R.id.rb_supervisor);
        rb_all = findViewById(R.id.rb_all);
        cb_sms = findViewById(R.id.cb_sms);
        cb_whatsapp = findViewById(R.id.cb_whatsapp);
        cb_email = findViewById(R.id.cb_email);
        cb_notification = findViewById(R.id.cb_notification);
        btn_save = findViewById(R.id.btn_save);

        groupsList = new ArrayList<>();
        selectedGroups = new JsonArray();
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetMessageCount().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        edt_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupsList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetGroupAdminsGroups().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showGroupsListDialog();
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private class GetMessageCount extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveMsgCount"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMSGCOUNTAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type;
            try {
                if (!result.equals("")) {
                    GroupMessagesCountsModel pojoDetails = new Gson().fromJson(result, GroupMessagesCountsModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        tv_sms_count.setText(pojoDetails.getResult().getSmscount());
                        tv_whatsapp_count.setText(pojoDetails.getResult().getWhatsappcount());
                        tv_email_count.setText(pojoDetails.getResult().getEmailcount());
                        tv_notifications_count.setText(pojoDetails.getResult().getNotificationcount());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetGroupAdminsGroups extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveAllGroupDetails"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    groupsList = new ArrayList<>();
                    GroupAdminsGroupsListModel pojoDetails = new Gson().fromJson(result, GroupAdminsGroupsListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        groupsList = pojoDetails.getResult();
                        if (groupsList.size() > 0) {
                            showGroupsListDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showGroupsListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Groups");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new GroupListAdapter());

        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_groups.setText("");
                selectedGroups = new JsonArray();
                StringBuilder selectedGroupsName = new StringBuilder();

                for (GroupAdminsGroupsListModel.ResultBean grpDetails : groupsList) {
                    if (grpDetails.isChecked()) {
                        selectedGroups.add(grpDetails.getId());
                        selectedGroupsName.append(grpDetails.getGroup_name()).append(", ");
                    }
                }

                if (selectedGroupsName.toString().length() != 0) {
                    String selectedGroupsNameStr = selectedGroupsName.substring(0, selectedGroupsName.toString().length() - 2);
                    edt_groups.setText(selectedGroupsNameStr);
                }
            }
        });

        builder.create().show();
    }

    private class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(groupsList.get(position).getGroup_code() + " - " + groupsList.get(position).getGroup_name());

            if (groupsList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    groupsList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void submitData() {
        String receiverType = "";
        JsonArray messageTypes = new JsonArray();

        if (edt_groups.getText().toString().trim().isEmpty()) {
            edt_groups.setError("Please select atlease one group");
            edt_groups.requestFocus();
            return;
        }

        if (rb_supervisor.isChecked()) {
            receiverType = "group_supervisors";
        } else if (rb_all.isChecked()) {
            receiverType = "all";
        } else {
            Utilities.showMessage("Please select receiver type", context, 2);
            return;
        }

        if (edt_subject.getText().toString().trim().isEmpty()) {
            edt_subject.setError("Please enter subject");
            edt_subject.requestFocus();
            return;
        }

        if (edt_message.getText().toString().trim().isEmpty()) {
            edt_message.setError("Please enter message");
            edt_message.requestFocus();
            return;
        }

        if (!cb_sms.isChecked() && !cb_whatsapp.isChecked() && !cb_email.isChecked() && !cb_notification.isChecked()) {
            Utilities.showMessage("Please select message mode", context, 2);
            return;
        }

        if (cb_sms.isChecked()) {
            messageTypes.add("sms");
        }
        if (cb_whatsapp.isChecked()) {
            messageTypes.add("whatsapp");
        }
        if (cb_email.isChecked()) {
            messageTypes.add("email");
        }
        if (cb_notification.isChecked()) {
            messageTypes.add("notification");
        }

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("type", "sendMessage");
        mainObject.addProperty("user_id", userId);
        mainObject.addProperty("subject", edt_subject.getText().toString().trim());
        mainObject.addProperty("message", edt_message.getText().toString().trim());
        mainObject.add("groups", selectedGroups);
        mainObject.addProperty("receiver_type", receiverType);
        mainObject.add("message_types", messageTypes);

        if (Utilities.isNetworkAvailable(context)) {
            new SendMessage().execute(mainObject.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class SendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINSENDMESSAGEAPI, params[0]);
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
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Message sent successfully");
                        alertDialogBuilder.setCancelable(false);
                        final androidx.appcompat.app.AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
