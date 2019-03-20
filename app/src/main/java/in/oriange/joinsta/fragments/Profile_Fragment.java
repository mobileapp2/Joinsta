package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.Settings_Activity;
import in.oriange.joinsta.adapters.ProfileViewAdapter;
import in.oriange.joinsta.models.ProfileListModel;

public class Profile_Fragment extends Fragment {

    private Context context;
    private RecyclerView rv_profilelist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        rv_profilelist = rootView.findViewById(R.id.rv_profilelist);
        rv_profilelist.setLayoutManager(new LinearLayoutManager(context));

    }

    private void setDefault() {
        ArrayList<ProfileListModel> profileList = new ArrayList<>();
        profileList.add(new ProfileListModel("Oriange", "Business, Service Peovider"));
        profileList.add(new ProfileListModel("Oriange", "Business, Service Peovider"));

        rv_profilelist.setAdapter(new ProfileViewAdapter(context, profileList));

    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menus_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                break;
            case R.id.action_settings:
                startActivity(new Intent(context, Settings_Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
