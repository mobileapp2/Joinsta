package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.EventsFreeModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class ViewEventsFree_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private ImageButton imv_back, imv_share, imv_edit, imv_delete;
    private TextView tv_name, tv_type, tv_is_online, tv_description, tv_time_date, tv_venue, tv_view_on_map, tv_confirmation,
            tv_remark;
    private CardView cv_description, cv_date_time, cv_venue, cv_confirmation, cv_remaark;

    private String userId;
    private EventsFreeModel.ResultBean eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_free);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = ViewEventsFree_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_back = findViewById(R.id.imv_back);
        imv_share = findViewById(R.id.imv_share);
        imv_edit = findViewById(R.id.imv_edit);
        imv_delete = findViewById(R.id.imv_delete);

        tv_name = findViewById(R.id.tv_name);
        tv_type = findViewById(R.id.tv_type);
        tv_is_online = findViewById(R.id.tv_is_online);
        tv_description = findViewById(R.id.tv_description);
        tv_time_date = findViewById(R.id.tv_time_date);
        tv_venue = findViewById(R.id.tv_venue);
        tv_view_on_map = findViewById(R.id.tv_view_on_map);
        tv_confirmation = findViewById(R.id.tv_confirmation);
        tv_remark = findViewById(R.id.tv_remark);

        cv_description = findViewById(R.id.cv_description);
        cv_date_time = findViewById(R.id.cv_date_time);
        cv_venue = findViewById(R.id.cv_venue);
        cv_confirmation = findViewById(R.id.cv_confirmation);
        cv_remaark = findViewById(R.id.cv_remaark);
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
        eventDetails = (EventsFreeModel.ResultBean) getIntent().getSerializableExtra("eventDetails");

        tv_name.setText(eventDetails.getEvent_code() + " - " + eventDetails.getName());

        tv_type.setText("This is a " + eventDetails.getEvent_type_name() + " event");

        if (eventDetails.getIs_online_event().equals("1"))
            tv_is_online.setVisibility(View.VISIBLE);
        else
            tv_is_online.setVisibility(View.GONE);

        if (eventDetails.getDescription().equals(""))
            cv_description.setVisibility(View.GONE);
        else
            tv_description.setText(eventDetails.getDescription());

        tv_time_date.setText(eventDetails.getDateTime());

        if (eventDetails.getVenue_address().equals("") && (eventDetails.getVenue_latitude().equals("") || eventDetails.getVenue_longitude().equals("")))
            cv_venue.setVisibility(View.GONE);
        else {
            tv_venue.setText(eventDetails.getVenue_address());
            if (eventDetails.getVenue_latitude().equals("") || eventDetails.getVenue_longitude().equals(""))
                tv_view_on_map.setVisibility(View.GONE);
            else
                tv_view_on_map.setVisibility(View.VISIBLE);
        }

        if (eventDetails.getIs_confirmation_required().equals("1"))
            cv_confirmation.setVisibility(View.VISIBLE);
        else
            cv_confirmation.setVisibility(View.GONE);

        if (eventDetails.getRemark().equals(""))
            cv_remaark.setVisibility(View.GONE);
        else
            tv_remark.setText(eventDetails.getRemark());

        if (!eventDetails.getCreated_by().equals(userId)) {
            imv_share.setVisibility(View.GONE);
            imv_edit.setVisibility(View.GONE);
            imv_delete.setVisibility(View.GONE);
        }

    }

    private void setEventHandler() {
        tv_view_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventDetails.getVenue_latitude().trim().isEmpty() || eventDetails.getVenue_longitude().trim().isEmpty()) {
                    Utilities.showMessage("Location not added", context, 2);
                    return;
                }

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + eventDetails.getVenue_latitude() + "," + eventDetails.getVenue_longitude()));
                startActivity(intent);
            }
        });

        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditEventsFree_Activity.class)
                        .putExtra("eventDetails", eventDetails));
                finish();
            }
        });

        imv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new DeleteFreeEvent().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

    }

    private class DeleteFreeEvent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "DeleteFreeEvent");
            obj.addProperty("free_event_id", eventDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.FREEEVENTSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsFree_Fragment"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Event details deleted successfully");
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
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
