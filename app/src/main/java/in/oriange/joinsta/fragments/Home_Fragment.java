package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.SelectLocation_Activity;
import in.oriange.joinsta.adapters.CategoryAdapter;
import in.oriange.joinsta.models.CategotyListModel;
import in.oriange.joinsta.pojos.CategotyListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;

import static android.app.Activity.RESULT_OK;

public class Home_Fragment extends Fragment {

    private Context context;
    private final String TAG = "bottom_sheet";
    private AppCompatEditText edt_type, edt_location;
    private RecyclerView rv_category;
    private SpinKitView progressBar;
    private PowerMenu iconMenu;
    private String categoryTypeId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {

        edt_type = rootView.findViewById(R.id.edt_type);
        edt_location = rootView.findViewById(R.id.edt_location);

        progressBar = rootView.findViewById(R.id.progressBar);

        rv_category = rootView.findViewById(R.id.rv_category);
        rv_category.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {

        categoryTypeId = "1";
        edt_type.setText("Business");

        if (Utilities.isNetworkAvailable(context)) {
            new GetCategotyList().execute("0", "0", categoryTypeId);
        }

    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new BottomSheetMenu_Fragment().show(getFragmentManager(), TAG);

                iconMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("Business", R.drawable.icon_trader1))
                        .addItem(new PowerMenuItem("Profession", R.drawable.icon_profession))
                        .addItem(new PowerMenuItem("Empolyee", R.drawable.icon_employee))
                        .addItem(new PowerMenuItem("Student", R.drawable.icon_student))
                        .setOnMenuItemClickListener(onIconMenuItemClickListener)
                        .setAnimation(MenuAnimation.FADE)
                        .setMenuEffect(MenuEffect.BODY)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                iconMenu.showAsDropDown(v);


            }
        });

        edt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SelectLocation_Activity.class), 10000);
            }
        });

    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            edt_type.setText(item.getTitle());
            categoryTypeId = String.valueOf(position + 1);
            iconMenu.dismiss();

            if (Utilities.isNetworkAvailable(context)) {
                new GetCategotyList().execute("0", "0", categoryTypeId);
            }

        }
    };

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
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
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
                    edt_type.setText("Employee");
                    bottomDialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10000 && resultCode == RESULT_OK) {
            String requiredValue = data.getStringExtra("locationname");
            edt_location.setText(requiredValue);
        }

    }
}
