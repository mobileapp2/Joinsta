package in.oriange.joinsta.ccavenue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;


public class CCAvenueWebView_Activity extends AppCompatActivity {
    Intent mainIntent;
    String encVal;
    String vResponse, user_id;
    private int showWebview = 1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ccavenue_webview);
        mainIntent = getIntent();
        user_id = mainIntent.getStringExtra("user_id");
//get rsa key method
        get_RSA_key(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
    }

    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            LoadingDialog.showLoadingDialog(CCAvenueWebView_Activity.this, "Loading...");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);  //encrypt amount and currency
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            LoadingDialog.cancelLoading();

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    showWebview = 2;
                    // process the html source code to get final status of transaction
                    String status = null;
                    if (html.indexOf("Failure") != -1) {
                        AlertDialog("Transaction Declined!");
                    } else if (html.indexOf("Success") != -1) {

                        Document doc = Jsoup.parse(html);
                        Elements tableElements = doc.select("table");
                        Elements tableRowElements = tableElements.select(":not(thead) tr");
                        ArrayList<HashMap<String, String>> arraylist = new ArrayList();

                        Calendar calendar = Calendar.getInstance();


                        HashMap<String, String> map1 = new HashMap<>();
                        HashMap<String, String> map2 = new HashMap<>();
                        HashMap<String, String> map3 = new HashMap<>();
                        HashMap<String, String> map4 = new HashMap<>();
                        HashMap<String, String> map5 = new HashMap<>();
                        HashMap<String, String> map6 = new HashMap<>();
                        HashMap<String, String> map7 = new HashMap<>();
                        HashMap<String, String> map8 = new HashMap<>();
                        HashMap<String, String> map9 = new HashMap<>();
                        HashMap<String, String> map10 = new HashMap<>();
                        HashMap<String, String> map11 = new HashMap<>();
                        HashMap<String, String> map12 = new HashMap<>();
                        HashMap<String, String> map13 = new HashMap<>();
                        HashMap<String, String> map14 = new HashMap<>();
                        HashMap<String, String> map15 = new HashMap<>();

                        map1.put("key", "type");
                        map1.put("value", "addEventPaymentDetails");
                        arraylist.add(map1);

                        map2.put("key", "payment_mode");
                        map2.put("value", "online");
                        arraylist.add(map2);

                        map3.put("key", "order_gateway");
                        map3.put("value", "ccavenue");
                        arraylist.add(map3);

                        map4.put("key", "gateway_configuration_id");
                        map4.put("value", mainIntent.getStringExtra("gateway_configuration_id"));
                        arraylist.add(map4);

                        map5.put("key", "transaction_id");
                        map5.put("value", "");
                        arraylist.add(map5);

                        map6.put("key", "transaction_date");
                        map6.put("value", calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                        arraylist.add(map6);

                        map7.put("key", "paid_to");
                        map7.put("value", "");
                        arraylist.add(map7);

                        map8.put("key", "event_id");
                        map8.put("value", mainIntent.getStringExtra("event_id"));
                        arraylist.add(map8);

                        map9.put("key", "user_id");
                        map9.put("value", mainIntent.getStringExtra("user_id"));
                        arraylist.add(map9);

                        map10.put("key", "created_by");
                        map10.put("value", mainIntent.getStringExtra("user_id"));
                        arraylist.add(map10);

                        map11.put("key", "quantity");
                        map11.put("value", mainIntent.getStringExtra("quantity"));
                        arraylist.add(map11);

                        map12.put("key", "transaction_status");
                        map12.put("value", "SUCCESS");
                        arraylist.add(map12);

                        map13.put("key", "cc_bank_ref_no");
                        map13.put("value", "");
                        arraylist.add(map13);

                        map14.put("key", "record_status");
                        map14.put("value", "");
                        arraylist.add(map14);

                        map15.put("key", "is_early_bird_availed");
                        map15.put("value", mainIntent.getStringExtra("is_early_bird_availed"));
                        arraylist.add(map15);

                        for (int i = 0; i < tableRowElements.size(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();

                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");

                            map.put("key", rowItems.get(0).text());
                            map.put("value", rowItems.get(1).text());
                            arraylist.add(map);
                        }


                        JsonObject mainObj = new JsonObject();
                        try {
                            for (HashMap<String, String> entry : arraylist) {
                                String myID = entry.get("key");
                                String mySKU = entry.get("value");
                                mainObj.addProperty(myID, mySKU);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Log.i("CCAVENUE", mainObj.toString());

                        if (Utilities.isNetworkAvailable(CCAvenueWebView_Activity.this)) {
                            new BuyPlan().execute(mainObj.toString());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, CCAvenueWebView_Activity.this, 2);
                        }

                    } else if (html.indexOf("Aborted") != -1) {
                        AlertDialog("Transaction Cancelled!");
                    } else {
                        AlertDialog("Status Not Known!");
                    }

                }
            }

            final WebView webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    LoadingDialog.cancelLoading();
                    if (url.indexOf("/ccavResponseHandler.php") != -1) {
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        webview.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LoadingDialog.showLoadingDialog(CCAvenueWebView_Activity.this, "Loading...");
                }
            });


            try {
                String postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8") + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8") + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8") + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8");
                webview.postUrl(ApplicationConstants.TRANS_URL, postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public void get_RSA_key(final String ac, final String od) {
        LoadingDialog.showLoadingDialog(CCAvenueWebView_Activity.this, "Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mainIntent.getStringExtra(AvenuesParams.RSA_KEY_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(CCAvenueWebView_Activity.this,response,Toast.LENGTH_LONG).show();
                        LoadingDialog.cancelLoading();

                        if (response != null && !response.equals("")) {
                            vResponse = response;     ///save retrived rsa key
                            if (vResponse.contains("!ERROR!")) {
                                show_alert(vResponse);
                            } else {
                                new RenderView().execute();   // Calling async task to get display content
                            }
                        } else {
                            show_alert("No response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.cancelLoading();
                        //Toast.makeText(CCAvenueWebView_Activity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AvenuesParams.ACCESS_CODE, ac);
                params.put(AvenuesParams.ORDER_ID, od);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void show_alert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                CCAvenueWebView_Activity.this).create();

        alertDialog.setTitle("Error!!!");
        if (msg.contains("\n"))
            msg = msg.replaceAll("\\\n", "");

        alertDialog.setMessage(msg);


        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        alertDialog.show();
    }

    public class BuyPlan extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CCAvenueWebView_Activity.this, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
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
                        LocalBroadcastManager.getInstance(CCAvenueWebView_Activity.this).sendBroadcast(new Intent("EventsPaid_Fragment"));
                        LayoutInflater layoutInflater = LayoutInflater.from(CCAvenueWebView_Activity.this);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CCAvenueWebView_Activity.this, R.style.CustomDialogTheme);
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

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    private void AlertDialog(String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CCAvenueWebView_Activity.this, R.style.CustomDialogTheme);
        builder.setMessage(status);
        builder.setTitle("Fail");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alertD = builder.create();
        alertD.show();
    }

}

//{"type":"addEventPaymentDetails","payment_mode":"Debit Card","order_gateway":"ccavenue","gateway_configuration_id":"1","transaction_id":"","transaction_date":"2020-1-22","paid_to":"","event_id":"52","user_id":"271","created_by":"271","quantity":"1","transaction_status":"SUCCESS","cc_bank_ref_no":"","record_status":"","order_id":"8604557","tracking_id":"109770480541","bank_ref_no":"805710","order_status":"Success","failure_message":"","card_name":"Visa Debit Card","status_code":"null","status_message":"SUCCESS","currency":"INR","amount":"1.00","billing_name":"Jfjejekdek","billing_address":"Jrejjeiw","billing_city":"Pune","billing_state":"Maharashtra","billing_zip":"411028","billing_country":"India","billing_tel":"8149115089","billing_email":"Priyeshpwar07@email.com","delivery_name":"Jfjejekdek","delivery_address":"Jrejjeiw","delivery_city":"Pune","delivery_state":"Maharashtra","delivery_zip":"411028","delivery_country":"India","delivery_tel":"8149115089","merchant_param1":"","merchant_param2":"","merchant_param3":"","merchant_param4":"","merchant_param5":"","vault":"N","offer_type":"null","offer_code":"null","discount_value":"0.0","mer_amount":"1.00","eci_value":"null","retry":"N","response_code":"0","billing_notes":"","trans_date":"12/02/2020 12:20:25","bin_country":"INDIA"}
