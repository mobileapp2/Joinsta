package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class EventFreeMemberStatus_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;

    private EditText edt_search;
    private ImageButton imb_filter;
    private TextView tv_filter_count;
    private RecyclerView rv_status;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private String userId, eventId;

    private List<MasterModel> statusTypeList;
    private List<EventMemberStatusModel.ResultBean> membersList, filteredMembersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_free_member_status);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EventFreeMemberStatus_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        imb_filter = findViewById(R.id.imb_filter);
        tv_filter_count = findViewById(R.id.tv_filter_count);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = findViewById(R.id.ll_nopreview);

        rv_status = findViewById(R.id.rv_status);
        rv_status.setLayoutManager(new LinearLayoutManager(context));

        membersList = new ArrayList<>();
        filteredMembersList = new ArrayList<>();
        statusTypeList = new ArrayList<>();
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

        statusTypeList.add(new MasterModel("Accepted", "accepted", false));
        statusTypeList.add(new MasterModel("May Be", "maybe", false));
        statusTypeList.add(new MasterModel("Rejected", "rejected", false));
        statusTypeList.add(new MasterModel("Deleted", "deleted", false));
        statusTypeList.add(new MasterModel("Pending", "", false));
    }

    private void setEventHandler() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    for (int i = 0; i < statusTypeList.size(); i++) {
                        statusTypeList.get(i).setChecked(false);
                    }

                    edt_search.setText("");
                    tv_filter_count.setVisibility(View.GONE);
                    new GetMemberStatus().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        imb_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventTypeListDialog();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_status.setAdapter(new EventMemberStatusAdapter(context, filteredMembersList));
                    return;
                }

                if (filteredMembersList.size() == 0) {
                    rv_status.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<EventMemberStatusModel.ResultBean> membersSearchedList = new ArrayList<>();
                    for (EventMemberStatusModel.ResultBean memberDetails : filteredMembersList) {

                        String memberToBeSearched = memberDetails.getFirst_name().toLowerCase() +
                                memberDetails.getMobile().toLowerCase() +
                                memberDetails.getEmail().toLowerCase();

                        if (memberToBeSearched.contains(query.toString().toLowerCase())) {
                            membersSearchedList.add(memberDetails);
                        }
                    }
                    rv_status.setAdapter(new EventMemberStatusAdapter(context, membersSearchedList));
                } else {
                    rv_status.setAdapter(new EventMemberStatusAdapter(context, filteredMembersList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showEventTypeListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Status Type");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new StatusTypeListAdapter());

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_search.setText("");
                int selectedTypeCount = 0;
                filteredMembersList = new ArrayList<>();
                for (MasterModel statusType : statusTypeList) {
                    if (statusType.isChecked()) {
                        selectedTypeCount = selectedTypeCount + 1;
                        for (EventMemberStatusModel.ResultBean memberDetails : membersList)
                            if (statusType.getId().equals(memberDetails.getStatus()))
                                filteredMembersList.add(memberDetails);
                    }
                }

                if (selectedTypeCount == 0) {
                    filteredMembersList = membersList;
                    tv_filter_count.setVisibility(View.GONE);
                    rv_status.setAdapter(new EventMemberStatusAdapter(context, filteredMembersList));
                } else {
                    tv_filter_count.setVisibility(View.VISIBLE);
                    tv_filter_count.setText(String.valueOf(selectedTypeCount));
                    rv_status.setAdapter(new EventMemberStatusAdapter(context, filteredMembersList));
                }
            }
        });

        builder.setNegativeButton("Clear Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_search.setText("");
                for (int i = 0; i < statusTypeList.size(); i++) {
                    statusTypeList.get(i).setChecked(false);
                }

                filteredMembersList = membersList;
                tv_filter_count.setVisibility(View.GONE);
                rv_status.setAdapter(new EventMemberStatusAdapter(context, filteredMembersList));
            }
        });

        builder.create().show();
    }

    private class StatusTypeListAdapter extends RecyclerView.Adapter<StatusTypeListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(statusTypeList.get(position).getName());

            if (statusTypeList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    statusTypeList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return statusTypeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
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
            obj.addProperty("type", "getFreeEventMembers");
            obj.addProperty("event_id", eventId);
            res = APICall.JSONAPICall(ApplicationConstants.FREEEVENTSAPI, obj.toString());
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
                    membersList = new ArrayList<>();
                    filteredMembersList = new ArrayList<>();
                    EventMemberStatusModel pojoDetails = new Gson().fromJson(result, EventMemberStatusModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        membersList = pojoDetails.getResult();
                        filteredMembersList = membersList;

                        if (membersList.size() > 0) {
                            rv_status.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_status.setAdapter(new EventMemberStatusAdapter(context, membersList));
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
