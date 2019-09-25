package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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

        holder.tv_name.setText(groupDetails.getFirst_name().trim());
        holder.tv_mobile.setText(groupDetails.getMobile());

        if (!groupDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(groupDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .into(holder.imv_user, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.imv_user.setVisibility(View.VISIBLE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupMembersProfileDetails_Activity.class)
                        .putExtra("userId", groupDetails.getId())
                        .putExtra("name", groupDetails.getFirst_name().trim())
                        .putExtra("imageUrl", groupDetails.getImage_url().trim()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private CircleImageView imv_user;
        private ProgressBar progressBar;
        private TextView tv_name, tv_mobile;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_user = view.findViewById(R.id.imv_user);
            progressBar = view.findViewById(R.id.progressBar);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile = view.findViewById(R.id.tv_mobile);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
