package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.GroupMemebersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class CanPostFeedsAuthorization_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private EditText edt_search;
    private RecyclerView rv_members;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<GroupMemebersListModel.ResultBean> groupMembersList;
    private String userId, groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_post_feeds_authorization);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = CanPostFeedsAuthorization_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        rv_members = findViewById(R.id.rv_members);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        rv_members.setLayoutManager(new LinearLayoutManager(context));

        groupMembersList = new ArrayList<>();
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
        groupId = getIntent().getStringExtra("groupId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetGroupMembers().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (!query.toString().equals("")) {
                    List<GroupMemebersListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (GroupMemebersListModel.ResultBean groupsDetails : groupMembersList) {
                        if (groupsDetails.getFirst_name().toLowerCase().contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }
                    rv_members.setAdapter(new GroupMembersListAdapter(groupsSearchedList));
                } else {
                    rv_members.setAdapter(new GroupMembersListAdapter(groupMembersList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetGroupMembers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_members.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getgroupmember");
            obj.addProperty("id", groupId);
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_members.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    groupMembersList = new ArrayList<>();
                    GroupMemebersListModel pojoDetails = new Gson().fromJson(result, GroupMemebersListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        groupMembersList = pojoDetails.getResult();

                        ArrayList<GroupMemebersListModel.ResultBean> foundMembers = new ArrayList<GroupMemebersListModel.ResultBean>();
                        for (GroupMemebersListModel.ResultBean groupDetails : groupMembersList) {
                            if (groupDetails.getRole().equals("group_member") || groupDetails.getRole().equals("group_supervisor")) {
                                foundMembers.add(groupDetails);
                            }
                        }


                        groupMembersList.clear();
                        groupMembersList.addAll(foundMembers);

                        if (groupMembersList.size() > 0) {
                            rv_members.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_members.setAdapter(new GroupMembersListAdapter(groupMembersList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_members.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_members.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_members.setVisibility(View.GONE);
            }
        }
    }

    private class GroupMembersListAdapter extends RecyclerView.Adapter<GroupMembersListAdapter.MyViewHolder> {

        private List<GroupMemebersListModel.ResultBean> districtList;

        public GroupMembersListAdapter(List<GroupMemebersListModel.ResultBean> districtList) {
            this.districtList = districtList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_can_post, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();
            final GroupMemebersListModel.ResultBean memberObj = districtList.get(position);

            holder.cb_member.setText(memberObj.getFirst_name());

            if (memberObj.getCan_post().equals("1")) {
                holder.cb_member.setChecked(true);
            } else {
                holder.cb_member.setChecked(false);
            }

            holder.cb_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String canPost = "0";

                    if (isChecked) {
                        canPost = "1";
                    }

                    for (int i = 0; i < groupMembersList.size(); i++) {
                        if (groupMembersList.get(i).getId().equals(memberObj.getId())) {
                            groupMembersList.get(i).setCan_post(canPost);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return districtList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_member;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_member = view.findViewById(R.id.cb_member);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_info_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                JsonArray jsonArray = new JsonArray();
                for (GroupMemebersListModel.ResultBean bean : groupMembersList) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("member_id", bean.getGroup_member_id());
                    jsonObject.addProperty("can_post", bean.getCan_post());
                    jsonArray.add(jsonObject);
                }
                JsonObject obj = new JsonObject();
                obj.addProperty("type", "multiplePostFeedAction");
                obj.add("members", jsonArray);

                if (Utilities.isNetworkAvailable(context))
                    new CanPost().execute(obj.toString());
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
                break;
            case R.id.action_info:

                break;
            default:
                break;
        }
        return true;
    }

    private class CanPost extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = APICall.JSONAPICall(ApplicationConstants.FEEDSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Can post status changed successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        hideSoftKeyboard(CanPostFeedsAuthorization_Activity.this);
    }

}
