package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.BookOrderBusinessOwnerModel;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class BookOrderStatusAdapter extends RecyclerView.Adapter<BookOrderStatusAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean> statusList;

    public BookOrderStatusAdapter(Context context, List<BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_book_order_status, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean statusDetails = statusList.get(position);

        holder.tv_date.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy", statusDetails.getDate()));
        holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a", statusDetails.getDate()));

        switch (statusDetails.getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                holder.tv_status.setText("Order Added in Cart");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "2":
                holder.tv_status.setText("Order Placed");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "3":
                holder.tv_status.setText("Order Accepted");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "4":
                holder.tv_status.setText("Order in Progress");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "5":
                holder.tv_status.setText("Order Delivered");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "6":
                holder.tv_status.setText("Order Billing");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_outline));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "7":
                holder.tv_status.setText("Order Cancelled");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_filled));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
        }

        if (position == statusList.size() - 1) {
            holder.view_divider.setVisibility(View.GONE);
            if (!statusDetails.getStatus().equals("7")) {
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_circle_filled));
                holder.imv_status.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_status;
        private TextView tv_date, tv_time, tv_status;
        private View view_divider;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_status = itemView.findViewById(R.id.imv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_status = itemView.findViewById(R.id.tv_status);
            view_divider = itemView.findViewById(R.id.view_divider);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
