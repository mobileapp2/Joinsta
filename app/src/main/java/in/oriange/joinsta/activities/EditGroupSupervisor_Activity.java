package in.oriange.joinsta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.GetDistrictListModel;
import in.oriange.joinsta.models.GetStateListModel;
import in.oriange.joinsta.models.GroupSupervisorsDetailsModel;
import in.oriange.joinsta.models.GroupSupervisorsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class EditGroupSupervisor_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private MaterialEditText edt_name, edt_mobile, edt_email, edt_state, edt_district;
    private Button btn_save;
    private String userId, groupId;

    private GroupSupervisorsListModel.ResultBean memberDetails;
    private JsonArray selectedStatesIds, selectedDistIds;
    private List<GetStateListModel.ResultBean> stateList;
    private List<GetDistrictListModel.ResultBean> districtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_groupsupervisor);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditGroupSupervisor_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);

        btn_save = findViewById(R.id.btn_save);

        selectedStatesIds = new JsonArray();
        selectedDistIds = new JsonArray();

        stateList = new ArrayList<>();
        districtList = new ArrayList<>();
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
        memberDetails = (GroupSupervisorsListModel.ResultBean) getIntent().getSerializableExtra("memberDetails");
        groupId = getIntent().getStringExtra("groupId");

        edt_state.setText("");
        edt_district.setText("");

        if (Utilities.isNetworkAvailable(context)) {
            new GetGroupAdminsGroups().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {

        edt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetStateList().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showStateListDialog();
                }
            }
        });

        edt_district.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_state.getText().toString().trim().isEmpty()) {
                    edt_state.setText("Please select atleast one state");
                    return;
                }

                if (districtList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetDistrictList().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    startActivityForResult(new Intent(context, SelectDistrict_Activity.class)
                            .putExtra("districtList", (Serializable) districtList)
                            .putExtra("selectedDistIds", String.valueOf(selectedDistIds)), 1);
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

    private class GetStateList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getstate");
            res = APICall.JSONAPICall(ApplicationConstants.STATESAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    stateList = new ArrayList<>();
                    GetStateListModel pojoDetails = new Gson().fromJson(result, GetStateListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        stateList = pojoDetails.getResult();
                        if (stateList.size() > 0) {

                            for (int j = 0; j < selectedStatesIds.size(); j++) {
                                for (int i = 0; i < stateList.size(); i++) {
                                    if (selectedStatesIds.get(j).getAsString().equals(stateList.get(i).getId())) {
                                        stateList.get(i).setChecked(true);
                                        break;
                                    }
                                }
                            }

                            showStateListDialog();
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

    private void showStateListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select States");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new GroupListAdapter());

        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_state.setText("");
                selectedStatesIds = new JsonArray();

                edt_district.setText("");
                selectedDistIds = new JsonArray();
                districtList.clear();

                StringBuilder selectedStateName = new StringBuilder();

                for (GetStateListModel.ResultBean grpDetails : stateList) {
                    if (grpDetails.isChecked()) {
                        selectedStatesIds.add(grpDetails.getId());
                        selectedStateName.append(grpDetails.getName()).append(", ");
                    }
                }

                if (selectedStateName.toString().length() != 0) {
                    String selectedGroupsNameStr = selectedStateName.substring(0, selectedStateName.toString().length() - 2);
                    edt_state.setText(selectedGroupsNameStr);
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

            holder.cb_select.setText(stateList.get(position).getName());

            if (stateList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    stateList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return stateList.size();
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

    private class GetDistrictList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getdistrict");
            obj.add("state_id", selectedStatesIds);
            res = APICall.JSONAPICall(ApplicationConstants.DISTRICTAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    districtList = new ArrayList<>();
                    GetDistrictListModel pojoDetails = new Gson().fromJson(result, GetDistrictListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        districtList = pojoDetails.getResult();
                        if (districtList.size() > 0) {
                            startActivityForResult(new Intent(context, SelectDistrict_Activity.class)
                                    .putExtra("districtList", (Serializable) districtList)
                                    .putExtra("selectedDistIds", String.valueOf(selectedDistIds)), 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                districtList = (List<GetDistrictListModel.ResultBean>) data.getSerializableExtra("districtList");
                selectedDistIds = new JsonArray();
                StringBuilder selectedDistName = new StringBuilder();
                for (GetDistrictListModel.ResultBean bean : districtList) {
                    if (bean.isChecked()) {
                        selectedDistIds.add(bean.getDistrictId());
                        selectedDistName.append(bean.getDistrict()).append(", ");
                    }
                }

                if (selectedDistName.toString().length() != 0) {
                    String selectedGroupsNameStr = selectedDistName.substring(0, selectedDistName.toString().length() - 2);
                    edt_district.setText(selectedGroupsNameStr);
                }

            }
        }
    }

    private void submitData() {

        if (edt_name.getText().toString().isEmpty()) {
            edt_name.setError("Please enter name");
            edt_name.requestFocus();
            return;
        }

        if (!Utilities.isValidMobileno(edt_mobile.getText().toString())) {
            edt_mobile.setError("Please enter valid mobile");
            edt_mobile.requestFocus();
            return;
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "updateGroupSupervisorDetails");
        mainObj.addProperty("edit_member_name", edt_name.getText().toString().trim());
        mainObj.addProperty("edit_member_email", edt_email.getText().toString().trim());
        mainObj.addProperty("edit_member_mobile", edt_mobile.getText().toString().trim());
        mainObj.addProperty("edit_member_role", "group_supervisor");
        mainObj.add("edit_member_state", selectedStatesIds);
        mainObj.add("edit_member_district", selectedDistIds);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("group_id", groupId);
        mainObj.addProperty("member_id", memberDetails.getId());

        if (Utilities.isNetworkAvailable(context)) {
            new EditGroupSupervisor().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
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
            param.add(new ParamsPojo("type", "giveGroupSupervisorsDetails"));
            param.add(new ParamsPojo("group_supervisor_id", memberDetails.getId()));
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
                    GroupSupervisorsDetailsModel pojoDetails = new Gson().fromJson(result, GroupSupervisorsDetailsModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        GroupSupervisorsDetailsModel.ResultBean superVisorDetails = pojoDetails.getResult();
                        edt_name.setText(superVisorDetails.getFirst_name());
                        edt_mobile.setText(superVisorDetails.getMobile());
                        edt_email.setText(superVisorDetails.getEmail());

                        StringBuilder stateSb = new StringBuilder();
                        List<GroupSupervisorsDetailsModel.ResultBean.StatesBean> statesList = new ArrayList<>();
                        statesList = superVisorDetails.getStates();

                        for (GroupSupervisorsDetailsModel.ResultBean.StatesBean statesBean : statesList) {
                            selectedStatesIds.add(statesBean.getState_id());
                            stateSb.append(statesBean.getState_name()).append(", ");
                        }

                        if (stateSb.toString().length() != 0) {
                            String selectedStatesStr = stateSb.substring(0, stateSb.toString().length() - 2);
                            edt_state.setText(selectedStatesStr);
                        }

                        StringBuilder districtSb = new StringBuilder();
                        List<GroupSupervisorsDetailsModel.ResultBean.DistrictsBean> districtList = new ArrayList<>();
                        districtList = superVisorDetails.getDistricts();

                        for (GroupSupervisorsDetailsModel.ResultBean.DistrictsBean districtBean : districtList) {
                            selectedDistIds.add(districtBean.getDistrict_id());
                            districtSb.append(districtBean.getDistrict_name()).append(", ");
                        }

                        if (districtSb.toString().length() != 0) {
                            String selectedDistrictStr = districtSb.substring(0, districtSb.toString().length() - 2);
                            edt_district.setText(selectedDistrictStr);
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

    private class EditGroupSupervisor extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, params[0]);
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupSupervisors_Fragment"));
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Group supervisor details updated successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to report your issue", context, 3);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditGroupSupervisor_Activity.this);
    }

}
