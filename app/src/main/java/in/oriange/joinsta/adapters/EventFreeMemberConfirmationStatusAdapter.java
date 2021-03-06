package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.EventFreeMemberStatusModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class EventFreeMemberConfirmationStatusAdapter extends RecyclerView.Adapter<EventFreeMemberConfirmationStatusAdapter.MyViewHolder> {

    private Context context;
    private List<EventFreeMemberStatusModel.ResultBean> statusList;

    public EventFreeMemberConfirmationStatusAdapter(Context context, List<EventFreeMemberStatusModel.ResultBean> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_events_member_status, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {

        final int position = holder.getAdapterPosition();
        final EventFreeMemberStatusModel.ResultBean memberDetails = statusList.get(position);

        holder.tv_name.setText(memberDetails.getFirst_name());
        holder.tv_mobile_email.setText(memberDetails.getMobile());

        holder.ib_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    provideCallPremission(context);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to make a call?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.icon_call);
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + memberDetails.getMobile())));
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            }
        });

        switch (memberDetails.getStatus()) {
            case "accepted":
                holder.tv_status.setText("Accepted");
                holder.cv_main_layout.setCardBackgroundColor(context.getResources().getColor(R.color.lightgreen));
                break;
            case "maybe":
                holder.cv_main_layout.setCardBackgroundColor(context.getResources().getColor(R.color.lightyellow));
                holder.tv_status.setText("May Be");
                break;
            case "rejected":
                holder.cv_main_layout.setCardBackgroundColor(context.getResources().getColor(R.color.lightred));
                holder.tv_status.setText("Rejected");
                break;
            case "deleted":
                holder.cv_main_layout.setCardBackgroundColor(context.getResources().getColor(R.color.lightblue));
                holder.tv_status.setText("Deleted");
                break;
            case "":
                holder.tv_status.setText("Pending");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_main_layout;
        private TextView tv_name, tv_mobile_email, tv_status;
        private ImageButton ib_call;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_main_layout = view.findViewById(R.id.cv_main_layout);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mobile_email = view.findViewById(R.id.tv_mobile_email);
            tv_status = view.findViewById(R.id.tv_status);
            ib_call = view.findViewById(R.id.ib_call);


        }
    }
}
