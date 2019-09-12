package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupMembersProfileDetails_Activity;
import in.oriange.joinsta.models.GroupMemebersListModel;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyViewHolder> {

    private List<GroupMemebersListModel.ResultBean> resultArrayList;
    private Context context;

    public GroupMembersAdapter(Context context, List<GroupMemebersListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_grpmembers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GroupMemebersListModel.ResultBean groupDetails = resultArrayList.get(position);

        if (!groupDetails.getFirst_name().trim().isEmpty()) {
            holder.tv_initletter.setText(groupDetails.getFirst_name().trim().substring(0, 1).toUpperCase());
        }
        holder.tv_name.setText(groupDetails.getFirst_name().trim());
        holder.tv_mobile.setText(groupDetails.getMobile());

        if (groupDetails.getRole().equalsIgnoreCase("group_admin")) {
            holder.tv_role.setVisibility(View.VISIBLE);
            holder.tv_role.setText("Admin");
        } else if (groupDetails.getRole().equalsIgnoreCase("group_supervisor")) {
            holder.tv_role.setVisibility(View.VISIBLE);
            holder.tv_role.setText("Supervisor");
        } else if (groupDetails.getRole().equalsIgnoreCase("group_member")) {
            holder.tv_role.setVisibility(View.VISIBLE);
            holder.tv_role.setText("Member");
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupMembersProfileDetails_Activity.class)
                        .putExtra("userId", "1")
                        .putExtra("name", groupDetails.getFirst_name().trim()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_initletter, tv_name, tv_role, tv_mobile;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_initletter = view.findViewById(R.id.tv_initletter);
            tv_name = view.findViewById(R.id.tv_name);
            tv_role = view.findViewById(R.id.tv_role);
            tv_mobile = view.findViewById(R.id.tv_mobile);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
