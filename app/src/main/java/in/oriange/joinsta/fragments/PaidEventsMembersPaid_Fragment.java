package in.oriange.joinsta.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.PaidEventsMembersPaidAdapter;
import in.oriange.joinsta.models.EventPaidMemberStatusModel;

public class PaidEventsMembersPaid_Fragment extends Fragment {

    private Context context;
    private EditText edt_search;
    private RecyclerView rv_members;

    private List<EventPaidMemberStatusModel.ResultBean> membersList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paid_events_members_paid, container, false);
        context = getActivity();

        init(rootView);
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        edt_search = rootView.findViewById(R.id.edt_search);
        rv_members = rootView.findViewById(R.id.rv_members);
        rv_members.setLayoutManager(new LinearLayoutManager(context));

        membersList = new ArrayList<>();
    }

    private void setDefault() {
        membersList = (List<EventPaidMemberStatusModel.ResultBean>) this.getArguments().getSerializable("membersList");

        rv_members.setAdapter(new PaidEventsMembersPaidAdapter(context, membersList));
    }

    private void setEventHandler() {
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (query.toString().isEmpty()) {
                    rv_members.setAdapter(new PaidEventsMembersPaidAdapter(context, membersList));
                    return;
                }

                if (membersList.size() == 0) {
                    rv_members.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<EventPaidMemberStatusModel.ResultBean> memberSearchedList = new ArrayList<>();
                    for (EventPaidMemberStatusModel.ResultBean groupsDetails : membersList) {

                        String memberToBeSearched = groupsDetails.getFirst_name().toLowerCase() +
                                groupsDetails.getMobile().toLowerCase() +
                                groupsDetails.getEmail().toLowerCase();

                        if (memberToBeSearched.contains(query.toString().toLowerCase())) {
                            memberSearchedList.add(groupsDetails);
                        }
                    }
                    rv_members.setAdapter(new PaidEventsMembersPaidAdapter(context, memberSearchedList));
                } else {
                    rv_members.setAdapter(new PaidEventsMembersPaidAdapter(context, membersList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
