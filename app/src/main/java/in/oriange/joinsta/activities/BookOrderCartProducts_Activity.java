package in.oriange.joinsta.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Search_Fragment;
import in.oriange.joinsta.models.BookOrderGetMyOrdersModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class BookOrderCartProducts_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.ll_action_buttons)
    LinearLayout llActionButtons;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails;
    private List<BookOrderGetMyOrdersModel.ResultBean> ordersList;
    private List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsList;
    private LocalBroadcastManager localBroadcastManager;
    private String userId /*,businessOwnerId*/;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_cart_products);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderCartProducts_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        rvProducts.setLayoutManager(new LinearLayoutManager(context));

        ordersList = new ArrayList<>();
        productsList = new ArrayList<>();
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
//        businessOwnerOrderDetails = (BookOrderGetMyOrdersModel.ResultBean) getIntent().getSerializableExtra("businessOwnerOrderDetails");
//        businessOwnerId = getIntent().getStringExtra("businessOwnerId");

        rvProducts.setAdapter(new BookOrderProductsListAdapter());

        if (Utilities.isNetworkAvailable(context)) {
            new GetOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderCartProducts_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnSave.setOnClickListener(v -> startActivity(new Intent(context, BookOrderImageUpload_Activity.class)
                .putExtra("orderDetails", businessOwnerOrderDetails)));

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

    }

    private class BookOrderProductsListAdapter extends RecyclerView.Adapter<BookOrderProductsListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_cart_products, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            final BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetails = productsList.get(position);
            final int[] quantity = {Integer.parseInt(productDetails.getQuantity())};

            if (productDetails.getProduct_images().size() != 0) {
                Picasso.with(context)
                        .load(IMAGE_LINK + "product/" + productDetails.getProduct_images().get(0))
                        .into(holder.imv_productimage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.tv_nopreview.setVisibility(View.GONE);
                                holder.imv_productimage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.tv_nopreview.setVisibility(View.VISIBLE);
                                holder.imv_productimage.setVisibility(View.GONE);
                            }
                        });

            } else {
                holder.tv_nopreview.setVisibility(View.VISIBLE);
                holder.imv_productimage.setVisibility(View.GONE);
            }

            holder.tv_product_name.setText(productDetails.getName());
            holder.tv_product_info.setText(productDetails.getDescription());

            int sellingPrice = (int) Float.parseFloat(productDetails.getCurrent_amount());
            holder.tv_total_product_price.setText("₹ " + sellingPrice * quantity[0]);

            holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));

            holder.btn_remove.setOnClickListener(v -> {
                if (quantity[0] == 1) {
                    return;
                }
                quantity[0] = quantity[0] - 1;
                holder.tv_total_product_price.setText("₹ " + sellingPrice * quantity[0]);
                holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));

            });

            holder.btn_add.setOnClickListener(v -> {
                quantity[0] = quantity[0] + 1;
                holder.tv_total_product_price.setText("₹ " + sellingPrice * quantity[0]);
                holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));
            });

            holder.btn_addtocart.setOnClickListener(v -> updateProductQuantity(productDetails, quantity[0]));

            holder.ib_delete.setOnClickListener(v -> {
                if (productsList.size() == 1) {
                    Utilities.showMessage("There must be atleast one product in the order", context, 2);
                    return;
                }

                deleteProduct(productDetails.getId());
            });
        }

        @Override
        public int getItemCount() {
            return productsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CardView cv_mainlayout;
            private ImageView imv_productimage;
            private TextView tv_nopreview, tv_product_name, tv_product_info, tv_total_product_price, tv_quantity;
            private ImageButton btn_remove, btn_add;
            private Button btn_addtocart;
            private ImageButton ib_delete;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                imv_productimage = view.findViewById(R.id.imv_productimage);
                tv_nopreview = view.findViewById(R.id.tv_nopreview);
                tv_product_name = view.findViewById(R.id.tv_product_name);
                tv_product_info = view.findViewById(R.id.tv_product_info);
                tv_total_product_price = view.findViewById(R.id.tv_total_product_price);
                tv_quantity = view.findViewById(R.id.tv_quantity);
                btn_remove = view.findViewById(R.id.btn_remove);
                btn_add = view.findViewById(R.id.btn_add);
                btn_addtocart = view.findViewById(R.id.btn_addtocart);
                ib_delete = view.findViewById(R.id.ib_delete);
            }
        }
    }

    private void updateProductQuantity(BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean selectedProduct, int quantity) {

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsToBeAddedList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean orderProduct : businessOwnerOrderDetails.getProduct_details()) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    orderProduct.getProduct_id(),
                    businessOwnerOrderDetails.getId(),
                    orderProduct.getQuantity(),
                    orderProduct.getCurrent_amount()
            ));
        }

        boolean isProductAlreadyAddedInOrder = false;

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (productsToBeAddedList.get(i).getProduct_id().equals(selectedProduct.getProduct_id())) {
                isProductAlreadyAddedInOrder = true;
                productsToBeAddedList.get(i).setQuantity(String.valueOf(quantity));
                productsToBeAddedList.get(i).setAmount(selectedProduct.getCurrent_amount());
            }
        }

        if (!isProductAlreadyAddedInOrder) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    selectedProduct.getId(),
                    businessOwnerOrderDetails.getId(),
                    String.valueOf(quantity),
                    selectedProduct.getCurrent_amount()
            ));
        }

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (i <= businessOwnerOrderDetails.getProduct_details().size() - 1) {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", businessOwnerOrderDetails.getProduct_details().get(i).getId());
                productObject.addProperty("product_id", productsToBeAddedList.get(i).getProduct_id());
                productObject.addProperty("quantity", productsToBeAddedList.get(i).getQuantity());
                productObject.addProperty("amount", productsToBeAddedList.get(i).getAmount());
                productsDetailsArray.add(productObject);
            } else {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", "0");
                productObject.addProperty("product_id", productsToBeAddedList.get(i).getProduct_id());
                productObject.addProperty("quantity", productsToBeAddedList.get(i).getQuantity());
                productObject.addProperty("amount", productsToBeAddedList.get(i).getAmount());
                productsDetailsArray.add(productObject);
            }
        }

        updateJson(productsDetailsArray);
    }

    private void deleteProduct(String id) {
        JsonArray productsDetailsArray = new JsonArray();

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsList = businessOwnerOrderDetails.getProduct_details();
        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> newProductList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetails : productsList) {
            if (!productDetails.getId().equals(id)) {
                newProductList.add(productDetails);
            }
        }

        for (int i = 0; i < newProductList.size(); i++) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("id", newProductList.get(i).getId());
            productObject.addProperty("product_id", newProductList.get(i).getProduct_id());
            productObject.addProperty("quantity", newProductList.get(i).getQuantity());
            productObject.addProperty("amount", newProductList.get(i).getAmount());
            productsDetailsArray.add(productObject);
        }

        updateJson(productsDetailsArray);
    }

    private void updateJson(JsonArray productsDetailsArray) {
        JsonObject mainObj = new JsonObject();
        JsonArray orderImageJsonArray = new JsonArray();

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", businessOwnerOrderDetails.getId());
        mainObj.addProperty("order_id", businessOwnerOrderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", businessOwnerOrderDetails.getOwner_business_id());
        mainObj.addProperty("order_type", "1");
        mainObj.addProperty("order_text", "");
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "1");    // status = 'IN CART'-2
        mainObj.addProperty("purchase_order_type", "1");
        mainObj.addProperty("business_id", "0");
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new UpdateOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class GetOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllOrders");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        ordersList = pojoDetails.getResult();
                        updateProductsList();
                    } else {
                        llActionButtons.setVisibility(View.GONE);
                        llNopreview.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateOrder extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();
                    new Search_Fragment.GetOrders().execute();
                    if (type.equalsIgnoreCase("success")) {
                        ordersList = pojoDetails.getResult();
                        updateProductsList();
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            obj.addProperty("id", businessOwnerOrderDetails.getId());
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderMyOrders_Activity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Order cancelled successfully");
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

    private void updateProductsList() {
        for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList)
//            if (orderDetail.getOwner_business_id().equals(businessOwnerId))
            if (orderDetail.getStatus_details().size() == 1)
                if (orderDetail.getStatus_details().get(0).getStatus().equals("1")) {
//                    if (orderDetail.getOwner_business_id().equals(businessOwnerId)) {
                    businessOwnerOrderDetails = orderDetail;
                    break;
//                    }
                }


        if (businessOwnerOrderDetails != null) {
            productsList = businessOwnerOrderDetails.getProduct_details();

            if (productsList.size() != 0) {
                llActionButtons.setVisibility(View.VISIBLE);
                llNopreview.setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);
                rvProducts.setAdapter(new BookOrderProductsListAdapter());
            } else {
                llActionButtons.setVisibility(View.GONE);
                llNopreview.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }
        } else {
            llActionButtons.setVisibility(View.GONE);
            llNopreview.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
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
