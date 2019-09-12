package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewGroupMemberBizDetails_Activity;
import in.oriange.joinsta.models.GetBusinessModel;

public class GroupMemberBusinessAdapter extends RecyclerView.Adapter<GroupMemberBusinessAdapter.MyViewHolder> {

    private Context context;
    private List<GetBusinessModel.ResultBean> resultArrayList;

    public GroupMemberBusinessAdapter(Context context, List<GetBusinessModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GetBusinessModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getBusiness_name());

        if (searchDetails.getTag().get(0) != null) {
            if (searchDetails.getTag().get(0).size() > 0) {
                StringBuilder tag = new StringBuilder();
                try {
                    for (int i = 0; i < 4; i++) {
                        tag.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                    }
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                } catch (IndexOutOfBoundsException e) {
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                }

            } else {
                if (!searchDetails.getSubtype_description().isEmpty())
                    holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
                else
                    holder.tv_subheading.setText(searchDetails.getType_description());
            }
        } else {
            if (!searchDetails.getType_description().isEmpty() && !searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
            } else if (searchDetails.getType_description().isEmpty() && searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setVisibility(View.GONE);
            } else if (!searchDetails.getType_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getType_description());
            } else if (!searchDetails.getSubtype_description().isEmpty()) {
                holder.tv_subheading.setText(searchDetails.getSubtype_description());
            }

        }

        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewGroupMemberBizDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
