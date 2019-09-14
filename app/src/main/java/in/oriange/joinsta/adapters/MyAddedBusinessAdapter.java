package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewMyBizDetails_Activity;
import in.oriange.joinsta.models.GetBusinessModel;

public class MyAddedBusinessAdapter extends RecyclerView.Adapter<MyAddedBusinessAdapter.MyViewHolder> {

    private List<GetBusinessModel.ResultBean> resultArrayList;
    private Context context;

    public MyAddedBusinessAdapter(Context context, List<GetBusinessModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_myadded, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GetBusinessModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());

        if (!searchDetails.getSubtype_description().isEmpty())
            holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
        else
            holder.tv_subheading.setText(searchDetails.getType_description());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewMyBizDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_heading, tv_subheading;

        public MyViewHolder(View view) {
            super(view);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
