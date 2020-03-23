package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import in.oriange.joinsta.fragments.EventsFree_Fragment;
import in.oriange.joinsta.fragments.EventsPaid_Fragment;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;

public class Events_Activity extends AppCompatActivity {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;
    private MenuItem action_notification;

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
        context = Events_Activity.this;
        tabs = findViewById(R.id.tabs);
        viewpager = findViewById(R.id.viewpager);
    }

    private void setDefault() {

        Bundle bundle = new Bundle();
        bundle.putString("groupId", getIntent().getStringExtra("groupId"));
        bundle.putString("isAdmin", getIntent().getStringExtra("isAdmin"));

        EventsFree_Fragment eventsFreeFragment = new EventsFree_Fragment();
        eventsFreeFragment.setArguments(bundle);

        EventsPaid_Fragment eventsPaidFragment = new EventsPaid_Fragment();
        eventsPaidFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(eventsFreeFragment, "Free");
        adapter.addFrag(eventsPaidFragment, "Paid");
        viewpager.setAdapter(adapter);
        tabs.setViewPager(viewpager);
    }

    private void setEventHandler() {
        tabs.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                switch (position) {
                    case 0:
                        action_notification.setVisible(false);
                        break;
                    case 1:
                        action_notification.setVisible(true);
                        break;
                }
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
        inflater.inflate(R.menu.menus_notification, menu);
        action_notification = menu.findItem(R.id.action_notification);
        action_notification.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                startActivity(new Intent(context, AllEventNotifications_Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
