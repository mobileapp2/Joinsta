package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.ReportIssueAdapter;
import in.oriange.joinsta.models.UserFeedbackListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class ReportIssue_Activity extends AppCompatActivity {

    private static Context context;
    private UserSessionManager session;
    private FloatingActionButton btn_add;
    private static RecyclerView rv_feedback;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static SpinKitView progressBar;
    private static LinearLayout ll_nopreview;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();

    }

    private void init() {
        context = ReportIssue_Activity.this;
        session = new UserSessionManager(context);

        btn_add = findViewById(R.id.btn_add);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_feedback = findViewById(R.id.rv_feedback);
        rv_feedback.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = findViewById(R.id.ll_nopreview);

    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetUserFeedback().execute(userId);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
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

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetUserFeedback().execute(userId);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddReportIssue_Activity.class));
            }
        });
    }

    public static class GetUserFeedback extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_feedback.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getuserfeedback");
            obj.addProperty("userid", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.FEEDBACKAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ArrayList<UserFeedbackListModel.ResultBean> feedbackList = new ArrayList<>();
                    UserFeedbackListModel pojoDetails = new Gson().fromJson(result, UserFeedbackListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        feedbackList = pojoDetails.getResult();


                        ArrayList<UserFeedbackListModel.ResultBean> filterfeedbackList = new ArrayList<>();

                        for (int i = 0; i < feedbackList.size(); i++) {
                            if (feedbackList.get(i).getRating().equals("0")) {
                                filterfeedbackList.add(feedbackList.get(i));
                            }
                        }

                        if (filterfeedbackList.size() > 0) {
                            rv_feedback.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_feedback.setAdapter(new ReportIssueAdapter(context, filterfeedbackList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_feedback.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_feedback.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_feedback.setVisibility(View.GONE);
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
