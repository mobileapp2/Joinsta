package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.AllEventNotificationListModel;

public class AllEventNotificationParentAdapter extends RecyclerView.Adapter<AllEventNotificationParentAdapter.MyViewHolder> {
    
    private Context context;
    private List<AllEventNotificationListModel.ResultBean> notificationList;

    public AllEventNotificationParentAdapter(Context context, List<AllEventNotificationListModel.ResultBean> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_allgroupnotification, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        AllEventNotificationListModel.ResultBean notificationDetails = notificationList.get(position);

        holder.tv_groupname.setText(notificationDetails.getEvent_name());

        holder.rv_notifications.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_notifications.setAdapter(new AllEventNotificationChildAdapter(context, notificationDetails.getEvent_member_details()));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_groupname;
        private RecyclerView rv_notifications;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tv_groupname = view.findViewById(R.id.tv_groupname);
            rv_notifications = view.findViewById(R.id.rv_notifications);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
