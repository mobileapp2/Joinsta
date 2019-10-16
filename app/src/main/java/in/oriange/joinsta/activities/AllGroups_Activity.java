package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.AllGroupsAdapter;
import in.oriange.joinsta.models.AllGroupsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class AllGroups_Activity extends AppCompatActivity {

    private static Context context;
    private UserSessionManager session;
    private static EditText edt_search;
    private static RecyclerView rv_groups;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static SpinKitView progressBar;
    private static LinearLayout ll_nopreview;
    private static String userId;
    private static ArrayList<AllGroupsListModel.ResultBean> groupsList;
    private static InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_groups);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AllGroups_Activity.this;
        session = new UserSessionManager(context);
        edt_search = findViewById(R.id.edt_search);
        rv_groups = findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        ll_nopreview = findViewById(R.id.ll_nopreview);

        groupsList = new ArrayList<>();
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

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
            new GetGroupsList().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetGroupsList().execute();
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
                    rv_groups.setVisibility(View.GONE);
                } else {
                    rv_groups.setVisibility(View.VISIBLE);
                }


                if (groupsList.size() == 0) {
                    rv_groups.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<AllGroupsListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (AllGroupsListModel.ResultBean groupsDetails : groupsList) {

                        String groupsToBeSearched = groupsDetails.getGroup_name().toLowerCase() +
                                groupsDetails.getGroup_code().toLowerCase();

                        if (groupsToBeSearched.contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }
                    rv_groups.setAdapter(new AllGroupsAdapter(context, groupsSearchedList));
                } else {
                    rv_groups.setAdapter(new AllGroupsAdapter(context, groupsList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static class GetGroupsList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_groups.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "searchgroup");
            obj.addProperty("code", "");
            obj.addProperty("search_term", "");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    groupsList = new ArrayList<>();
                    AllGroupsListModel pojoDetails = new Gson().fromJson(result, AllGroupsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        groupsList = pojoDetails.getResult();

                        if (!edt_search.getText().toString().trim().isEmpty()) {
                            ArrayList<AllGroupsListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                            for (AllGroupsListModel.ResultBean groupsDetails : groupsList) {

                                String groupsToBeSearched = groupsDetails.getGroup_name().toLowerCase() +
                                        groupsDetails.getGroup_code().toLowerCase();

                                if (groupsToBeSearched.contains(edt_search.getText().toString().toLowerCase())) {
                                    groupsSearchedList.add(groupsDetails);
                                }
                            }

                            rv_groups.setAdapter(new AllGroupsAdapter(context, groupsSearchedList));
                        }
                        rv_groups.setVisibility(View.VISIBLE);
                        inputMethodManager.toggleSoftInputFromWindow(edt_search.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                        edt_search.setFocusable(true);
                        edt_search.setFocusableInTouchMode(true);
                        edt_search.requestFocus();
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_groups.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_groups.setVisibility(View.GONE);
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
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(AllGroups_Activity.this);

    }
}
