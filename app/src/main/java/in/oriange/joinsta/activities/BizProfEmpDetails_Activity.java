package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.AddBusiness_Fragment;
import in.oriange.joinsta.fragments.AddEmployee_Fragment;
import in.oriange.joinsta.fragments.AddProfessional_Fragment;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;
import in.oriange.joinsta.utilities.UserSessionManager;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class BizProfEmpDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizprofemp_details);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BizProfEmpDetails_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tabs = findViewById(R.id.tabs);
        viewpager = findViewById(R.id.viewpager);
    }

    private void setDefault() {
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddBusiness_Fragment(), "Business");
        adapter.addFrag(new AddEmployee_Fragment(), "Employment");
        adapter.addFrag(new AddProfessional_Fragment(), "Professional");
        viewpager.setAdapter(adapter);
        tabs.setViewPager(viewpager);

        viewpager.setCurrentItem(currentPosition);
    }

    private void setEventHandler() {

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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppCompatEditText toolbar_title = findViewById(R.id.toolbar_title);

        if (currentPosition == 0) {
            toolbar_title.setText("Add Business Details");
        } else if (currentPosition == 1) {
            toolbar_title.setText("Add Employment Details");
        } else if (currentPosition == 2) {
            toolbar_title.setText("Add Professional Details");
        }

        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void removeMobileLayoutBiz(View v) {
        AddBusiness_Fragment.removeMobileLayout(v);
    }

    public void removeLandlineLayoutBiz(View v) {
        AddBusiness_Fragment.removeLandlineLayout(v);
    }

    public void removeMobileLayoutProf(View v) {
        AddProfessional_Fragment.removeMobileLayout(v);
    }

    public void removeLandlineLayoutProf(View v) {
        AddProfessional_Fragment.removeLandlineLayout(v);
    }

    public void removeMobileLayoutEmp(View v) {
        AddEmployee_Fragment.removeMobileLayout(v);
    }

    public void removeLandlineLayoutEmp(View v) {
        AddEmployee_Fragment.removeLandlineLayout(v);
    }

    public void selectContryCodeBiz(View v) {
        AddBusiness_Fragment.selectContryCode(v);
    }

    public void selectContryCodeProf(View v) {
        AddProfessional_Fragment.selectContryCode(v);
    }

    public void selectContryCodeEmp(View v) {
        AddEmployee_Fragment.selectContryCode(v);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(BizProfEmpDetails_Activity.this);
    }

}
