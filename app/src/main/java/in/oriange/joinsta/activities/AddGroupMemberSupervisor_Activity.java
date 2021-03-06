package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.GetDistrictListModel;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.models.GetStateListModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;

public class AddGroupMemberSupervisor_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private MaterialEditText edt_name, edt_mobile, edt_email, edt_role, edt_state, edt_district;
    private TextView tv_countrycode_mobile;
    private Button btn_save;
    private String userId, groupId, role;

    private JsonArray selectedStatesIds, selectedDistIds;
    private List<GetStateListModel.ResultBean> stateList;
    private List<GetDistrictListModel.ResultBean> districtList;
    private ArrayList<ContryCodeModel> countryCodeList;
    private AlertDialog countryCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_groupmembersupervisor);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddGroupMemberSupervisor_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_role = findViewById(R.id.edt_role);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);

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

        try {
            JSONArray m_jArry = new JSONArray(loadJSONForCountryCode(context));
            countryCodeList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                countryCodeList.add(new ContryCodeModel(
                        jo_inside.getString("name"),
                        jo_inside.getString("dial_code"),
                        jo_inside.getString("code")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        groupId = getIntent().getStringExtra("groupId");
        role = getIntent().getStringExtra("role");

        if (role.equals("group_supervisor")) {
            edt_role.setText("Group Supervisor");
            edt_state.setVisibility(View.VISIBLE);
            edt_district.setVisibility(View.VISIBLE);
        } else if (role.equals("group_member")) {
            edt_role.setText("Group Member");
            edt_state.setVisibility(View.GONE);
            edt_district.setVisibility(View.GONE);
        }
    }

    private void setEventHandler() {
        edt_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MasterModel> rolesList = new ArrayList<>();
                rolesList.add(new MasterModel("Group Supervisor", "group_supervisor"));
                rolesList.add(new MasterModel("Group Member", "group_member"));
                showRoleListDialog(rolesList);
            }
        });

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
                    edt_state.setText("Please select at least one state");
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
                            .putExtra("selectedDistIds", "[]"), 1);
                }
            }
        });

        tv_countrycode_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryCodesListDialog();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void showRoleListDialog(final List<MasterModel> rolesList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setTitle("Select Role");
        builder.setCancelable(false);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (MasterModel masterObj : rolesList) {
            arrayAdapter.add(masterObj.getName());
        }

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MasterModel modelObj = rolesList.get(which);

                edt_role.setText(modelObj.getName());
                role = modelObj.getId();

                if (role.equals("group_supervisor")) {
                    edt_state.setVisibility(View.VISIBLE);
                    edt_district.setVisibility(View.VISIBLE);
                } else if (role.equals("group_member")) {
                    edt_state.setVisibility(View.GONE);
                    edt_district.setVisibility(View.GONE);
                }

            }
        });
        builder.create().show();
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
                                    .putExtra("selectedDistIds", "[]"), 1);
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

    private void showCountryCodesListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_countrycodes_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Country");
        builder.setCancelable(false);

        final RecyclerView rv_country = view.findViewById(R.id.rv_country);
        EditText edt_search = view.findViewById(R.id.edt_search);
        rv_country.setLayoutManager(new LinearLayoutManager(context));
        rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                    return;
                }

                if (countryCodeList.size() == 0) {
                    rv_country.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContryCodeModel> searchedCountryList = new ArrayList<>();
                    for (ContryCodeModel countryDetails : countryCodeList) {

                        String countryToBeSearched = countryDetails.getName().toLowerCase();

                        if (countryToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedCountryList.add(countryDetails);
                        }
                    }
                    rv_country.setAdapter(new CountryCodeAdapter(searchedCountryList));
                } else {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        countryCodeDialog = builder.create();
        countryCodeDialog.show();
    }

    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

        private ArrayList<ContryCodeModel> countryCodeList;

        public CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList) {
            this.countryCodeList = countryCodeList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText(countryCodeList.get(position).getName());

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_countrycode_mobile.setText(countryCodeList.get(position).getDial_code());
                    countryCodeDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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

        if (!edt_email.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edt_email.getText().toString())) {
                edt_email.setError("Please enter valid email");
                edt_email.requestFocus();
                return;
            }
        }

        if (!edt_role.getText().toString().trim().isEmpty()) {
            edt_role.setError("Please enter select role");
            edt_role.requestFocus();
            return;
        }

        JsonObject mainObj = new JsonObject();
        if (role.equals("group_supervisor")) {
            mainObj.addProperty("type", "addGroupSupervisorDetails");
            mainObj.addProperty("member_name", edt_name.getText().toString().trim());
            mainObj.addProperty("member_email", edt_email.getText().toString().trim());
            mainObj.addProperty("member_mobile", edt_mobile.getText().toString().trim());
            mainObj.addProperty("country_code", tv_countrycode_mobile.getText().toString().trim().replace("+", ""));
            mainObj.addProperty("member_role", role);
            mainObj.add("member_state", selectedStatesIds);
            mainObj.add("member_district", selectedDistIds);
            mainObj.addProperty("user_id", userId);
            mainObj.addProperty("group_id", groupId);
            mainObj.addProperty("is_visible", "1");
        } else if (role.equals("group_member")) {
            mainObj.addProperty("type", "addGroupMemberDetails");
            mainObj.addProperty("member_name", edt_name.getText().toString().trim());
            mainObj.addProperty("member_email", edt_email.getText().toString().trim());
            mainObj.addProperty("member_mobile", edt_mobile.getText().toString().trim());
            mainObj.addProperty("country_code", tv_countrycode_mobile.getText().toString().trim().replace("+", ""));
            mainObj.addProperty("member_role", role);
            mainObj.addProperty("member_state", "");
            mainObj.addProperty("member_district", "");
            mainObj.addProperty("user_id", userId);
            mainObj.addProperty("group_id", groupId);
            mainObj.addProperty("is_visible", "1");
        }

        if (Utilities.isNetworkAvailable(context)) {
            new AddGroupMemberSupervisor().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class AddGroupMemberSupervisor extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type, message;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupMembers_Fragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupSupervisors_Fragment"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Participant added successfully");
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
                        Utilities.showMessage(message, context, 3);
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
        hideSoftKeyboard(AddGroupMemberSupervisor_Activity.this);
    }

}
