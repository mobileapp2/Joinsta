package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import in.oriange.joinsta.adapters.EventNotificationAdapter;
import in.oriange.joinsta.models.EventNotificationsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class EventsNotifications_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private EditText edt_search;
    private RecyclerView rv_notification;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String userId, eventId, eventName;

    private List<EventNotificationsModel.ResultBean> notificationList, notificationListForSearch;
    private LocalBroadcastManager localBroadcastManager;

    private EventNotificationAdapter notificationAdapter;

    private boolean isFavouriteFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_notifications);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EventsNotifications_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_notification = findViewById(R.id.rv_notification);
        rv_notification.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);

        notificationList = new ArrayList<>();
        notificationAdapter = new EventNotificationAdapter(context, notificationList);
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
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");

        if (Utilities.isNetworkAvailable(context)) {
            new GetEventNotification().execute("0");
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("EventsNotifications_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetEventNotification().execute("0");
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
                    notificationAdapter = new EventNotificationAdapter(context, notificationListForSearch);
                    rv_notification.setAdapter(notificationAdapter);
                    return;
                }

                if (notificationList.size() == 0) {
                    rv_notification.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<EventNotificationsModel.ResultBean> notificationSearchedList = new ArrayList<>();
                    for (EventNotificationsModel.ResultBean groupsDetails : notificationList) {

                        String groupsToBeSearched = groupsDetails.getSubject().toLowerCase() +
                                groupsDetails.getMessage().toLowerCase() +
                                groupsDetails.getCreated_at().toLowerCase();

                        if (groupsToBeSearched.contains(query.toString().toLowerCase())) {
                            notificationSearchedList.add(groupsDetails);
                        }
                    }
                    notificationAdapter = new EventNotificationAdapter(context, notificationSearchedList);
                    rv_notification.setAdapter(notificationAdapter);
                } else {
                    notificationAdapter = new EventNotificationAdapter(context, notificationListForSearch);
                    rv_notification.setAdapter(notificationAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetEventNotification extends AsyncTask<String, Void, String> {

        private String TYPE = "";

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
            TYPE = params[0];
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventNotificationDetails");
            obj.addProperty("event_id", eventId);
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
                    EventNotificationsModel pojoDetails = new Gson().fromJson(result, EventNotificationsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        notificationList = pojoDetails.getResult();
                        if (TYPE.equals("0")) {
                            if (!isFavouriteFiltered) {
                                notificationAdapter = new EventNotificationAdapter(context, notificationList);
                                rv_notification.setAdapter(notificationAdapter);
                                notificationListForSearch = notificationList;
                            } else {
                                ArrayList<EventNotificationsModel.ResultBean> filteredNotifications = new ArrayList<>();
                                for (EventNotificationsModel.ResultBean resultBean : notificationList) {
                                    if (resultBean.getIs_fav().equals("1"))
                                        filteredNotifications.add(resultBean);
                                }
                                notificationAdapter = new EventNotificationAdapter(context, filteredNotifications);
                                rv_notification.setAdapter(notificationAdapter);

                                notificationListForSearch = filteredNotifications;
                            }
                        } else {
                            if (!isFavouriteFiltered) {
                                notificationAdapter.refresh(notificationList);
                                notificationListForSearch = notificationList;
                            } else {
                                ArrayList<EventNotificationsModel.ResultBean> filteredNotifications = new ArrayList<>();
                                for (EventNotificationsModel.ResultBean resultBean : notificationList) {
                                    if (resultBean.getIs_fav().equals("1"))
                                        filteredNotifications.add(resultBean);
                                }
                                notificationAdapter.refresh(filteredNotifications);
                                notificationListForSearch = filteredNotifications;
                            }
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
        AppCompatEditText toolbar_event_name = findViewById(R.id.toolbar_event_name);
        toolbar_event_name.setText(eventName);
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
                new GetEventNotification().execute("1");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_filter_notifications, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setView(promptView);

            final RadioButton rb_all = promptView.findViewById(R.id.rb_all);
            final RadioButton rb_favourite = promptView.findViewById(R.id.rb_favourite);

            if (isFavouriteFiltered) rb_favourite.setChecked(true);
            else rb_all.setChecked(true);

            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (rb_all.isChecked()) {
                        isFavouriteFiltered = false;
                        notificationAdapter.refresh(notificationList);
                        notificationListForSearch = notificationList;
                    } else if (rb_favourite.isChecked()) {
                        isFavouriteFiltered = true;
                        ArrayList<EventNotificationsModel.ResultBean> filteredNotifications = new ArrayList<>();
                        for (EventNotificationsModel.ResultBean resultBean : notificationList) {
                            if (resultBean.getIs_fav().equals("1"))
                                filteredNotifications.add(resultBean);
                        }
                        notificationAdapter.refresh(filteredNotifications);
                        notificationListForSearch = filteredNotifications;
                    }
                }
            });

            final AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
        }
        return true;
    }
}
