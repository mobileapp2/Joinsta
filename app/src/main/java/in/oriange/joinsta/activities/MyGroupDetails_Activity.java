package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupBannerSliderAdapter;
import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.models.GroupBannerListModel;
import in.oriange.joinsta.models.MyGroupsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class MyGroupDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CardView cv_grp_details, cv_banner, cv_rejoin, cv_add_member, cv_requests, cv_send_message, cv_group_banners;
    private LinearLayout ll_group_admin;
    private SliderView imageSlider;
    private TextView tv_codename, tv_description, tv_praticipants;
    private MaterialButton btn_members;
    private RecyclerView rv_group_members;
    private Button btn_connect, btn_status;
    private ImageButton ib_more;

    private MyGroupsListModel.ResultBean groupDetails;
    private ArrayList<MyGroupsListModel.ResultBean.GroupMemberDetailsBean> leadsList;
    private boolean isExpanded;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup_details);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = MyGroupDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        cv_grp_details = findViewById(R.id.cv_grp_details);
        cv_banner = findViewById(R.id.cv_banner);
        cv_rejoin = findViewById(R.id.cv_rejoin);
        cv_add_member = findViewById(R.id.cv_add_member);
        cv_requests = findViewById(R.id.cv_requests);
        cv_send_message = findViewById(R.id.cv_send_message);
        cv_group_banners = findViewById(R.id.cv_group_banners);
        ll_group_admin = findViewById(R.id.ll_group_admin);
        imageSlider = findViewById(R.id.imageSlider);
        tv_codename = findViewById(R.id.tv_codename);
        tv_description = findViewById(R.id.tv_description);
        tv_praticipants = findViewById(R.id.tv_praticipants);
        rv_group_members = findViewById(R.id.rv_group_members);
        rv_group_members.setLayoutManager(new LinearLayoutManager(context));
        btn_members = findViewById(R.id.btn_members);
        btn_connect = findViewById(R.id.btn_connect);
        btn_status = findViewById(R.id.btn_status);
        ib_more = findViewById(R.id.ib_more);

        leadsList = new ArrayList<>();
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
        groupDetails = (MyGroupsListModel.ResultBean) getIntent().getSerializableExtra("groupDetails");
        leadsList = groupDetails.getGroup_Member_Details();

        switch (groupDetails.getStatus()) {
            case "":
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                break;
            case "left":
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                cv_rejoin.setVisibility(View.VISIBLE);
                btn_connect.setText("REJOIN");
                break;
            case "requested":
                btn_connect.setVisibility(View.GONE);
                btn_status.setVisibility(View.VISIBLE);
                btn_status.setText("Requested");
                break;
            case "accepted":
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                btn_members.setVisibility(View.VISIBLE);
                btn_connect.setText("EXIT GORUP");
                break;
        }

        if (leadsList != null) {
            if (leadsList.size() != 0) {
                ArrayList<MyGroupsListModel.ResultBean.GroupMemberDetailsBean> foundMembers = new ArrayList<>();
                for (MyGroupsListModel.ResultBean.GroupMemberDetailsBean groupDetails : leadsList) {
                    if (groupDetails.getRole().equals("group_admin") || groupDetails.getRole().equals("group_supervisor")) {
                        foundMembers.add(groupDetails);
                    }
                }

                leadsList.clear();
                leadsList.addAll(foundMembers);
            }
        }

        rv_group_members.setAdapter(new GroupMembersAdapter());

        if (groupDetails.getIs_admin().equals("1")) {
            ll_group_admin.setVisibility(View.VISIBLE);
            btn_connect.setVisibility(View.GONE);
            btn_status.setVisibility(View.GONE);
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetBannersGroup().execute();
            new GetSingleGroupDetails().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        rv_group_members.setFocusable(false);
        cv_grp_details.requestFocus();
    }

    private void setEventHandler() {
        btn_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupMembersList_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });

        ib_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (leadsList != null) {
                    if (leadsList.size() != 0) {
                        if (!isExpanded) {
                            isExpanded = true;
                            animateExpand();
                            rv_group_members.setVisibility(View.VISIBLE);
                        } else {
                            isExpanded = false;
                            animateCollapse();
                            rv_group_members.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupDetails.getStatus().equals("") || groupDetails.getStatus().equals("left")) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new JoinGroup().execute(groupDetails.getId());
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else if (groupDetails.getStatus().equals("accepted")) {
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View promptView = layoutInflater.inflate(R.layout.dialog_exit_group, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.icon_alertred);
                    builder.setView(promptView);
                    builder.setCancelable(false);

                    final TextView tv_message = promptView.findViewById(R.id.tv_message);
                    final Button btn_exit = promptView.findViewById(R.id.btn_exit);
                    final Button btn_cancel = promptView.findViewById(R.id.btn_cancel);

                    tv_message.setText("Once you exit, you will not be able to receive any communication from this group - " + "\"" + groupDetails.getGroup_name() + "\"");

                    final AlertDialog alertD = builder.create();

                    btn_exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ExitGroup().execute(
                                    userId,
                                    groupDetails.getId(),
                                    "left"
                            );
                            alertD.dismiss();
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertD.dismiss();
                        }
                    });


                    alertD.show();
                }
            }
        });

        cv_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupMembersSupervisors_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });

        cv_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupsMembersRequests_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });

        cv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupsSendMessage_Activity.class));
            }
        });

        cv_group_banners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupBanners_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });
    }

    private class GetSingleGroupDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getsinglegroupDetails");
            obj.addProperty("id", groupDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {

                    JSONObject jsonObject = new JSONObject(result);
                    type = jsonObject.getString("type");
                    message = jsonObject.getString("message");

                    if (type.equalsIgnoreCase("success")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        tv_codename.setText(jsonObject1.getString("group_code") + " - " + jsonObject1.getString("group_name"));
                        tv_description.setText(jsonObject1.getString("group_description"));
                        tv_praticipants.setText(jsonObject1.getString("no_of_member") + " Participants");

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void animateExpand() {
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        ib_more.startAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        ib_more.startAnimation(rotate);
    }

    public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_grpleads, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();
            final MyGroupsListModel.ResultBean.GroupMemberDetailsBean memberDetails = leadsList.get(position);

            holder.tv_name.setText(memberDetails.getFirst_name().trim());
            holder.tv_mobile.setText(memberDetails.getMobile());

            if (memberDetails.getIs_joinsta_member().equals("0")) {
                holder.btn_invite.setVisibility(View.VISIBLE);
            }

            if (memberDetails.getRole().equalsIgnoreCase("group_admin")) {
                holder.tv_role.setVisibility(View.VISIBLE);
                holder.tv_role.setText("Admin");
            } else if (memberDetails.getRole().equalsIgnoreCase("group_supervisor")) {
                holder.tv_role.setVisibility(View.VISIBLE);
                holder.tv_role.setText("Supervisor");
            } else if (memberDetails.getRole().equalsIgnoreCase("group_member")) {
                holder.tv_role.setVisibility(View.VISIBLE);
                holder.tv_role.setText("Member");
            }

            if (!memberDetails.getImage_url().trim().isEmpty()) {
                Picasso.with(context)
                        .load(memberDetails.getImage_url().trim())
                        .placeholder(R.drawable.icon_user)
                        .into(holder.imv_user, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.imv_user.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.imv_user.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.imv_user.setVisibility(View.VISIBLE);
            }

            holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, GroupMembersProfileDetails_Activity.class)
                            .putExtra("userId", memberDetails.getId())
                            .putExtra("name", memberDetails.getFirst_name().trim())
                            .putExtra("imageUrl", memberDetails.getImage_url().trim()));
                }
            });

            holder.btn_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to send an invitation?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.icon_alertred);
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new SendInviteSMS().execute(groupDetails.getId(), userId, groupDetails.getId());
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return leadsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CardView cv_mainlayout;
            private CircleImageView imv_user;
            private ProgressBar progressBar;
            private Button btn_invite;
            private TextView tv_name, tv_role, tv_mobile;

            public MyViewHolder(View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                imv_user = view.findViewById(R.id.imv_user);
                progressBar = view.findViewById(R.id.progressBar);
                tv_name = view.findViewById(R.id.tv_name);
                tv_role = view.findViewById(R.id.tv_role);
                tv_mobile = view.findViewById(R.id.tv_mobile);
                btn_invite = view.findViewById(R.id.btn_invite);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    private class JoinGroup extends AsyncTask<String, Void, String> {

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
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "joingroup");
            obj.addProperty("status", "requested");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("user_id", userId);
            obj.addProperty("role", "group_member");
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res;
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
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        new AllGroups_Activity.GetGroupsList().execute();
                        new Groups_Fragment.GetMyGroupsList().execute();

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Your request to join this group is submitted successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SendInviteSMS extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "sendInviteSMS");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("sender_id", params[1]);
            obj.addProperty("receiver_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Invitation sent successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ExitGroup extends AsyncTask<String, Void, String> {

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
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "exitgroup");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("group_id", params[1]);
            obj.addProperty("status", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.GROUPSAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        new Groups_Fragment.GetMyGroupsList().execute();

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Group left successfully");
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

    private class GetBannersGroup extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getGroupBanners");
            obj.addProperty("group_id", groupDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.GROUPBANNERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    List<GroupBannerListModel.ResultBean> bannerList = new ArrayList<>();
                    GroupBannerListModel pojoDetails = new Gson().fromJson(result, GroupBannerListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        bannerList = pojoDetails.getResult();
                        if (bannerList.size() > 0) {
                            cv_banner.setVisibility(View.VISIBLE);

                            GroupBannerSliderAdapter adapter = new GroupBannerSliderAdapter(context, bannerList);
                            imageSlider.setSliderAdapter(adapter);
                            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            imageSlider.setAutoCycle(true);
                            imageSlider.setScrollTimeInSec(10);
                        }
                    } else {
                        cv_banner.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cv_banner.setVisibility(View.GONE);

            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
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
