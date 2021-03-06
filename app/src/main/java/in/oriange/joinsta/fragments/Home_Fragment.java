package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.PublicOfficeByLocation_Activity;
import in.oriange.joinsta.activities.SelectCity_Activity;
import in.oriange.joinsta.adapters.BannerSliderAdapter;
import in.oriange.joinsta.adapters.CategoryAdapter;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.CategotyListModel;
import in.oriange.joinsta.models.MainCategoryListModel;
import in.oriange.joinsta.pojos.CategotyListPojo;
import in.oriange.joinsta.pojos.MainCategoryListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Home_Fragment extends Fragment {

    private Context context;
    private final String TAG = "bottom_sheet";
    private AppCompatEditText edt_type, edt_location;
    private AnimatedRecyclerView rv_category;
    private CardView cv_banner, cv_public_office;
    private SliderView imageSlider;
    private SpinKitView progressBar;
    private PowerMenu iconMenu;
    private String categoryTypeId;
    private ProgressDialog pd;
    private ArrayList<MainCategoryListModel> mainCategoryList;
    private ArrayList<PowerMenuItem> powerMenuItems;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        edt_type = rootView.findViewById(R.id.edt_type);
        edt_location = rootView.findViewById(R.id.edt_location);
        cv_banner = rootView.findViewById(R.id.cv_banner);
        cv_public_office = rootView.findViewById(R.id.cv_public_office);
        imageSlider = rootView.findViewById(R.id.imageSlider);

        progressBar = rootView.findViewById(R.id.progressBar);
        rv_category = rootView.findViewById(R.id.rv_category);
        rv_category.setLayoutManager(new LinearLayoutManager(context));
        mainCategoryList = new ArrayList<>();
        powerMenuItems = new ArrayList<>();
    }

    private void setDefault() {
        categoryTypeId = "1";
        edt_type.setText("Business");

        if (Utilities.isNetworkAvailable(context)) {
            new GetBanners().execute(categoryTypeId, new UserSessionManager(context).getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            new GetCategotyList().execute("0", "0", categoryTypeId);
        }
    }

    private void getSessionDetails() {
        try {
            UserSessionManager session = new UserSessionManager(context);
            edt_location.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        cv_public_office.setOnClickListener(v -> {
            startActivity(new Intent(context, PublicOfficeByLocation_Activity.class));
        });

        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new BottomSheetMenu_Fragment().show(getFragmentManager(), TAG);

                if (powerMenuItems.size() == 0)
                    if (Utilities.isNetworkAvailable(context))
                        new GetMainCategotyList().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                else
                    showCategoryMenus(powerMenuItems);
            }
        });

        edt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                try {
//                    getActivity().startActivityForResult(builder.build(getActivity()), 0);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }


//                Places.initialize(context, "AIzaSyAbJV1S2i3HcrO9IADky8omovUTUC1FDU8");
//                List<com.google.android.libraries.places.api.model.Place.Field> placeFields = new ArrayList<>(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.values()));
//                List<TypeFilter> typeFilters = new ArrayList<>(Arrays.asList(TypeFilter.values()));
//
//                RectangularBounds bounds = RectangularBounds.newInstance(
//                        new LatLng(-33.880490, 151.184363),
//                        new LatLng(-33.858754, 151.229596));
//                Intent autocompleteIntent =
//                        new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
//                                .setLocationBias(bounds)
//                                .setTypeFilter(typeFilters.get(0))
//                                .build(context);
//                startActivityForResult(autocompleteIntent, 0);
                startActivity(new Intent(context, SelectCity_Activity.class)
                        .putExtra("requestCode", 0));

            }
        });

    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            edt_type.setText(item.getTitle());
            categoryTypeId = mainCategoryList.get(position).getId();
            iconMenu.dismiss();

            if (Utilities.isNetworkAvailable(context)) {
                cv_banner.setVisibility(View.GONE);
                new GetBanners().execute(categoryTypeId, new UserSessionManager(context).getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
                new GetCategotyList().execute("0", "0", categoryTypeId);
            }

        }
    };

    private class GetBanners extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCategoryTypeBanners");
            obj.addProperty("category_type_id", params[0]);
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

    private class GetCategotyList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_category.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategory");
            obj.addProperty("parent_id", params[0]);
            obj.addProperty("level", params[1]);
            obj.addProperty("category_type_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_category.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ArrayList<CategotyListModel> categotyList = new ArrayList<>();
                    CategotyListPojo pojoDetails = new Gson().fromJson(result, CategotyListPojo.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
                            cv_public_office.setVisibility(View.VISIBLE);
                            rv_category.setAdapter(new CategoryAdapter(context, categotyList, categoryTypeId));
                        }
                    } else {
                        rv_category.setAdapter(new CategoryAdapter(context, categotyList, categoryTypeId));
                        Utilities.showAlertDialog(context, "Categories not available", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
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

    @SuppressLint("ValidFragment")
    public class BottomSheetMenu_Fragment extends BottomSheetDialogFragment {

        private LinearLayout ll_business, ll_profession, ll_employee;
        private BottomSheetDialog bottomDialog;

        @Override
        public int getTheme() {
            return R.style.BottomSheetDialogTheme;
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            bottomDialog = new BottomSheetDialog(context);
            bottomDialog = (BottomSheetDialog) dialog;
            View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom_sheet_dialog, null);
            dialog.setContentView(view);

            init(view);
            setDefaults();
            setEventListner();
        }

        private void init(View view) {
            context = getActivity();
            ll_business = view.findViewById(R.id.ll_business);
            ll_profession = view.findViewById(R.id.ll_profession);
            ll_employee = view.findViewById(R.id.ll_employee);

        }

        private void setDefaults() {

        }

        private void setEventListner() {
            ll_business.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Business");
                    bottomDialog.dismiss();
                }
            });
            ll_profession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Profession");
                    bottomDialog.dismiss();
                }
            });
            ll_employee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Employment");
                    bottomDialog.dismiss();
                }
            });

        }
    }

}
