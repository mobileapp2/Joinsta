package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.CityAdapter;
import in.oriange.joinsta.models.GetCityListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class SelectCity_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private EditText edt_search_city;
    private RecyclerView rv_city;
    private LinearLayout ll_user_current_location, ll_recent_city;
    private TextView tv_recent_city;
    private String userId;

    private ArrayList<GetCityListModel.ResultBean> cityList;
    private int requestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = SelectCity_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_search_city = findViewById(R.id.edt_search_city);
        ll_user_current_location = findViewById(R.id.ll_user_current_location);
        ll_recent_city = findViewById(R.id.ll_recent_city);
        tv_recent_city = findViewById(R.id.tv_recent_city);
        rv_city = findViewById(R.id.rv_city);
        rv_city.setLayoutManager(new LinearLayoutManager(context));

        cityList = new ArrayList<>();
    }

    private void setDefault() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetCityList().execute();
        }

        requestCode = getIntent().getIntExtra("requestCode", 0);
    }

    private void getSessionDetails() {
        try {
            session = new UserSessionManager(context);
            String city = session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO);

            if (city == null)
                ll_recent_city.setVisibility(View.GONE);
            else
                tv_recent_city.setText(city);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        edt_search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ll_user_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SelectCity_Activity.this), requestCode);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        edt_search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (cityList.size() == 0) {
                    rv_city.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<GetCityListModel.ResultBean> citySearchedList = new ArrayList<>();
                    for (GetCityListModel.ResultBean cityDetails : cityList) {

                        String cityToBeSearched = cityDetails.getCity_name().toLowerCase();

                        if (cityToBeSearched.contains(query.toString().toLowerCase())) {
                            citySearchedList.add(cityDetails);
                        }
                    }
                    rv_city.setAdapter(new CityAdapter(context, citySearchedList, requestCode));
                } else {
                    rv_city.setAdapter(new CityAdapter(context, cityList, requestCode));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0 || requestCode == 1 || requestCode == 3)
                try {
                    Place place = PlacePicker.getPlace(this, data);
//                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Geocoder gcd = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                    if (addresses.size() != 0) {
                        session.setLocation(addresses.get(0).getLocality());
                        finishAffinity();
                        startActivity(new Intent(context, MainDrawer_Activity.class)
                                .putExtra("startOrigin", requestCode));


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setTitle("Alert");
                        builder.setMessage("City not found, please try again");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                                try {
                                    startActivityForResult(builder.build(SelectCity_Activity.this), 0);
                                } catch (GooglePlayServicesRepairableException e) {
                                    e.printStackTrace();
                                } catch (GooglePlayServicesNotAvailableException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.create();
                        AlertDialog alertD = builder.create();
                        alertD.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("Alert");
                    builder.setMessage("City not found, please try again");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            try {
                                startActivityForResult(builder.build(SelectCity_Activity.this), 0);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
        }
    }

    private class GetCityList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "giveCityDetails");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.CITYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    cityList = new ArrayList<>();
                    GetCityListModel pojoDetails = new Gson().fromJson(result, GetCityListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        cityList = pojoDetails.getResult();


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
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(SelectCity_Activity.this);
    }

}
