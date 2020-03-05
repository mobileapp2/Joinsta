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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.DisclaimerAdapter;
import in.oriange.joinsta.models.DisclaimerModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.Utilities;

public class FeedsDisclaimer_Activity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.rv_disclaimer)
    RecyclerView rvDisclaimer;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_disclaimer);
        ButterKnife.bind(this);

        init();
        setDefault();
        setUpToolbar();
    }

    private void init() {
        context = FeedsDisclaimer_Activity.this;
    }

    private void setDefault() {
        rvDisclaimer.setLayoutManager(new LinearLayoutManager(context));

        if (Utilities.isNetworkAvailable(context))
            new GetDisclaimer().execute();
        else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
    }

    private class GetDisclaimer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvDisclaimer.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getDisclaimerString"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.FEEDSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rvDisclaimer.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<DisclaimerModel.ResultBean> disclaimerList = new ArrayList<>();
                    DisclaimerModel pojoDetails = new Gson().fromJson(result, DisclaimerModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        disclaimerList = pojoDetails.getResult();

                        if (disclaimerList.size() > 0) {
                            rvDisclaimer.setAdapter(new DisclaimerAdapter(context, disclaimerList));
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvDisclaimer.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvDisclaimer.setVisibility(View.GONE);
                    }
                } else {
                    llNopreview.setVisibility(View.VISIBLE);
                    rvDisclaimer.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvDisclaimer.setVisibility(View.GONE);
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
