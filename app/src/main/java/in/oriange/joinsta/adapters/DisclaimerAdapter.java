package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.DisclaimerModel;

public class DisclaimerAdapter extends RecyclerView.Adapter<DisclaimerAdapter.MyViewHolder> {

    private Context context;
    private List<DisclaimerModel.ResultBean> disclaimerList;

    public DisclaimerAdapter(Context context, List<DisclaimerModel.ResultBean> disclaimerList) {
        this.context = context;
        this.disclaimerList = disclaimerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_row_disclaimer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        DisclaimerModel.ResultBean disclaimer = disclaimerList.get(position);

        holder.tv_disclaimer.setText(disclaimer.getString());
    }

    @Override
    public int getItemCount() {
        return disclaimerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_disclaimer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_disclaimer = itemView.findViewById(R.id.tv_disclaimer);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
