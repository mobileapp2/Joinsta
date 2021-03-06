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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.goodiebag.pinview.Pinview;
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
import in.oriange.joinsta.models.GroupsEventPaymentDoneModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.models.MyGroupsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class MyGroupDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CardView cv_grp_details, cv_banner, cv_rejoin, cv_members, cv_grp_settings, cv_group_utils;
    private SliderView imageSlider;
    private TextView tv_codename, tv_description, tv_praticipants, tv_members;
    private Switch sw_hide_members;
    private TextView tv_group_type;
    private RecyclerView rv_group_members, rv_group_utils;
    private Button btn_connect, btn_status, btn_delete_group;
    private ImageButton ib_more;
    private CheckBox cb_like;

    private MyGroupsListModel.ResultBean groupDetails;
    private ArrayList<MyGroupsListModel.ResultBean.GroupMemberDetailsBean> leadsList;
    private ArrayList<MasterModel> grpUtilsList;
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
        cv_members = findViewById(R.id.cv_members);
        cv_grp_settings = findViewById(R.id.cv_grp_settings);
        cv_group_utils = findViewById(R.id.cv_group_utils);
        imageSlider = findViewById(R.id.imageSlider);
        tv_codename = findViewById(R.id.tv_codename);
        tv_description = findViewById(R.id.tv_description);
        tv_praticipants = findViewById(R.id.tv_praticipants);
        tv_members = findViewById(R.id.tv_members);
        tv_group_type = findViewById(R.id.tv_group_type);
        sw_hide_members = findViewById(R.id.sw_hide_members);
        rv_group_members = findViewById(R.id.rv_group_members);
        rv_group_members.setLayoutManager(new LinearLayoutManager(context));
        rv_group_utils = findViewById(R.id.rv_group_utils);
        rv_group_utils.setLayoutManager(new GridLayoutManager(context, 2));
        btn_connect = findViewById(R.id.btn_connect);
        btn_status = findViewById(R.id.btn_status);
        btn_delete_group = findViewById(R.id.btn_delete_group);
        ib_more = findViewById(R.id.ib_more);
        cb_like = findViewById(R.id.cb_like);

        leadsList = new ArrayList<>();
        grpUtilsList = new ArrayList<>();
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
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                cv_rejoin.setVisibility(View.GONE);
                btn_connect.setText("Cancel Request");
                break;
            case "rejected":
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                cv_rejoin.setVisibility(View.GONE);
                btn_connect.setText("Cancel");
                break;
            case "accepted":
                btn_connect.setVisibility(View.VISIBLE);
                btn_status.setVisibility(View.GONE);
                cv_members.setVisibility(View.VISIBLE);
                cv_group_utils.setVisibility(View.VISIBLE);
                btn_connect.setText("EXIT GROUP");
                break;
        }

        int members = 0;
        if (leadsList != null) {
            if (leadsList.size() != 0) {
                ArrayList<MyGroupsListModel.ResultBean.GroupMemberDetailsBean> foundMembers = new ArrayList<>();
                for (MyGroupsListModel.ResultBean.GroupMemberDetailsBean memberDetails : leadsList) {
                    if (memberDetails.getRole().equals("group_admin") || memberDetails.getRole().equals("group_supervisor")) {
                        foundMembers.add(memberDetails);
                    } else if (memberDetails.getRole().equals("group_member")) {
                        members = members + 1;
                    }
                }

                ArrayList<MyGroupsListModel.ResultBean.GroupMemberDetailsBean> tempFoundMembers = new ArrayList<>(foundMembers);

                for (MyGroupsListModel.ResultBean.GroupMemberDetailsBean memberDetails : tempFoundMembers) {
                    if (!groupDetails.getIs_admin().equals("1")) {
                        if (memberDetails.getIs_hidden().equals("1")) {
                            foundMembers.remove(memberDetails);
                        }
                    }
                }

                leadsList.clear();
                leadsList.addAll(foundMembers);
            }
        }

        tv_members.setText("Members (" + members + ")");

        rv_group_members.setAdapter(new GroupMembersAdapter());

        if (groupDetails.getIs_admin().equals("1")) {
            cv_group_utils.setVisibility(View.VISIBLE);
            cv_grp_settings.setVisibility(View.VISIBLE);
            btn_connect.setVisibility(View.GONE);
            btn_status.setVisibility(View.GONE);

            if (groupDetails.getIs_public_group().equals("0")) {
                tv_group_type.setText("Private Group");
            } else if (groupDetails.getIs_public_group().equals("1")) {
                tv_group_type.setText("Public Group");
            } else if (groupDetails.getIs_public_group().equals("2")) {
                tv_group_type.setText("Social Group");
            }

            if (leadsList != null)
                if (leadsList.size() != 0)
                    if (leadsList.size() == 1) {
                        btn_connect.setVisibility(View.GONE);
                        btn_delete_group.setVisibility(View.VISIBLE);
                    } else {
                        btn_connect.setVisibility(View.VISIBLE);
                        btn_delete_group.setVisibility(View.GONE);
                        btn_connect.setText("EXIT GROUP");
                    }
        }

        if (groupDetails.getIs_visible().equals("1")) {
            sw_hide_members.setChecked(true);
        }

        if (groupDetails.getIs_favourite().equals("1")) {
            cb_like.setChecked(true);
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetBannersGroup().execute();
            new GetSingleGroupDetails().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        rv_group_members.setFocusable(false);
        cv_grp_details.requestFocus();

        tv_codename.setText(groupDetails.getGroup_code() + " - " + groupDetails.getGroup_name());
        tv_description.setText(groupDetails.getGroup_description());

        setUpGroupUtils();

    }

    private void setEventHandler() {

        cv_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupDetails.getIs_admin().equals("1")) {
                    startActivity(new Intent(context, GroupMembersList_Activity.class)
                            .putExtra("groupId", groupDetails.getId())
                            .putExtra("isAdmin", groupDetails.getIs_admin()));
                } else {
                    if (!groupDetails.getIs_public_group().equals("2"))
                        if (!sw_hide_members.isChecked())
                            startActivity(new Intent(context, GroupMembersList_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("isAdmin", groupDetails.getIs_admin()));
                }
            }
        });

        sw_hide_members.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String is_visible = "";
                if (isChecked) {
                    is_visible = "1";
                } else {
                    is_visible = "0";
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new GroupMembersVisiblity().execute(groupDetails.getId(), is_visible);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        tv_group_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MasterModel> groupTypeList = new ArrayList<>();
                groupTypeList.add(new MasterModel("Private Group", "0"));
                groupTypeList.add(new MasterModel("Public Group", "1"));
                groupTypeList.add(new MasterModel("Social Group", "2"));
                showGroupTypeListDialog(groupTypeList);
            }
        });


        btn_delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isNetworkAvailable(context)) {
                    new CanDeleteGroup().execute(groupDetails.getId());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

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
                        if (groupDetails.getIs_public_group().equals("2")) {
                            new JoinGroup().execute("accepted", groupDetails.getId());
                        } else {
                            new JoinGroup().execute("requested", groupDetails.getId());
                        }
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
                            if (Utilities.isNetworkAvailable(context)) {
                                new ExitGroup().execute(
                                        userId,
                                        groupDetails.getId(),
                                        "left"
                                );
                                alertD.dismiss();
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertD.dismiss();
                        }
                    });
                    alertD.show();
                } else if (groupDetails.getStatus().equals("requested") || groupDetails.getStatus().equals("rejected")) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new CancelRequest().execute(
                                userId,
                                groupDetails.getId()
                        );
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                }
            }
        });

        cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav = groupDetails.getIs_favourite();

                if (cb_like.isChecked())
                    isFav = "1";
                else
                    isFav = "0";

                if (Utilities.isNetworkAvailable(context)) {
                    new MarkFavouriteGroup().execute(groupDetails.getId(), userId, isFav);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

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

    private void setUpGroupUtils() {
        if (groupDetails.getIs_admin().equals("1")) {
            grpUtilsList.add(new MasterModel("Participants", "1"));
            if (!groupDetails.getIs_public_group().equals("2"))
                grpUtilsList.add(new MasterModel("Requests", "2"));
            grpUtilsList.add(new MasterModel("Send Message", "3"));
            grpUtilsList.add(new MasterModel("Banners", "4"));
            grpUtilsList.add(new MasterModel("Notifications", "5"));
            grpUtilsList.add(new MasterModel("Posts", "6"));
            grpUtilsList.add(new MasterModel("Events", "7"));
        } else {
            if (groupDetails.getStatus().equals("accepted")) {
                grpUtilsList.add(new MasterModel("Notifications", "5"));
                grpUtilsList.add(new MasterModel("Posts", "6"));
                grpUtilsList.add(new MasterModel("Events", "7"));
            } else {
                cv_group_utils.setVisibility(View.GONE);
            }
        }

        rv_group_utils.setAdapter(new GroupUtilsAdapter());
    }

    private void showGroupTypeListDialog(List<MasterModel> groupTypeList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Group Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < groupTypeList.size(); i++) {
            arrayAdapter.add(String.valueOf(groupTypeList.get(i).getName()));
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MasterModel event = groupTypeList.get(which);
                tv_group_type.setText(event.getName());
                if (Utilities.isNetworkAvailable(context)) {
                    new ChangeGroupType().execute(groupDetails.getId(), event.getId());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });
        builderSingle.show();
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
            holder.tv_city.setText(memberDetails.getNative_place());

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

            if (memberDetails.getIs_hidden().equals("1")) {
                holder.ib_ishidden.setVisibility(View.VISIBLE);
            }

            if (!memberDetails.getImage_url().trim().isEmpty()) {
                String url = IMAGE_LINK + "" + memberDetails.getId() + "/" + memberDetails.getImage_url();
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.icon_user)
                        .resize(250, 250)
                        .centerCrop()
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

            holder.ib_ishidden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.showMessage("User has marked himself/herself as hidden member", context, 2);
                }
            });

            holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (memberDetails.getIs_joinsta_member().equals("1")) {
                        context.startActivity(new Intent(context, GroupMembersProfileDetails_Activity.class)
                                .putExtra("userId", memberDetails.getId())
                                .putExtra("name", memberDetails.getFirst_name().trim())
                                .putExtra("imageUrl", memberDetails.getImage_url().trim()));
                    }
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
                                new SendInviteSMS().execute(groupDetails.getId(), userId, memberDetails.getId());
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
            private ImageButton ib_ishidden;
            private TextView tv_name, tv_role, tv_mobile, tv_city;

            public MyViewHolder(View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                imv_user = view.findViewById(R.id.imv_user);
                progressBar = view.findViewById(R.id.progressBar);
                tv_name = view.findViewById(R.id.tv_name);
                tv_role = view.findViewById(R.id.tv_role);
                tv_mobile = view.findViewById(R.id.tv_mobile);
                tv_city = view.findViewById(R.id.tv_city);
                btn_invite = view.findViewById(R.id.btn_invite);
                ib_ishidden = view.findViewById(R.id.ib_ishidden);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    public class GroupUtilsAdapter extends RecyclerView.Adapter<GroupUtilsAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_grputils, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();
            final MasterModel grpUtil = grpUtilsList.get(position);

            holder.tv_name.setText(grpUtil.getName());

            holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (grpUtil.getId()) {
                        case "1": {
                            startActivity(new Intent(context, GroupMembersAdmins_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("isPublicGroup", groupDetails.getIs_public_group()));
                        }
                        break;
                        case "2": {
                            startActivity(new Intent(context, GroupsMembersRequests_Activity.class)
                                    .putExtra("groupId", groupDetails.getId()));
                        }
                        break;
                        case "3": {
                            startActivity(new Intent(context, GroupsSendMessage_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("groupName", groupDetails.getGroup_name()));
                        }
                        break;
                        case "4": {
                            startActivity(new Intent(context, GroupBanners_Activity.class)
                                    .putExtra("groupId", groupDetails.getId()));
                        }
                        break;
                        case "5": {
                            context.startActivity(new Intent(context, GroupNotifications_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("groupName", groupDetails.getGroup_name()));
                        }
                        break;
                        case "6": {
                            context.startActivity(new Intent(context, GroupFeeds_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("groupName", groupDetails.getGroup_name())
                                    .putExtra("isAdmin", groupDetails.getIs_admin())
                                    .putExtra("canPost", groupDetails.getCan_post())
                                    .putExtra("isPublicGroup", groupDetails.getIs_public_group()));
                        }
                        break;
                        case "7": {
                            context.startActivity(new Intent(context, Events_Activity.class)
                                    .putExtra("groupId", groupDetails.getId())
                                    .putExtra("groupName", groupDetails.getGroup_name())
                                    .putExtra("isAdmin", groupDetails.getIs_admin()));
                        }
                        break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return grpUtilsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CardView cv_mainlayout;
            private TextView tv_name;

            public MyViewHolder(View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    private void deleteGroupConfirmation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertBuilder.setMessage("Once you delete the group, all members, messages, posts will be deleted. Are you sure, you want to delete the group?");
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String[] mode = {"SMS", "Email"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("OTP verification is required to delete group");
                builder.setSingleChoiceItems(mode, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (Utilities.isNetworkAvailable(context)) {
                            if (item == 0) {
                                new GetOtpToDelete().execute(
                                        userId,
                                        "otp"
                                );
                            } else if (item == 1) {
                                new GetOtpToDelete().execute(
                                        userId,
                                        "email"
                                );
                            }
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }

                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void createDialogForOTP(final String otp) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_otp, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        Pinview pinview_opt = promptView.findViewById(R.id.pinview_opt);
        TextView tv_message = promptView.findViewById(R.id.tv_message);
        TextView tv_title = promptView.findViewById(R.id.tv_title);
        Button btn_cancel = promptView.findViewById(R.id.btn_cancel);
        pinview_opt.setPinLength(otp.length());
        tv_title.setText("Please enter the code");
        tv_message.setVisibility(View.GONE);

        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        pinview_opt.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {

                if (pinview.getValue().length() == otp.length()) {
                    if (pinview.getValue().equals(otp)) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteGroup().execute(groupDetails.getId());

                            alertD.dismiss();

                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        Utilities.showMessage("OTP did not match", context, 3);
                    }
                }
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
            obj.addProperty("status", params[0]);
            obj.addProperty("group_id", params[1]);
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
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText(message);
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        alertD.show();

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendInviteSMS extends AsyncTask<String, Void, String> {

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

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetOtpToDelete extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deleteGroupDetails");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("send_type", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, obj.toString());
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
                        JSONObject otpObj = mainObj.getJSONObject("result");
                        String OTP = otpObj.getString("otp");
                        createDialogForOTP(OTP);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CanDeleteGroup extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "checkGroupEvents");
            obj.addProperty("group_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type;
            try {
                if (!result.equals("")) {
                    List<List<GroupsEventPaymentDoneModel.ResultBean>> eventList = new ArrayList<>();
                    GroupsEventPaymentDoneModel pojoDetails = new Gson().fromJson(result, GroupsEventPaymentDoneModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        eventList = pojoDetails.getResult();
                        if (eventList.size() > 0) {
                            Utilities.showMessage("You cannot delete this group", context, 2);
                        } else {
                            deleteGroupConfirmation();
                        }
                    } else {
                        deleteGroupConfirmation();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                deleteGroupConfirmation();
            }
        }
    }

    private class DeleteGroup extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "confirmDeleteGroupDetails"));
            param.add(new ParamsPojo("group_id", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
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
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Group deleted successfully");
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

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MarkFavouriteGroup extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "markFavouriteGroupDetails"));
            param.add(new ParamsPojo("group_id", params[0]));
            param.add(new ParamsPojo("user_id", params[1]));
            param.add(new ParamsPojo("is_fav", params[2]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
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
                        new AllGroups_Activity.GetGroupsList().execute();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CancelRequest extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "cancelRequest");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("group_id", params[1]);
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
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Request cancelled successfully");
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
                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

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

    private class GroupMembersVisiblity extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "updateGroupMembersVisiblity");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("is_visible", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINSETMEMBERVISIBILITYAPI, obj.toString());
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
                        Utilities.showMessage("Group members visibility status changed successfully", context, 1);

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeGroupType extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "setGroupVisiblity");
            obj.addProperty("group_id", params[0]);
            obj.addProperty("is_public", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINSETGROUPVISIBILITYAPI, obj.toString());
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
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Group type changed successfully");
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

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utilities.hideSoftKeyboard(MyGroupDetails_Activity.this);
    }
}
