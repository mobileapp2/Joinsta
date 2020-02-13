package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.library.banner.BannerLayout;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

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

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.OfferRecyclerBannerAdapter;
import in.oriange.joinsta.ccavenue.AvenuesParams;
import in.oriange.joinsta.ccavenue.CCAvenueWebView_Activity;
import in.oriange.joinsta.ccavenue.ServiceUtility;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.paytm.PaytmPayment_Activity;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class ViewEventsPaid_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private ImageButton imv_back, imv_share, imv_edit, imv_delete, imv_message_organizer;
    private BannerLayout rv_images;
    private ReadMoreTextView tv_description;
    private TextView tv_name, tv_type, tv_is_online, tv_time_date, tv_venue, tv_view_on_map,
            tv_earlybird_price, tv_earlybird_due_date, tv_normal_price, tv_normal_due_date, tv_message_paid,
            tv_message_unpaid, tv_organizer_name, tv_remark;
    private LinearLayout ll_paid_msg, ll_unpaid_msg;
    private Button btn_buy;
    private RecyclerView rv_documents;
    private CardView cv_description, cv_date_time, cv_venue, cv_get_direction, cv_add_calendar, cv_message,
            cv_price, cv_remark, cv_documents;

    private ArrayList<String> imagesList, documentsList;
    private String userId, randomNum;
    private File file, downloadedDocumentfolder;
    private EventsPaidModel.ResultBean eventDetails;
    private boolean isMyEvent;

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
        tv_is_online = findViewById(R.id.tv_is_online);
        tv_description = findViewById(R.id.tv_description);
        tv_time_date = findViewById(R.id.tv_time_date);
        tv_venue = findViewById(R.id.tv_venue);
        tv_remark = findViewById(R.id.tv_remark);
        tv_earlybird_price = findViewById(R.id.tv_earlybird_price);
        tv_earlybird_due_date = findViewById(R.id.tv_earlybird_due_date);
        tv_normal_price = findViewById(R.id.tv_normal_price);
        tv_normal_due_date = findViewById(R.id.tv_normal_due_date);
        tv_message_paid = findViewById(R.id.tv_message_paid);
        tv_message_unpaid = findViewById(R.id.tv_message_unpaid);
        tv_organizer_name = findViewById(R.id.tv_organizer_name);

        ll_paid_msg = findViewById(R.id.ll_paid_msg);
        ll_unpaid_msg = findViewById(R.id.ll_unpaid_msg);

        btn_buy = findViewById(R.id.btn_buy);

        rv_images = findViewById(R.id.rv_images);
        rv_documents = findViewById(R.id.rv_documents);
        rv_documents.setLayoutManager(new LinearLayoutManager(context));

        cv_description = findViewById(R.id.cv_description);
        cv_date_time = findViewById(R.id.cv_date_time);
        cv_venue = findViewById(R.id.cv_venue);
        cv_remark = findViewById(R.id.cv_remark);
        cv_documents = findViewById(R.id.cv_documents);
        cv_message = findViewById(R.id.cv_message);
        cv_price = findViewById(R.id.cv_price);
        cv_get_direction = findViewById(R.id.cv_get_direction);
        cv_add_calendar = findViewById(R.id.cv_add_calendar);

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

        tv_name.setText(eventDetails.getEvent_code() + " - " + eventDetails.getName());

        if (eventDetails.getIs_online_events().equals("1"))
            tv_is_online.setVisibility(View.VISIBLE);
        else
            tv_is_online.setVisibility(View.GONE);

        tv_venue.setText(eventDetails.getVenue_address());

        tv_time_date.setText(eventDetails.getDateTime());

        if (eventDetails.getDescription().equals(""))
            cv_description.setVisibility(View.GONE);
        else
            tv_description.setText(eventDetails.getDescription());

        tv_type.setText(eventDetails.getEvent_type_name() + " event");

        tv_organizer_name.setText(eventDetails.getOrganizer_name());

        if (eventDetails.getRemark().equals(""))
            cv_remark.setVisibility(View.GONE);
        else
            tv_remark.setText(eventDetails.getRemark());

        if (eventDetails.getMessage_for_paidmember().equals("") && eventDetails.getMessage_for_unpaidmember().equals("")) {
            cv_message.setVisibility(View.GONE);
        } else {

            if (!eventDetails.getMessage_for_paidmember().equals(""))
                tv_message_paid.setText(eventDetails.getMessage_for_paidmember());
            else
                ll_paid_msg.setVisibility(View.GONE);

            if (!eventDetails.getMessage_for_unpaidmember().equals(""))
                tv_message_unpaid.setText(eventDetails.getMessage_for_unpaidmember());
            else
                ll_unpaid_msg.setVisibility(View.GONE);
        }

        tv_earlybird_price.setText("₹ " + eventDetails.getEarlybird_price() + " off");

        tv_earlybird_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getEarlybird_price_duedate()));

        tv_normal_price.setText("₹ " + eventDetails.getNormal_price());

        tv_normal_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getNormal_price_duedate()));

        if (!eventDetails.getCreated_by().equals(userId)) {
            imv_share.setVisibility(View.GONE);
            imv_edit.setVisibility(View.GONE);
            imv_delete.setVisibility(View.GONE);
        }

        List<EventsPaidModel.ResultBean.DocumentsBean> docList = eventDetails.getDocuments();

        if (docList.size() == 0) {
            rv_images.setVisibility(View.GONE);
            cv_documents.setVisibility(View.GONE);
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
            rv_images.setVisibility(View.GONE);
        else {
            OfferRecyclerBannerAdapter webBannerAdapter = new OfferRecyclerBannerAdapter(this, imagesList);
            webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    showImageDialog(imagesList.get(position));
                }
            });
            rv_images.setAdapter(webBannerAdapter);
        }

        if (documentsList.size() == 0)
            cv_documents.setVisibility(View.GONE);
        else
            rv_documents.setAdapter(new DocumentsAdapter());


        if (isMyEvent || eventDetails.getPayment_status().equalsIgnoreCase("unpaid")) {
            btn_buy.setVisibility(View.GONE);
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

            }
        });

        imv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditEventsPaid_Activity.class).putExtra("eventDetails", eventDetails));
                finish();
            }
        });

        imv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this event?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeletePaidEvent().execute();
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

        cv_get_direction.setOnClickListener(new View.OnClickListener() {
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

        cv_add_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showMessage("Coming Soon", context, 2);
            }
        });

    }

    private void showPaymentOptionsDialog() {
        List<MasterModel> paymentModeList = new ArrayList<>();
        paymentModeList.add(new MasterModel("Online", "online", false));
        paymentModeList.add(new MasterModel("Offline", "offline", false));
        paymentModeList.add(new MasterModel("Payment Link", "paymentlink", false));


        for (EventsPaidModel.ResultBean.PaideventsPaymentoptionsBean selectedModes : eventDetails.getPaidevents_paymentoptions()) {
            for (int i = 0; i < paymentModeList.size(); i++) {
                if (selectedModes.getPayment_mode().equals(paymentModeList.get(i).getId())) {
                    paymentModeList.get(i).setChecked(true);
                }
            }
        }

        final List<MasterModel> selectedPaymentModeList = new ArrayList<>();
        for (int i = 0; i < paymentModeList.size(); i++) {
            if (paymentModeList.get(i).isChecked())
                selectedPaymentModeList.add(paymentModeList.get(i));
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Payment Mode");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < selectedPaymentModeList.size(); i++) {
            arrayAdapter.add(String.valueOf(selectedPaymentModeList.get(i).getName()));
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
                if (selectedPaymentModeList.get(which).getId().equals("online")) {
                    showOnlinePaymentDialog();
                } else if (selectedPaymentModeList.get(which).getId().equals("offline")) {
                    showOfflinePaymentDialog();
                } else if (selectedPaymentModeList.get(which).getId().equals("paymentlink")) {
                    openPaylinkUrl();
                }
            }
        });
        builderSingle.show();


    }

    private void showOnlinePaymentDialog() {
        final ArrayList<String> gatewayList = new ArrayList<>();
        gatewayList.add("Paytm");
        gatewayList.add("CC Avenue");

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Payment Gateway");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < gatewayList.size(); i++) {
            arrayAdapter.add(String.valueOf(gatewayList.get(i)));
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
                if (gatewayList.get(which).equals("Paytm")) {
                    Intent intent = new Intent(context, PaytmPayment_Activity.class);
                    intent.putExtra("orderid", randomNum);
                    intent.putExtra("custid", "632541" + userId);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("amount", "1");
                    intent.putExtra("quantity", "1");
                    intent.putExtra("event_id", eventDetails.getid());
                    intent.putExtra("gateway_configuration_id", "1");
                    startActivity(intent);
                } else if (gatewayList.get(which).equals("CC Avenue")) {
                    Intent intent = new Intent(context, CCAvenueWebView_Activity.class);
                    intent.putExtra(AvenuesParams.ACCESS_CODE, ApplicationConstants.ACCESS_CODE);
                    intent.putExtra(AvenuesParams.MERCHANT_ID, ApplicationConstants.MERCHANT_ID);
                    intent.putExtra(AvenuesParams.ORDER_ID, randomNum);
                    intent.putExtra(AvenuesParams.CURRENCY, ApplicationConstants.CURRENCY);
                    intent.putExtra(AvenuesParams.AMOUNT, "1");
                    intent.putExtra(AvenuesParams.REDIRECT_URL, ApplicationConstants.REDIRECT_URL);
                    intent.putExtra(AvenuesParams.CANCEL_URL, ApplicationConstants.CANCEL_URL);
                    intent.putExtra(AvenuesParams.RSA_KEY_URL, ApplicationConstants.RSA_KEY_URL);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("amount", "1");
                    intent.putExtra("quantity", "1");
                    intent.putExtra("event_id", eventDetails.getid());
                    intent.putExtra("gateway_configuration_id", "1");

                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        builderSingle.show();
    }

    private void showOfflinePaymentDialog() {

    }

    private void openPaylinkUrl() {

    }

    private void showImageDialog(final String offerUrl) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_image, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_offer = promptView.findViewById(R.id.imv_offer);
        final Button btn_download = promptView.findViewById(R.id.btn_download);
        final Button btn_close = promptView.findViewById(R.id.btn_close);

        Picasso.with(context)
                .load(offerUrl)
                .into(imv_offer);

        final AlertDialog dialog = alertDialogBuilder.create();

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new DownloadDocument().execute(offerUrl);
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });

        dialog.show();
    }

    private class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.MyViewHolder> {

        DocumentsAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText("View Document " + (position + 1));

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isNetworkAvailable(context))
                        new DownloadDocument().execute(documentsList.get(position));
                    else
                        Utilities.showMessage("Please check your internet connection", context, 2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return documentsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            private MyViewHolder(View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);

            }
        }
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
            pd = new ProgressDialog(context);
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


    @Override
    protected void onStart() {
        super.onStart();
        //generating new order number for every transaction
        randomNum = String.valueOf(ServiceUtility.randInt(0, 9999999));
    }

}
