package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import in.oriange.joinsta.activities.ViewEventsPaid_Activity;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.EventsPaidModel;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class EventsPaidAdapter extends RecyclerView.Adapter<EventsPaidAdapter.MyViewHolder> {

    private Context context;
    private List<EventsPaidModel.ResultBean> eventList;
    private String isAdmin;
    private boolean isMyEvent;

    public EventsPaidAdapter(Context context, List<EventsPaidModel.ResultBean> eventList, String isAdmin, boolean isMyEvent) {
        this.context = context;
        this.eventList = eventList;
        this.isAdmin = isAdmin;
        this.isMyEvent = isMyEvent;
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
        holder.tv_venue.setText(eventDetails.getVenue_address());
        holder.tv_time.setText(eventDetails.getDateTime());

        if (!isMyEvent)
            if (eventDetails.getIs_early_payment_applicable().equals("1")) {
                holder.tv_saved.setVisibility(View.VISIBLE);

                int actualEarlybirdPrice = Integer.parseInt(eventDetails.getEarlybird_price());
                int actualNormalPrice = Integer.parseInt(eventDetails.getNormal_price());
                int savedAmount = actualNormalPrice - actualEarlybirdPrice;

                holder.tv_saved.setText(Html.fromHtml("<strike>₹ " + actualNormalPrice + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹ " + savedAmount + "</i></font>"));
                holder.tv_total_price.setText(Html.fromHtml("₹ " + actualEarlybirdPrice));
                holder.tv_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getEarlybird_price_duedate()));

            } else {
                holder.tv_saved.setVisibility(View.GONE);
                holder.tv_total_price.setText(Html.fromHtml("₹ " + Integer.parseInt(eventDetails.getNormal_price())));
                holder.tv_due_date.setText("Due Date: " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", eventDetails.getNormal_price_duedate()));
            }
        else
            holder.ll_prices.setVisibility(View.GONE);

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
                context.startActivity(new Intent(context, ViewEventsPaid_Activity.class)
                        .putExtra("isMyEvent", isMyEvent)
                        .putExtra("eventDetails", eventDetails)
                        .putExtra("isAdmin", isAdmin));
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
        private LinearLayout ll_prices;
        private TextView tv_title, tv_venue, tv_time, tv_saved, tv_total_price, tv_due_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_venue = itemView.findViewById(R.id.tv_venue);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_saved = itemView.findViewById(R.id.tv_saved);
            tv_total_price = itemView.findViewById(R.id.tv_total_price);
            tv_due_date = itemView.findViewById(R.id.tv_due_date);
            ll_prices = itemView.findViewById(R.id.ll_prices);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
