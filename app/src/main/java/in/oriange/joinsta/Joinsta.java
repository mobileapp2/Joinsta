package in.oriange.joinsta;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class Joinsta extends Application {

    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
