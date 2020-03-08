package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupMembersAdminsAdapter;
import in.oriange.joinsta.models.GroupMembersAdminsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersAdmins_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private EditText edt_search;
    private RecyclerView rv_participants;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private FloatingActionButton btn_add;
    private String userId, groupId;

    List<GroupMembersAdminsListModel.ResultBean> participantsList;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_admins);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }


    private void init() {
        context = GroupMembersAdmins_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_participants = findViewById(R.id.rv_participants);
        rv_participants.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);
        btn_add = findViewById(R.id.btn_add);

        participantsList = new ArrayList<>();
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
        groupId = getIntent().getStringExtra("groupId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetAllMembersAdminList().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("GroupMembersAdmins_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAllMembersAdminList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddGroupMembersAdmin_Activity.class)
                        .putExtra("groupId", groupId));
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_participants.setAdapter(new GroupMembersAdminsAdapter(context, participantsList, groupId));
                    return;
                }


                if (participantsList.size() == 0) {
                    rv_participants.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<GroupMembersAdminsListModel.ResultBean> participantsSearchedList = new ArrayList<>();
                    for (GroupMembersAdminsListModel.ResultBean groupsDetails : participantsList) {

                        String participantsToBeSearched = groupsDetails.getFirst_name().toLowerCase() +
                                groupsDetails.getCountry_code().toLowerCase() +
                                groupsDetails.getMobile().toLowerCase() +
                                groupsDetails.getEmail().toLowerCase();

                        if (participantsToBeSearched.contains(query.toString().toLowerCase())) {
                            participantsSearchedList.add(groupsDetails);
                        }
                    }

                    rv_participants.setAdapter(new GroupMembersAdminsAdapter(context, participantsSearchedList, groupId));
                } else {
                    rv_participants.setAdapter(new GroupMembersAdminsAdapter(context, participantsList, groupId));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private class GetAllMembersAdminList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_participants.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveAllGroupAdminDetails"));
            param.add(new ParamsPojo("group_id", groupId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_participants.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    GroupMembersAdminsListModel pojoDetails = new Gson().fromJson(result, GroupMembersAdminsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        participantsList = pojoDetails.getResult();

                        if (participantsList.size() > 0) {
                            rv_participants.setAdapter(new GroupMembersAdminsAdapter(context, participantsList, groupId));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_participants.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_participants.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_participants.setVisibility(View.GONE);
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
        Utilities.hideSoftKeyboard(GroupMembersAdmins_Activity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAllMembersAdminList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };
}
