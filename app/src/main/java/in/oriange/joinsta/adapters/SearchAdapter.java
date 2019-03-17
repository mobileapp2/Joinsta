package in.oriange.joinsta.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.SearchListModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private List<SearchListModel> resultArrayList;
    private Context context;

    public SearchAdapter(Context context, List<SearchListModel> resultArrayList) {
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
        final SearchListModel profileDetails = resultArrayList.get(position);

        holder.tv_heading.setText(profileDetails.getType());
        holder.tv_subheading.setText(profileDetails.getSubType());
        holder.tv_subsubheading.setText(profileDetails.getSubSubType());

        if (profileDetails.getIsFavourite().equals("1")) {
            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_filled));
        } else {
            holder.imv_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_fav_outline));
        }

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview, imv_favourite;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            imv_favourite = view.findViewById(R.id.imv_favourite);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
        }
    }
}
