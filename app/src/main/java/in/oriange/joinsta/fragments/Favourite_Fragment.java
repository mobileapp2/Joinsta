package in.oriange.joinsta.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.SelectLocation_Activity;
import in.oriange.joinsta.adapters.SearchBusinessAdapter;
import in.oriange.joinsta.adapters.SearchEmployeeAdapter;
import in.oriange.joinsta.adapters.SearchProfessionalAdapter;
import in.oriange.joinsta.models.MainCategoryListModel;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.pojos.MainCategoryListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Favourite_Fragment extends Fragment {

    private static Context context;
    private UserSessionManager session;
    private AppCompatEditText edt_type, edt_location;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView rv_searchlist;
    private static LinearLayout ll_nopreview;
    private static EditText edt_search;
    private static SpinKitView progressBar;
    private PowerMenu iconMenu;
    public static ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessList;
    public static ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> professionalList;
    public static ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> employeeList;

    private static String userId;
    private static String categoryTypeId;
    private ProgressDialog pd;
    private ArrayList<MainCategoryListModel> mainCategoryList;
    private ArrayList<PowerMenuItem> powerMenuItems;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        edt_location = rootView.findViewById(R.id.edt_location);
        progressBar = rootView.findViewById(R.id.progressBar);
        edt_type = rootView.findViewById(R.id.edt_type);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        rv_searchlist = rootView.findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();
        professionalList = new ArrayList<>();
        employeeList = new ArrayList<>();

        mainCategoryList = new ArrayList<>();
        powerMenuItems = new ArrayList<>();
    }

    private void setDefault() {
        categoryTypeId = "1";
        edt_type.setText("Business");

        if (Utilities.isNetworkAvailable(context)) {
            new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        }

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

        try {
            UserSessionManager session = new UserSessionManager(context);
            edt_location.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerMenuItems.size() == 0)
                    if (Utilities.isNetworkAvailable(context))
                        new GetMainCategotyList().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                else
                    showCategoryMenus(powerMenuItems);
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDetails(categoryTypeId, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SelectLocation_Activity.class)
                        .putExtra("startOrigin", 2));
            }
        });

    }

    private void searchDetails(String categoryTypeId, String query) {
        switch (categoryTypeId) {
            case "1":
                if (businessList.size() == 0) {
                    return;
                }

                if (!query.equals("")) {
                    ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessSearchedList = new ArrayList<>();
                    for (SearchDetailsModel.ResultBean.BusinessesBean businessDetails : businessList) {
                        String businessToBeSearched = businessDetails.getBusiness_name().toLowerCase() +
                                businessDetails.getCity().toLowerCase();
                        if (businessToBeSearched.contains(query.toLowerCase())) {
                            businessSearchedList.add(businessDetails);
                        }
                    }
                    rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessSearchedList, "1"));
                } else {
                    rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "1"));
                }
                break;
            case "2":
                if (professionalList.size() == 0) {
                    return;
                }

                if (!query.equals("")) {
                    ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> professionalSearchedList = new ArrayList<>();
                    for (SearchDetailsModel.ResultBean.ProfessionalsBean professionalDetails : professionalList) {
                        String professionalToBeSearched = professionalDetails.getFirm_name().toLowerCase() +
                                professionalDetails.getCity().toLowerCase();
                        if (professionalToBeSearched.contains(query.toLowerCase())) {
                            professionalSearchedList.add(professionalDetails);
                        }
                    }
                    rv_searchlist.setAdapter(new SearchProfessionalAdapter(context, professionalSearchedList, "1"));
                } else {
                    rv_searchlist.setAdapter(new SearchProfessionalAdapter(context, professionalList, "1"));
                }
                break;
            case "3":
                if (employeeList.size() == 0) {
                    return;
                }

                if (!query.equals("")) {
                    ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> employeeSearchedList = new ArrayList<>();
                    for (SearchDetailsModel.ResultBean.EmployeesBean employeeDetails : employeeList) {
                        String employeeToBeSearched = employeeDetails.getOrganization_name().toLowerCase() +
                                employeeDetails.getCity().toLowerCase();
                        if (employeeToBeSearched.contains(query.toLowerCase())) {
                            employeeSearchedList.add(employeeDetails);
                        }
                    }
                    rv_searchlist.setAdapter(new SearchEmployeeAdapter(context, employeeSearchedList, "1"));
                } else {
                    rv_searchlist.setAdapter(new SearchEmployeeAdapter(context, employeeList, "1"));
                }

                break;
        }
    }

    public static class GetSearchList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_searchlist.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
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
            edt_search.setText("");
            progressBar.setVisibility(View.GONE);
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

                        ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> foundbiz = new ArrayList<SearchDetailsModel.ResultBean.BusinessesBean>();
                        for (SearchDetailsModel.ResultBean.BusinessesBean bizdetails : businessList) {
                            if (bizdetails.getIsFavourite().equals("0")) {
                                foundbiz.add(bizdetails);
                            }
                        }
                        businessList.removeAll(foundbiz);

                        ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> foundProf = new ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean>();
                        for (SearchDetailsModel.ResultBean.ProfessionalsBean profdetails : professionalList) {
                            if (profdetails.getIsFavourite().equals("0")) {
                                foundProf.add(profdetails);
                            }
                        }
                        professionalList.removeAll(foundProf);

                        ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> foundEmp = new ArrayList<SearchDetailsModel.ResultBean.EmployeesBean>();
                        for (SearchDetailsModel.ResultBean.EmployeesBean empdetails : employeeList) {
                            if (empdetails.getIsFavourite().equals("0")) {
                                foundEmp.add(empdetails);
                            }
                        }
                        employeeList.removeAll(foundEmp);

                        setDataToRecyclerView(categoryTypeId);
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_searchlist.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_searchlist.setVisibility(View.GONE);
            }
        }
    }

    private class GetMainCategotyList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategorytypes");
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYTYPEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    mainCategoryList = new ArrayList<>();
                    MainCategoryListPojo pojoDetails = new Gson().fromJson(result, MainCategoryListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        mainCategoryList = pojoDetails.getResult();
                        if (mainCategoryList.size() > 0) {

                            for (int i = 0; i < mainCategoryList.size(); i++) {
                                powerMenuItems.add(new PowerMenuItem(mainCategoryList.get(i).getType_description()));
                            }
                            showCategoryMenus(powerMenuItems);

                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showCategoryMenus(ArrayList<PowerMenuItem> powerMenuItems) {
        iconMenu = new PowerMenu.Builder(context)
                .addItemList(powerMenuItems)
                .setOnMenuItemClickListener(onIconMenuItemClickListener)
                .setAnimation(MenuAnimation.FADE)
                .setMenuEffect(MenuEffect.BODY)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .build();
        iconMenu.showAsDropDown(edt_type);
    }

    private static void setDataToRecyclerView(String categoryTypeId) {
        switch (categoryTypeId) {
            case "1":
                if (businessList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "2"));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_searchlist.setVisibility(View.VISIBLE);
                } else {
                    rv_searchlist.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                if (employeeList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchEmployeeAdapter(context, employeeList, "2"));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_searchlist.setVisibility(View.VISIBLE);
                } else {
                    rv_searchlist.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
            case "3":
                if (professionalList.size() > 0) {
                    rv_searchlist.setAdapter(new SearchProfessionalAdapter(context, professionalList, "2"));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_searchlist.setVisibility(View.VISIBLE);
                } else {
                    rv_searchlist.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            edt_type.setText(item.getTitle());
            categoryTypeId = mainCategoryList.get(position).getId();
            iconMenu.dismiss();
            setDataToRecyclerView(categoryTypeId);
        }
    };
}
