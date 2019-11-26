package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.MyAddedBusinessAdapter;
import in.oriange.joinsta.adapters.MyAddedEmployeeAdapter;
import in.oriange.joinsta.adapters.MyAddedProfessionalAdapter;
import in.oriange.joinsta.models.GetBusinessModel;
import in.oriange.joinsta.models.GetEmployeeModel;
import in.oriange.joinsta.models.GetProfessionalModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class ProfileDetails_Activity extends AppCompatActivity {

    private static Context context;
    private CardView cv_basicinfo, cv_offers;
    private FloatingActionButton btn_add;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView rv_details;
    private static SpinKitView progressBar;

    private static LinearLayout ll_nopreview;
    private LinearLayout ll_business;
    private LinearLayout ll_employee;
    private LinearLayout ll_professional;
    private View v_business, v_employee, v_professional;
    private UserSessionManager session;
    private static String userId;

    public static ArrayList<GetBusinessModel.ResultBean> businessList;
    public static ArrayList<GetEmployeeModel.ResultBean> employeeList;
    public static ArrayList<GetProfessionalModel.ResultBean> professionalList;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ProfileDetails_Activity.this;
        session = new UserSessionManager(context);
        btn_add = findViewById(R.id.btn_add);
        cv_basicinfo = findViewById(R.id.cv_basicinfo);
        cv_offers = findViewById(R.id.cv_offers);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        rv_details = findViewById(R.id.rv_details);
        rv_details.setLayoutManager(new LinearLayoutManager(context));

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

        if (Utilities.isNetworkAvailable(context))
            new GetBusiness().execute();
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


        cv_basicinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BasicInformation_Activity.class));
            }
        });

        cv_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddOffers_Activity.class));
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BizProfEmpDetails_Activity.class)
                        .putExtra("currentPosition", currentPosition));
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

    public static class GetBusiness extends AsyncTask<String, Void, String> {

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
            obj.addProperty("current_user_id", userId);
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
                            rv_details.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_details.setAdapter(new MyAddedBusinessAdapter(context, businessList));
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

    public static class GetEmployee extends AsyncTask<String, Void, String> {

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
            obj.addProperty("current_user_id", userId);
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
                            rv_details.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_details.setAdapter(new MyAddedEmployeeAdapter(context, employeeList));
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

    public static class GetProfessional extends AsyncTask<String, Void, String> {

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
            obj.addProperty("current_user_id", userId);
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
                            rv_details.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(View.GONE);
                            rv_details.setAdapter(new MyAddedProfessionalAdapter(context, professionalList));
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
