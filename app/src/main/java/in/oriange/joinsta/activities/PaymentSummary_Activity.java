package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONObject;

import in.oriange.joinsta.R;
import in.oriange.joinsta.ccavenue.AvenuesParams;
import in.oriange.joinsta.ccavenue.CCAvenueWebView_Activity;
import in.oriange.joinsta.ccavenue.ServiceUtility;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.paytm.PaytmPayment_Activity;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;

public class PaymentSummary_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;

    private ImageButton imv_back, imv_substract, imv_add;
    private TextView tv_quantity, tv_total_price, tv_saved;
    private CardView cv_ccavenue, cv_paytm;

    private int quantity = 1, applicablePrice = 0, actualNormalPrice = 0, actualEarlybirdPrice = 0;
    private String userId, randomNum, isEarlyPaymentApplicable, isNormalPaymentApplicable;

    private EventsPaidModel.ResultBean eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = PaymentSummary_Activity.this;
        session = new UserSessionManager(context);

        imv_back = findViewById(R.id.imv_back);
        imv_substract = findViewById(R.id.imv_substract);
        imv_add = findViewById(R.id.imv_add);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_saved = findViewById(R.id.tv_saved);
        cv_ccavenue = findViewById(R.id.cv_ccavenue);
        cv_paytm = findViewById(R.id.cv_paytm);
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
        eventDetails = (EventsPaidModel.ResultBean) getIntent().getSerializableExtra("eventDetails");
        isEarlyPaymentApplicable = eventDetails.getIs_early_payment_applicable();
        isNormalPaymentApplicable = eventDetails.getIs_normal_payment_applicable();

        isEarlyPaymentApplicable = "1";
        isNormalPaymentApplicable = "0";

        tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));

        if (isEarlyPaymentApplicable.equals("1")) {
            tv_saved.setVisibility(View.VISIBLE);

            actualEarlybirdPrice = Integer.parseInt(eventDetails.getEarlybird_price());
            actualNormalPrice = Integer.parseInt(eventDetails.getNormal_price());

            applicablePrice = actualNormalPrice - actualEarlybirdPrice;
            int savedAmount = actualNormalPrice - applicablePrice;

            tv_saved.setText(Html.fromHtml("<strike>₹ " + actualEarlybirdPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));
            tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));

        } else {
            tv_saved.setVisibility(View.GONE);

            actualNormalPrice = Integer.parseInt(eventDetails.getNormal_price());

            applicablePrice = actualNormalPrice;

            tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));
        }

    }

    private void setEventHandler() {
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imv_substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity == 1) {
                    return;
                }
                quantity = quantity - 1;

                if (isEarlyPaymentApplicable.equals("1")) {
                    int earlybirdPrice = actualEarlybirdPrice * quantity;
                    int actualPrice = actualNormalPrice * quantity;
                    applicablePrice = actualPrice - earlybirdPrice;
                    int savedAmount = actualPrice - applicablePrice;

                    tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));
                    tv_saved.setText(Html.fromHtml("<strike>₹ " + actualPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));

                } else {
                    applicablePrice = actualNormalPrice * quantity;
                }

                tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));
            }
        });

        imv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;

                if (isEarlyPaymentApplicable.equals("1")) {
                    int earlybirdPrice = actualEarlybirdPrice * quantity;
                    int actualPrice = actualNormalPrice * quantity;
                    applicablePrice = actualPrice - earlybirdPrice;
                    int savedAmount = actualPrice - applicablePrice;

                    tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));
                    tv_saved.setText(Html.fromHtml("<strike>₹ " + actualPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));

                } else {
                    applicablePrice = actualNormalPrice * quantity;
                }

                tv_total_price.setText(Html.fromHtml("₹ " + applicablePrice));
            }
        });

        cv_ccavenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CCAvenueWebView_Activity.class);
                intent.putExtra(AvenuesParams.ACCESS_CODE, ApplicationConstants.ACCESS_CODE);
                intent.putExtra(AvenuesParams.MERCHANT_ID, ApplicationConstants.MERCHANT_ID);
                intent.putExtra(AvenuesParams.ORDER_ID, randomNum);
                intent.putExtra(AvenuesParams.CURRENCY, ApplicationConstants.CURRENCY);
                intent.putExtra(AvenuesParams.AMOUNT, "1");
                intent.putExtra(AvenuesParams.REDIRECT_URL, ApplicationConstants.REDIRECT_URL);
                intent.putExtra(AvenuesParams.CANCEL_URL, ApplicationConstants.CANCEL_URL);
                intent.putExtra(AvenuesParams.RSA_KEY_URL, ApplicationConstants.RSA_KEY_URL);
                intent.putExtra("amount", String.valueOf(applicablePrice));
                intent.putExtra("quantity", String.valueOf(quantity));
                intent.putExtra("event_id", eventDetails.getid());
                intent.putExtra("gateway_configuration_id", "0");
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });

        cv_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaytmPayment_Activity.class);
                intent.putExtra("orderid", randomNum);
                intent.putExtra("custid", "632541" + userId);
                intent.putExtra("user_id", userId);
                intent.putExtra("amount", String.valueOf(applicablePrice));
                intent.putExtra("quantity", String.valueOf(quantity));
                intent.putExtra("event_id", eventDetails.getid());
                intent.putExtra("gateway_configuration_id", "0");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        randomNum = String.valueOf(ServiceUtility.randInt(0, 9999999));
    }
}
