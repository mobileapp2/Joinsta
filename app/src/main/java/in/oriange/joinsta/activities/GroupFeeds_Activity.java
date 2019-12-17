package in.oriange.joinsta.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupFeedsAdapter;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupFeeds_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private RecyclerView rv_feeds;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private FloatingActionButton btn_add_post;
    private String userId, groupId, groupName, isAdmin;

    private LocalBroadcastManager localBroadcastManager;
    private GroupFeedsAdapter groupFeedsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feeds);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupFeeds_Activity.this;
        session = new UserSessionManager(context);

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_feeds = findViewById(R.id.rv_feeds);
        rv_feeds.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);
        btn_add_post = findViewById(R.id.btn_add_post);
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
        groupName = getIntent().getStringExtra("groupName");
        isAdmin = getIntent().getStringExtra("isAdmin");

        if (Utilities.isNetworkAvailable(context)) {
            new GetAllFeedDetails().execute("1");
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("GroupFeeds_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAllFeedDetails().execute("1");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddGroupFeeds_Activity.class)
                        .putExtra("groupId", groupId)
                        .putExtra("isAdmin", isAdmin));
            }
        });
    }

    private class GetAllFeedDetails extends AsyncTask<String, Void, String> {

        private String TYPE = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_feeds.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            TYPE = params[0];
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveAllFeedDetails"));
            param.add(new ParamsPojo("group_id", groupId));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.FEEDSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_feeds.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<GroupFeedsModel.ResultBean> feedsList = new ArrayList<>();
                    GroupFeedsModel pojoDetails = new Gson().fromJson(result, GroupFeedsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        feedsList = pojoDetails.getResult();

                        if (feedsList.size() > 0) {

                            rv_feeds.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            if (TYPE.equals("2")) {
                                if (feedsList.size() != 0) {
                                    GroupFeedsModel.ResultBean feedDetails = feedsList.get(GroupFeedsAdapter.itemClickedPosition);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeedsComments_Activity")
                                            .putExtra("feedDetails", feedDetails));
                                    groupFeedsAdapter.swap(feedsList);
                                }
                            } else {
                                groupFeedsAdapter = new GroupFeedsAdapter(context, feedsList);
                                rv_feeds.setAdapter(groupFeedsAdapter);
                            }

                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_feeds.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_feeds.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_feeds.setVisibility(View.GONE);
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
        Utilities.hideSoftKeyboard(GroupFeeds_Activity.this);
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
                new GetAllFeedDetails().execute("2");
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };
}
