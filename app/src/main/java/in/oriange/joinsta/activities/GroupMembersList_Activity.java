package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupMembersListAdapter;
import in.oriange.joinsta.models.GroupMemebersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersList_Activity extends AppCompatActivity {

    private Context context;
    private RecyclerView rv_group_members;
    private EditText edt_search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String groupId, isAdmin, userId;
    private List<GroupMemebersListModel.ResultBean> groupMembersFinalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_list);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupMembersList_Activity.this;

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        edt_search = findViewById(R.id.edt_search);
        rv_group_members = findViewById(R.id.rv_group_members);
        rv_group_members.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);

        groupMembersFinalList = new ArrayList<>();

    }

    private void getSessionDetails() {
        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        groupId = getIntent().getStringExtra("groupId");
        isAdmin = getIntent().getStringExtra("isAdmin");

        if (Utilities.isNetworkAvailable(context)) {
            new GetGroupMembers().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetGroupMembers().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_group_members.setAdapter(new GroupMembersListAdapter(context, groupMembersFinalList));
                    return;
                }


                if (groupMembersFinalList.size() == 0) {
                    rv_group_members.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<GroupMemebersListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (GroupMemebersListModel.ResultBean groupsDetails : groupMembersFinalList) {

                        String groupsToBeSearched = groupsDetails.getFirst_name().toLowerCase() + groupsDetails.getSearchQueryString().toLowerCase();

                        if (groupsToBeSearched.contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }
                    rv_group_members.setAdapter(new GroupMembersListAdapter(context, groupsSearchedList));
                } else {
                    rv_group_members.setAdapter(new GroupMembersListAdapter(context, groupMembersFinalList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private class GetGroupMembers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_group_members.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getgroupmember");
            obj.addProperty("id", groupId);
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_group_members.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<GroupMemebersListModel.ResultBean> groupMembersList = new ArrayList<>();
                    GroupMemebersListModel pojoDetails = new Gson().fromJson(result, GroupMemebersListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        groupMembersList = pojoDetails.getResult();

                        ArrayList<GroupMemebersListModel.ResultBean> foundMembers = new ArrayList<GroupMemebersListModel.ResultBean>();
                        for (GroupMemebersListModel.ResultBean groupDetails : groupMembersList) {
//                            if (groupDetails.getRole().equals("group_member")) {
                            foundMembers.add(groupDetails);
//                            }
                        }

                        ArrayList<GroupMemebersListModel.ResultBean> tempFoundMembers = new ArrayList<>(foundMembers);

                        for (GroupMemebersListModel.ResultBean memberDetails : tempFoundMembers) {
                            if (!isAdmin.equals("1")) {
                                if (memberDetails.getIs_hidden().equals("1")) {
                                    foundMembers.remove(memberDetails);
                                }
                            }
                        }

                        groupMembersList.clear();
                        groupMembersList.addAll(foundMembers);

                        groupMembersFinalList = new ArrayList<>();


                        for (GroupMemebersListModel.ResultBean memberDetails : groupMembersList) {
                            GroupMemebersListModel.ResultBean resultBean = new GroupMemebersListModel.ResultBean();
                            resultBean.setUser_id(memberDetails.getUser_id());
                            resultBean.setGroup_name(memberDetails.getGroup_name());
                            resultBean.setGroup_id(memberDetails.getGroup_id());
                            resultBean.setRole(memberDetails.getRole());
                            resultBean.setIs_hidden(memberDetails.getIs_hidden());
                            resultBean.setId(memberDetails.getId());
                            resultBean.setFirst_name(memberDetails.getFirst_name());
                            resultBean.setGroup_member_name(memberDetails.getGroup_member_name());
                            resultBean.setMobile(memberDetails.getMobile());
                            resultBean.setPassword(memberDetails.getPassword());
                            resultBean.setImage_url(memberDetails.getImage_url());
                            resultBean.setIs_joinsta_member(memberDetails.getIs_joinsta_member());

                            StringBuffer searchQuery = new StringBuffer();

                            for (GroupMemebersListModel.ResultBean.BussinessInfoBean bussinessInfoBean : memberDetails.getBussinessInfo()) {
                                searchQuery.append(bussinessInfoBean.getBusiness_name());
                                searchQuery.append(bussinessInfoBean.getType_description());
                                searchQuery.append(bussinessInfoBean.getSubtype_description());

                                for (GroupMemebersListModel.ResultBean.BussinessInfoBean.TagBean tagBean : memberDetails.getBussinessInfo().get(0).getTag()) {
                                    searchQuery.append(tagBean.getTag_name());

                                }
                            }

                            for (GroupMemebersListModel.ResultBean.EmployeeInfoBean employeeInfoBean : memberDetails.getEmployeeInfo()) {
                                searchQuery.append(employeeInfoBean.getOrganization_name());
                                searchQuery.append(employeeInfoBean.getType_description());
                                searchQuery.append(employeeInfoBean.getSubtype_description());

                                for (GroupMemebersListModel.ResultBean.EmployeeInfoBean.TagBeanX tagBean : memberDetails.getEmployeeInfo().get(0).getTag()) {
                                    searchQuery.append(tagBean.getTag_name());

                                }
                            }

                            for (GroupMemebersListModel.ResultBean.ProfessionalInfoBean professionalInfoBean : memberDetails.getProfessionalInfo()) {
                                searchQuery.append(professionalInfoBean.getFirm_name());
                                searchQuery.append(professionalInfoBean.getType_description());
                                searchQuery.append(professionalInfoBean.getSubtype_description());

                                for (GroupMemebersListModel.ResultBean.ProfessionalInfoBean.TagBeanXX tagBean : memberDetails.getProfessionalInfo().get(0).getTag()) {
                                    searchQuery.append(tagBean.getTag_name());
                                }
                            }

                            resultBean.setSearchQueryString(searchQuery.toString().replace(" ", ""));
                            groupMembersFinalList.add(resultBean);
                        }
                        if (groupMembersList.size() > 0) {
                            rv_group_members.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_group_members.setAdapter(new GroupMembersListAdapter(context, groupMembersFinalList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_group_members.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_group_members.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_group_members.setVisibility(View.GONE);
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
