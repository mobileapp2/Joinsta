package in.oriange.joinsta.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.BannerSliderAdapter;
import in.oriange.joinsta.adapters.SearchBusinessAdapter;
import in.oriange.joinsta.adapters.SearchEmployeeAdapter;
import in.oriange.joinsta.adapters.SearchProfessionalAdapter;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class BizProfEmpDetailsList_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private CardView cv_banner;
    private SliderView imageSlider;
    private RecyclerView rv_searchlist;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    public static ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessList;
    public static ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> professionalList;
    public static ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> employeeList;
    private String userId, mainCategoryTypeId, categoryTypeId, subCategoryTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizprofemp_detailslist);

        init();
        setDefault();
        setUpToolbar();
    }

    private void init() {
        context = BizProfEmpDetailsList_Activity.this;
        session = new UserSessionManager(context);

        cv_banner = findViewById(R.id.cv_banner);
        imageSlider = findViewById(R.id.imageSlider);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        progressBar = findViewById(R.id.progressBar);
        rv_searchlist = findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();
        professionalList = new ArrayList<>();
        employeeList = new ArrayList<>();
    }

    private void setDefault() {

        mainCategoryTypeId = getIntent().getStringExtra("mainCategoryTypeId");
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");
        subCategoryTypeId = getIntent().getStringExtra("subCategoryTypeId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            if (subCategoryTypeId.equals("NA")) {
                new GetBannersForCategory().execute(categoryTypeId, session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            } else {
                new GetBannersForSubCategory().execute(subCategoryTypeId, session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            }
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

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

    private class GetBannersForCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCategoryBanners");
            obj.addProperty("category_id", params[0]);
            obj.addProperty("location", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BANNERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
                    BannerListModel pojoDetails = new Gson().fromJson(result, BannerListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        bannerList = pojoDetails.getResult();
                        if (bannerList.size() > 0) {
                            cv_banner.setVisibility(View.VISIBLE);

                            BannerSliderAdapter adapter = new BannerSliderAdapter(context, bannerList);
                            imageSlider.setSliderAdapter(adapter);
                            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
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

    private class GetBannersForSubCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getSubCategoryBanners");
            obj.addProperty("sub_category_id", params[0]);
            obj.addProperty("location", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BANNERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
                    BannerListModel pojoDetails = new Gson().fromJson(result, BannerListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        bannerList = pojoDetails.getResult();
                        if (bannerList.size() > 0) {
                            cv_banner.setVisibility(View.VISIBLE);

                            BannerSliderAdapter adapter = new BannerSliderAdapter(context, bannerList);
                            imageSlider.setSliderAdapter(adapter);
                            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
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

    private void setDataToRecyclerView() {
        switch (mainCategoryTypeId) {
            case "1":
                if (businessList.size() > 0) {

                    if (!subCategoryTypeId.equals("NA")) {
                        ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> foundbiz = new ArrayList<SearchDetailsModel.ResultBean.BusinessesBean>();
                        for (SearchDetailsModel.ResultBean.BusinessesBean bizdetails : businessList) {
                            if (!bizdetails.getSub_type_id().equals(subCategoryTypeId)) {
                                foundbiz.add(bizdetails);
                            }
                        }
                        businessList.removeAll(foundbiz);
                    } else {
                        ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> foundbiz = new ArrayList<SearchDetailsModel.ResultBean.BusinessesBean>();
                        for (SearchDetailsModel.ResultBean.BusinessesBean bizdetails : businessList) {
                            if (!bizdetails.getType_id().equals(categoryTypeId)) {
                                foundbiz.add(bizdetails);
                            }
                        }
                        businessList.removeAll(foundbiz);
                    }

                    if (businessList.size() == 0) {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_searchlist.setVisibility(View.GONE);
                    } else {
                        rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "3"));
                    }
                }
                break;
            case "2":
                if (employeeList.size() > 0) {

                    if (!subCategoryTypeId.equals("NA")) {
                        ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> foundEmp = new ArrayList<SearchDetailsModel.ResultBean.EmployeesBean>();
                        for (SearchDetailsModel.ResultBean.EmployeesBean empdetails : employeeList) {
                            if (!empdetails.getSub_type_id().equals(subCategoryTypeId)) {
                                foundEmp.add(empdetails);
                            }
                        }
                        employeeList.removeAll(foundEmp);
                    } else {
                        ArrayList<SearchDetailsModel.ResultBean.EmployeesBean> foundEmp = new ArrayList<SearchDetailsModel.ResultBean.EmployeesBean>();
                        for (SearchDetailsModel.ResultBean.EmployeesBean empdetails : employeeList) {
                            if (!empdetails.getType_id().equals(categoryTypeId)) {
                                foundEmp.add(empdetails);
                            }
                        }
                        employeeList.removeAll(foundEmp);
                    }

                    if (employeeList.size() == 0) {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_searchlist.setVisibility(View.GONE);
                    } else {
                        rv_searchlist.setAdapter(new SearchEmployeeAdapter(context, employeeList, "3"));
                    }
                }
                break;
            case "3":
                if (professionalList.size() > 0) {

                    if (!subCategoryTypeId.equals("NA")) {
                        ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> foundProf = new ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean>();
                        for (SearchDetailsModel.ResultBean.ProfessionalsBean profdetails : professionalList) {
                            if (!profdetails.getSub_type_id().equals(subCategoryTypeId)) {
                                foundProf.add(profdetails);
                            }
                        }
                        professionalList.removeAll(foundProf);
                    } else {
                        ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean> foundProf = new ArrayList<SearchDetailsModel.ResultBean.ProfessionalsBean>();
                        for (SearchDetailsModel.ResultBean.ProfessionalsBean profdetails : professionalList) {
                            if (!profdetails.getType_id().equals(categoryTypeId)) {
                                foundProf.add(profdetails);
                            }
                        }
                        professionalList.removeAll(foundProf);
                    }

                    if (professionalList.size() == 0) {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_searchlist.setVisibility(View.GONE);
                    } else {
                        rv_searchlist.setAdapter(new SearchProfessionalAdapter(context, professionalList, "3"));
                    }
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
