package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.MyEventsFree_Fragment;
import in.oriange.joinsta.fragments.MyEventsPaid_Fragment;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;

public class MyEvents_Activity extends AppCompatActivity {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = MyEvents_Activity.this;
        tabs = findViewById(R.id.tabs);
        viewpager = findViewById(R.id.viewpager);
    }

    private void setDefault() {

        Bundle bundle = new Bundle();
        bundle.putString("groupId", getIntent().getStringExtra("groupId"));
        bundle.putString("isAdmin", getIntent().getStringExtra("isAdmin"));

        MyEventsFree_Fragment eventsFreeFragment = new MyEventsFree_Fragment();
        eventsFreeFragment.setArguments(bundle);

        MyEventsPaid_Fragment eventsPaidFragment = new MyEventsPaid_Fragment();
        eventsPaidFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(eventsFreeFragment, "Free");
        adapter.addFrag(eventsPaidFragment, "Paid");
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
}
