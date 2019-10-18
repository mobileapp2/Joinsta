package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.AddGroupMemberSupervisor_Activity;
import in.oriange.joinsta.adapters.GroupMembersSupervisorsAdapter;
import in.oriange.joinsta.models.DUMMYGroupMembers;

public class AddGroupMembers_Fragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_group_supervisor;
    private FloatingActionButton btn_add;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addgroup_members, container, false);
        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_group_supervisor = rootView.findViewById(R.id.rv_group_supervisor);
        rv_group_supervisor.setLayoutManager(new LinearLayoutManager(context));
        btn_add = rootView.findViewById(R.id.btn_add);
    }

    private void getSessionDetails() {

    }

    private void setDefault() {
        List<DUMMYGroupMembers> groupMembers = new ArrayList<>();
        groupMembers.add(new DUMMYGroupMembers("Mohan Deshmukh", "8149115089", "priyeshpawar07@gmail.com", "1"));
        groupMembers.add(new DUMMYGroupMembers("Test Name One", "7412589630", "testnameone@gmail.com", "1"));
        rv_group_supervisor.setAdapter(new GroupMembersSupervisorsAdapter(context, groupMembers));
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddGroupMemberSupervisor_Activity.class));
            }
        });
    }


}
