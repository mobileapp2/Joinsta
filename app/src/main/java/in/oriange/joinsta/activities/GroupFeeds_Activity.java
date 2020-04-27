package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private EditText edt_search;
    private RecyclerView rv_feeds;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private FloatingActionButton btn_add_post;
    private String userId, groupId, isAdmin, canPost, isPublicGroup;

    private LocalBroadcastManager localBroadcastManager;
    private GroupFeedsAdapter groupFeedsAdapter;

    private List<GroupFeedsModel.ResultBean> feedsList;

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

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_feeds = findViewById(R.id.rv_feeds);
        rv_feeds.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);
        btn_add_post = findViewById(R.id.btn_add_post);

        feedsList = new ArrayList<>();
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
        isAdmin = getIntent().getStringExtra("isAdmin");
        canPost = getIntent().getStringExtra("canPost");
        isPublicGroup = getIntent().getStringExtra("isPublicGroup");

        if (isPublicGroup.equals("2") && !isAdmin.equals("1"))
            btn_add_post.setVisibility(View.GONE);

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
                if (canPost.equals("1")) {
                    startActivity(new Intent(context, AddGroupFeeds_Activity.class)
                            .putExtra("groupId", groupId)
                            .putExtra("isAdmin", isAdmin));
                } else {
                    Utilities.showMessage("You are not authorised to add post", context, 2);
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
                    groupFeedsAdapter = new GroupFeedsAdapter(context, feedsList, isAdmin);
                    rv_feeds.setAdapter(groupFeedsAdapter);
                    return;
                }

                if (feedsList.size() == 0) {
                    rv_feeds.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<GroupFeedsModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (GroupFeedsModel.ResultBean groupsDetails : feedsList) {

                        String feedToBeSearched = groupsDetails.getFirst_name().toLowerCase() + groupsDetails.getFeed_title().toLowerCase() + groupsDetails.getFeed_text().toLowerCase();

                        if (feedToBeSearched.contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }

                    groupFeedsAdapter = new GroupFeedsAdapter(context, groupsSearchedList, isAdmin);
                    rv_feeds.setAdapter(groupFeedsAdapter);
                } else {
                    groupFeedsAdapter = new GroupFeedsAdapter(context, feedsList, isAdmin);
                    rv_feeds.setAdapter(groupFeedsAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    feedsList = new ArrayList<>();
                    GroupFeedsModel pojoDetails = new Gson().fromJson(result, GroupFeedsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        feedsList = pojoDetails.getResult();

                        if (feedsList.size() > 0) {

                            List<GroupFeedsModel.ResultBean> tempFeedsList = new ArrayList<>();

                            if (!isAdmin.equals("1")) {
                                for (GroupFeedsModel.ResultBean feedDetails : feedsList) {
                                    if (feedDetails.getIs_hidden().equals("1")) {
                                        if (feedDetails.getCreated_by().equals(userId)) {
                                            tempFeedsList.add(feedDetails);
                                        }
                                    } else {
                                        tempFeedsList.add(feedDetails);
                                    }
                                }
                                feedsList.clear();
                                feedsList.addAll(tempFeedsList);
                            }

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
                                    groupFeedsAdapter = new GroupFeedsAdapter(context, feedsList, isAdmin);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin.equals("1") && !isPublicGroup.equals("2")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menus_settings, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(context, CanPostFeedsAuthorization_Activity.class)
                        .putExtra("isAdmin", isAdmin)
                        .putExtra("groupId", groupId));
                break;
            default:
                break;
        }
        return true;
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
