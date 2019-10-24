package in.oriange.joinsta.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.AddGroupMemberSupervisor_Activity;
import in.oriange.joinsta.activities.AddGroupSupervisor_Activity;
import in.oriange.joinsta.adapters.GroupSupervisorsAdapter;
import in.oriange.joinsta.models.GroupSupervisorsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.Utilities;

public class GroupSupervisors_Fragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_group_supervisor;
    private FloatingActionButton btn_add;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<GroupSupervisorsListModel.ResultBean> groupSupervisors;
    private String groupId;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_supervisors, container, false);
        context = getActivity();

        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_group_supervisor = rootView.findViewById(R.id.rv_group_supervisor);
        rv_group_supervisor.setLayoutManager(new LinearLayoutManager(context));
        btn_add = rootView.findViewById(R.id.btn_add);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        groupSupervisors = new ArrayList<>();
    }

    private void getSessionDetails() {

    }

    private void setDefault() {
        groupId = this.getArguments().getString("groupId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetGroupSupervisors().execute();
        } else {
            Utilities.showMessage("Please check your internet connection", context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("GroupSupervisors_Fragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetGroupSupervisors().execute();
                } else {
                    Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddGroupMemberSupervisor_Activity.class)
                        .putExtra("groupId", groupId)
                        .putExtra("role", "group_supervisor"));
            }
        });
    }

    private class GetGroupSupervisors extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_group_supervisor.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveAllGroupSupervisorsDetails"));
            param.add(new ParamsPojo("group_id", groupId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_group_supervisor.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    groupSupervisors = new ArrayList<>();
                    GroupSupervisorsListModel pojoDetails = new Gson().fromJson(result, GroupSupervisorsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        groupSupervisors = pojoDetails.getResult();

                        if (groupSupervisors.size() > 0) {
                            rv_group_supervisor.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_group_supervisor.setAdapter(new GroupSupervisorsAdapter(context, groupSupervisors, groupId));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_group_supervisor.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_group_supervisor.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_group_supervisor.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetGroupSupervisors().execute();
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
