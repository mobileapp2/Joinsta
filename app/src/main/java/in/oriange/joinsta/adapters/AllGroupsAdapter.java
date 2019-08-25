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
import in.oriange.joinsta.activities.GroupDetails_Activity;
import in.oriange.joinsta.models.AllGroupsListModel;

public class AllGroupsAdapter extends RecyclerView.Adapter<AllGroupsAdapter.MyViewHolder> {

    private List<AllGroupsListModel.ResultBean> resultArrayList;
    private Context context;

    public AllGroupsAdapter(Context context, List<AllGroupsListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_allgroups, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final AllGroupsListModel.ResultBean groupDetails = resultArrayList.get(position);

        holder.tv_heading.setText(groupDetails.getGroup_name() + " (" + groupDetails.getGroup_code() + ")");

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupDetails_Activity.class)
                        .putExtra("groupDetails", groupDetails));
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_heading;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_heading = view.findViewById(R.id.tv_heading);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
