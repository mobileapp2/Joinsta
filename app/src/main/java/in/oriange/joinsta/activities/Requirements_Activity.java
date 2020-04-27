package in.oriange.joinsta.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.RequirementAdapter;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.pojos.RequirementsListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Requirements_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private AppCompatEditText edt_location;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_requirementlist;
    private LinearLayout ll_nopreview;
    private Button btn_post_requirement;
    private ImageButton ib_filter;
    private SpinKitView progressBar;
    private String userId;
    private ArrayList<RequirementsListModel> requirementsList;

    private final int LOCATION_REQUEST = 300;
    private boolean business, employment, professional, posted, starred;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = Requirements_Activity.this;
        session = new UserSessionManager(context);
        edt_location = findViewById(R.id.edt_location);
        ib_filter = findViewById(R.id.ib_filter);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        btn_post_requirement = findViewById(R.id.btn_post_requirement);
        rv_requirementlist = findViewById(R.id.rv_requirementlist);
        rv_requirementlist.setLayoutManager(new LinearLayoutManager(context));

        requirementsList = new ArrayList<>();

    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            edt_location.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetRequirementList().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }


        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("Requirements_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetRequirementList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ib_filter.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_layout_filter, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setTitle("Filter");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setView(promptView);

            final CheckBox cb_business = promptView.findViewById(R.id.cb_business);
            final CheckBox cb_employment = promptView.findViewById(R.id.cb_employment);
            final CheckBox cb_professional = promptView.findViewById(R.id.cb_professional);
            final CheckBox cb_postedbyme = promptView.findViewById(R.id.cb_postedbyme);
            final CheckBox cb_staredbyme = promptView.findViewById(R.id.cb_staredbyme);

            if (business)
                cb_business.setChecked(true);

            if (employment)
                cb_employment.setChecked(true);

            if (professional)
                cb_professional.setChecked(true);

            if (posted)
                cb_postedbyme.setChecked(true);

            if (starred)
                cb_staredbyme.setChecked(true);


            alertDialogBuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    ArrayList<RequirementsListModel> filteredRequirementsList = new ArrayList<>();

                    if (cb_business.isChecked() || cb_employment.isChecked() || cb_professional.isChecked()
                            || cb_postedbyme.isChecked() || cb_staredbyme.isChecked()) {


                        if (cb_business.isChecked()) {
                            business = true;
                            for (int j = 0; j < requirementsList.size(); j++)
                                if (requirementsList.get(j).getCategory_type_id().equals("1"))
                                    filteredRequirementsList.add(requirementsList.get(j));
                        } else {
                            business = false;
                        }


                        if (cb_employment.isChecked()) {
                            employment = true;
                            for (int j = 0; j < requirementsList.size(); j++) {
                                if (requirementsList.get(j).getCategory_type_id().equals("2")) {
                                    filteredRequirementsList.add(requirementsList.get(j));
                                }
                            }
                        } else {
                            employment = false;
                        }


                        if (cb_professional.isChecked()) {
                            professional = true;
                            for (int j = 0; j < requirementsList.size(); j++) {
                                if (requirementsList.get(j).getCategory_type_id().equals("3")) {
                                    filteredRequirementsList.add(requirementsList.get(j));
                                }
                            }
                        } else {
                            professional = false;
                        }


                        if (cb_postedbyme.isChecked()) {
                            posted = true;

                            if (filteredRequirementsList.size() > 0) {
                                ArrayList<RequirementsListModel> filteredRequirementsListByMe = new ArrayList<>();
                                for (int j = 0; j < filteredRequirementsList.size(); j++) {
                                    if (filteredRequirementsList.get(j).getCreated_by().equals(userId)) {
                                        filteredRequirementsListByMe.add(filteredRequirementsList.get(j));
                                    }
                                }

                                filteredRequirementsList.clear();
                                filteredRequirementsList.addAll(filteredRequirementsListByMe);

                            } else {
                                for (int j = 0; j < requirementsList.size(); j++) {
                                    if (requirementsList.get(j).getCreated_by().equals(userId)) {
                                        filteredRequirementsList.add(requirementsList.get(j));
                                    }
                                }
                            }
                        } else {
                            posted = false;
                        }

                        if (cb_staredbyme.isChecked()) {
                            starred = true;

                            if (filteredRequirementsList.size() > 0) {
                                ArrayList<RequirementsListModel> filteredRequirementsListByMe = new ArrayList<>();
                                for (int j = 0; j < filteredRequirementsList.size(); j++) {
                                    if (filteredRequirementsList.get(j).getIsStarred().equals("1")) {
                                        filteredRequirementsListByMe.add(filteredRequirementsList.get(j));
                                    }
                                }

                                filteredRequirementsList.clear();
                                filteredRequirementsList.addAll(filteredRequirementsListByMe);

                            } else {
                                for (int j = 0; j < requirementsList.size(); j++) {
                                    if (requirementsList.get(j).getIsStarred().equals("1")) {
                                        filteredRequirementsList.add(requirementsList.get(j));
                                    }
                                }
                            }
                        } else {
                            starred = false;
                        }


                        rv_requirementlist.setAdapter(new RequirementAdapter(context, filteredRequirementsList));
                    } else {

                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Clear Filter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    business = false;
                    employment = false;
                    professional = false;
                    posted = false;
                    starred = false;
                    if (requirementsList.size() > 0) {
                        rv_requirementlist.setVisibility(View.VISIBLE);
                        ll_nopreview.setVisibility(View.GONE);
                        rv_requirementlist.setAdapter(new RequirementAdapter(context, requirementsList));
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_requirementlist.setVisibility(View.GONE);
                    }
                }
            });


            final AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
        });

        btn_post_requirement.setOnClickListener(v -> startActivity(new Intent(context, AddRequirement_Activity.class)));

        edt_location.setOnClickListener(v -> {
            startActivityForResult(new Intent(context, SelectCity_Activity.class).putExtra("requestCode", LOCATION_REQUEST), LOCATION_REQUEST);
        });
    }

    private class GetRequirementList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_requirementlist.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getrequirements");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.REQUIREMENTAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    RequirementsListPojo pojoDetails = new Gson().fromJson(result, RequirementsListPojo.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        requirementsList = pojoDetails.getResult();
                        business = false;
                        employment = false;
                        professional = false;
                        posted = false;
                        starred = false;
                        if (requirementsList.size() > 0) {
                            ArrayList<RequirementsListModel> foundEmp = new ArrayList<>();
                            for (RequirementsListModel empdetails : requirementsList) {
                                if (!empdetails.getCity().equals(edt_location.getText().toString().trim())) {
                                    foundEmp.add(empdetails);
                                }
                            }
                            requirementsList.removeAll(foundEmp);
                            if (requirementsList.size() > 0) {
                                rv_requirementlist.setVisibility(View.VISIBLE);
                                ll_nopreview.setVisibility(View.GONE);
                                rv_requirementlist.setAdapter(new RequirementAdapter(context, requirementsList));
                            } else {
                                ll_nopreview.setVisibility(View.VISIBLE);
                                rv_requirementlist.setVisibility(View.GONE);
                            }
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_requirementlist.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_requirementlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_requirementlist.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            if (requestCode == LOCATION_REQUEST) {
                edt_location.setText(data.getStringExtra("cityName"));
                new GetRequirementList().execute();
            }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetRequirementList().execute();
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
}
