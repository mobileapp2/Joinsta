package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewSearchProfDetails_Activity;
import in.oriange.joinsta.models.SearchDetailsModel;

public class SearchProfessionalAdapter extends RecyclerView.Adapter<SearchProfessionalAdapter.MyViewHolder> {

    private List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList;
    private Context context;
    private String type;            //  1 = from search  // 2 = from favorite  // 3 = from home

    public SearchProfessionalAdapter(Context context, List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList, String type) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.type = type;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final SearchDetailsModel.ResultBean.ProfessionalsBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getFirm_name());
        holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewSearchProfDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails)
                        .putExtra("type", type));
            }
        });

        float scale = context.getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (5 * scale + 0.5f);
        if (!searchDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(searchDetails.getImage_url().trim())
                    .into(holder.imv_preview, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_preview.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_preview.setVisibility(View.VISIBLE);
                            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.imv_preview.setVisibility(View.VISIBLE);
            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
        }

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview;
        private CardView cv_mainlayout;
        private ProgressBar progressBar;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

}
