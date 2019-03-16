package in.oriange.joinsta.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.SearchAdapter;
import in.oriange.joinsta.models.SearchListModel;

public class Search_Fragment extends Fragment {

    private Context context;
    private RecyclerView rv_searchlist;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
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


        rv_searchlist = rootView.findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {

        ArrayList<SearchListModel> searchList = new ArrayList<>();
        searchList.add(new SearchListModel("Joinsta Development", "This is the sub heading", "This is the sub sub heading", "0"));
        searchList.add(new SearchListModel("Joinsta Development", "This is the sub heading", "This is the sub sub heading", "1"));
        searchList.add(new SearchListModel("Joinsta Development", "This is the sub heading", "This is the sub sub heading", "0"));
        searchList.add(new SearchListModel("Joinsta Development", "This is the sub heading", "This is the sub sub heading", "0"));

        rv_searchlist.setAdapter(new SearchAdapter(context, searchList));
    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {

    }
}
