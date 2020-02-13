package in.oriange.joinsta.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;

public class PaymentSuccess_Activity extends Activity {

    private Context context;
    private Button btn_done;
    private TextView tv_orderid, tv_validity, tv_space, tv_whatsapp, tv_textsms, tv_clients, tv_policies;
    private String JSONString, user_id;
    private LottieAnimationView animationView;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        init();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = PaymentSuccess_Activity.this;
        session = new UserSessionManager(context);
        animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();
        tv_orderid = findViewById(R.id.tv_orderid);
        tv_validity = findViewById(R.id.tv_validity);
        tv_space = findViewById(R.id.tv_space);
        tv_whatsapp = findViewById(R.id.tv_whatsapp);
        tv_textsms = findViewById(R.id.tv_textsms);
        tv_clients = findViewById(R.id.tv_clients);
        tv_policies = findViewById(R.id.tv_policies);

        btn_done = findViewById(R.id.btn_done);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDefaults() {

    }

    private void setEventHandler() {
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
