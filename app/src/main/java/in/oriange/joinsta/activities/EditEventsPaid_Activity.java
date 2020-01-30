package in.oriange.joinsta.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.EventTypeModel;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.yyyyMMddDate;

public class EditEventsPaid_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_name, edt_type, edt_description, edt_date, edt_start_time, edt_end_time, edt_select_from_map,
            edt_address, edt_city, edt_early_bird_amount, edt_early_bird_due_date, edt_normal_amount, edt_normal_due_date,
            edt_remark, edt_msg_forpaid, edt_msg_forunpaid, edt_payment_mode, edt_paytm_link,
            edt_attach_doc_multi, edt_doc_type_multi;
    private CheckBox cb_online_event, cb_displayto_members, cb_displayin_city;
    private LinearLayout ll_documents;
    private Button btn_add_document;

    private List<EventTypeModel.ResultBean> eventTypeList;
    private ArrayList<LinearLayout> docsLayoutsList;
    private List<MasterModel> paymentModeList;
    private String userId, groupId, eventTypeId, eventDate, earlyBirdDueDate, normalDueDate, latitude = "", longitude = "";
    private File photoFileFolder;
    private Uri photoURI;
    private JsonArray selectedPaymentModes;
    private int mYear, mMonth, mDay, startHour, startMinutes;

    private final int DOCUMENT_REQUEST = 100, CAMERA_REQUEST = 200, GALLERY_REQUEST = 300;
    private EventsPaidModel.ResultBean eventsPaidDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events_paid);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditEventsPaid_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_type = findViewById(R.id.edt_type);
        edt_description = findViewById(R.id.edt_description);
        edt_date = findViewById(R.id.edt_date);
        edt_start_time = findViewById(R.id.edt_start_time);
        edt_end_time = findViewById(R.id.edt_end_time);
        edt_select_from_map = findViewById(R.id.edt_select_from_map);
        edt_address = findViewById(R.id.edt_address);
        edt_city = findViewById(R.id.edt_city);
        edt_early_bird_amount = findViewById(R.id.edt_early_bird_amount);
        edt_early_bird_due_date = findViewById(R.id.edt_early_bird_due_date);
        edt_normal_amount = findViewById(R.id.edt_normal_amount);
        edt_normal_due_date = findViewById(R.id.edt_normal_due_date);
        edt_remark = findViewById(R.id.edt_remark);
        edt_msg_forpaid = findViewById(R.id.edt_msg_forpaid);
        edt_msg_forunpaid = findViewById(R.id.edt_msg_forunpaid);
        edt_payment_mode = findViewById(R.id.edt_payment_mode);
        edt_paytm_link = findViewById(R.id.edt_paytm_link);
        ll_documents = findViewById(R.id.ll_documents);
        btn_add_document = findViewById(R.id.btn_add_document);
        cb_online_event = findViewById(R.id.cb_online_event);
        cb_displayto_members = findViewById(R.id.cb_displayto_members);
        cb_displayin_city = findViewById(R.id.cb_displayin_city);

        eventTypeList = new ArrayList<>();
        docsLayoutsList = new ArrayList<>();
        paymentModeList = new ArrayList<>();

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Events");
        if (!photoFileFolder.exists())
            photoFileFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        paymentModeList.add(new MasterModel("Online", "online", false));
        paymentModeList.add(new MasterModel("Offline", "offline", false));
        paymentModeList.add(new MasterModel("Payment Link", "paymentlink", false));
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
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        eventsPaidDetails = (EventsPaidModel.ResultBean) getIntent().getSerializableExtra("eventsPaidDetails");

        eventTypeId = eventsPaidDetails.getEvent_type_id();

        edt_name.setText(eventsPaidDetails.getName());
        edt_type.setText(eventsPaidDetails.getEvent_type_id());
        edt_description.setText(eventsPaidDetails.getDescription());
        edt_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", eventsPaidDetails.getEvent_date()));
        edt_start_time.setText(eventsPaidDetails.getEvent_start_time());
        edt_end_time.setText(eventsPaidDetails.getEvent_end_time());
//        edt_select_from_map.setText(eventsPaidDetails);
        edt_address.setText(eventsPaidDetails.getVenue_address());
        edt_city.setText(eventsPaidDetails.getEvent_city());
        edt_early_bird_amount.setText(eventsPaidDetails.getEarlybird_price());
        edt_early_bird_due_date.setText(eventsPaidDetails.getEarlybird_price_duedate());
        edt_normal_amount.setText(eventsPaidDetails.getNormal_price());
        edt_normal_due_date.setText(eventsPaidDetails.getNormal_price_duedate());
        edt_remark.setText(eventsPaidDetails.getRemark());
        edt_msg_forpaid.setText(eventsPaidDetails.getMessage_for_paidmember());
        edt_msg_forunpaid.setText(eventsPaidDetails.getMessage_for_unpaidmember());


//        if (eventsPaidDetails.getIs_confirmation_required().equals("1"))
//            cb_confirmation_required.setChecked(true);

        if (eventsPaidDetails.getIs_online_events().equals("1"))
            cb_online_event.setChecked(true);

        if (eventsPaidDetails.getIs_displaytomembers().equals("1"))
            cb_displayto_members.setChecked(true);

        if (eventsPaidDetails.getDisplay_in_city().equals("1"))
            cb_displayin_city.setChecked(true);
    }

    private void setEventHandler() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventTypeList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetEventTypeList().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showEventTypeListDialog();
                }
            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", eventDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {

                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edt_end_time.setText("");
                        startMinutes = selectedMinute;
                        startHour = selectedHour;
                        edt_start_time.setText(selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Event Start Time");
                mTimePicker.show();
            }
        });

        edt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_start_time.getText().toString().trim().isEmpty()) {
                    edt_start_time.setError("Please select start date");
                    edt_start_time.requestFocus();
                    edt_start_time.getParent().requestChildFocus(edt_start_time, edt_start_time);
                }

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedHour < startHour) {
                            Utilities.showMessage("End time cannot be before start time", context, 2);
                            return;
                        } else if ((selectedHour == startHour)) {
                            if (selectedMinute <= startMinutes) {
                                Utilities.showMessage("End time cannot be before start time", context, 2);
                                return;
                            }
                        }

                        edt_end_time.setText(selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Event End Time");
                mTimePicker.show();
            }
        });

        edt_select_from_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(EditEventsPaid_Activity.this), 10001);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        edt_early_bird_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        earlyBirdDueDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_early_bird_due_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", earlyBirdDueDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {

                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_normal_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        normalDueDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_normal_due_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", normalDueDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {

                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_payment_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentModelListDialog();
            }
        });

        btn_add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_events_document, null);
                LinearLayout ll = (LinearLayout) rowView;
                docsLayoutsList.add(ll);
                ll_documents.addView(rowView, ll_documents.getChildCount());
            }
        });
    }

    private void showPaymentModelListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Payment Modes");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new PaymentModeListAdapter());

        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_payment_mode.setText("");
                selectedPaymentModes = new JsonArray();
                StringBuilder selectedGroupsName = new StringBuilder();

                for (MasterModel grpDetails : paymentModeList) {
                    if (grpDetails.isChecked()) {
                        selectedPaymentModes.add(grpDetails.getId());
                        selectedGroupsName.append(grpDetails.getName()).append(", ");
                    }
                }

                boolean isPaymentLinkSelected = false;

                for (int i = 0; i < selectedPaymentModes.size(); i++) {
                    String s = selectedPaymentModes.get(i).getAsString();
                    if (s.equals("paymentlink")) {
                        isPaymentLinkSelected = true;
                        break;
                    }
                }

                if (isPaymentLinkSelected) edt_paytm_link.setVisibility(View.VISIBLE);
                else edt_paytm_link.setVisibility(View.GONE);

                if (selectedGroupsName.toString().length() != 0) {
                    String selectedGroupsNameStr = selectedGroupsName.substring(0, selectedGroupsName.toString().length() - 2);
                    edt_payment_mode.setText(selectedGroupsNameStr);
                }
            }
        });

        builder.create().show();
    }

    private class PaymentModeListAdapter extends RecyclerView.Adapter<PaymentModeListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(paymentModeList.get(position).getName());

            if (paymentModeList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    paymentModeList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return paymentModeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void submitData() {

        if (edt_name.getText().toString().trim().isEmpty()) {
            edt_name.setError("Please enter name");
            edt_name.requestFocus();
            edt_name.getParent().requestChildFocus(edt_name, edt_name);
            return;
        }

        if (edt_type.getText().toString().trim().isEmpty()) {
            edt_type.setError("Please select type");
            edt_type.requestFocus();
            edt_type.getParent().requestChildFocus(edt_type, edt_type);
            return;
        }

        if (edt_description.getText().toString().trim().isEmpty()) {
            edt_description.setError("Please enter description");
            edt_description.requestFocus();
            edt_description.getParent().requestChildFocus(edt_description, edt_description);
            return;
        }

        if (edt_date.getText().toString().trim().isEmpty()) {
            edt_date.setError("Please select date");
            edt_date.requestFocus();
            edt_date.getParent().requestChildFocus(edt_date, edt_date);
            return;
        }

        if (edt_start_time.getText().toString().trim().isEmpty()) {
            edt_start_time.setError("Please select start time");
            edt_start_time.requestFocus();
            edt_start_time.getParent().requestChildFocus(edt_start_time, edt_start_time);
            return;
        }

        if (edt_end_time.getText().toString().trim().isEmpty()) {
            edt_end_time.setError("Please select end time");
            edt_end_time.requestFocus();
            edt_end_time.getParent().requestChildFocus(edt_end_time, edt_end_time);
            return;
        }

        if (edt_address.getText().toString().trim().isEmpty()) {
            edt_address.setError("Please enter address");
            edt_address.requestFocus();
            edt_address.getParent().requestChildFocus(edt_address, edt_address);
            return;
        }

        if (edt_city.getText().toString().trim().isEmpty()) {
            edt_city.setError("Please enter city");
            edt_city.requestFocus();
            edt_city.getParent().requestChildFocus(edt_city, edt_city);
            return;
        }

        if (edt_early_bird_amount.getText().toString().trim().isEmpty()) {
            edt_early_bird_amount.setError("Please enter amount");
            edt_early_bird_amount.requestFocus();
            edt_early_bird_amount.getParent().requestChildFocus(edt_early_bird_amount, edt_early_bird_amount);
            return;
        }

        if (edt_early_bird_due_date.getText().toString().trim().isEmpty()) {
            edt_early_bird_due_date.setError("Please select date");
            edt_early_bird_due_date.requestFocus();
            edt_early_bird_due_date.getParent().requestChildFocus(edt_early_bird_due_date, edt_early_bird_due_date);
            return;
        }

        if (edt_normal_amount.getText().toString().trim().isEmpty()) {
            edt_normal_amount.setError("Please enter amount");
            edt_normal_amount.requestFocus();
            edt_normal_amount.getParent().requestChildFocus(edt_normal_amount, edt_normal_amount);
            return;
        }

        if (edt_normal_due_date.getText().toString().trim().isEmpty()) {
            edt_normal_due_date.setError("Please select date");
            edt_normal_due_date.requestFocus();
            edt_normal_due_date.getParent().requestChildFocus(edt_normal_due_date, edt_normal_due_date);
            return;
        }

        if (edt_payment_mode.getText().toString().trim().isEmpty()) {
            edt_payment_mode.setError("Please select payment mode");
            edt_payment_mode.requestFocus();
            edt_payment_mode.getParent().requestChildFocus(edt_payment_mode, edt_payment_mode);
            return;
        }

        if (edt_paytm_link.getVisibility() == View.VISIBLE) {
            if (!Utilities.isWebsiteValid(edt_paytm_link.getText().toString().trim())) {
                edt_paytm_link.setError("Please enter valid website");
                edt_paytm_link.requestFocus();
                edt_paytm_link.getParent().requestChildFocus(edt_paytm_link, edt_paytm_link);
                return;
            }
        } else {
            edt_paytm_link.setText("");
        }

        String is_online_event = cb_online_event.isChecked() ? "1" : "0";
        String is_displaytomembers = cb_displayto_members.isChecked() ? "1" : "0";
        String display_in_city = cb_displayin_city.isChecked() ? "1" : "0";

        JsonArray documentsArray = new JsonArray();

        for (int i = 0; i < docsLayoutsList.size(); i++) {
            if (!((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString().trim().equals("")) {
                JsonObject jsonObject = new JsonObject();
                if (((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_doc_type)).getText().toString().equalsIgnoreCase("Image")) {
                    jsonObject.addProperty("document", "invitationimage");
                }
                else if (((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_doc_type)).getText().toString().equalsIgnoreCase("Document")) {
                    jsonObject.addProperty("document", "invitationdocument");
                }

                jsonObject.addProperty("document_type", ((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString());
                documentsArray.add(jsonObject);
            }
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "UpdatePaidEvent");
        mainObj.addProperty("paid_events_id", eventsPaidDetails.getId());
        mainObj.addProperty("event_type_id", eventTypeId);
        mainObj.addProperty("group_id", groupId);
        mainObj.addProperty("name", edt_name.getText().toString().trim());
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("event_date", edt_date.getText().toString().trim());
        mainObj.addProperty("event_start_time", edt_start_time.getText().toString().trim());
        mainObj.addProperty("event_end_time", edt_end_time.getText().toString().trim());
        mainObj.addProperty("venue_address", edt_address.getText().toString().trim());
        mainObj.addProperty("venue_longitude", latitude);
        mainObj.addProperty("venue_latitude", longitude);
        mainObj.addProperty("is_online_event", is_online_event);
        mainObj.addProperty("is_displaytomembers", is_displaytomembers);
        mainObj.addProperty("event_city", edt_city.getText().toString().trim());
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("display_in_city", display_in_city);
        mainObj.addProperty("event_category_id", "2");
        mainObj.addProperty("message_for_paidmember", edt_msg_forpaid.getText().toString().trim());
        mainObj.addProperty("message_for_unpaidmember", edt_msg_forunpaid.getText().toString().trim());
        mainObj.addProperty("is_offline_payments_allowed", "1");
        mainObj.addProperty("payment_account_id", "1");
        mainObj.addProperty("earlybird_price", edt_early_bird_amount.getText().toString().trim());
        mainObj.addProperty("normal_price", edt_normal_amount.getText().toString().trim());
        mainObj.addProperty("earlybird_price_duedate", earlyBirdDueDate);
        mainObj.addProperty("normal_price_duedate", normalDueDate);
        mainObj.addProperty("is_online_events", is_online_event);
        mainObj.addProperty("payment_link", edt_paytm_link.getText().toString().trim());
        mainObj.addProperty("created_by", userId);
        mainObj.addProperty("updated_by", userId);
        mainObj.add("document_path", documentsArray);
        mainObj.add("payment_mode", selectedPaymentModes);

        if (Utilities.isNetworkAvailable(context)) {
            new AddPaidEvent().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class GetEventTypeList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getAllEventTypes");
            res = APICall.JSONAPICall(ApplicationConstants.EVENTSTYPEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    eventTypeList = new ArrayList<>();
                    EventTypeModel pojoDetails = new Gson().fromJson(result, EventTypeModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        eventTypeList = pojoDetails.getResult();
                        if (eventTypeList.size() > 0) {
                            showEventTypeListDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private class AddPaidEvent extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.PAIDEVENTSAPI, params[0]);
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
                        tv_title.setText("Event details submitted successfully");
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

    private void showEventTypeListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Event Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < eventTypeList.size(); i++) {
            arrayAdapter.add(String.valueOf(eventTypeList.get(i).getEvent_type()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventTypeModel.ResultBean event = eventTypeList.get(which);
                edt_type.setText(event.getEvent_type());
                eventTypeId = event.getId();
            }
        });
        builderSingle.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            submitData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditEventsPaid_Activity.this);
    }
}
