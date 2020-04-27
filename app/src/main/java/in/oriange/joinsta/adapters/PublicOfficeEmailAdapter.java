package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.PublicOfficeModel;

public class PublicOfficeEmailAdapter extends RecyclerView.Adapter<PublicOfficeEmailAdapter.MyViewHolder> {

    private Context context;
    private List<PublicOfficeModel.ResultBean.EmailsBean> resultList;

    public PublicOfficeEmailAdapter(Context context, List<PublicOfficeModel.ResultBean.EmailsBean> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_mobile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();

        final PublicOfficeModel.ResultBean.EmailsBean emailDetails = resultList.get(position);

        holder.tv_mobile.setText(emailDetails.getEmail());
        holder.tv_mobile.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.icon_email), null);

        holder.tv_mobile.setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailDetails.getEmail(), null));
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_mobile;

        public MyViewHolder(View view) {
            super(view);
            tv_mobile = view.findViewById(R.id.tv_mobile);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
