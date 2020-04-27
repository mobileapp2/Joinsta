package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.PublicOfficeAdapter;
import in.oriange.joinsta.models.PublicOfficeAssignRequestModel;
import in.oriange.joinsta.models.PublicOfficeModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class MyPublicOffice_Activity extends AppCompatActivity {

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
    @BindView(R.id.btn_add)
    FloatingActionButton btnAdd;
    @BindView(R.id.btn_requests)
    MaterialButton btnRequests;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.tv_requests_count)
    TextView tvRequestsCount;

    private Context context;
    private UserSessionManager session;
    private String userId, officeTypeId, officeTypeName;
    private LocalBroadcastManager localBroadcastManager;
    private List<PublicOfficeModel.ResultBean> officeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_public_office);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = MyPublicOffice_Activity.this;
        session = new UserSessionManager(context);
        officeList = new ArrayList<>();

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
        officeTypeId = getIntent().getStringExtra("officeTypeId");
        officeTypeName = getIntent().getStringExtra("officeTypeName");

        if (Utilities.isNetworkAvailable(context)) {
            new GetPublicOffice().execute();
            new GetAssignRequests().execute(userId);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("MyPublicOffice_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetPublicOffice().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(context, AddPublicOffice_Activity.class)
                .putExtra("officeTypeId", officeTypeId)
                .putExtra("officeTypeName", officeTypeName)));

        btnRequests.setOnClickListener(v -> startActivity(new Intent(context, PublicOfficeAssignRequestList_Activity.class)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 1));
                    return;
                }

                if (officeList.size() == 0) {
                    rvPublicOffice.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<PublicOfficeModel.ResultBean> officeSearchedList = new ArrayList<>();
                    for (PublicOfficeModel.ResultBean groupsDetails : officeList) {

                        StringBuilder tag = new StringBuilder();
                        if (groupsDetails.getTags().size() != 0)
                            for (PublicOfficeModel.ResultBean.TagsBean tags : groupsDetails.getTags())
                                tag.append(tags.getTag_name());

                        String groupsToBeSearched = groupsDetails.getName().toLowerCase() +
                                groupsDetails.getLocal_name().toLowerCase() +
                                groupsDetails.getDepartment_functions().toLowerCase() +
                                tag.toString().toLowerCase();

                        if (groupsToBeSearched.contains(query.toString().toLowerCase())) {
                            officeSearchedList.add(groupsDetails);
                        }
                    }
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeSearchedList, 1));
                } else {
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 1));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetPublicOffice extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getoffice");
            obj.addProperty("user_id", userId);
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
                    PublicOfficeModel pojoDetails = new Gson().fromJson(result, PublicOfficeModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        officeList = pojoDetails.getResult();

                        if (officeList.size() > 0) {
                            rvPublicOffice.setVisibility(View.VISIBLE);
                            llNopreview.setVisibility(View.GONE);
                            rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 1));
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

    private class GetAssignRequests extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            String type;
            try {
                if (!result.equals("")) {
                    PublicOfficeAssignRequestModel pojoDetails = new Gson().fromJson(result, PublicOfficeAssignRequestModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<PublicOfficeAssignRequestModel.ResultBean> assignList = pojoDetails.getResult();

                        if (assignList.size() > 0) {
                            List<PublicOfficeAssignRequestModel.ResultBean> filteredAssignList = new ArrayList<>();

                            for (PublicOfficeAssignRequestModel.ResultBean resultBean : assignList) {
                                if (resultBean.getStatus().equals("requested")) {
                                    filteredAssignList.add(resultBean);
                                }
                            }

                            if (filteredAssignList.size() > 0) {
                                tvRequestsCount.setVisibility(View.VISIBLE);
                                tvRequestsCount.setText(String.valueOf(filteredAssignList.size()));
                            } else {
                                tvRequestsCount.setVisibility(View.GONE);
                            }

                        } else {
                            tvRequestsCount.setVisibility(View.GONE);
                        }
                    } else {
                        tvRequestsCount.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvRequestsCount.setVisibility(View.GONE);
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
                new GetPublicOffice().execute();
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(MyPublicOffice_Activity.this);
    }

}
