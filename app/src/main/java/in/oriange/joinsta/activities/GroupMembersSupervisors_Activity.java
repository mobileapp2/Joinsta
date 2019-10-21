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
import in.oriange.joinsta.fragments.GroupMembers_Fragment;
import in.oriange.joinsta.fragments.GroupSupervisors_Fragment;
import in.oriange.joinsta.models.MyGroupsListModel;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;

public class GroupMembersSupervisors_Activity extends AppCompatActivity {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memberssupervisors);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupMembersSupervisors_Activity.this;
        tabs = findViewById(R.id.tabs);
        viewpager = findViewById(R.id.viewpager);

    }

    private void setDefault() {

        Bundle bundle = new Bundle();
        bundle.putString("groupId", getIntent().getStringExtra("groupId"));

        GroupSupervisors_Fragment groupSupervisors_fragment = new GroupSupervisors_Fragment();
        groupSupervisors_fragment.setArguments(bundle);

        GroupMembers_Fragment groupMembers_fragment = new GroupMembers_Fragment();
        groupMembers_fragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(groupSupervisors_fragment, "Supervisors");
        adapter.addFrag(groupMembers_fragment, "Members");
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
