package in.oriange.joinsta.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import in.oriange.joinsta.R;

public class SplashScreen_Activity extends Activity {

    private Context context;
    private int secondsDelayed = 1;
//    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        init();
    }

    private void init() {
        context = SplashScreen_Activity.this;
//        session = new UserSessionManager(context);

        new Handler().postDelayed(new Runnable() {
            public void run() {
//                if (session.isUserLoggedIn()) {
                startActivity(new Intent(context, MainDrawer_Activity.class));
//                } else {
//                    startActivity(new Intent(context, Login_Activity.class));
//                }
                finish();
            }
        }, secondsDelayed * 500);
    }

}
