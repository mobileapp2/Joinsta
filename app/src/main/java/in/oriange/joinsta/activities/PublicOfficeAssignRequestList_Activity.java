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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.PublicOfficeAssignRequestAdapter;
import in.oriange.joinsta.models.PublicOfficeAssignRequestModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class PublicOfficeAssignRequestList_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_public_office)
    RecyclerView rvPublicOffice;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;

    private Context context;
    private UserSessionManager session;
    private String userId;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_office_assign_request_list);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = PublicOfficeAssignRequestList_Activity.this;
        session = new UserSessionManager(context);
        rvPublicOffice.setLayoutManager(new LinearLayoutManager(context));
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
        if (Utilities.isNetworkAvailable(context)) {
            new GetAssignRequests().execute(userId);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("PublicOfficeAssignRequestList_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAssignRequests().execute(userId);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class GetAssignRequests extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvPublicOffice.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getPublicOfficeRequest");
            obj.addProperty("user_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rvPublicOffice.setVisibility(View.VISIBLE);
            String type;
            try {
                if (!result.equals("")) {
                    PublicOfficeAssignRequestModel pojoDetails = new Gson().fromJson(result, PublicOfficeAssignRequestModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<PublicOfficeAssignRequestModel.ResultBean> assignList = pojoDetails.getResult();

                        if (assignList.size() > 0) {
                            rvPublicOffice.setVisibility(View.VISIBLE);
                            llNopreview.setVisibility(View.GONE);
                            rvPublicOffice.setAdapter(new PublicOfficeAssignRequestAdapter(context, assignList));
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvPublicOffice.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvPublicOffice.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvPublicOffice.setVisibility(View.GONE);
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAssignRequests().execute(userId);
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
