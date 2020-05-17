package in.oriange.joinsta.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.BookOrderOrderImagesAdapter;
import in.oriange.joinsta.adapters.BookOrderProductsAdapter;
import in.oriange.joinsta.adapters.BookOrderStatusAdapter;
import in.oriange.joinsta.models.BookOrderBusinessOwnerModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class ViewBookOrderBusinessOwnerOrder_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_customer_mobile)
    TextView tvCustomerMobile;
    @BindView(R.id.tv_customer_email)
    TextView tvCustomerEmail;
    @BindView(R.id.tv_customer_address)
    TextView tvCustomerAddress;
    @BindView(R.id.cv_customer)
    CardView cvCustomer;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.cv_text)
    CardView cvText;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.cv_images)
    CardView cvImages;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.cv_products)
    CardView cvProducts;
    @BindView(R.id.rv_status)
    RecyclerView rvStatus;
    @BindView(R.id.cv_status)
    CardView cvStatus;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.ib_call)
    ImageButton ibCall;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.ib_email)
    ImageButton ibEmail;
    @BindView(R.id.ll_email)
    LinearLayout llEmail;
    @BindView(R.id.tv_more_order_images)
    TextView tvMoreOrderImages;
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_purchase_order_type)
    TextView tvPurchaseOrderType;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;
    private BookOrderBusinessOwnerModel.ResultBean orderDetails;
    private boolean isOrderImagesExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_order_business_owner_order);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewBookOrderBusinessOwnerOrder_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

//        rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
        rvProducts.setLayoutManager(new LinearLayoutManager(context));
        rvStatus.setLayoutManager(new LinearLayoutManager(context));
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
        orderDetails = (BookOrderBusinessOwnerModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        tvOrderId.setText("Order Id - " + orderDetails.getOrder_id());

        switch (orderDetails.getPurchase_order_type()) {      //purchase_order_type = 'individual' - 1, 'business' -2
            case "1":
                tvPurchaseOrderType.setText("Purchase Type - Individual");
                tvCustomerName.setText(orderDetails.getCustomer_name());

                tvCustomerMobile.setText("+" + orderDetails.getCustomer_country_code() + orderDetails.getCustomer_mobile());

                tvCustomerEmail.setText(orderDetails.getCustomer_email());

                break;
            case "2":
                tvPurchaseOrderType.setText("Purchase Type - Business");
                tvCustomerName.setText(orderDetails.getCustomer_business_name());

                List<BookOrderBusinessOwnerModel.ResultBean.CustomerBusinessMobileBean> mobilesList = orderDetails.getCustomer_business_mobile().get(0);
                if (mobilesList != null) {
                    if (mobilesList.size() != 0) {
                        tvCustomerMobile.setText(mobilesList.get(0).getCountry_code() + mobilesList.get(0).getMobile_number());
                    }
                }

                tvCustomerEmail.setText(orderDetails.getCustomer_business_email());
                tvCustomerAddress.setText(orderDetails.getCustomer_business_address());
                break;
        }

        if (tvCustomerMobile.getText().toString().trim().equals(""))
            llMobile.setVisibility(View.GONE);

        if (tvCustomerEmail.getText().toString().trim().equals(""))
            llEmail.setVisibility(View.GONE);

        if (tvCustomerAddress.getText().toString().trim().equals(""))
            tvCustomerAddress.setVisibility(View.GONE);

        switch (orderDetails.getOrder_type()) {
            case "1":
                tvOrderType.setText("Order by Product");
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.VISIBLE);

                rvProducts.setAdapter(new BookOrderProductsAdapter(context, orderDetails.getProduct_details()));

                break;
            case "2":
                tvOrderType.setText("Order by Image");
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.VISIBLE);
                cvProducts.setVisibility(View.GONE);

//                ArrayList<String> orderImagesList = new ArrayList<>();
//
//                for (int i = 0; i < orderDetails.getOrder_images().size(); i++)
//                    orderImagesList.add(IMAGE_LINK + "orders/" + orderDetails.getOrder_images().get(i));
//
//                rvImages.setAdapter(new BookOrderOrderImagesAdapter(context, orderImagesList));
                break;
            case "3":
                tvOrderType.setText("Order by Text");
                cvText.setVisibility(View.VISIBLE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.GONE);

                tvText.setText(orderDetails.getOrder_text());
                break;
        }

        ArrayList<String> orderImagesList = new ArrayList<>();

        for (int i = 0; i < orderDetails.getOrder_images().size(); i++)
            orderImagesList.add(IMAGE_LINK + "orders/" + orderDetails.getOrder_images().get(i));

        if (orderImagesList.size() != 0) {
            cvImages.setVisibility(View.VISIBLE);
            rvImages.setAdapter(new BookOrderOrderImagesAdapter(context, orderImagesList));
        }

        if (orderImagesList.size() > 3) {
            tvMoreOrderImages.setVisibility(View.VISIBLE);
        } else {
            tvMoreOrderImages.setVisibility(View.GONE);
        }

        rvStatus.setAdapter(new BookOrderStatusAdapter(context, orderDetails.getStatus_details()));

        switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                tvOrderStatus.setText("Order Added in Cart");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            case "2":
                btnAction.setText("Accept");
                tvOrderStatus.setText("Order Placed");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case "3":
                btnAction.setText("In Progress");
                tvOrderStatus.setText("Order Accepted");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "4":
                btnAction.setText("Delivered");
                tvOrderStatus.setText("Order in Progress");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case "5":
                btnAction.setText("Billing");
                btnReject.setVisibility(View.GONE);
                tvOrderStatus.setText("Order Delivered");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "6":
                tvOrderStatus.setText("Order Billing");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
            case "7":
                tvOrderStatus.setText("Order Cancelled");
                tvOrderStatus.setTextColor(context.getResources().getColor(R.color.red));
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
        }
    }

    private void setEventHandler() {
        btnAction.setOnClickListener(v -> {
            String status = "";

            switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
                //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
//                case "1":
//                case "6":
//                case "7":
//                    btnAction.setVisibility(View.GONE);
//                    btnReject.setVisibility(View.GONE);
//                    break;
                case "2":
                    status = "3";
                    break;
                case "3":
                    status = "4";
                    break;
                case "4":
                    status = "5";
                    break;
                case "5":
                    status = "6";
//                    btnReject.setVisibility(View.GONE);
                    break;
            }
            if (Utilities.isNetworkAvailable(context)) {
                new ChangeOrderStatus().execute(status);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        btnReject.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setMessage("Are you sure you want to cancel this order?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                if (Utilities.isNetworkAvailable(context)) {
                    new ChangeOrderStatus().execute("7");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {

            });
            alertDialogBuilder.create().show();
        });

        ibCall.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
            } else {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to make a call?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_call);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", (dialog, id) ->
                        context.startActivity(new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + tvCustomerMobile.getText().toString().trim())))
                );
                builder.setNegativeButton("NO", (dialog, which) ->
                        dialog.dismiss()
                );
                androidx.appcompat.app.AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        ibEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", tvCustomerEmail.getText().toString().trim(), null));
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        tvMoreOrderImages.setOnClickListener(v -> {
            if (!isOrderImagesExpanded) {
                isOrderImagesExpanded = true;
                tvMoreOrderImages.setText("View More");
                rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                isOrderImagesExpanded = false;
                tvMoreOrderImages.setText("View Less");
                rvImages.setLayoutManager(new GridLayoutManager(context, 3));
            }
        });
    }

    private class ChangeOrderStatus extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "changeOrderStatus");
            obj.addProperty("id", orderDetails.getId());
            obj.addProperty("status", params[0]);    //status = 'CANCEL'-7
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderBusinessOwnerOrders_Activity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Order status changed successfully");
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
                        Utilities.showMessage(message, context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
}
