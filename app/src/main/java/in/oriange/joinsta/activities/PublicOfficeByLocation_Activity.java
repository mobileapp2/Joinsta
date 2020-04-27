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

import androidx.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.PublicOfficeAdapter;
import in.oriange.joinsta.models.PublicOfficeModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class PublicOfficeByLocation_Activity extends AppCompatActivity {

    @BindView(R.id.edt_location)
    AppCompatEditText edtLocation;
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
    @BindView(R.id.edt_search)
    EditText edtSearch;

    private Context context;
    private UserSessionManager session;
    private String userId;
    private final int LOCATION_REQUEST = 300;
    private LocalBroadcastManager localBroadcastManager;
    private List<PublicOfficeModel.ResultBean> officeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_office_by_location);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = PublicOfficeByLocation_Activity.this;
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
            edtLocation.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetPublicOfficeForLocation().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("PublicOfficeByLocation_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetPublicOfficeForLocation().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        edtLocation.setOnClickListener(v -> {
            startActivityForResult(new Intent(context, SelectCity_Activity.class).putExtra("requestCode", LOCATION_REQUEST), LOCATION_REQUEST);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 2));
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
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeSearchedList, 2));
                } else {
                    rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 2));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetPublicOfficeForLocation extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getPublicOfficesbyLocation");
            obj.addProperty("user_id", userId);
            obj.addProperty("location", edtLocation.getText().toString().trim());
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
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
                            rvPublicOffice.setAdapter(new PublicOfficeAdapter(context, officeList, 2));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            if (requestCode == LOCATION_REQUEST) {
                edtLocation.setText(data.getStringExtra("cityName"));
                if (Utilities.isNetworkAvailable(context)) {
                    new GetPublicOfficeForLocation().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
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
                new GetPublicOfficeForLocation().execute();
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
        hideSoftKeyboard(PublicOfficeByLocation_Activity.this);
    }


}
