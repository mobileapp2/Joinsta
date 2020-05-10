package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.BookOrderGetMyOrdersModel;
import in.oriange.joinsta.models.GetBusinessModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class BookOrderPurchaseTypeSelection_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rb_individual)
    RadioButton rbIndividual;
    @BindView(R.id.imv_user)
    CircleImageView imvUser;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.ll_individual_details)
    LinearLayout llIndividualDetails;
    @BindView(R.id.rb_business)
    RadioButton rbBusiness;
    @BindView(R.id.rv_business)
    RecyclerView rvBusiness;
    @BindView(R.id.ll_business)
    LinearLayout llBusiness;
    @BindView(R.id.rg_purchase_type)
    RadioGroup rgPurchaseType;
    @BindView(R.id.btn_save)
    Button btnSave;

    private Context context;
    private UserSessionManager session;
    private String userId, businessOwnerId, orderType, orderText = "", customerBusinessId = "", orderImageArray = "";

    public static ArrayList<GetBusinessModel.ResultBean> businessList;
    private GetBusinessModel.ResultBean businessDetails;
    private LocalBroadcastManager localBroadcastManager;
    private BookOrderGetMyOrdersModel.ResultBean orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_purchase_type_selection);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderPurchaseTypeSelection_Activity.this;
        session = new UserSessionManager(context);

        businessList = new ArrayList<>();
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            String imageUrl = json.getString("image_url");
            String name = json.getString("first_name");
            String country_code = json.getString("country_code");
            String mobile = json.getString("mobile");

            if (!imageUrl.equals("")) {
                String url = IMAGE_LINK + userId + "/" + imageUrl;
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.icon_userphoto)
                        .into(imvUser);
            }

            tvName.setText(name);
            tvMobile.setText("+" + country_code + mobile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        orderType = getIntent().getStringExtra("orderType");
        orderText = getIntent().getStringExtra("orderText");
        orderImageArray = getIntent().getStringExtra("orderImageArray");
        orderDetails = (BookOrderGetMyOrdersModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        if (orderDetails == null) {
            orderDetails = new BookOrderGetMyOrdersModel.ResultBean();
        }

        rvBusiness.setLayoutManager(new LinearLayoutManager(context));

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderPurchaseTypeSelection_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        rgPurchaseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_individual:
                        llIndividualDetails.setVisibility(View.VISIBLE);
                        llBusiness.setVisibility(View.GONE);
                        break;
                    case R.id.rb_business:
                        llIndividualDetails.setVisibility(View.GONE);

                        if (businessList.size() == 0) {
                            if (Utilities.isNetworkAvailable(context))
                                new GetBusiness().execute();
                            else
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        } else {
                            llBusiness.setVisibility(View.VISIBLE);
                            rvBusiness.setAdapter(new BusinessAdapter());
                        }
                        break;
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void submitData() {
        if (rbIndividual.isChecked()) {
            startActivity(new Intent(context, BookOrderPurchaseSummary_Activity.class)
                    .putExtra("purchaseOrderType", "1")            // purchase_order_type = 'individual' - 1, 'business' -2
                    .putExtra("businessOwnerId", businessOwnerId)
                    .putExtra("orderType", orderType)                   //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
                    .putExtra("orderText", orderText)
                    .putExtra("orderImageArray", orderImageArray)
                    .putExtra("orderDetails", orderDetails));
        } else if (rbBusiness.isChecked()) {
            if (customerBusinessId.equals("")) {
                Utilities.showMessage("Please select business", context, 2);
                return;
            }

            startActivity(new Intent(context, BookOrderPurchaseSummary_Activity.class)
                    .putExtra("purchaseOrderType", "2")            // purchase_order_type = 'individual' - 1, 'business' -2
                    .putExtra("businessOwnerId", businessOwnerId)
                    .putExtra("orderType", orderType)                   //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
                    .putExtra("orderText", orderText)
                    .putExtra("businessDetails", businessDetails)
                    .putExtra("orderImageArray", orderImageArray)
                    .putExtra("orderDetails", orderDetails));
        } else {
            Utilities.showMessage("Please select purchase type", context, 2);
            return;
        }
    }

    private class GetBusiness extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    GetBusinessModel pojoDetails = new Gson().fromJson(result, GetBusinessModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult();
                        if (businessList.size() > 0) {
                            llBusiness.setVisibility(View.VISIBLE);
                            rvBusiness.setAdapter(new BusinessAdapter());
                        } else {
                            Utilities.showMessage("There are no businesses added by you", context, 2);
                            rbBusiness.setChecked(false);
                            rbIndividual.setChecked(true);
                        }
                    } else {
                        Utilities.showMessage("There are no businesses added by you", context, 2);
                        rbBusiness.setChecked(false);
                        rbIndividual.setChecked(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_business_select, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();
            final GetBusinessModel.ResultBean searchDetails = businessList.get(position);

            if (searchDetails.isChecked())
                holder.rb_select_business.setChecked(true);
            else
                holder.rb_select_business.setChecked(false);

            holder.tv_heading.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());

            if (!searchDetails.getSubtype_description().isEmpty())
                holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
            else
                holder.tv_subheading.setText(searchDetails.getType_description());

            holder.rb_select_business.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.rb_select_business.isChecked()) {
                        for (int i = 0; i < businessList.size(); i++) {
                            businessList.get(i).setChecked(false);
                        }
                        businessDetails = businessList.get(position);
                        customerBusinessId = businessList.get(position).getId();
                        businessList.get(position).setChecked(true);
                    }
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return businessList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private RadioButton rb_select_business;
            private TextView tv_heading, tv_subheading;

            public MyViewHolder(View view) {
                super(view);
                tv_heading = view.findViewById(R.id.tv_heading);
                tv_subheading = view.findViewById(R.id.tv_subheading);
                rb_select_business = view.findViewById(R.id.rb_select_business);
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
