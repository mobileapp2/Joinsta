package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import in.oriange.joinsta.activities.BasicInformation_Activity;
import in.oriange.joinsta.activities.BizProfEmpDetails_Activity;
import in.oriange.joinsta.activities.Notification_Activity;
import in.oriange.joinsta.activities.Settings_Activity;
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

public class Profile_Fragment extends Fragment {

    private static Context context;
    private CardView cv_basicinfo;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_add = rootView.findViewById(R.id.btn_add);
        cv_basicinfo = rootView.findViewById(R.id.cv_basicinfo);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        progressBar = rootView.findViewById(R.id.progressBar);
        rv_details = rootView.findViewById(R.id.rv_details);
        rv_details.setLayoutManager(new LinearLayoutManager(context));

        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        ll_business = rootView.findViewById(R.id.ll_business);
        ll_employee = rootView.findViewById(R.id.ll_employee);
        ll_professional = rootView.findViewById(R.id.ll_professional);
        v_business = rootView.findViewById(R.id.v_business);
        v_employee = rootView.findViewById(R.id.v_employee);
        v_professional = rootView.findViewById(R.id.v_professional);

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

    private void setEventHandlers() {

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menus_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                startActivity(new Intent(context, Notification_Activity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(context, Settings_Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
