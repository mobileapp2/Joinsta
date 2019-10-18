package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.DUMMYGroupMembers;

public class GroupMembersSupervisorsAdapter extends RecyclerView.Adapter<GroupMembersSupervisorsAdapter.MyViewHolder> {

    private Context context;
    private List<DUMMYGroupMembers> groupMembers;

    public GroupMembersSupervisorsAdapter(Context context, List<DUMMYGroupMembers> groupMembers) {
        this.context = context;
        this.groupMembers = groupMembers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_group_membersuperviosr, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final DUMMYGroupMembers groupDetails = groupMembers.get(position);

        holder.tv_name.setText(groupDetails.getName());

        if (!groupDetails.getMobile().isEmpty() && !groupDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(groupDetails.getMobile() + " | " + groupDetails.getEmail());
        } else if (!groupDetails.getMobile().isEmpty() && groupDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(groupDetails.getMobile());
        } else if (groupDetails.getMobile().isEmpty() && !groupDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setText(groupDetails.getEmail());
        } else if (groupDetails.getMobile().isEmpty() && groupDetails.getEmail().isEmpty()) {
            holder.tv_mobile_email.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_mobile_email;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile_email = view.findViewById(R.id.tv_mobile_email);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
