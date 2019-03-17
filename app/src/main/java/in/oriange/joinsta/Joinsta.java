package in.oriange.joinsta;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

public class Joinsta extends Application {

    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
