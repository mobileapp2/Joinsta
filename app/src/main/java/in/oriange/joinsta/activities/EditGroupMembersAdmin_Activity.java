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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.models.GroupMembersAdminsListModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.models.MemberCategoryModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;

public class EditGroupMembersAdmin_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private MaterialEditText edt_name, edt_mobile, edt_email, edt_role, edt_members_category;
    private TextView tv_countrycode_mobile;
    private Button btn_save;
    private String userId, groupId, role;

    private JsonArray selectedCategoryIds;
    private List<MemberCategoryModel.ResultBean> categotyList;
    private ArrayList<ContryCodeModel> countryCodeList;
    private AlertDialog countryCodeDialog;
    private GroupMembersAdminsListModel.ResultBean memberDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_members_admin);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditGroupMembersAdmin_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_role = findViewById(R.id.edt_role);
        edt_members_category = findViewById(R.id.edt_members_category);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);

        btn_save = findViewById(R.id.btn_save);

        selectedCategoryIds = new JsonArray();
        categotyList = new ArrayList<>();
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
        memberDetails = (GroupMembersAdminsListModel.ResultBean) getIntent().getSerializableExtra("memberDetails");
        role = memberDetails.getRole();

        edt_name.setText(memberDetails.getFirst_name());
        edt_mobile.setText(memberDetails.getMobile());
        edt_email.setText(memberDetails.getEmail());
        tv_countrycode_mobile.setText(memberDetails.getCountry_code());

        if (memberDetails.getRole().equals("group_admin"))
            edt_role.setText("Group Admin");
        else if (memberDetails.getRole().equals("group_member"))
            edt_role.setText("Group Member");

        StringBuilder categorySb = new StringBuilder();
        List<GroupMembersAdminsListModel.ResultBean.MemberCategoriesBean> categoryList = memberDetails.getMember_categories();

        for (GroupMembersAdminsListModel.ResultBean.MemberCategoriesBean categoryBean : categoryList) {
            selectedCategoryIds.add(categoryBean.getMember_category_id());
            categorySb.append(categoryBean.getMember_category()).append(", ");
        }

        if (categorySb.toString().length() != 0) {
            String selectedStatesStr = categorySb.substring(0, categorySb.toString().length() - 2);
            edt_members_category.setText(selectedStatesStr);
        }
    }

    private void setEventHandler() {
        edt_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MasterModel> rolesList = new ArrayList<>();
                rolesList.add(new MasterModel("Group Admin", "group_admin"));
                rolesList.add(new MasterModel("Group Member", "group_member"));
                showRoleListDialog(rolesList);
            }
        });

        edt_members_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categotyList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetMemberCategoryList().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showMemberCategoryListDialog();
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

            }
        });
        builder.create().show();
    }

    private void showMemberCategoryListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Member Category");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new GroupListAdapter());

        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_members_category.setText("");
                selectedCategoryIds = new JsonArray();

                StringBuilder selectedCategoryName = new StringBuilder();

                for (MemberCategoryModel.ResultBean categoryDetails : categotyList) {
                    if (categoryDetails.isChecked()) {
                        selectedCategoryIds.add(categoryDetails.getId());
                        selectedCategoryName.append(categoryDetails.getMember_category()).append(", ");
                    }
                }

                if (selectedCategoryName.toString().length() != 0) {
                    String selectedGroupsNameStr = selectedCategoryName.substring(0, selectedCategoryName.toString().length() - 2);
                    edt_members_category.setText(selectedGroupsNameStr);
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

        JsonArray jsonArray = new JsonArray();

        for (int i = 0; i < selectedCategoryIds.size(); i++) {
            if (i <= memberDetails.getMember_categories().size() - 1) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", memberDetails.getMember_categories().get(i).getId());
                jsonObject.addProperty("category_id", selectedCategoryIds.get(i).getAsString());
                jsonArray.add(jsonObject);
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", "0");
                jsonObject.addProperty("category_id", selectedCategoryIds.get(i).getAsString());
                jsonArray.add(jsonObject);
            }
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "updateGroupMemberDetails");
        mainObj.addProperty("edit_member_name", edt_name.getText().toString().trim());
        mainObj.addProperty("edit_member_email", edt_email.getText().toString().trim());
        mainObj.addProperty("edit_member_mobile", edt_mobile.getText().toString().trim());
        mainObj.addProperty("edit_country_code", tv_countrycode_mobile.getText().toString().trim().replace("+", ""));
        mainObj.addProperty("edit_member_role", role);
        mainObj.addProperty("edit_member_state", "");
        mainObj.addProperty("edit_member_district", "");
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("group_id", groupId);
        mainObj.addProperty("member_id", memberDetails.getId());
        mainObj.add("member_categories", jsonArray);

        if (Utilities.isNetworkAvailable(context)) {
            new EditGroupMemberAdmin().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
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
        hideSoftKeyboard(EditGroupMembersAdmin_Activity.this);
    }

    private class GetMemberCategoryList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getAllCategories");
            res = APICall.JSONAPICall(ApplicationConstants.MEMBERSCATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    categotyList = new ArrayList<>();
                    MemberCategoryModel pojoDetails = new Gson().fromJson(result, MemberCategoryModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
                            showMemberCategoryListDialog();
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

            holder.cb_select.setText(categotyList.get(position).getMember_category());

            if (categotyList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    categotyList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categotyList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }
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

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }
    }

    private class EditGroupMemberAdmin extends AsyncTask<String, Void, String> {

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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupMembersAdmins_Activity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Participant details updated successfully");
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

}
