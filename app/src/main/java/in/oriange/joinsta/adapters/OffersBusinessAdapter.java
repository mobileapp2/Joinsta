package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewOfferDetails_Activity;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.models.OfferDetailsModel;

public class OffersBusinessAdapter extends RecyclerView.Adapter<OffersBusinessAdapter.MyViewHolder> {

    private Context context;
    private List<OfferDetailsModel.ResultBean.BusinessesBean> businessOffersList;

    public OffersBusinessAdapter(Context context, List<OfferDetailsModel.ResultBean.BusinessesBean> businessOffersList) {
        this.context = context;
        this.businessOffersList = businessOffersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_offers, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final OfferDetailsModel.ResultBean.BusinessesBean offerDetails = businessOffersList.get(position);
        holder.tv_title.setText(offerDetails.getTitle());
        holder.tv_description.setText(offerDetails.getDescription());

        if (!offerDetails.getUrl().trim().isEmpty()) {
            holder.tv_url.setText(offerDetails.getUrl());
            holder.tv_url.setPaintFlags(holder.tv_url.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            holder.tv_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = offerDetails.getUrl();

                    if (!url.startsWith("https://") || !url.startsWith("http://")) {
                        url = "http://" + url;
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });

        } else {
            holder.tv_url.setVisibility(View.GONE);
        }

        List<MyOffersListModel.ResultBean.DocumentsBean> documentsList = new ArrayList<>();

        for (int i = 0; i < offerDetails.getOffer_documents().size(); i++) {
            documentsList.add(new MyOffersListModel.ResultBean.DocumentsBean(offerDetails.getOffer_documents().get(i).getDocument()));
        }

        final MyOffersListModel.ResultBean resultBean = new MyOffersListModel.ResultBean(
                offerDetails.getOffer_id(), "1", offerDetails.getId(),
                offerDetails.getTitle(), offerDetails.getDescription(), offerDetails.getStart_date(),
                offerDetails.getEnd_date(), offerDetails.getUrl(), offerDetails.getPromo_code(),
                offerDetails.getCreated_by(), offerDetails.getUpdated_by(), offerDetails.getCreated_at(),
                offerDetails.getUpdated_at(), documentsList
        );

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewOfferDetails_Activity.class)
                        .putExtra("offerDetails", resultBean)
                        .putExtra("isFromMyOfferOrFromParticularOffer", "2")
                        .putExtra("isRecordAddedByCurrentUserId", "0"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return businessOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_title, tv_description, tv_url;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_url = view.findViewById(R.id.tv_url);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
