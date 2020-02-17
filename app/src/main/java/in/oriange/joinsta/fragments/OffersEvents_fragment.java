package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.SelectCity_Activity;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.NonSwipeableViewPager;
import in.oriange.joinsta.utilities.UserSessionManager;

public class OffersEvents_fragment extends Fragment {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;
    private AppCompatEditText edt_type, edt_location;

    private ArrayList<PowerMenuItem> powerMenuItems;
    private PowerMenu iconMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers_events, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        tabs = rootView.findViewById(R.id.tabs);
        viewpager = rootView.findViewById(R.id.viewpager);
        edt_type = rootView.findViewById(R.id.edt_type);
        edt_location = rootView.findViewById(R.id.edt_location);

        powerMenuItems = new ArrayList<>();
        powerMenuItems.add(new PowerMenuItem("Free Events"));
        powerMenuItems.add(new PowerMenuItem("Paid Events"));
    }

    private void setDefault() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new Offers_Fragment(), "Offers");
        adapter.addFrag(new Events_Fragment(), "Events");
        viewpager.setAdapter(adapter);
        tabs.setViewPager(viewpager);

        edt_type.setVisibility(View.GONE);
    }

    private void getSessionDetails() {

        try {
            UserSessionManager session = new UserSessionManager(context);
            edt_location.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        edt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SelectCity_Activity.class)
                        .putExtra("requestCode", 3));
            }
        });

        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryMenus(powerMenuItems);
            }
        });

        tabs.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                switch (position) {
                    case 0:
                        edt_type.setVisibility(View.GONE);
                        break;
                    case 1:
                        edt_type.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
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

    private void showCategoryMenus(ArrayList<PowerMenuItem> powerMenuItems) {
        iconMenu = new PowerMenu.Builder(context)
                .addItemList(powerMenuItems)
                .setOnMenuItemClickListener(onIconMenuItemClickListener)
                .setAnimation(MenuAnimation.FADE)
                .setMenuEffect(MenuEffect.BODY)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .build();
        iconMenu.showAsDropDown(edt_type);
    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            edt_type.setText(item.getTitle());
            iconMenu.dismiss();
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(new Intent("Events_Fragment")
                            .putExtra("eventCategoryId", String.valueOf(position + 1)));
        }
    };


}
