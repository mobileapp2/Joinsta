package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupFeedsCommentsReplys_Activity;
import in.oriange.joinsta.models.GroupFeedsModel;

public class GroupFeedsCommentsAdapter extends RecyclerView.Adapter<GroupFeedsCommentsAdapter.MyViewHolder> {

    private Context context;
    private List<GroupFeedsModel.ResultBean.FeedCommentsBean> feedCommentsList;
    private PrettyTime p;

    public GroupFeedsCommentsAdapter(Context context, List<GroupFeedsModel.ResultBean.FeedCommentsBean> feedCommentsList) {
        this.context = context;
        this.feedCommentsList = feedCommentsList;
        p = new PrettyTime();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_feeds_comments, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final GroupFeedsModel.ResultBean.FeedCommentsBean commentsDetails = feedCommentsList.get(position);

        if (!commentsDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(commentsDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(100, 100)
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(commentsDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(commentsDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tv_comment.setText(commentsDetails.getMessage());

        if (commentsDetails.getComment_reply().size() != 0) {
            holder.btn_replies.setVisibility(View.VISIBLE);
            if (commentsDetails.getComment_reply().size() == 1) {
                holder.btn_replies.setText("1 Reply");
            } else {
                holder.btn_replies.setText(commentsDetails.getComment_reply().size() + " Replies");
            }
        } else {
            holder.btn_replies.setVisibility(View.GONE);
        }

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupFeedsCommentsReplys_Activity.class)
                        .putExtra("commentsDetails", commentsDetails));
            }
        });

        holder.btn_replies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupFeedsCommentsReplys_Activity.class)
                        .putExtra("commentsDetails", commentsDetails));
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedCommentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_mainlayout;
        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_comment;
        private Button btn_replies;

        public MyViewHolder(@NonNull View view) {
            super(view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_time = view.findViewById(R.id.tv_time);
            tv_comment = view.findViewById(R.id.tv_comment);
            btn_replies = view.findViewById(R.id.btn_replies);
        }
    }
}
