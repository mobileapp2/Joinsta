package in.oriange.joinsta;

import android.app.Application;
import android.content.Context;

import in.oriange.joinsta.utilities.TypefaceUtil;

public class Joinsta extends Application {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

//        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/ebrima.ttf"); // font from assets
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/WhitneyHTF-Book.ttf"); // font from assets
    }

}
