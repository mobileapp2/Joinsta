package in.oriange.joinsta.paytm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.oriange.joinsta.utilities.ApplicationConstants.MID;
import static in.oriange.joinsta.utilities.ApplicationConstants.PAYTMURL;
import static in.oriange.joinsta.utilities.ApplicationConstants.TRANSSTATUSURL;

public class PaytmPayment_Activity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private Context context;
    private String custid = "", orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        context = PaytmPayment_Activity.this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        orderId = getIntent().getStringExtra("orderid");
        custid = getIntent().getStringExtra("custid");

        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(PaytmPayment_Activity.this);


        String PAYTMVERIFYURL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID" + orderId;
        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(PaytmPayment_Activity.this);
            String param =
                    "MID=" + MID +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custid +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + getIntent().getStringExtra("amount") + "&WEBSITE=APPSTAGING" +
                            "&CALLBACK_URL=" + PAYTMVERIFYURL + "&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(PAYTMURL, "POST", param);
            // yaha per PaytmPayment_Activity ke saht order id or status receive hoga..
            Log.e("CheckSum result >>", jsonObject.toString());
            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {

                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e("CheckSum result >>", CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

//            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            PaytmPGService Service = PaytmPGService.getProductionService();

            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", MID); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", getIntent().getStringExtra("amount"));
            paramMap.put("WEBSITE", "APPSTAGING");
            paramMap.put("CALLBACK_URL", PAYTMVERIFYURL);
            //paramMap.put( "EMAIL" , "");   // no need
            // paramMap.put( "MOBILE_NO" , "");  // no need
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("PaytmPayment_Activity ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(PaytmPayment_Activity.this, true, true,
                    PaytmPayment_Activity.this);
        }

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("PaytmPayment_Activity ", " respon true " + bundle.toString());
        String response = bundle.toString();
        response = response.replace("Bundle", "");

        if (response.contains("TXN_SUCCESS")) {
            new TransactionStatusAPI().execute(MID, orderId);
        } else {
            Utilities.showMessage("Paytm payment failed, please try again", context, 3);
            finish();
        }

    }

    private class TransactionStatusAPI extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("MID", params[0])
                    .add("ORDERID", params[1])
                    .build();
            Request request = new Request.Builder()
                    .url(TRANSSTATUSURL)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            try {
                JSONObject object1 = new JSONObject(result);
                String string = object1.getString("OUTPUT");
                JSONObject object = new JSONObject(string);
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("type", "addEventPaymentDetails");
                jsonObject.addProperty("payment_mode", "online");
                jsonObject.addProperty("order_gateway", "paytm");
                jsonObject.addProperty("order_status", "Success");
                jsonObject.addProperty("gateway_configuration_id", getIntent().getStringExtra("gateway_configuration_id"));
                jsonObject.addProperty("transaction_id", object.getString("TXNID"));
                jsonObject.addProperty("transaction_date", object.getString("TXNDATE"));
                jsonObject.addProperty("paid_to", "");
                jsonObject.addProperty("event_id", getIntent().getStringExtra("event_id"));
                jsonObject.addProperty("user_id", getIntent().getStringExtra("user_id"));
                jsonObject.addProperty("created_by", getIntent().getStringExtra("created_by"));
                jsonObject.addProperty("quantity", getIntent().getStringExtra("quantity"));
                jsonObject.addProperty("transaction_status", object.getString("STATUS"));

                jsonObject.addProperty("CHECKSUMHASH", object1.getString("CHECKSUMHASH"));
                jsonObject.addProperty("BANKNAME", object.getString("BANKNAME"));
                jsonObject.addProperty("ORDERID", object.getString("ORDERID"));
                jsonObject.addProperty("MID", object.getString("MID"));
                jsonObject.addProperty("TXNID", object.getString("TXNID"));
                jsonObject.addProperty("PAYMENTMODE", object.getString("PAYMENTMODE"));
                jsonObject.addProperty("BANKTXNID", object.getString("BANKTXNID"));
                jsonObject.addProperty("CURRENCY", "INR");
                jsonObject.addProperty("GATEWAYNAME", object.getString("GATEWAYNAME"));
                jsonObject.addProperty("RESPMSG", object.getString("RESPMSG"));
                jsonObject.addProperty("amount", getIntent().getStringExtra("amount"));
                new BuyPlan().execute(jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public class BuyPlan extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

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
            res = APICall.JSONAPICall(ApplicationConstants.PAYMENTTRACKAPI, params[0]);
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
                        if (Utilities.isNetworkAvailable(context)) {

                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));
                            LayoutInflater layoutInflater = LayoutInflater.from(context);
                            View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            alertDialogBuilder.setView(promptView);

                            LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                            TextView tv_title = promptView.findViewById(R.id.tv_title);
                            Button btn_ok = promptView.findViewById(R.id.btn_ok);

                            animation_view.playAnimation();
                            tv_title.setText("Payment done successfully");
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
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }


                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("PaytmPayment_Activity ", " ui fail respon  " + s);
        finish();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("PaytmPayment_Activity ", " error loading pagerespon true " + s + "  s1 " + s1);
        finish();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("PaytmPayment_Activity ", " cancel call back respon  ");
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("PaytmPayment_Activity ", "  transaction cancel ");
        finish();
    }

}
