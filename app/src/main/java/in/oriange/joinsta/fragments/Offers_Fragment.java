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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.SelectCity_Activity;
import in.oriange.joinsta.adapters.OffersBusinessAdapter;
import in.oriange.joinsta.adapters.OffersEmployeeAdapter;
import in.oriange.joinsta.adapters.OffersProfessionalAdapter;
import in.oriange.joinsta.models.MainCategoryListModel;
import in.oriange.joinsta.models.OfferDetailsModel;
import in.oriange.joinsta.pojos.MainCategoryListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Offers_Fragment extends Fragment {

    private static Context context;
    private UserSessionManager session;
    private AppCompatEditText edt_type, edt_location;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView rv_offerslist;
    private static LinearLayout ll_nopreview;
    private static EditText edt_search;
    private static SpinKitView progressBar;
    private PowerMenu iconMenu;
    public static List<OfferDetailsModel.ResultBean.BusinessesBean> businessOffersList;
    public static List<OfferDetailsModel.ResultBean.ProfessionalsBean> professionalOffersList;
    public static List<OfferDetailsModel.ResultBean.EmployeesBean> employeeOffersList;

    private static String userId;
    private static String categoryTypeId;
    private ProgressDialog pd;
    private ArrayList<MainCategoryListModel> mainCategoryList;
    private ArrayList<PowerMenuItem> powerMenuItems;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = rootView.findViewById(R.id.progressBar);
        edt_location = rootView.findViewById(R.id.edt_location);
        edt_type = rootView.findViewById(R.id.edt_type);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        rv_offerslist = rootView.findViewById(R.id.rv_offerslist);
        rv_offerslist.setLayoutManager(new LinearLayoutManager(context));

        businessOffersList = new ArrayList<>();
        professionalOffersList = new ArrayList<>();
        employeeOffersList = new ArrayList<>();

        mainCategoryList = new ArrayList<>();
        powerMenuItems = new ArrayList<>();
    }

    private void setDefault() {
        categoryTypeId = "1";
        edt_type.setText("Business");

        if (Utilities.isNetworkAvailable(context)) {
            new GetOffersList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
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

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetOffersList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
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
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    getActivity().startActivityForResult(builder.build(getActivity()), 3);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }

                startActivity(new Intent(context, SelectCity_Activity.class)
                        .putExtra("requestCode", 3));
            }
        });

    }

    private void searchDetails(String categoryTypeId, String query) {
        switch (categoryTypeId) {
            case "1":
                if (businessOffersList.size() == 0) {
                    return;
                }

                if (!query.equals("")) {
                    ArrayList<OfferDetailsModel.ResultBean.BusinessesBean> businessOfferSearchedList = new ArrayList<>();
                    for (OfferDetailsModel.ResultBean.BusinessesBean businessDetails : businessOffersList) {

                        String businessToBeSearched = businessDetails.getBusiness_code().toLowerCase() +
                                businessDetails.getBusiness_name().toLowerCase() +
                                businessDetails.getCity().toLowerCase() +
                                businessDetails.getTitle().toLowerCase() +
                                businessDetails.getDescription().toLowerCase() +
                                businessDetails.getStart_date().toLowerCase() +
                                businessDetails.getEnd_date().toLowerCase() +
                                businessDetails.getPromo_code().toLowerCase() +
                                businessDetails.getCategory_name().toLowerCase();
                        if (businessToBeSearched.contains(query.toLowerCase())) {
                            businessOfferSearchedList.add(businessDetails);
                        }
                    }
                    rv_offerslist.setAdapter(new OffersBusinessAdapter(context, businessOfferSearchedList));
                } else {
                    rv_offerslist.setAdapter(new OffersBusinessAdapter(context, businessOffersList));
                }
                break;
            case "2":
                break;
            case "3":
                break;
        }
    }

    public static class GetOffersList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_offerslist.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getOfferDetailsbyLocation");
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
                    businessOffersList = new ArrayList<>();
                    professionalOffersList = new ArrayList<>();
                    employeeOffersList = new ArrayList<>();
                    OfferDetailsModel pojoDetails = new Gson().fromJson(result, OfferDetailsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessOffersList = pojoDetails.getResult().getBusinesses();
                        professionalOffersList = pojoDetails.getResult().getProfessionals();
                        employeeOffersList = pojoDetails.getResult().getEmployees();

                        setDataToRecyclerView(categoryTypeId);
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_offerslist.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_offerslist.setVisibility(View.GONE);
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
        edt_search.setText("");
        switch (categoryTypeId) {
            case "1":
                if (businessOffersList.size() > 0) {
                    rv_offerslist.setAdapter(new OffersBusinessAdapter(context, businessOffersList));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_offerslist.setVisibility(View.VISIBLE);
                } else {
                    rv_offerslist.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                if (employeeOffersList.size() > 0) {
                    rv_offerslist.setAdapter(new OffersEmployeeAdapter(context, employeeOffersList));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_offerslist.setVisibility(View.VISIBLE);
                } else {
                    rv_offerslist.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
            case "3":
                if (professionalOffersList.size() > 0) {
                    rv_offerslist.setAdapter(new OffersProfessionalAdapter(context, professionalOffersList));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_offerslist.setVisibility(View.VISIBLE);
                } else {
                    rv_offerslist.setVisibility(View.GONE);
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
