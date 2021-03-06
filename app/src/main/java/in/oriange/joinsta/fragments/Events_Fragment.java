package in.oriange.joinsta.fragments;

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

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.EventsFreeAdapter;
import in.oriange.joinsta.adapters.EventsPaidAdapter;
import in.oriange.joinsta.models.EventTypeModel;
import in.oriange.joinsta.models.EventsFreeModel;
import in.oriange.joinsta.models.EventsModel;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Events_Fragment extends Fragment {

    private Context context;
    private UserSessionManager session;

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText edt_search;
    private ImageButton imb_filter;
    private TextView tv_filter_count;
    private RecyclerView rv_event;
    private FloatingActionButton btn_add;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<EventTypeModel.ResultBean> eventTypeList;
    private List<EventsFreeModel.ResultBean> eventFreeList;
    private List<EventsPaidModel.ResultBean> eventPaidList;
    private String userId, eventCategoryId = "1";           // 1 for free events 2 for paid events

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        edt_search = rootView.findViewById(R.id.edt_search);
        imb_filter = rootView.findViewById(R.id.imb_filter);
        tv_filter_count = rootView.findViewById(R.id.tv_filter_count);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_event = rootView.findViewById(R.id.rv_event);
        rv_event.setLayoutManager(new LinearLayoutManager(context));
        btn_add = rootView.findViewById(R.id.btn_add);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        eventFreeList = new ArrayList<>();
        eventPaidList = new ArrayList<>();
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

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetEventsList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("Events_Fragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    tv_filter_count.setVisibility(View.GONE);

                    for (int i = 0; i < eventTypeList.size(); i++) {
                        eventTypeList.get(i).setChecked(false);
                    }
                    new GetEventsList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    swipeRefreshLayout.setRefreshing(false);
                }
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


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

                switch (eventCategoryId) {
                    case "1":
                        List<EventsFreeModel.ResultBean> filteredFreeEventList = new ArrayList<>();
                        int selectedFreeTypeCount = 0;

                        for (EventTypeModel.ResultBean eventType : eventTypeList)
                            if (eventType.isChecked()) {
                                selectedFreeTypeCount = selectedFreeTypeCount + 1;
                                for (EventsFreeModel.ResultBean eventsDetails : eventFreeList)
                                    if (eventType.getId().equals(eventsDetails.getEvent_type_id()))
                                        filteredFreeEventList.add(eventsDetails);
                            }

                        if (selectedFreeTypeCount == 0) {
                            tv_filter_count.setVisibility(View.GONE);
                            rv_event.setAdapter(new EventsFreeAdapter(context, eventFreeList, "0", 2));
                        } else {
                            tv_filter_count.setVisibility(View.VISIBLE);
                            tv_filter_count.setText(String.valueOf(selectedFreeTypeCount));
                            rv_event.setAdapter(new EventsFreeAdapter(context, filteredFreeEventList, "0", 2));
                        }

                        break;
                    case "2":
                        List<EventsPaidModel.ResultBean> filteredPaidEventList = new ArrayList<>();
                        int selectedPaidTypeCount = 0;

                        for (EventTypeModel.ResultBean eventType : eventTypeList)
                            if (eventType.isChecked()) {
                                selectedPaidTypeCount = selectedPaidTypeCount + 1;
                                for (EventsPaidModel.ResultBean eventsDetails : eventPaidList)
                                    if (eventType.getId().equals(eventsDetails.getEvent_type_id()))
                                        filteredPaidEventList.add(eventsDetails);
                            }

                        if (selectedPaidTypeCount == 0) {
                            tv_filter_count.setVisibility(View.GONE);
                            rv_event.setAdapter(new EventsPaidAdapter(context, eventPaidList, "0", 2));
                        } else {
                            tv_filter_count.setVisibility(View.VISIBLE);
                            tv_filter_count.setText(String.valueOf(selectedPaidTypeCount));
                            rv_event.setAdapter(new EventsPaidAdapter(context, filteredPaidEventList, "0", 2));
                        }

                        break;
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
                setDataToRecyclerView();
            }
        });

        builder.create().show();
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

    private void searchDetails(String query) {
        switch (eventCategoryId) {
            case "1":
                if (query.isEmpty()) {
                    rv_event.setAdapter(new EventsFreeAdapter(context, eventFreeList, "0", 2));
                    return;
                }

                if (eventFreeList.size() == 0) {
                    rv_event.setVisibility(View.GONE);
                    return;
                }


                ArrayList<EventsFreeModel.ResultBean> eventsFreeSearchedList = new ArrayList<>();
                for (EventsFreeModel.ResultBean eventDetails : eventFreeList) {

                    String eventsToBeSearched = eventDetails.getName().toLowerCase() +
                            eventDetails.getDescription().toLowerCase() +
                            eventDetails.getEvent_code().toLowerCase();

                    if (eventsToBeSearched.contains(query.toLowerCase())) {
                        eventsFreeSearchedList.add(eventDetails);
                    }
                }
                rv_event.setAdapter(new EventsFreeAdapter(context, eventsFreeSearchedList, "0", 2));

                break;
            case "2":
                if (query.isEmpty()) {
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventPaidList, "0", 2));
                    return;
                }

                if (eventPaidList.size() == 0) {
                    rv_event.setVisibility(View.GONE);
                    return;
                }


                ArrayList<EventsPaidModel.ResultBean> eventsPaidSearchedList = new ArrayList<>();
                for (EventsPaidModel.ResultBean eventDetails : eventPaidList) {

                    String eventsToBeSearched = eventDetails.getName().toLowerCase() +
                            eventDetails.getDescription().toLowerCase() +
                            eventDetails.getEvent_code().toLowerCase();

                    if (eventsToBeSearched.contains(query.toLowerCase())) {
                        eventsPaidSearchedList.add(eventDetails);
                    }
                }
                rv_event.setAdapter(new EventsPaidAdapter(context, eventsPaidSearchedList, "0", 2));

                break;
        }
    }

    public class GetEventsList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_event.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventsbyLocation");
            obj.addProperty("user_id", userId);
            obj.addProperty("location", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            edt_search.setText("");
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    eventFreeList = new ArrayList<>();
                    eventPaidList = new ArrayList<>();
                    EventsModel pojoDetails = new Gson().fromJson(result, EventsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        eventFreeList = pojoDetails.getResult().getFree_events();
                        eventPaidList = pojoDetails.getResult().getPaid_events();

                        setDataToRecyclerView();
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

    private void setDataToRecyclerView() {
        edt_search.setText("");
        switch (eventCategoryId) {
            case "1":
                if (eventFreeList.size() > 0) {
                    rv_event.setAdapter(new EventsFreeAdapter(context, eventFreeList, "0", 2));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_event.setVisibility(View.VISIBLE);
                } else {
                    rv_event.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                if (eventPaidList.size() > 0) {
                    rv_event.setAdapter(new EventsPaidAdapter(context, eventPaidList, "0", 2));
                    ll_nopreview.setVisibility(View.GONE);
                    rv_event.setVisibility(View.VISIBLE);
                } else {
                    rv_event.setVisibility(View.GONE);
                    ll_nopreview.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            eventCategoryId = intent.getStringExtra("eventCategoryId");
            tv_filter_count.setVisibility(View.GONE);

            for (int i = 0; i < eventTypeList.size(); i++) {
                eventTypeList.get(i).setChecked(false);
            }
            new GetEventsList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

}
