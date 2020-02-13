package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.oriange.joinsta.R;

public class PaymentSummary_Activity extends AppCompatActivity {

    private Context context;

    private ImageButton imv_substract, imv_add;
    private TextView tv_quantity, tv_total_price, tv_saved;

    int quantity = 2, totalPrice = 1000, actualPrice = 1200, savedAmount = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = PaymentSummary_Activity.this;

        imv_substract = findViewById(R.id.imv_substract);
        imv_add = findViewById(R.id.imv_add);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_saved = findViewById(R.id.tv_saved);
    }

    private void getSessionDetails() {

    }

    private void setDefault() {
        tv_quantity.setText(Html.fromHtml("<font color=\"#616161\"> <b> Quantity - </b></font> <font color=\"#EF6C00\"> <b>" + quantity + "</b></font>"));
//        tv_total_price.setText(Html.fromHtml("<sup><small>₹ </small></sup>" + totalPrice));
        tv_total_price.setText(Html.fromHtml("₹ " + totalPrice));
        tv_saved.setText(Html.fromHtml("MRP: <strike>₹ " + actualPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));

    }

    private void setEventHandler() {

    }

    private void setUpToolbar() {

    }
}
