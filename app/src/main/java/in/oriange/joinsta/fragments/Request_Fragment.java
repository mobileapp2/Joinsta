package in.oriange.joinsta.fragments;

import android.content.Context;
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
import in.oriange.joinsta.adapters.RequirementAdapter;
import in.oriange.joinsta.models.RequirementListModel;

public class Request_Fragment extends Fragment {

    private Context context;
    private RecyclerView rv_requirementlist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
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

        rv_requirementlist = rootView.findViewById(R.id.rv_requirementlist);
        rv_requirementlist.setLayoutManager(new LinearLayoutManager(context));

    }

    private void setDefault() {
        ArrayList<RequirementListModel> requirementList = new ArrayList<>();
        requirementList.add(new RequirementListModel("Web Dev", "Varsha Swami", "3 months ago", "Latur"));
        requirementList.add(new RequirementListModel("Web Site", "Varsha Swami", "3 months ago", "Latur"));
        requirementList.add(new RequirementListModel("Android Dev", "Varsha Swami", "3 months ago", "Latur"));
        requirementList.add(new RequirementListModel("IOS Dev", "Varsha Swami", "3 months ago", "Latur"));

        rv_requirementlist.setAdapter(new RequirementAdapter(context, requirementList));

    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menus_requirement, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
