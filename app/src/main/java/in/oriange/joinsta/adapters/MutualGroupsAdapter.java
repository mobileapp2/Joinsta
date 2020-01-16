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
import in.oriange.joinsta.models.MutualGroupsModel;

public class MutualGroupsAdapter extends RecyclerView.Adapter<MutualGroupsAdapter.MyViewHolder> {

    private Context context;
    private List<MutualGroupsModel> mutualGroupsList;

    public MutualGroupsAdapter(Context context, List<MutualGroupsModel> mutualGroupsList) {
        this.context = context;
        this.mutualGroupsList = mutualGroupsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_mutual_groups, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        MutualGroupsModel mutualGroups = mutualGroupsList.get(position);

        holder.tv_group.setText(mutualGroups.getGroup_code() + " - " + mutualGroups.getGroup_name());
    }

    @Override
    public int getItemCount() {
        return mutualGroupsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_group;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_group = itemView.findViewById(R.id.tv_group);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
