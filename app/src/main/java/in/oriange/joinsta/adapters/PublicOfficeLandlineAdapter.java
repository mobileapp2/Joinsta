package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.PublicOfficeModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class PublicOfficeLandlineAdapter extends RecyclerView.Adapter<PublicOfficeLandlineAdapter.MyViewHolder> {

    private Context context;
    private List<PublicOfficeModel.ResultBean.LandlineNumberBean> resultList;

    public PublicOfficeLandlineAdapter(Context context, List<PublicOfficeModel.ResultBean.LandlineNumberBean> resultList) {
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

        final PublicOfficeModel.ResultBean.LandlineNumberBean landlineDetails = resultList.get(position);

        holder.tv_mobile.setText(landlineDetails.getCountry_code() + landlineDetails.getLandlinenumbers());

        holder.tv_mobile.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builder.setMessage("Are you sure you want to make a call?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.icon_call);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", (dialog, id) -> context.startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + landlineDetails.getCountry_code() + landlineDetails.getLandlinenumbers()))));
            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertD = builder.create();
            alertD.show();
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
