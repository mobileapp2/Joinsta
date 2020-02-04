package in.oriange.joinsta.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashScreen_Activity extends Activity {

    private Context context;
    private UserSessionManager session;

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
        session = new UserSessionManager(context);

        if (Utilities.isNetworkAvailable(context)) {
            new CheckVersion().execute();
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_layout_error, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setView(promptView);

            LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
            TextView tv_title = promptView.findViewById(R.id.tv_title);
            Button btn_ok = promptView.findViewById(R.id.btn_ok);

            animation_view.playAnimation();
            tv_title.setText("Please check your internet connection");
            btn_ok.setText("Retry");
            alertDialogBuilder.setCancelable(false);
            final AlertDialog alertD = alertDialogBuilder.create();

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertD.dismiss();
                    startActivity(new Intent(context, MainDrawer_Activity.class)
                            .putExtra("startOrigin", 0));
                    finish();
                }
            });

            alertD.show();
        }
    }

    public class CheckVersion extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverResponse = "";
            try {

                JsonObject obj = new JsonObject();
                obj.addProperty("type", "getversiondetails");

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .writeTimeout(5, TimeUnit.MINUTES)
                        .readTimeout(5, TimeUnit.MINUTES)
                        .build();

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, obj.toString());
                Request request = new Request.Builder()
                        .url(ApplicationConstants.VERSIONAPI)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                serverResponse = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serverResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (!result.equals("")) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                    float versionName = 0;
                    float majorVersion = Float.parseFloat(jsonObject1.getString("major_version"));
                    float minorVersion = Float.parseFloat(jsonObject1.getString("minor_version"));
                    int isMandatory = Integer.parseInt(jsonObject1.getString("is_mandatory"));

                    try {
                        PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionName = Float.parseFloat(pinfo.versionName.trim());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    if ((isMandatory == 1) && (versionName < majorVersion)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertBuilder.setTitle("Update !!!");
//                        alertBuilder.setIcon(R.drawable.icon_launcher);
                        alertBuilder.setMessage("We update the application regularly so we can make it better for you. Get the latest version for all of the available features and improvements.");
                        alertBuilder.setCancelable(false);
                        alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String appPackageName = context.getPackageName();
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                context.startActivity(intent);
                                finish();
                            }
                        });
                        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(context, MainDrawer_Activity.class).putExtra("startOrigin", 0));
                        } else {
                            startActivity(new Intent(context, Login_Activity.class));
                        }
                        finish();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
