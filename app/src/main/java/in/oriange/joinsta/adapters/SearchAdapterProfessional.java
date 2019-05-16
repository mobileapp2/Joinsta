package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewSearchProfDetails_Activity;
import in.oriange.joinsta.models.SearchDetailsModel;

public class SearchAdapterProfessional extends RecyclerView.Adapter<SearchAdapterProfessional.MyViewHolder> {

    private List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList;
    private Context context;

    public SearchAdapterProfessional(Context context, List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList) {
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
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final SearchDetailsModel.ResultBean.ProfessionalsBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getFirm_name());
        holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());
//        if (profileDetails.getIsFavourite().equals("1")) {
//            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_filled));
//        } else {
//            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_outline));
//        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewSearchProfDetails_Activity.class).putExtra("searchDetails", searchDetails));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview;
        private CardView cv_mainlayout;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
        }
    }
}
