package in.oriange.joinsta.adapters;

import android.content.Context;
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
import in.oriange.joinsta.models.GroupFeedsModel;

public class GroupFeedsCommentsRepliesAdapter extends RecyclerView.Adapter<GroupFeedsCommentsRepliesAdapter.MyViewHolder> {

    private Context context;
    private List<GroupFeedsModel.ResultBean.FeedCommentsBean.CommentReplyBean> commentReplyList;
    private PrettyTime p;

    public GroupFeedsCommentsRepliesAdapter(Context context, List<GroupFeedsModel.ResultBean.FeedCommentsBean.CommentReplyBean> commentReplyList) {
        this.context = context;
        this.commentReplyList = commentReplyList;
        p = new PrettyTime();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_feeds_comments_replies, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final GroupFeedsModel.ResultBean.FeedCommentsBean.CommentReplyBean replyDetails = commentReplyList.get(position);

        if (!replyDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(replyDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)// resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(replyDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(replyDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tv_reply.setText(replyDetails.getMessage());
    }

    @Override
    public int getItemCount() {
        return commentReplyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_mainlayout;
        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_reply;

        public MyViewHolder(@NonNull View view) {
            super(view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_time = view.findViewById(R.id.tv_time);
            tv_reply = view.findViewById(R.id.tv_reply);
        }
    }
}
