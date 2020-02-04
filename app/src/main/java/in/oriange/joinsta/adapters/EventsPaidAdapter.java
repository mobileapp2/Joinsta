package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewEventsPaid_Activity;
import in.oriange.joinsta.models.EventsPaidModel;

public class EventsPaidAdapter extends RecyclerView.Adapter<EventsPaidAdapter.MyViewHolder> {


    private Context context;
    private List<EventsPaidModel.ResultBean> eventList;
    private String groupId;


    public EventsPaidAdapter(Context context, List<EventsPaidModel.ResultBean> eventList, String groupId) {
        this.context = context;
        this.eventList = eventList;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_events_paid, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();

        final EventsPaidModel.ResultBean eventDetails = eventList.get(position);

        holder.tv_title.setText(eventDetails.getEvent_code() + " - " + eventDetails.getName());
        holder.tv_description.setText(eventDetails.getDescription());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewEventsPaid_Activity.class)
                        .putExtra("eventDetails", eventDetails));
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_title, tv_description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
