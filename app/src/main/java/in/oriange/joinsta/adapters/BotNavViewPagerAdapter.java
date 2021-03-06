package in.oriange.joinsta.adapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.fragments.Home_Fragment;
import in.oriange.joinsta.fragments.More_Fragment;
import in.oriange.joinsta.fragments.OffersEvents_fragment;
import in.oriange.joinsta.fragments.Search_Fragment;

public class BotNavViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public BotNavViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(new Home_Fragment());
        fragments.add(new Search_Fragment());
        fragments.add(new Groups_Fragment());
        fragments.add(new OffersEvents_fragment());
        fragments.add(new More_Fragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}