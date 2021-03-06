package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EventFreeMemberStatus_Activity;
import in.oriange.joinsta.activities.ViewEventsFree_Activity;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.EventsFreeModel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class EventsFreeAdapter extends RecyclerView.Adapter<EventsFreeAdapter.MyViewHolder> {

    private Context context;
    private List<EventsFreeModel.ResultBean> eventList;
    private String isAdmin;
    private int callType; //1 = Group Events,  2 = Offer section events  3 = My Events

    public EventsFreeAdapter(Context context, List<EventsFreeModel.ResultBean> eventList, String isAdmin, int callType) {
        this.context = context;
        this.eventList = eventList;
        this.isAdmin = isAdmin;
        this.callType = callType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_events_free, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();

        final EventsFreeModel.ResultBean eventDetails = eventList.get(position);

        holder.tv_title.setText(eventDetails.getEvent_code() + " - " + eventDetails.getName());
        holder.tv_venue.setText(eventDetails.getVenue_address());
        holder.tv_time.setText(eventDetails.getDateTime());

        if (isAdmin.equals("1"))
            holder.tv_confirmation_status.setVisibility(View.VISIBLE);
        else
            holder.tv_confirmation_status.setVisibility(View.GONE);

        if (eventDetails.getDocuments().size() != 0) {

            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();

            for (int i = 0; i < eventDetails.getDocuments().size(); i++) {
                if (eventDetails.getDocuments().get(i).getDocument_type().equalsIgnoreCase("invitationimage")) {
                    bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "events/invitation_image/" + eventDetails.getDocuments().get(i).getDocument_path()));
                }
            }
            if (bannerList.size() != 0) {
                OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, bannerList);
                holder.imageSlider.setSliderAdapter(adapter);
                holder.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                holder.imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                holder.imageSlider.setIndicatorSelectedColor(Color.WHITE);
                holder.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                holder.imageSlider.setAutoCycle(true);
                holder.imageSlider.setScrollTimeInSec(10);
            } else {
                holder.imageSlider.setVisibility(View.GONE);
            }
        } else {
            holder.imageSlider.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewEventsFree_Activity.class)
                        .putExtra("callType", callType)
                        .putExtra("eventDetails", eventDetails)
                        .putExtra("isAdmin", isAdmin));
            }
        });

        holder.tv_confirmation_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EventFreeMemberStatus_Activity.class)
                        .putExtra("eventId", eventDetails.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private SliderView imageSlider;
        private TextView tv_title, tv_venue, tv_time, tv_confirmation_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_venue = itemView.findViewById(R.id.tv_venue);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_confirmation_status = itemView.findViewById(R.id.tv_confirmation_status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
