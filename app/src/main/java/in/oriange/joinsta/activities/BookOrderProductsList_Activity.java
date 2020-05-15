package in.oriange.joinsta.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.library.banner.BannerLayout;
import com.github.ybq.android.spinkit.SpinKitView;
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
import in.oriange.joinsta.adapters.OfferRecyclerBannerAdapter;
import in.oriange.joinsta.fragments.Search_Fragment;
import in.oriange.joinsta.models.BookOrderGetMyOrdersModel;
import in.oriange.joinsta.models.BookOrderProductsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class BookOrderProductsList_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.ib_cart)
    ImageButton ibCart;
    @BindView(R.id.tv_cart_count)
    TextView tvCartCount;
    @BindView(R.id.fl_cart)
    FrameLayout flCart;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private String userId, businessOwnerId;
    private List<BookOrderProductsListModel.ResultBean> productsList, searchedProductsList;
    private List<BookOrderGetMyOrdersModel.ResultBean> ordersList;
    private BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails;

    private LocalBroadcastManager localBroadcastManager;
    private int quantity = 1;
    private int sellingPrice, applicablePrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_products_list);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderProductsList_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

//        rvProducts.setLayoutManager(new LinearLayoutManager(context));
        rvProducts.setLayoutManager(new GridLayoutManager(context, 2));

        productsList = new ArrayList<>();
        ordersList = new ArrayList<>();
        searchedProductsList = new ArrayList<>();
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
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetAllProducts().execute();
            new GetOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderProductsList_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    searchedProductsList = productsList;
                    rvProducts.setAdapter(new BookOrderProductsListAdapter());
                    return;
                }

                if (ordersList.size() == 0) {
                    searchedProductsList = productsList;
                    rvProducts.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<BookOrderProductsListModel.ResultBean> searchedList = new ArrayList<>();
                    for (BookOrderProductsListModel.ResultBean orderDetails : productsList) {

                        String orderToBeSearched = orderDetails.getName().toLowerCase() +
                                orderDetails.getRemark().toLowerCase();

                        if (orderToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedList.add(orderDetails);
                        }
                    }
                    searchedProductsList = searchedList;
                    rvProducts.setAdapter(new BookOrderProductsListAdapter());
                } else {
                    searchedProductsList = productsList;
                    rvProducts.setAdapter(new BookOrderProductsListAdapter());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ibCart.setOnClickListener(v -> {
            startActivity(new Intent(context, BookOrderCartProducts_Activity.class)
                    .putExtra("businessOwnerOrderDetails", businessOwnerOrderDetails)
                    .putExtra("businessOwnerId", businessOwnerId));
            finish();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAllProducts().execute();
                new GetOrders().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
    }

    private class GetAllProducts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvProducts.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getallProducts");
            obj.addProperty("user_id", userId);
            obj.addProperty("business_id", businessOwnerId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderProductsListModel pojoDetails = new Gson().fromJson(result, BookOrderProductsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        productsList = pojoDetails.getResult();
                        searchedProductsList = pojoDetails.getResult();

                        if (productsList.size() != 0) {
                            rvProducts.setAdapter(new BookOrderProductsListAdapter());
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvProducts.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }
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
                        showCartCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showCartCount() {
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
            if (businessOwnerOrderDetails.getProduct_details().size() != 0) {
                flCart.setVisibility(View.VISIBLE);
                tvCartCount.setText(String.valueOf(businessOwnerOrderDetails.getProduct_details().size()));
            } else {
                flCart.setVisibility(View.GONE);
                tvCartCount.setText("");
            }
        }
    }

    private class BookOrderProductsListAdapter extends RecyclerView.Adapter<BookOrderProductsListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_grid_book_order_products, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            final BookOrderProductsListModel.ResultBean productDetails = searchedProductsList.get(position);
            final int[] quantity = {1};

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

            int maxRetailPrice = (int) Float.parseFloat(productDetails.getMax_retail_price());
            int sellingPrice = (int) Float.parseFloat(productDetails.getSelling_price());

            int savedAmount = maxRetailPrice - sellingPrice;

            if (savedAmount <= 0) {
                holder.tv_selling_price.setText("₹ " + sellingPrice);
                holder.tv_max_retail_price.setVisibility(View.GONE);
                holder.tv_precentage_off.setVisibility(View.GONE);
            } else {

                int percent = maxRetailPrice % sellingPrice;

                holder.tv_selling_price.setText("₹ " + sellingPrice);
                holder.tv_max_retail_price.setText(Html.fromHtml("<strike>₹ " + maxRetailPrice + "</strike>"));
                holder.tv_precentage_off.setText(percent + "% off");
            }

            holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));

            holder.cv_mainlayout.setOnClickListener(v -> {
                showProductDetailsDialog(productDetails);
            });

            holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity[0] == 1) {
                        return;
                    }
                    quantity[0] = quantity[0] - 1;
                    holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));

                }
            });

            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantity[0] = quantity[0] + 1;
                    holder.tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Qty - </b></font> <font color=\"#EF6C00\"> <b>" + quantity[0] + "</b></font>"));
                }
            });

            holder.btn_addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findValidOrderId(productDetails, quantity[0]);
                }
            });

        }

        @Override
        public int getItemCount() {
            return searchedProductsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CardView cv_mainlayout;
            private ImageView imv_productimage;
            private TextView tv_nopreview, tv_product_name, tv_selling_price, tv_max_retail_price, tv_precentage_off, tv_quantity;
            private ImageButton btn_remove, btn_add;
            private Button btn_addtocart;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                imv_productimage = view.findViewById(R.id.imv_productimage);
                tv_nopreview = view.findViewById(R.id.tv_nopreview);
                tv_product_name = view.findViewById(R.id.tv_product_name);
                tv_selling_price = view.findViewById(R.id.tv_selling_price);
                tv_max_retail_price = view.findViewById(R.id.tv_max_retail_price);
                tv_precentage_off = view.findViewById(R.id.tv_precentage_off);
                tv_quantity = view.findViewById(R.id.tv_quantity);
                btn_remove = view.findViewById(R.id.btn_remove);
                btn_add = view.findViewById(R.id.btn_add);
                btn_addtocart = view.findViewById(R.id.btn_addtocart);
            }
        }
    }

    private void showProductDetailsDialog(BookOrderProductsListModel.ResultBean productDetails) {
        final View dialogView = View.inflate(this, R.layout.dialog_book_order_product_details, null);
        final Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle);
        dialog.setContentView(dialogView);

        quantity = 1;
        ImageButton ib_close = dialog.findViewById(R.id.ib_close);
        BannerLayout rv_images = dialog.findViewById(R.id.rv_images);
        TextView tv_productname = dialog.findViewById(R.id.tv_productname);
        TextView tv_productprice = dialog.findViewById(R.id.tv_productprice);
        TextView tv_quantity = dialog.findViewById(R.id.tv_quantity);
        TextView tv_productinfo = dialog.findViewById(R.id.tv_productinfo);
        TextView tv_total_price = dialog.findViewById(R.id.tv_total_price);
        TextView tv_saved = dialog.findViewById(R.id.tv_saved);
        ImageButton imv_substract = dialog.findViewById(R.id.imv_substract);
        ImageButton imv_add = dialog.findViewById(R.id.imv_add);
        Button btn_addtocart = dialog.findViewById(R.id.btn_addtocart);
        LinearLayout ll_description = dialog.findViewById(R.id.ll_description);

        if (productDetails.getProduct_images().size() == 0)
            rv_images.setVisibility(View.GONE);
        else {
            ArrayList<String> productsList = new ArrayList<>();
            for (int i = 0; i < productDetails.getProduct_images().size(); i++) {
                productsList.add(IMAGE_LINK + "product/" + productDetails.getProduct_images().get(i));
            }

            OfferRecyclerBannerAdapter webBannerAdapter = new OfferRecyclerBannerAdapter(this, productsList);
            rv_images.setAdapter(webBannerAdapter);
        }

        tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));
        tv_productname.setText(productDetails.getName());
        tv_productprice.setText("₹" + productDetails.getSelling_price() + " / " + productDetails.getUnit_of_measure());
        tv_productinfo.setText(productDetails.getDescription());

        if (tv_productinfo.getText().toString().trim().equals(""))
            ll_description.setVisibility(View.GONE);

        int maxRetailPrice = (int) Float.parseFloat(productDetails.getMax_retail_price());
        sellingPrice = (int) Float.parseFloat(productDetails.getSelling_price());
        int savedAmount = maxRetailPrice - sellingPrice;

        if (savedAmount <= 0) {
            applicablePrice = sellingPrice;
            tv_saved.setVisibility(View.GONE);
            tv_total_price.setText(Html.fromHtml("₹ " + sellingPrice));
        } else {
            applicablePrice = savedAmount;
            tv_saved.setText(Html.fromHtml("<strike>₹ " + maxRetailPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));
            tv_total_price.setText(Html.fromHtml("₹ " + sellingPrice));
        }

        imv_substract.setOnClickListener(v -> {
            if (quantity == 1) {
                return;
            }
            quantity = quantity - 1;
            tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));

            if (savedAmount <= 0) {
                applicablePrice = maxRetailPrice * quantity;
            } else {
                int earlybirdPrice = sellingPrice * quantity;
                int actualPrice = maxRetailPrice * quantity;
                applicablePrice = earlybirdPrice;
                int savedAmount1 = actualPrice - applicablePrice;
                tv_saved.setText(Html.fromHtml("<strike>₹ " + actualPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount1 + "</i></font>"));
            }

            tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));
        });

        imv_add.setOnClickListener(v -> {
            quantity = quantity + 1;
            tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));

            if (savedAmount <= 0) {
                applicablePrice = maxRetailPrice * quantity;
            } else {
                int earlybirdPrice = sellingPrice * quantity;
                int actualPrice = maxRetailPrice * quantity;
                applicablePrice = earlybirdPrice;
                int savedAmount12 = actualPrice - applicablePrice;
                tv_saved.setText(Html.fromHtml("<strike>₹ " + actualPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount12 + "</i></font>"));
            }

            tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));
        });

        btn_addtocart.setOnClickListener(v -> {
            findValidOrderId(productDetails, quantity);
        });

        ib_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void findValidOrderId(BookOrderProductsListModel.ResultBean productDetails, int quantity) {
        if (ordersList.size() == 0) {
            addOrderJsonCreation(productDetails, quantity);
        } else {

            boolean isPendingOrderAvailable = false;

            for (BookOrderGetMyOrdersModel.ResultBean orderDetails : ordersList) {
                if (orderDetails.getStatus_details().size() == 1) {
                    if (orderDetails.getStatus_details().get(0).getStatus().equals("1")) {
                        isPendingOrderAvailable = true;
                        if (!orderDetails.getOwner_business_id().equals(businessOwnerId)) {
                            cancelOtherBusinessOwnerOrderDialog(orderDetails);
                            break;
                        } else if (orderDetails.getOwner_business_id().equals(businessOwnerId)) {
                            updateOrderWithNewProduct(productDetails, orderDetails, quantity);
                            break;
                        }
                    }
                }
            }

            if (!isPendingOrderAvailable) {
                addOrderJsonCreation(productDetails, quantity);
            }
        }
    }

    private void addOrderJsonCreation(BookOrderProductsListModel.ResultBean productDetails, int quantity) {
        JsonObject mainObj = new JsonObject();

        JsonArray productsDetailsArray = new JsonArray();
        JsonArray orderImageJsonArray = new JsonArray();

        JsonObject productObject = new JsonObject();
        productObject.addProperty("product_id", productDetails.getId());
        productObject.addProperty("quantity", String.valueOf(quantity));
        productObject.addProperty("amount", productDetails.getSelling_price());
        productsDetailsArray.add(productObject);

        mainObj.addProperty("type", "addOrder");
        mainObj.addProperty("owner_business_id", businessOwnerId);
        mainObj.addProperty("order_type", "1");
        mainObj.addProperty("order_text", "");
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "1");    // status = 'IN CART'-2
        mainObj.addProperty("purchase_order_type", "1");
        mainObj.addProperty("business_id", "0");
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new AddOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void cancelOtherBusinessOwnerOrderDialog(BookOrderGetMyOrdersModel.ResultBean orderDetails) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("There is already an order pending for " + orderDetails.getOwner_business_name() + ", do you want to cancel that order?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            JsonObject mainObj = new JsonObject();

            mainObj.addProperty("type", "changeOrderStatus");
            mainObj.addProperty("id", orderDetails.getId());
            mainObj.addProperty("status", "7");    //status = 'CANCEL'-7
            mainObj.addProperty("user_id", userId);

            if (Utilities.isNetworkAvailable(context)) {
                new ChangeOrderStatus().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {

        });
        alertDialogBuilder.create().show();
    }

    private void updateOrderWithNewProduct(BookOrderProductsListModel.ResultBean selectedProduct, BookOrderGetMyOrdersModel.ResultBean orderDetails, int quantity) {
        JsonObject mainObj = new JsonObject();

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsToBeAddedList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean orderProduct : orderDetails.getProduct_details()) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    orderProduct.getProduct_id(),
                    orderDetails.getId(),
                    orderProduct.getQuantity(),
                    orderProduct.getCurrent_amount()
            ));
        }

        boolean isProductAlreadyAddedInOrder = false;

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (productsToBeAddedList.get(i).getProduct_id().equals(selectedProduct.getId())) {
                isProductAlreadyAddedInOrder = true;
                productsToBeAddedList.get(i).setQuantity(String.valueOf(quantity));
                productsToBeAddedList.get(i).setAmount(selectedProduct.getSelling_price());
            }
        }

        if (!isProductAlreadyAddedInOrder) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    selectedProduct.getId(),
                    orderDetails.getId(),
                    String.valueOf(quantity),
                    selectedProduct.getSelling_price()
            ));
        }

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (i <= orderDetails.getProduct_details().size() - 1) {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", orderDetails.getProduct_details().get(i).getId());
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

        JsonArray orderImageJsonArray = new JsonArray();

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", orderDetails.getId());
        mainObj.addProperty("order_id", orderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", businessOwnerId);
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

    private class AddOrder extends AsyncTask<String, Void, String> {

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
                        Utilities.showMessage("Product added to cart", context, 1);
                        ordersList = pojoDetails.getResult();
                        showCartCount();
                    } else {
                        Utilities.showMessage(message, context, 3);
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
                        Utilities.showMessage("Product added to cart", context, 1);
                        ordersList = pojoDetails.getResult();
                        showCartCount();
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
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, params[0]);
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
                        Utilities.showMessage("Order cancelled successfully", context, 1);
                        new GetOrders().execute();
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

//private class BookOrderProductsListAdapter extends RecyclerView.Adapter<BookOrderProductsListAdapter.MyViewHolder> {
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.list_row_book_order_products, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
//        int position = holder.getAdapterPosition();
//
//        final BookOrderProductsListModel.ResultBean productDetails = searchedProductsList.get(position);
//
//        final List<MyOffersListModel.ResultBean.DocumentsBean> documentsList = new ArrayList<>();
//        for (int i = 0; i < productDetails.getProduct_images().size(); i++) {
//            documentsList.add(new MyOffersListModel.ResultBean.DocumentsBean(productDetails.getProduct_images().get(i)));
//        }
//        if (documentsList.size() != 0) {
//            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
//            for (int i = 0; i < documentsList.size(); i++) {
//                bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "product/" + documentsList.get(i).getDocument()));
//            }
//
//            OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, bannerList);
//            holder.imageSlider.setSliderAdapter(adapter);
//            holder.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//            holder.imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
//            holder.imageSlider.setIndicatorSelectedColor(Color.WHITE);
//            holder.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
//            holder.imageSlider.setAutoCycle(true);
//            holder.imageSlider.setScrollTimeInSec(10);
//        } else {
//            holder.imageSlider.setVisibility(View.GONE);
//        }
//
//        holder.tv_product_name.setText(productDetails.getName());
//        holder.tv_product_description.setText(productDetails.getRemark());
//
//        float maxRetailPrice = Float.parseFloat(productDetails.getMax_retail_price());
//        float sellingPrice = Float.parseFloat(productDetails.getSelling_price());
////            int maxRetailPrice = 100;
////            int sellingPrice = 70;
//        float savedAmount = maxRetailPrice - sellingPrice;
//
//        if (savedAmount <= 0) {
//            holder.tv_price.setText(Html.fromHtml("<font color=\"#008000\"> <b>₹ " + sellingPrice + " / " + productDetails.getUnit_of_measure() + "</b></font>"));
//            holder.tv_saved_price.setVisibility(View.GONE);
//        } else {
//            holder.tv_price.setText(Html.fromHtml("<font color=\"#008000\"> <b>₹ " + sellingPrice + " / " + productDetails.getUnit_of_measure() + "</b></font>"));
//            holder.tv_saved_price.setText(Html.fromHtml("<strike>₹ " + maxRetailPrice + "</strike> <font color=\"#ff0000\"> <i>Save ₹ " + savedAmount + "</i></font>"));
//        }
//
//        holder.cv_mainlayout.setOnClickListener(v -> {
//            showProductDetailsDialog(productDetails);
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return searchedProductsList.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        private CardView cv_mainlayout;
//        private SliderView imageSlider;
//        private TextView tv_product_name, tv_product_description, tv_price, tv_saved_price;
//
//        public MyViewHolder(@NonNull View view) {
//            super(view);
//            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
//            imageSlider = view.findViewById(R.id.imageSlider);
//            tv_product_name = view.findViewById(R.id.tv_product_name);
//            tv_product_description = view.findViewById(R.id.tv_product_description);
//            tv_price = view.findViewById(R.id.tv_price);
//            tv_saved_price = view.findViewById(R.id.tv_saved_price);
//        }
//    }
//}