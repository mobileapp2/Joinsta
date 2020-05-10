package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.BookOrderBusinessOrdersAdapter;
import in.oriange.joinsta.models.BookOrderBusinessOwnerModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class BookOrderBusinessOwnerOrders_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.rv_orders)
    RecyclerView rvOrders;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.imb_filter)
    ImageButton imbFilter;
    @BindView(R.id.tv_filter_count)
    TextView tvFilterCount;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private LocalBroadcastManager localBroadcastManager;
    private String userId, businessId;

    private List<MasterModel> orderStatusList;
    private List<BookOrderBusinessOwnerModel.ResultBean> orderList, mFilteredOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_business_owner_orders);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderBusinessOwnerOrders_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        rvOrders.setLayoutManager(new LinearLayoutManager(context));

        orderStatusList = new ArrayList<>();
        orderList = new ArrayList<>();
        mFilteredOrderList = new ArrayList<>();

        // status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7

        orderStatusList.add(new MasterModel("Orders added in cart", "1", false));
        orderStatusList.add(new MasterModel("Placed Orders", "2", false));
        orderStatusList.add(new MasterModel("Accepted Orders", "3", false));
        orderStatusList.add(new MasterModel("Orders in Progress", "4", false));
        orderStatusList.add(new MasterModel("Delivered Orders", "5", false));
        orderStatusList.add(new MasterModel("Orders applicable for Billing", "6", false));
        orderStatusList.add(new MasterModel("Cancelled Orders", "7", false));
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

    private void setDefault() {
        businessId = getIntent().getStringExtra("businessId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetReceivedOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderBusinessOwnerOrders_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetReceivedOrders().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        imbFilter.setOnClickListener(v -> showOrderStatusListDialog());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, mFilteredOrderList));
                    return;
                }

                if (mFilteredOrderList.size() == 0) {
                    rvOrders.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<BookOrderBusinessOwnerModel.ResultBean> ordersSearchedList = new ArrayList<>();
                    for (BookOrderBusinessOwnerModel.ResultBean orderDetails : mFilteredOrderList) {

                        String orderToBeSearched = orderDetails.getCustomer_business_code().toLowerCase() +
                                orderDetails.getCustomer_business_name().toLowerCase() +
                                orderDetails.getCustomer_name().toLowerCase();

                        if (orderToBeSearched.contains(query.toString().toLowerCase())) {
                            ordersSearchedList.add(orderDetails);
                        }
                    }
                    rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, ordersSearchedList));
                } else {
                    rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, mFilteredOrderList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showOrderStatusListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Order Status");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new OrderStatusAdapter());

        builder.setPositiveButton("Apply", (dialog, which) -> {
            List<BookOrderBusinessOwnerModel.ResultBean> filteredOrderList = new ArrayList<>();
            int selectedTypeCount = 0;

            for (MasterModel orderStatus : orderStatusList)
                if (orderStatus.isChecked()) {
                    selectedTypeCount = selectedTypeCount + 1;
                    for (BookOrderBusinessOwnerModel.ResultBean orderDetails : orderList)
                        if (orderStatus.getId().equals(orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()))
                            filteredOrderList.add(orderDetails);
                }

            if (selectedTypeCount == 0) {
                mFilteredOrderList = orderList;
                tvFilterCount.setVisibility(View.GONE);
                rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, orderList));
            } else {
                mFilteredOrderList = filteredOrderList;
                tvFilterCount.setVisibility(View.VISIBLE);
                tvFilterCount.setText(String.valueOf(selectedTypeCount));
                rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, mFilteredOrderList));
            }
        });

        builder.setNegativeButton("Clear Filter", (dialog, which) -> {
            edtSearch.setText("");
            for (int i = 0; i < orderStatusList.size(); i++) {
                orderStatusList.get(i).setChecked(false);
            }

            tvFilterCount.setVisibility(View.GONE);
            rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, orderList));
        });

        builder.create().show();
    }

    private class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(orderStatusList.get(position).getName());

            if (orderStatusList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    orderStatusList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderStatusList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }
    }

    private class GetReceivedOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvOrders.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getReceivedOrders");
//            obj.addProperty("business_id", businessId);
//            obj.addProperty("user_id", userId);
            obj.addProperty("business_id", "18");
            obj.addProperty("user_id", "21");
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            String type = "", message = "";

            tvFilterCount.setVisibility(View.GONE);
            for (int i = 0; i < orderStatusList.size(); i++) {
                orderStatusList.get(i).setChecked(false);
            }
            edtSearch.setText("");

            try {
                if (!result.equals("")) {
                    BookOrderBusinessOwnerModel pojoDetails = new Gson().fromJson(result, BookOrderBusinessOwnerModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<BookOrderBusinessOwnerModel.ResultBean> orderList = pojoDetails.getResult();

                        if (orderList.size() != 0) {
                            mFilteredOrderList = orderList;
                            rvOrders.setAdapter(new BookOrderBusinessOrdersAdapter(context, orderList));
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvOrders.setVisibility(View.GONE);
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
            if (Utilities.isNetworkAvailable(context)) {
                new GetReceivedOrders().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
