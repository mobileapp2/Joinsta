package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.AddEventsPaid_Activity;
import in.oriange.joinsta.adapters.EventsPaidAdapter;
import in.oriange.joinsta.models.EventTypeModel;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EventsPaid_Fragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText edt_search;
    private ImageButton imb_filter, imb_calendar;
    private TextView tv_is_calendar_applied, tv_filter_count;
    private RecyclerView rv_event;
    private FloatingActionButton btn_add;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<EventTypeModel.ResultBean> eventTypeList;
    private List<EventsPaidModel.ResultBean> eventList, mFilteredEventList;
    private String groupId, userId, isAdmin;

    private LocalBroadcastManager localBroadcastManager;

    private int mYear, mMonth, mDay;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_paid, container, false);
        context = getActivity();

        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        edt_search = rootView.findViewById(R.id.edt_search);
        imb_filter = rootView.findViewById(R.id.imb_filter);
        imb_calendar = rootView.findViewById(R.id.imb_calendar);
        tv_filter_count = rootView.findViewById(R.id.tv_filter_count);
        tv_is_calendar_applied = rootView.findViewById(R.id.tv_is_calendar_applied);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_event = rootView.findViewById(R.id.rv_event);
        rv_event.setLayoutManager(new LinearLayoutManager(context));
        btn_add = rootView.findViewById(R.id.btn_add);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        eventList = new ArrayList<>();
        eventTypeList = new ArrayList<>();
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

    @SuppressLint("RestrictedApi")
    private void setDefault() {
        groupId = this.getArguments().getString("groupId");
        isAdmin = this.getArguments().getString("isAdmin");

        if (Utilities.isNetworkAvailable(context)) {
            new GetPaidEvents().execute();
        } else {
            Utilities.showMessage("Please check your internet connection", context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("EventsPaid_Fragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        if (isAdmin.equals("1"))
            btn_add.setVisibility(View.VISIBLE);
        else
            btn_add.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    edt_search.setText("");
                    tv_filter_count.setVisibility(View.GONE);

                    for (int i = 0; i < eventTypeList.size(); i++) {
                        eventTypeList.get(i).setChecked(false);
                    }
                    new GetPaidEvents().execute();
                } else {
                    Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddEventsPaid_Activity.class)
                        .putExtra("groupId", groupId));
            }
        });

        imb_filter.setOnClickListener(new View.OnClickListener() {
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

        imb_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarFilterDialog();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
                    return;
                }

                if (eventList.size() == 0) {
                    rv_event.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<EventsPaidModel.ResultBean> eventsSearchedList = new ArrayList<>();
                    for (EventsPaidModel.ResultBean eventDetails : eventList) {

                        String eventsToBeSearched = eventDetails.getName().toLowerCase() +
                                eventDetails.getDescription().toLowerCase() +
                                eventDetails.getEvent_code().toLowerCase();

                        if (eventsToBeSearched.contains(query.toString().toLowerCase())) {
                            eventsSearchedList.add(eventDetails);
                        }
                    }
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventsSearchedList, isAdmin, false));
                } else {
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetPaidEvents extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_event.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllPaidEvent");
            obj.addProperty("user_id", userId);
            obj.addProperty("group_id", groupId);
            res = APICall.JSONAPICall(ApplicationConstants.PAIDEVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_event.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    eventList = new ArrayList<>();
                    EventsPaidModel pojoDetails = new Gson().fromJson(result, EventsPaidModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        eventList = pojoDetails.getResult();
                        mFilteredEventList = eventList;
                        if (eventList.size() > 0) {
                            List<EventsPaidModel.ResultBean> filteredEventList = new ArrayList<>();

                            for (EventsPaidModel.ResultBean eventsDetails : eventList) {
                                if (eventsDetails.getCreated_by().equals(userId)) {
                                    filteredEventList.add(eventsDetails);
                                } else {
                                    if (!eventsDetails.getIs_displaytomembers().equals("0") && !eventsDetails.getIs_active().equals("0") && !eventsDetails.isEndDatePassed()) {
                                        filteredEventList.add(eventsDetails);
                                    }
                                }
                            }

                            eventList.clear();
                            eventList.addAll(filteredEventList);

                            if (eventList.size() > 0) {
                                rv_event.setVisibility(View.VISIBLE);
                                ll_nopreview.setVisibility(View.GONE);
                                rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
                            } else {
                                ll_nopreview.setVisibility(View.VISIBLE);
                                rv_event.setVisibility(View.GONE);
                            }
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_event.setVisibility(View.GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_event.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_event.setVisibility(View.GONE);
            }
        }
    }

    private class GetEventTypeList extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
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

    private void showEventTypeListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Event Type");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new EventTypeListAdapter());

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<EventsPaidModel.ResultBean> filteredEventList = new ArrayList<>();
                int selectedTypeCount = 0;

                for (EventTypeModel.ResultBean eventType : eventTypeList)
                    if (eventType.isChecked()) {
                        selectedTypeCount = selectedTypeCount + 1;
                        for (EventsPaidModel.ResultBean eventsDetails : eventList)
                            if (eventType.getId().equals(eventsDetails.getEvent_type_id()))
                                filteredEventList.add(eventsDetails);
                    }

                if (selectedTypeCount == 0) {
                    mFilteredEventList = eventList;
                    tv_filter_count.setVisibility(View.GONE);
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
                } else {
                    mFilteredEventList = filteredEventList;
                    tv_filter_count.setVisibility(View.VISIBLE);
                    tv_filter_count.setText(String.valueOf(selectedTypeCount));
                    rv_event.setAdapter(new EventsPaidAdapter(context, filteredEventList, isAdmin, false));
                }
            }
        });

        builder.setNegativeButton("Clear Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < eventTypeList.size(); i++) {
                    eventTypeList.get(i).setChecked(false);
                }

                tv_filter_count.setVisibility(View.GONE);
                rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
            }
        });

        builder.create().show();
    }

    private void showCalendarFilterDialog() {
        List<String> calOptions = new ArrayList<>();
        calOptions.add("Today");
        calOptions.add("Tomorrow");
        calOptions.add("Custom");

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Calendar Filter");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < calOptions.size(); i++) {
            arrayAdapter.add(calOptions.get(i));
        }

        builderSingle.setNegativeButton("Clear Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                tv_is_calendar_applied.setVisibility(View.GONE);
                rv_event.setAdapter(new EventsPaidAdapter(context, eventList, isAdmin, false));
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (calOptions.get(which)) {
                    case "Today":
                        Date todayDate = Calendar.getInstance().getTime();
                        filterEventsAccordingToDate(todayDate);
                        break;
                    case "Tomorrow":
                        Calendar tomoCal = Calendar.getInstance();
                        tomoCal.add(Calendar.DATE, +1);
                        Date tommorowDate = Calendar.getInstance().getTime();
                        filterEventsAccordingToDate(tommorowDate);
                        break;
                    case "Custom":
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                            try {
                                Date customDate = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + month + "/" + year);
                                filterEventsAccordingToDate(customDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            mYear = year;
                            mMonth = month;
                            mDay = dayOfMonth;
                        }, mYear, mMonth, mDay);
                        try {
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        datePickerDialog.show();
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void filterEventsAccordingToDate(Date date) {
        tv_is_calendar_applied.setVisibility(View.VISIBLE);
        List<EventsPaidModel.ResultBean> filteredEventList = new ArrayList<>();

        for (EventsPaidModel.ResultBean event : mFilteredEventList)
            if (date.after(event.getEventStartDate()) && date.before(event.getEventEndDate()))
                filteredEventList.add(event);

        if (filteredEventList.size() == 0) {
            Utilities.showAlertDialog(context, "Events not found for applied filter", false);
            tv_is_calendar_applied.setVisibility(View.GONE);
            return;
        }

        rv_event.setAdapter(new EventsPaidAdapter(context, filteredEventList, isAdmin, false));
    }

    private class EventTypeListAdapter extends RecyclerView.Adapter<EventTypeListAdapter.MyViewHolder> {

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

            holder.cb_select.setText(eventTypeList.get(position).getEvent_type());

            if (eventTypeList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    eventTypeList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventTypeList.size();
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetPaidEvents().execute();
            } else {
                Utilities.showMessage("Please check your internet connection", context, 2);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

}
