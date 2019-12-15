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
import androidx.appcompat.widget.AppCompatEditText;
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
import in.oriange.joinsta.adapters.GroupNotificationAdapter;
import in.oriange.joinsta.models.GroupNotificationListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupNotifications_Activity extends AppCompatActivity {

    private static Context context;
    private UserSessionManager session;
    private EditText edt_search;
    private static RecyclerView rv_notification;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static SpinKitView progressBar;
    private static LinearLayout ll_nopreview;
    private static String userId, groupId, groupName;

    private static List<GroupNotificationListModel.ResultBean> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notifications);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupNotifications_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_notification = findViewById(R.id.rv_notification);
        rv_notification.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);

        notificationList = new ArrayList<>();
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


        if (Utilities.isNetworkAvailable(context)) {
            new GetGroupNotification().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetGroupNotification().execute();
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
                    rv_notification.setAdapter(new GroupNotificationAdapter(context, notificationList));
                    return;
                }

                if (notificationList.size() == 0) {
                    rv_notification.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<GroupNotificationListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (GroupNotificationListModel.ResultBean groupsDetails : notificationList) {

                        String groupsToBeSearched = groupsDetails.getSubject().toLowerCase() +
                                groupsDetails.getMessage().toLowerCase() +
                                groupsDetails.getCreated_at().toLowerCase();

                        if (groupsToBeSearched.contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }
                    rv_notification.setAdapter(new GroupNotificationAdapter(context, groupsSearchedList));
                } else {
                    rv_notification.setAdapter(new GroupNotificationAdapter(context, notificationList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static class GetGroupNotification extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_notification.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getGroupNotificationDetails");
            obj.addProperty("group_id", groupId);
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_notification.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    notificationList = new ArrayList<>();
                    GroupNotificationListModel pojoDetails = new Gson().fromJson(result, GroupNotificationListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        notificationList = pojoDetails.getResult();

                        if (notificationList.size() > 0) {
                            rv_notification.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_notification.setAdapter(new GroupNotificationAdapter(context, notificationList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_notification.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_notification.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_notification.setVisibility(View.GONE);
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        AppCompatEditText toolbar_groupname = findViewById(R.id.toolbar_groupname);
        toolbar_groupname.setText(groupName);
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
