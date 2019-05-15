package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.SearchDetailsModel;

public class SearchAdapterEmployee extends RecyclerView.Adapter<SearchAdapterEmployee.MyViewHolder> {

    private List<SearchDetailsModel.ResultBean.EmployeesBean> resultArrayList;
    private Context context;

    public SearchAdapterEmployee(Context context, List<SearchDetailsModel.ResultBean.EmployeesBean> resultArrayList) {
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
        final SearchDetailsModel.ResultBean.EmployeesBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getDesignation());
        holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());
//        if (profileDetails.getIsFavourite().equals("1")) {
//            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_filled));
//        } else {
//            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_outline));
//        }

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
        }
    }
}
