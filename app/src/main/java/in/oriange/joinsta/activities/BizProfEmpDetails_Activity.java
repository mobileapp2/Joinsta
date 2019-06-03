package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.AddBusiness_Fragment;
import in.oriange.joinsta.fragments.AddEmployee_Fragment;
import in.oriange.joinsta.fragments.AddProfessional_Fragment;
import in.oriange.joinsta.utilities.UserSessionManager;

public class BizProfEmpDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private SmartTabLayout tabs;
    private ViewPager viewpager;

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddBusiness_Fragment(), "Business");
        adapter.addFrag(new AddEmployee_Fragment(), "Employee");
        adapter.addFrag(new AddProfessional_Fragment(), "Professional");
        viewpager.setAdapter(adapter);
        tabs.setViewPager(viewpager);
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

    public void selectContryCode(View v) {
        AddBusiness_Fragment.selectContryCode(v);
    }


}
