package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.SubCategotyListModel;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private List<SubCategotyListModel> resultArrayList;
    private Context context;

    public SubCategoryAdapter(Context context, List<SubCategotyListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_sub_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final SubCategotyListModel subCategotyDetails = resultArrayList.get(position);

        holder.tv_categoty.setText(subCategotyDetails.getName());

//        if ((position + 1) == resultArrayList.size()) {
//            holder.view_divider.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_category;
        private TextView tv_categoty;
//        private View view_divider;

        public MyViewHolder(View view) {
            super(view);
            imv_category = view.findViewById(R.id.imv_category);
            tv_categoty = view.findViewById(R.id.tv_categoty);
//            view_divider = view.findViewById(R.id.view_divider);
        }
    }
}