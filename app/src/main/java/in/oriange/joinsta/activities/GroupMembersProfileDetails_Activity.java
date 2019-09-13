package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupMemberBusinessAdapter;
import in.oriange.joinsta.adapters.GroupMemberEmployeeAdapter;
import in.oriange.joinsta.adapters.GroupMemberProfessionalAdapter;
import in.oriange.joinsta.models.GetBusinessModel;
import in.oriange.joinsta.models.GetEmployeeModel;
import in.oriange.joinsta.models.GetProfessionalModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;

public class GroupMembersProfileDetails_Activity extends AppCompatActivity {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_details;
    private SpinKitView progressBar;

    private TextView tv_name, tv_initletter;
    private LinearLayout ll_nopreview, ll_business, ll_employee, ll_professional;
    private View v_business, v_employee, v_professional;
    private String userId, name;

    public ArrayList<GetBusinessModel.ResultBean> businessList;
    public ArrayList<GetEmployeeModel.ResultBean> employeeList;
    public ArrayList<GetProfessionalModel.ResultBean> professionalList;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmembers_profiledetails);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupMembersProfileDetails_Activity.this;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        rv_details = findViewById(R.id.rv_details);
        rv_details.setLayoutManager(new LinearLayoutManager(context));

        tv_name = findViewById(R.id.tv_name);
        tv_initletter = findViewById(R.id.tv_initletter);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        ll_business = findViewById(R.id.ll_business);
        ll_employee = findViewById(R.id.ll_employee);
        ll_professional = findViewById(R.id.ll_professional);
        v_business = findViewById(R.id.v_business);
        v_employee = findViewById(R.id.v_employee);
        v_professional = findViewById(R.id.v_professional);

        businessList = new ArrayList<>();
        professionalList = new ArrayList<>();
        employeeList = new ArrayList<>();

    }

    private void setDefault() {
        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");

        if (!name.trim().isEmpty()) {
            tv_initletter.setText(name.trim().substring(0, 1).toUpperCase());
        }
        tv_name.setText(name.trim());

        if (Utilities.isNetworkAvailable(context))
            new GetBusiness().execute();
    }

    private void setEventHandler() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (currentPosition) {
                    case 0:
                        if (Utilities.isNetworkAvailable(context)) {
                            new GetBusiness().execute();
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        break;
                    case 1:
                        if (Utilities.isNetworkAvailable(context)) {
                            new GetEmployee().execute();
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        break;
                    case 2:
                        if (Utilities.isNetworkAvailable(context)) {
                            new GetProfessional().execute();
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        break;
                }
            }
        });

        ll_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = 0;
                v_business.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                v_employee.setBackgroundColor(getResources().getColor(R.color.white));
                v_professional.setBackgroundColor(getResources().getColor(R.color.white));

                if (Utilities.isNetworkAvailable(context))
                    new GetBusiness().execute();
            }
        });

        ll_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = 1;
                v_business.setBackgroundColor(getResources().getColor(R.color.white));
                v_employee.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                v_professional.setBackgroundColor(getResources().getColor(R.color.white));

                if (Utilities.isNetworkAvailable(context))
                    new GetEmployee().execute();

            }
        });

        ll_professional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = 2;
                v_business.setBackgroundColor(getResources().getColor(R.color.white));
                v_employee.setBackgroundColor(getResources().getColor(R.color.white));
                v_professional.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                if (Utilities.isNetworkAvailable(context))
                    new GetProfessional().execute();

            }
        });
    }

    private class GetBusiness extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_details.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getbusiness");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    GetBusinessModel pojoDetails = new Gson().fromJson(result, GetBusinessModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult();

                        if (businessList.size() > 0) {

                            ArrayList<GetBusinessModel.ResultBean> filteredBusinessList = new ArrayList<>();

                            for (GetBusinessModel.ResultBean bizDetails : businessList) {
                                if (bizDetails.getIs_verified().equalsIgnoreCase("1")) {
                                    filteredBusinessList.add(bizDetails);
                                }
                            }

                            businessList.clear();
                            businessList.addAll(filteredBusinessList);

                            if (businessList.size() > 0) {
                                rv_details.setVisibility(View.VISIBLE);
                                ll_nopreview.setVisibility(View.GONE);
                                rv_details.setAdapter(new GroupMemberBusinessAdapter(context, businessList));
                            }
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_details.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_details.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_details.setVisibility(View.GONE);
            }
        }
    }

    private class GetEmployee extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_details.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getemployee");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.EMPLOYEEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    employeeList = new ArrayList<>();
                    GetEmployeeModel pojoDetails = new Gson().fromJson(result, GetEmployeeModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        employeeList = pojoDetails.getResult();

                        if (employeeList.size() > 0) {

                            ArrayList<GetEmployeeModel.ResultBean> filteredEmployeeList = new ArrayList<>();

                            for (GetEmployeeModel.ResultBean empDetails : employeeList) {
                                if (empDetails.getIs_verified().equalsIgnoreCase("1")) {
                                    filteredEmployeeList.add(empDetails);
                                }
                            }

                            employeeList.clear();
                            employeeList.addAll(filteredEmployeeList);

                            if (employeeList.size() > 0) {
                                rv_details.setVisibility(View.VISIBLE);
                                ll_nopreview.setVisibility(View.GONE);
                                rv_details.setAdapter(new GroupMemberEmployeeAdapter(context, employeeList));
                            }
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_details.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_details.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_details.setVisibility(View.GONE);
            }
        }
    }

    private class GetProfessional extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_details.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getprofessional");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.PROFESSIONALAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    professionalList = new ArrayList<>();
                    GetProfessionalModel pojoDetails = new Gson().fromJson(result, GetProfessionalModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        professionalList = pojoDetails.getResult();

                        if (professionalList.size() > 0) {

                            ArrayList<GetProfessionalModel.ResultBean> filteredProfessionalList = new ArrayList<>();

                            for (GetProfessionalModel.ResultBean profDetails : professionalList) {
                                if (profDetails.getIs_verified().equalsIgnoreCase("1")) {
                                    filteredProfessionalList.add(profDetails);
                                }
                            }

                            professionalList.clear();
                            professionalList.addAll(filteredProfessionalList);

                            if (professionalList.size() > 0) {
                                rv_details.setVisibility(View.VISIBLE);
                                ll_nopreview.setVisibility(View.GONE);
                                rv_details.setAdapter(new GroupMemberProfessionalAdapter(context, professionalList));
                            }
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_details.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_details.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_details.setVisibility(View.GONE);
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
