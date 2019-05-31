package in.oriange.joinsta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skydoves.powermenu.PowerMenu;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.SearchAdapterBusiness;
import in.oriange.joinsta.adapters.SearchAdapterEmployee;
import in.oriange.joinsta.adapters.SearchAdapterProfessional;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class BizProfEmpDetailsList_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private RecyclerView rv_searchlist;
    private EditText edt_search;
    private SpinKitView progressBar;

    public static ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessList;
    public static ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> professionalList;
    public static ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> employeeList;
    private String userId, categoryTypeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizprofemp_detailslist);

        init();
        setDefault();
        setEventHandler();
        getSessionDetails();
        setUpToolbar();
    }

    private void init() {
        context = BizProfEmpDetailsList_Activity.this;
        session = new UserSessionManager(context);

        edt_search = findViewById(R.id.edt_search);
        progressBar = findViewById(R.id.progressBar);
        rv_searchlist = findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();
        professionalList = new ArrayList<>();
        employeeList = new ArrayList<>();
    }

    private void setDefault() {

        categoryTypeId = getIntent().getStringExtra("categoryTypeId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private void setEventHandler() {

    }

    private void getSessionDetails() {

    }

    private class GetSearchList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_searchlist.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getDetailsByLocation");
            obj.addProperty("user_id", userId);
            obj.addProperty("location", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_searchlist.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    professionalList = new ArrayList<>();
                    employeeList = new ArrayList<>();
                    SearchDetailsModel pojoDetails = new Gson().fromJson(result, SearchDetailsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult().getBusinesses();
                        professionalList = pojoDetails.getResult().getProfessionals();
                        employeeList = pojoDetails.getResult().getEmployees();

                        setDataToRecyclerView();
                    } else {
                        Utilities.showAlertDialog(context, "Categories not available", false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void setDataToRecyclerView() {
        switch (categoryTypeId) {
            case "1":
                if (businessList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchAdapterBusiness(context, businessList));
                }
                break;
            case "2":
                if (employeeList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchAdapterEmployee(context, employeeList));
                }
                break;
            case "3":
                if (professionalList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchAdapterProfessional(context, professionalList));
                }
                break;
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
