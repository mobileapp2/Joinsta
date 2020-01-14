package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupRequestsAdapter;
import in.oriange.joinsta.models.GroupRequestsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupRequests_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_group_requests;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String userId, groupId;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_requests);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupRequests_Activity.this;
        session = new UserSessionManager(context);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        rv_group_requests = findViewById(R.id.rv_group_requests);
        rv_group_requests.setLayoutManager(new LinearLayoutManager(context));
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
            new GetRequestedGroups().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("GroupRequests_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {

    }

    private class GetRequestedGroups extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_group_requests.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getRequestedGroups"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_group_requests.setVisibility(View.VISIBLE);
            String type;
            try {
                if (!result.equals("")) {
                    GroupRequestsListModel pojoDetails = new Gson().fromJson(result, GroupRequestsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<GroupRequestsListModel.ResultBean> groupRequests = pojoDetails.getResult();

                        if (groupRequests.size() > 0) {
                            rv_group_requests.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_group_requests.setAdapter(new GroupRequestsAdapter(context, groupRequests));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_group_requests.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_group_requests.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_group_requests.setVisibility(View.GONE);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetRequestedGroups().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
