package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;

public class AddReportIssue_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private TextView tv_countrycode;
    private MaterialEditText edt_mobile;
    private EditText edt_feedback;
    private Button btn_save;

    private ArrayList<ContryCodeModel> countryCodeList;
    private String userId, name, mobile, countryCode;
    private AlertDialog countryCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report_issue);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddReportIssue_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tv_countrycode = findViewById(R.id.tv_countrycode);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_feedback = findViewById(R.id.edt_feedback);
        btn_save = findViewById(R.id.btn_save);
    }

    private void setDefault() {

        try {
            JSONArray m_jArry = new JSONArray(loadJSONForCountryCode(context));
            countryCodeList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                countryCodeList.add(new ContryCodeModel(
                        jo_inside.getString("name"),
                        jo_inside.getString("dial_code"),
                        jo_inside.getString("code")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mobile != null) {
            tv_countrycode.setText("+" + countryCode);
            edt_mobile.setText(mobile);
        }
    }

    private void getSessionDetails() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            name = json.getString("first_name");
            mobile = json.getString("mobile");
            try {
                countryCode = json.getString("country_code");
            } catch (Exception e) {
                countryCode = "91";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        tv_countrycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContryCodeDialog();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

    }

    private void showContryCodeDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_countrycodes_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Country");
        builder.setCancelable(false);

        final RecyclerView rv_country = view.findViewById(R.id.rv_country);
        EditText edt_search = view.findViewById(R.id.edt_search);
        rv_country.setLayoutManager(new LinearLayoutManager(context));
        rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                    return;
                }

                if (countryCodeList.size() == 0) {
                    rv_country.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContryCodeModel> searchedCountryList = new ArrayList<>();
                    for (ContryCodeModel countryDetails : countryCodeList) {

                        String countryToBeSearched = countryDetails.getName().toLowerCase();

                        if (countryToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedCountryList.add(countryDetails);
                        }
                    }
                    rv_country.setAdapter(new CountryCodeAdapter(searchedCountryList));
                } else {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        countryCodeDialog = builder.create();
        countryCodeDialog.show();
    }


    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

        private ArrayList<ContryCodeModel> countryCodeList;

        public CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList) {
            this.countryCodeList = countryCodeList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText(countryCodeList.get(position).getName());

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_countrycode.setText(countryCodeList.get(position).getDial_code());
                    countryCodeDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void submitData() {

        if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
            edt_mobile.setError("Please enter valid mobile number");
            edt_mobile.requestFocus();
            return;
        }

        if (edt_feedback.getText().toString().trim().isEmpty()) {
            edt_feedback.setError("Please enter feedback");
            edt_feedback.requestFocus();
            return;
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "createuserfeedback");
        mainObj.addProperty("user_name", name);
        mainObj.addProperty("userid", userId);
        mainObj.addProperty("feedback_text", edt_feedback.getText().toString().trim());
        mainObj.addProperty("mobile", edt_mobile.getText().toString().trim());
        mainObj.addProperty("Rating", "0");

        if (Utilities.isNetworkAvailable(context)) {
            new AddRequirement().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class AddRequirement extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.FEEDBACKAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        new ReportIssue_Activity.GetUserFeedback().execute(userId);
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Issue submitted successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to report your issue", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
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
        hideSoftKeyboard(AddReportIssue_Activity.this);
    }

}
