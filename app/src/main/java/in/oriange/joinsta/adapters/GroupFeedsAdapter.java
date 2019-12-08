package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupFeedsComments_Activity;
import in.oriange.joinsta.models.GroupFeedsModel;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class GroupFeedsAdapter extends RecyclerView.Adapter<GroupFeedsAdapter.MyViewHolder> {

    private Context context;
    private List<GroupFeedsModel.ResultBean> feedsList;
    private PrettyTime p;

    public GroupFeedsAdapter(Context context, List<GroupFeedsModel.ResultBean> feedsList) {
        this.context = context;
        this.feedsList = feedsList;
        p = new PrettyTime();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_feeds, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final GroupFeedsModel.ResultBean feedDetails = feedsList.get(position);

        if (!feedDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(feedDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(100, 100)
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(feedDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(feedDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tv_feed_text.setText(feedDetails.getFeed_text());

        if (!feedDetails.getFeed_doc().equals("")) {
            String url = IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc();
            Picasso.with(context)
                    .load(url)
                    .resize(450, 300)
                    .into(holder.imv_feed_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.cv_feed_image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.cv_feed_image.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.cv_feed_image.setVisibility(View.GONE);
        }

        if (feedDetails.getFeed_comments().size() != 0) {
            if (feedDetails.getFeed_comments().size() == 1) {
                holder.btn_comment.setText("1 Comment");
            } else {
                holder.btn_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
            }
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });

        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GroupFeedsComments_Activity.class)
                        .putExtra("feedDetails", feedDetails));
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imv_user;
        private TextView tv_name, tv_time, tv_feed_text;
        private CardView cv_mainlayout, cv_feed_image;
        private ImageView imv_feed_image;
        private Button btn_comment, btn_share;

        public MyViewHolder(@NonNull View view) {
            super(view);
            imv_user = view.findViewById(R.id.imv_user);
            tv_name = view.findViewById(R.id.tv_name);
            tv_feed_text = view.findViewById(R.id.tv_feed_text);
            tv_time = view.findViewById(R.id.tv_time);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            cv_feed_image = view.findViewById(R.id.cv_feed_image);
            imv_feed_image = view.findViewById(R.id.imv_feed_image);
            btn_comment = view.findViewById(R.id.btn_comment);
            btn_share = view.findViewById(R.id.btn_share);

        }
    }
}
