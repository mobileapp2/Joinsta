package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.AddEventsFree_Activity;
import in.oriange.joinsta.adapters.EventsFreeAdapter;
import in.oriange.joinsta.models.EventsFreeModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class EventsFree_Fragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText edt_search;
    private RecyclerView rv_event;
    private FloatingActionButton btn_add;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<EventsFreeModel.ResultBean> eventList;
    private String groupId, userId, isAdmin;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_free, container, false);
        context = getActivity();

        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_event = rootView.findViewById(R.id.rv_event);
        rv_event.setLayoutManager(new LinearLayoutManager(context));
        btn_add = rootView.findViewById(R.id.btn_add);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        eventList = new ArrayList<>();
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

    @SuppressLint("RestrictedApi")
    private void setDefault() {
        groupId = this.getArguments().getString("groupId");
        isAdmin = this.getArguments().getString("isAdmin");

        if (Utilities.isNetworkAvailable(context)) {
            new GetFreeEvents().execute();
        } else {
            Utilities.showMessage("Please check your internet connection", context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("EventsFree_Fragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        if (isAdmin.equals("1"))
            btn_add.setVisibility(View.VISIBLE);
        else
            btn_add.setVisibility(View.GONE);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetFreeEvents().execute();
                } else {
                    Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddEventsFree_Activity.class)
                        .putExtra("groupId", groupId));
            }
        });
    }

    private class GetFreeEvents extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_event.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getGroupFreeEvent");
            obj.addProperty("group_id", groupId);
            res = APICall.JSONAPICall(ApplicationConstants.FREEEVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_event.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    eventList = new ArrayList<>();
                    EventsFreeModel pojoDetails = new Gson().fromJson(result, EventsFreeModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        eventList = pojoDetails.getResult();

                        if (eventList.size() > 0) {
                            rv_event.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_event.setAdapter(new EventsFreeAdapter(context, eventList, groupId));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_event.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_event.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_event.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetFreeEvents().execute();
            } else {
                Utilities.showMessage("Please check your internet connection", context, 2);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

}
