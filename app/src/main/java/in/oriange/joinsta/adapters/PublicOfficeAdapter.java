package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewPublicOffice_Activity;
import in.oriange.joinsta.models.PublicOfficeModel;

public class PublicOfficeAdapter extends RecyclerView.Adapter<PublicOfficeAdapter.MyViewHolder> {

    private Context context;
    private List<PublicOfficeModel.ResultBean> officeList;
    private int callType;    //    1 == My Public Office,  2 == Location-wise Public Office  3 == Approval Request

    public PublicOfficeAdapter(Context context, List<PublicOfficeModel.ResultBean> officeList, int callType) {
        this.context = context;
        this.officeList = officeList;
        this.callType = callType;
    }

    @NonNull
    @Override
    public PublicOfficeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_public_office, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicOfficeAdapter.MyViewHolder holder, int pos) {
        int postition = holder.getAdapterPosition();
        PublicOfficeModel.ResultBean publicOffice = officeList.get(postition);

        holder.tv_name.setText(publicOffice.getName());

        if (!publicOffice.getTypeSubType().equals(""))
            holder.tv_type_subtype.setText(publicOffice.getTypeSubType());
        else
            holder.tv_type_subtype.setVisibility(View.GONE);

        if (!publicOffice.getLocal_name().equals(""))
            holder.tv_local_name.setText(publicOffice.getLocal_name());
        else
            holder.tv_local_name.setVisibility(View.GONE);
        
        if (publicOffice.getTotal_number_review().equals("0")) {
            holder.rl_rating.setVisibility(View.GONE);
        } else {
            holder.rl_rating.setVisibility(View.VISIBLE);
            float averageRating = Float.parseFloat(publicOffice.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));
            holder.tv_total_rating.setText(String.valueOf(averageRating));
            holder.tv_total_reviews.setText("(" + publicOffice.getTotal_number_review() + ")");
            holder.rb_feedback_stars.setRating(averageRating);
        }

        holder.cv_mainlayout.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ViewPublicOffice_Activity.class)
                    .putExtra("publicOfficeDetails", publicOffice)
                    .putExtra("callType", callType));
        });
    }

    @Override
    public int getItemCount() {
        return officeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_name, tv_type_subtype, tv_local_name, tv_total_reviews, tv_total_rating;
        private RatingBar rb_feedback_stars;
        private RelativeLayout rl_rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_type_subtype = itemView.findViewById(R.id.tv_type_subtype);
            tv_local_name = itemView.findViewById(R.id.tv_local_name);
            tv_total_reviews = itemView.findViewById(R.id.tv_total_reviews);
            tv_total_rating = itemView.findViewById(R.id.tv_total_rating);
            rb_feedback_stars = itemView.findViewById(R.id.rb_feedback_stars);
            rl_rating = itemView.findViewById(R.id.rl_rating);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
