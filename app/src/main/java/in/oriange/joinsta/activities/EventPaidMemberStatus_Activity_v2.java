package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.PaidEventsMembersAll_Fragment;
import in.oriange.joinsta.fragments.PaidEventsMembersNonMembers_Fragment;
import in.oriange.joinsta.fragments.PaidEventsMembersPaid_Fragment;
import in.oriange.joinsta.fragments.PaidEventsMembersPayAtCounter_Fragment;
import in.oriange.joinsta.fragments.PaidEventsMembersUnPaid_Fragment;
import in.oriange.joinsta.models.EventPaidMemberStatusModel;
import in.oriange.joinsta.models.EventsPaidModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;
import in.oriange.joinsta.utilities.Utilities;

public class EventPaidMemberStatus_Activity_v2 extends AppCompatActivity {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;

    private EventsPaidModel.ResultBean eventDetails;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_paid_member_status_v2);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EventPaidMemberStatus_Activity_v2.this;
        tabs = findViewById(R.id.tabs);
        viewpager = findViewById(R.id.viewpager);
    }

    private void setDefault() {
        eventDetails = (EventsPaidModel.ResultBean) getIntent().getSerializableExtra("eventDetails");

        if (Utilities.isNetworkAvailable(context)) {
            new GetMemberStatus().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private void setEventHandler() {
        tabs.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int i) {
                position = i;
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class GetMemberStatus extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getPaidEventMembers");
            obj.addProperty("event_id", eventDetails.getid());
            res = APICall.JSONAPICall(ApplicationConstants.PAYMENTTRACKAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<EventPaidMemberStatusModel.ResultBean> membersList = new ArrayList<>();
                    EventPaidMemberStatusModel pojoDetails = new Gson().fromJson(result, EventPaidMemberStatusModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        membersList = pojoDetails.getResult();

                        if (membersList.size() > 0) {
                            filterMembersList(membersList);
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Members not found", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void filterMembersList(List<EventPaidMemberStatusModel.ResultBean> membersList) {
        List<EventPaidMemberStatusModel.ResultBean> paidEventsMembersAllList = new ArrayList<>();
        List<EventPaidMemberStatusModel.ResultBean> paidEventsMembersPaidList = new ArrayList<>();
        List<EventPaidMemberStatusModel.ResultBean> paidEventsMembersUnPaidList = new ArrayList<>();
        List<EventPaidMemberStatusModel.ResultBean> paidEventsMembersPayAtCounterList = new ArrayList<>();
        List<EventPaidMemberStatusModel.ResultBean> paidEventsMembersNonMemberList = new ArrayList<>();

        for (EventPaidMemberStatusModel.ResultBean resultBean : membersList) {
            if (resultBean.getIs_group_member().equals("1")) {
                paidEventsMembersAllList.add(resultBean);
                if (resultBean.getStatus().equals("paid")) {
                    if (resultBean.getPayment_mode().equals("online") || resultBean.getPayment_mode().equals("offline")) {
                        paidEventsMembersPaidList.add(resultBean);
                    } else if (resultBean.getPayment_mode().equals("pay_at_counter")) {
                        paidEventsMembersPayAtCounterList.add(resultBean);
                    }
                } else if (resultBean.getStatus().equals("unpaid")) {
                    paidEventsMembersUnPaidList.add(resultBean);
                }
            } else if (resultBean.getIs_group_member().equals("0")) {
                paidEventsMembersNonMemberList.add(resultBean);
            }
        }


        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("membersList", (Serializable) paidEventsMembersAllList);
        PaidEventsMembersAll_Fragment paidEventsMembersAll_fragment = new PaidEventsMembersAll_Fragment();
        paidEventsMembersAll_fragment.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("membersList", (Serializable) paidEventsMembersPaidList);
        PaidEventsMembersPaid_Fragment paidEventsMembersPaid_fragment = new PaidEventsMembersPaid_Fragment();
        paidEventsMembersPaid_fragment.setArguments(bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("membersList", (Serializable) paidEventsMembersUnPaidList);
        PaidEventsMembersUnPaid_Fragment paidEventsMembersUnPaid_fragment = new PaidEventsMembersUnPaid_Fragment();
        paidEventsMembersUnPaid_fragment.setArguments(bundle3);

        Bundle bundle4 = new Bundle();
        bundle4.putSerializable("membersList", (Serializable) paidEventsMembersPayAtCounterList);
        PaidEventsMembersPayAtCounter_Fragment paidEventsMembersPayAtCounter_fragment = new PaidEventsMembersPayAtCounter_Fragment();
        paidEventsMembersPayAtCounter_fragment.setArguments(bundle4);

        Bundle bundle5 = new Bundle();
        bundle5.putSerializable("membersList", (Serializable) paidEventsMembersNonMemberList);
        PaidEventsMembersNonMembers_Fragment paidEventsMembersNonMembers_fragment = new PaidEventsMembersNonMembers_Fragment();
        paidEventsMembersNonMembers_fragment.setArguments(bundle5);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(paidEventsMembersAll_fragment, "All");
        adapter.addFrag(paidEventsMembersPaid_fragment, "Paid");
        adapter.addFrag(paidEventsMembersUnPaid_fragment, "Unpaid");
        adapter.addFrag(paidEventsMembersPayAtCounter_fragment, "Pay At Counter");
        adapter.addFrag(paidEventsMembersNonMembers_fragment, "Non Member Payment");
        viewpager.setAdapter(adapter);
        tabs.setViewPager(viewpager);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_export) {
            showExportDialog();
        } else if (item.getItemId() == R.id.action_message) {

            startActivity(new Intent(context, EventsSendMessage_Activity.class)
                    .putExtra("eventId", eventDetails.getid())
                    .putExtra("messageForPaid", eventDetails.getMessage_for_paidmember())
                    .putExtra("messageForUnPaid", eventDetails.getMessage_for_unpaidmember())
                    .putExtra("position", position));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showExportDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_export, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Export Type");
        alertDialogBuilder.setView(promptView);

        final CardView cv_pdf = promptView.findViewById(R.id.cv_pdf);
        final CardView cv_excel = promptView.findViewById(R.id.cv_excel);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertD = alertDialogBuilder.create();

        cv_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnterEmailDialog("pdf");
                alertD.dismiss();
            }
        });

        cv_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnterEmailDialog("excel");
                alertD.dismiss();
            }
        });
        alertD.show();

    }

    private void showEnterEmailDialog(String exportType) {
        final MaterialEditText edt_entermobile = new MaterialEditText(context);
        edt_entermobile.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edt_entermobile.setHighlightColor(getResources().getColor(R.color.colorPrimary));
        float dpi = context.getResources().getDisplayMetrics().density;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Enter Email");

        alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!Utilities.isEmailValid(edt_entermobile.getText().toString().trim())) {
                    Utilities.showMessage("Please enter valid email", context, 2);
                    dialog.dismiss();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new SendPaidEventMemberStatus().execute(eventDetails.getid(), exportType, edt_entermobile.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertD = alertDialogBuilder.create();
        alertD.setView(edt_entermobile, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertD.show();
        alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        edt_entermobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Utilities.isEmailValid(edt_entermobile.getText().toString().trim())) {
                    alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
    }

    private class SendPaidEventMemberStatus extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

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
            obj.addProperty("type", "sendPaidEventMemberStatus");
            obj.addProperty("event_id", params[0]);
            obj.addProperty("report_type", params[1]);
            obj.addProperty("email", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.EVENTSAPI, obj.toString());
            return res;
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
                        Utilities.showMessage(message, context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
