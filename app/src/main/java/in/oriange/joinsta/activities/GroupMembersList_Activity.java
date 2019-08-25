package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupMembersAdapter;
import in.oriange.joinsta.models.GroupMemebersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersList_Activity extends AppCompatActivity {

    private Context context;
    private RecyclerView rv_group_members;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_list);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupMembersList_Activity.this;

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_group_members = findViewById(R.id.rv_group_members);
        rv_group_members.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);

    }

    private void setDefault() {
        groupId = getIntent().getStringExtra("groupId");
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
                    ArrayList<GroupMemebersListModel.ResultBean> feedbackList = new ArrayList<>();
                    GroupMemebersListModel pojoDetails = new Gson().fromJson(result, GroupMemebersListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        feedbackList = pojoDetails.getResult();

                        if (feedbackList.size() > 0) {
                            rv_group_members.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_group_members.setAdapter(new GroupMembersAdapter(context, feedbackList));
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
