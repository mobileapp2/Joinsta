package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.library.banner.BannerLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.OfferRecyclerBannerAdapter;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.view.View.GONE;
import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class ViewEventsPaid_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private ImageButton imv_back, imv_share, imv_edit, imv_delete, imv_message_organizer;
    private BannerLayout rv_images;
    private ReadMoreTextView tv_description;
    private TextView tv_name, tv_type, tv_created_by_name, tv_is_online, tv_time_date, tv_venue, tv_view_on_map,
            tv_message_paid, tv_message_unpaid, tv_organizer_name, tv_remark, tv_total_price, tv_saved, tv_due_date, tv_viewdocs;
    private LinearLayout ll_paid_msg, ll_unpaid_msg;
    private Button btn_buy;
    private CardView cv_description, cv_date_time, cv_venue, cv_message, cv_price, cv_remark, cv_organizer,
            cv_documents, cv_members_status, cv_report_issue;

    private ArrayList<String> imagesList, documentsList;
    private String userId, paymentLink, isAdmin;
    private File file, downloadedDocumentfolder;
    private EventsPaidModel.ResultBean eventDetails;
    private boolean isMyEvent;
    private String shareMessage;
    private ArrayList<Uri> downloadedImagesUriList;
    private int numOfDocuments = 0;
    private int numOfFilesDownloaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_paid);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = ViewEventsPaid_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_back = findViewById(R.id.imv_back);
        imv_share = findViewById(R.id.imv_share);
        imv_edit = findViewById(R.id.imv_edit);
        imv_delete = findViewById(R.id.imv_delete);
        imv_message_organizer = findViewById(R.id.imv_message_organizer);

        tv_name = findViewById(R.id.tv_name);
        tv_type = findViewById(R.id.tv_type);
        tv_created_by_name = findViewById(R.id.tv_created_by_name);
        tv_is_online = findViewById(R.id.tv_is_online);
        tv_description = findViewById(R.id.tv_description);
        tv_time_date = findViewById(R.id.tv_time_date);
        tv_venue = findViewById(R.id.tv_venue);
        tv_view_on_map = findViewById(R.id.tv_view_on_map);
        tv_remark = findViewById(R.id.tv_remark);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_saved = findViewById(R.id.tv_saved);
        tv_due_date = findViewById(R.id.tv_due_date);
        tv_message_paid = findViewById(R.id.tv_message_paid);
        tv_message_unpaid = findViewById(R.id.tv_message_unpaid);
        tv_organizer_name = findViewById(R.id.tv_organizer_name);
        tv_viewdocs = findViewById(R.id.tv_viewdocs);


        ll_paid_msg = findViewById(R.id.ll_paid_msg);
        ll_unpaid_msg = findViewById(R.id.ll_unpaid_msg);

        btn_buy = findViewById(R.id.btn_buy);

        rv_images = findViewById(R.id.rv_images);

        cv_description = findViewById(R.id.cv_description);
        cv_date_time = findViewById(R.id.cv_date_time);
        cv_venue = findViewById(R.id.cv_venue);
        cv_remark = findViewById(R.id.cv_remark);
        cv_organizer = findViewById(R.id.cv_organizer);
        cv_documents = findViewById(R.id.cv_documents);
        cv_message = findViewById(R.id.cv_message);
        cv_price = findViewById(R.id.cv_price);
        cv_members_status = findViewById(R.id.cv_members_status);
        cv_report_issue = findViewById(R.id.cv_report_issue);

        imagesList = new ArrayList<>();
        documentsList = new ArrayList<>();

        downloadedDocumentfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Events");
        if (!downloadedDocumentfolder.exists())
            downloadedDocumentfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
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
        isMyEvent = getIntent().getBooleanExtra("isMyEvent", false);
        isAdmin = getIntent().getStringExtra("isAdmin");

        tv_name.setText(eventDetails.getEvent_code() + " - " + eventDetails.getName());

//        if (eventDetails.getIs_online_events().equals("1"))
//            tv_is_online.setVisibility(View.VISIBLE);
//        else
        tv_is_online.setVisibility(GONE);

        if (eventDetails.getCreated_by_name().equals(""))
            tv_created_by_name.setVisibility(View.GONE);
        else
            tv_created_by_name.setText("Created by - " + eventDetails.getCreated_by_name());

        tv_venue.setText(eventDetails.getVenue_address());

        tv_time_date.setText(eventDetails.getDateTime());

        if (eventDetails.getDescription().equals(""))
            cv_description.setVisibility(GONE);
        else
            tv_description.setText(eventDetails.getDescription());

        tv_type.setText("This is " + eventDetails.getEvent_type_name() + " event");

        if (!eventDetails.getOrganizer_name().isEmpty() && !eventDetails.getMobile().isEmpty()) {
            tv_organizer_name.setText(eventDetails.getOrganizer_name());
        } else if (!eventDetails.getOrganizer_name().isEmpty() && eventDetails.getMobile().isEmpty()) {
            tv_organizer_name.setText(eventDetails.getOrganizer_name());
            imv_message_organizer.setVisibility(View.GONE);
        } else if (eventDetails.getOrganizer_name().isEmpty() && !eventDetails.getMobile().isEmpty()) {
            tv_organizer_name.setText(eventDetails.getMobile());
        } else if (eventDetails.getOrganizer_name().isEmpty() && eventDetails.getMobile().isEmpty()) {
            cv_organizer.setVisibility(View.GONE);
        }

        if (eventDetails.getRemark().equals(""))
            cv_remark.setVisibility(GONE);
        else
            tv_remark.setText(eventDetails.getRemark());

        if (isAdmin.equals("1")) {
            if (eventDetails.getMessage_for_paidmember().equals("") && eventDetails.getMessage_for_unpaidmember().equals("")) {
                cv_message.setVisibility(GONE);
            } else {

                if (!eventDetails.getMessage_for_paidmember().equals(""))
                    tv_message_paid.setText(eventDetails.getMessage_for_paidmember());
                else
                    ll_paid_msg.setVisibility(GONE);

                if (!eventDetails.getMessage_for_unpaidmember().equals(""))
                    tv_message_unpaid.setText(eventDetails.getMessage_for_unpaidmember());
                else
                    ll_unpaid_msg.setVisibility(GONE);
            }
        } else {
            cv_message.setVisibility(GONE);
        }

        if (!isMyEvent)
            if (eventDetails.getIs_early_payment_applicable().equals("1")) {
                tv_saved.setVisibility(View.VISIBLE);

                int actualEarlybirdPrice = Integer.parseInt(eventDetails.getEarlybird_price());
                int actualNormalPrice = Integer.parseInt(eventDetails.getNormal_price());

                int savedAmount = actualNormalPrice - actualEarlybirdPrice;

                tv_saved.setText(Html.fromHtml("<strike>₹ " + actualNormalPrice + "</strike> <font color=\"#ff0000\"> <i>Save ₹ " + savedAmount + "</i></font>"));
                tv_total_price.setText(Html.fromHtml("₹ " + actualEarlybirdPrice));
                tv_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getEarlybird_price_duedate()));

            } else {
                tv_saved.setVisibility(View.GONE);

                tv_total_price.setText(Html.fromHtml("₹ " + Integer.parseInt(eventDetails.getNormal_price())));
                tv_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getNormal_price_duedate()));
            }
        else
            cv_price.setVisibility(View.GONE);

        if (!eventDetails.getCreated_by().equals(userId)) {
            imv_edit.setVisibility(GONE);
        }

        List<EventsPaidModel.ResultBean.DocumentsBean> docList = eventDetails.getDocuments();

        if (docList.size() == 0) {
            rv_images.setVisibility(GONE);
            cv_documents.setVisibility(GONE);
        } else {
            for (EventsPaidModel.ResultBean.DocumentsBean documentsBean : docList) {
                if (documentsBean.getDocument_type().equalsIgnoreCase("invitationdocument")) {
                    documentsList.add(IMAGE_LINK + "events/invitation_doc/" + documentsBean.getDocument_path());
                } else if (documentsBean.getDocument_type().equalsIgnoreCase("invitationimage")) {
                    imagesList.add(IMAGE_LINK + "events/invitation_image/" + documentsBean.getDocument_path());
                }
            }
        }

        if (imagesList.size() == 0)
            rv_images.setVisibility(GONE);
        else {
            OfferRecyclerBannerAdapter webBannerAdapter = new OfferRecyclerBannerAdapter(this, imagesList);
            rv_images.setAdapter(webBannerAdapter);
        }

        if (documentsList.size() == 0)
            cv_documents.setVisibility(View.GONE);
        else {
            if (documentsList.size() == 1) {
                tv_viewdocs.setText(documentsList.size() + " Document Attached");
            } else {
                tv_viewdocs.setText(documentsList.size() + " Documents Attached");
            }
            tv_viewdocs.setPaintFlags(tv_viewdocs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        if (isMyEvent || eventDetails.getPayment_status().equalsIgnoreCase("paid")
                || eventDetails.getPayment_status().equalsIgnoreCase("pay_at_counter")) {
            btn_buy.setVisibility(GONE);
        }

        if (isMyEvent) {
            imv_edit.setVisibility(GONE);
            imv_delete.setVisibility(GONE);
        }

        if (isAdmin.equals("0")) {
            cv_members_status.setVisibility(GONE);
        }
    }

    private void setEventHandler() {

        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesList.size() != 0) {
                    numOfDocuments = imagesList.size();
                    shareMessage = getShareMessage();
                    downloadedImagesUriList = new ArrayList<>();
                    numOfFilesDownloaded = 0;
                    for (int i = 0; i < imagesList.size(); i++) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DownloadDocumentForShare().execute(imagesList.get(i));
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                } else {
                    String shareMessage = getShareMessage();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });

        imv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (eventDetails.getIs_atleast_one_paymentdone().equals("0")) {
                context.startActivity(new Intent(context, EditEventsPaid_Activity.class).putExtra("eventDetails", eventDetails));
                finish();
//                } else if (eventDetails.getIs_atleast_one_paymentdone().equals("1")) {
//                    Utilities.showMessage("You cannot edit this event details", context, 2);
//                }
            }
        });

        imv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventDetails.getCreated_by().equals(userId)) {
                    if (eventDetails.getIs_atleast_one_paymentdone().equals("1")) {
                        Utilities.showMessage("You cannot delete this event as payments for the events are started", context, 2);
                        return;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this event?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            if (eventDetails.getCreated_by().equals(userId)) {
                                if (eventDetails.getIs_atleast_one_paymentdone().equals("0")) {
                                    new DeletePaidEvent().execute();
                                } else if (eventDetails.getIs_atleast_one_paymentdone().equals("1")) {
                                    Utilities.showMessage("You cannot delete this event as payments for the events are started", context, 2);
                                }
                            } else {
                                new UserSpecificDeleteEvent().execute();
                            }
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentOptionsDialog();
            }
        });

        imv_message_organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_message, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setView(promptView);

                final EditText edt_text = promptView.findViewById(R.id.edt_text);

                alertDialogBuilder.setPositiveButton("PROCEED", null);

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt_text.getText().toString().trim().isEmpty()) {
                            Utilities.showMessage("Please enter your message", context, 2);
                            return;
                        }

                        Uri uri = Uri.parse("smsto:" + eventDetails.getMobile());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra("sms_body", edt_text.getText().toString());
                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
            }
        });

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

        cv_members_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EventPaidMemberStatus_Activity_v2.class)
                        .putExtra("eventId", eventDetails.getid()));
            }
        });

        cv_report_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddEventReportIssue_Activity.class)
                        .putExtra("eventId", eventDetails.getid())
                        .putExtra("eventCategoryId", "2"));  // Paid Event Category Id
            }
        });

        tv_viewdocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentsList();
            }
        });

    }

    private void showDocumentsList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Document List");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row_ellipsize);

        for (int i = 0; i < documentsList.size(); i++) {
            arrayAdapter.add(documentsList.get(i).substring(documentsList.get(i).lastIndexOf('/') + 1));
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context))
                    new DownloadDocument().execute(documentsList.get(which));
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
        builderSingle.show();
    }

    private void showPaymentOptionsDialog() {
        List<EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean> paymentModeList = new ArrayList<>();
        paymentModeList.add(new EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean("Online", "online", "", "0", false));
        paymentModeList.add(new EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean("Offline", "offline", "", "0", false));
        paymentModeList.add(new EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean("Payment link", "paymentlink", "", "0", false));
        paymentModeList.add(new EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean("Pay at counter", "pay_at_counter", "", "0", false));

        for (EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean selectedModes : eventDetails.getPaidevents_paymentoptions()) {
            for (int i = 0; i < paymentModeList.size(); i++) {
                if (selectedModes.getPayment_mode().equals(paymentModeList.get(i).getPayment_mode())) {
                    paymentModeList.get(i).setChecked(true);
                    paymentModeList.get(i).setPayment_link(selectedModes.getPayment_link());
                }
            }
        }

        final List<EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean> selectedPaymentModeList = new ArrayList<>();
        for (int i = 0; i < paymentModeList.size(); i++) {
            if (paymentModeList.get(i).isChecked())
                selectedPaymentModeList.add(paymentModeList.get(i));
        }

        for (int i = 0; i < paymentModeList.size(); i++) {
            if (paymentModeList.get(i).getPayment_mode().equals("paymentlink")) {
                paymentLink = paymentModeList.get(i).getPayment_link();
                break;
            }
        }


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Payment Mode");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < selectedPaymentModeList.size(); i++) {
            arrayAdapter.add(String.valueOf(selectedPaymentModeList.get(i).getPayment_mode_name()));
        }

        builderSingle.setNegativeButton(
                "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedPaymentModeList.get(which).getPayment_mode()) {
                    case "online":
                        startActivity(new Intent(context, PaymentSummary_Activity.class)
                                .putExtra("eventDetails", eventDetails));
                        finish();
                        break;
                    case "offline":
                        startActivity(new Intent(context, OfflinePayment_Activity.class)
                                .putExtra("eventId", eventDetails.getid()));
                        finish();
                        break;
                    case "paymentlink":
                        String url = paymentLink;

                        if (!url.startsWith("https://") || !url.startsWith("http://")) {
                            url = "http://" + url;
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case "pay_at_counter":
                        JsonObject mainObj = new JsonObject();
                        mainObj.addProperty("type", "addEventPaymentDetails");
                        mainObj.addProperty("payment_mode", "pay_at_counter");
                        mainObj.addProperty("order_status", "success");
                        mainObj.addProperty("order_gateway", "");
                        mainObj.addProperty("gateway_configuration_id", "0");
                        mainObj.addProperty("transaction_id", "");
                        mainObj.addProperty("transaction_date", "");
                        mainObj.addProperty("paid_to", "");
                        mainObj.addProperty("event_id", eventDetails.getid());
                        mainObj.addProperty("user_id", userId);
                        mainObj.addProperty("created_by", userId);
                        mainObj.addProperty("quantity", "");
                        mainObj.addProperty("is_early_bird_availed", "0");

                        if (Utilities.isNetworkAvailable(context)) {
                            new AddEventPaymentDetails().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private class DeletePaidEvent extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "DeletePaidEvent");
            obj.addProperty("paid_event_id", eventDetails.getid());
            res = APICall.JSONAPICall(ApplicationConstants.PAIDEVENTSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("Events_Fragment")
                                .putExtra("eventCategoryId", "2"));

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

    private class UserSpecificDeleteEvent extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "UserSpecificEvent_Delete");
            obj.addProperty("event_id", eventDetails.getid());
            obj.addProperty("event_category_id", "2");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.USERSPECIFICDELETEAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("Events_Fragment")
                                .putExtra("eventCategoryId", "2"));

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

    private class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Document");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;

            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocumentfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            pd.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

    private class DownloadDocumentForShare extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        ProgressDialog pd;
        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Document");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocumentfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            pd.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            super.onPostExecute(aBoolean);
            Uri uri = Uri.parse("file:///" + file);
            downloadedImagesUriList.add(uri);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            numOfFilesDownloaded = numOfFilesDownloaded + 1;

            if (numOfFilesDownloaded == numOfDocuments) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, downloadedImagesUriList);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }

    private class AddEventPaymentDetails extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.PAYMENTTRACKAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsPaid_Fragment"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Payment status saved successfully");
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

    private String getShareMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(eventDetails.getEvent_code() + " - " + eventDetails.getName() + "\n");

        if (eventDetails.getIs_online_events().equals("1"))
            sb.append("This is an online event" + "\n");

        sb.append("Venue - " + eventDetails.getVenue_address() + "\n");

        sb.append("Date and Time - " + eventDetails.getDateTime() + "\n");

        if (!eventDetails.getDescription().equals(""))
            sb.append("Description - " + eventDetails.getDescription() + "\n");

        sb.append("This is " + eventDetails.getEvent_type_name() + " event" + "\n");

        sb.append("Price - ₹ " + eventDetails.getNormal_price() + " (₹ " + eventDetails.getEarlybird_price() + " for early birds)" + "\n");

        sb.append("Early birds due date - " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getEarlybird_price_duedate()) + "\n");

        sb.append("Organizer - " + eventDetails.getOrganizer_name() + " (" + eventDetails.getMobile() + ")" + "\n");

        if (!eventDetails.getMessage_for_paidmember().equals("") || !eventDetails.getMessage_for_unpaidmember().equals("")) {

            if (!eventDetails.getMessage_for_paidmember().equals(""))
                sb.append("Message for Paid Members - " + eventDetails.getMessage_for_paidmember() + "\n");

            if (!eventDetails.getMessage_for_unpaidmember().equals(""))
                sb.append("Message for Unpaid Members - " + eventDetails.getMessage_for_unpaidmember() + "\n");
        }

        if (!eventDetails.getRemark().equals(""))
            sb.append("Remark - " + eventDetails.getRemark() + "\n");

        if (!eventDetails.getVenue_latitude().trim().isEmpty() && !eventDetails.getVenue_longitude().trim().isEmpty())
            sb.append("http://maps.google.com/maps?saddr=&daddr=" + eventDetails.getVenue_latitude() + "," + eventDetails.getVenue_longitude());

        return sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;
    }

}
