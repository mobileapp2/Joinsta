package in.oriange.joinsta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.yyyyMMddDate;

public class AddOffers_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_title, edt_start_date, edt_end_date, edt_url, edt_promo_code;
    private EditText edt_description;
    private ImageView imv_iamge_one, imv_iamge_two, imv_iamge_three;
    private Button btn_save;

    private int mYear, mMonth, mDay, mYear1, mMonth1, mDay1;
    private String userId, startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offers);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddOffers_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_title = findViewById(R.id.edt_title);
        edt_start_date = findViewById(R.id.edt_start_date);
        edt_end_date = findViewById(R.id.edt_end_date);
        edt_url = findViewById(R.id.edt_url);
        edt_promo_code = findViewById(R.id.edt_promo_code);
        edt_description = findViewById(R.id.edt_description);
        imv_iamge_one = findViewById(R.id.imv_iamge_one);
        imv_iamge_two = findViewById(R.id.imv_iamge_two);
        imv_iamge_three = findViewById(R.id.imv_iamge_three);
        btn_save = findViewById(R.id.btn_save);
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

        mYear1 = calendar.get(Calendar.YEAR);
        mMonth1 = calendar.get(Calendar.MONTH);
        mDay1 = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setEventHandler() {
        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edt_end_date.setText("");
                        startDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_start_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", startDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {
                    Calendar c = Calendar.getInstance();
                    c.set(mYear, mMonth + 1, mDay);

                    dialog.getDatePicker().setCalendarViewShown(false);
                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_start_date.getText().toString().trim().isEmpty()) {
                    edt_start_date.setError("Please select start date");
                    edt_start_date.requestFocus();
                    return;
                }

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_end_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", endDate));

                        mYear1 = year;
                        mMonth1 = month;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);
                try {
                    dialog.getDatePicker().setCalendarViewShown(false);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth, mDay);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(mYear, mMonth + 1, mDay);
                    dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                    dialog.getDatePicker().setMaxDate(calendar1.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });
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
        hideSoftKeyboard(AddOffers_Activity.this);
    }
}
