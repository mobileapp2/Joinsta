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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.EventMemberStatusAdapter;
import in.oriange.joinsta.models.EventMemberStatusModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class EventPaidMemberStatus_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;

    private RecyclerView rv_status;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String userId, eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_paid_member_status);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EventPaidMemberStatus_Activity.this;
        session = new UserSessionManager(context);

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = findViewById(R.id.ll_nopreview);

        rv_status = findViewById(R.id.rv_status);
        rv_status.setLayoutManager(new LinearLayoutManager(context));
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

        if (Utilities.isNetworkAvailable(context)) {
            new GetMemberStatus().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetMemberStatus().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private class GetMemberStatus extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_status.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getPaidEventMembers");
            obj.addProperty("event_id", eventId);
            res = APICall.JSONAPICall(ApplicationConstants.PAYMENTTRACKAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_status.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<EventMemberStatusModel.ResultBean> statusList = new ArrayList<>();
                    EventMemberStatusModel pojoDetails = new Gson().fromJson(result, EventMemberStatusModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        statusList = pojoDetails.getResult();

                        if (statusList.size() > 0) {
                            rv_status.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_status.setAdapter(new EventMemberStatusAdapter(context, statusList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_status.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_status.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_status.setVisibility(View.GONE);
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
