package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import in.oriange.joinsta.activities.AllGroupNotifications_Activity;
import in.oriange.joinsta.activities.AllGroups_Activity;
import in.oriange.joinsta.activities.GroupRequests_Activity;
import in.oriange.joinsta.adapters.MyGroupsAdapter;
import in.oriange.joinsta.models.GroupRequestsListModel;
import in.oriange.joinsta.models.MyGroupsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Groups_Fragment extends Fragment {

    private static Context context;
    private UserSessionManager session;
    private CardView cv_search;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView rv_groups;
    private static LinearLayout ll_nopreview;
    private static SpinKitView progressBar;
    private ImageButton ib_notifications, ib_requests;
    private static TextView tv_group_request_count;

    private static String userId;
    private static ArrayList<MyGroupsListModel.ResultBean> myGroupsList;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);

        cv_search = rootView.findViewById(R.id.cv_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_groups = rootView.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        progressBar = rootView.findViewById(R.id.progressBar);
        ib_notifications = rootView.findViewById(R.id.ib_notifications);
        ib_requests = rootView.findViewById(R.id.ib_requests);
        tv_group_request_count = rootView.findViewById(R.id.tv_group_request_count);

        myGroupsList = new ArrayList<>();
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
            new GetMyGroupsList().execute();
            new GetRequestedGroups().execute();
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetMyGroupsList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        cv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AllGroups_Activity.class));
            }
        });

        ib_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AllGroupNotifications_Activity.class));
            }
        });

        ib_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupRequests_Activity.class));
            }
        });
    }

    public static class GetMyGroupsList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getgroupDetails");
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
                    myGroupsList = new ArrayList<>();
                    MyGroupsListModel pojoDetails = new Gson().fromJson(result, MyGroupsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        myGroupsList = pojoDetails.getResult();
                        rv_groups.setAdapter(new MyGroupsAdapter(context, myGroupsList));
                        rv_groups.setVisibility(View.VISIBLE);
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

    public static class GetRequestedGroups extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getRequestedGroups"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    GroupRequestsListModel pojoDetails = new Gson().fromJson(result, GroupRequestsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<GroupRequestsListModel.ResultBean> groupRequests = pojoDetails.getResult();

                        if (groupRequests.size() > 0) {
                            tv_group_request_count.setVisibility(View.VISIBLE);
                            tv_group_request_count.setText(String.valueOf(groupRequests.size()));
                        } else {
                            tv_group_request_count.setVisibility(View.GONE);
                        }

                    } else {
                        tv_group_request_count.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                tv_group_request_count.setVisibility(View.GONE);
            }
        }
    }


}
