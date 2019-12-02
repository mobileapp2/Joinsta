package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewOfferDetails_Activity;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class MyAddedOffersAdapter extends RecyclerView.Adapter<MyAddedOffersAdapter.MyViewHolder> {

    private Context context;
    private List<MyOffersListModel.ResultBean> myOffersList;
    private String isFromMyOfferOrFromParticularOffer;            //1 = My Offers  2 = Particular record offer
    private String userId;

    public MyAddedOffersAdapter(Context context, List<MyOffersListModel.ResultBean> myOffersList, String isFromMyOfferOrFromParticularOffer) {
        this.context = context;
        this.myOffersList = myOffersList;
        this.isFromMyOfferOrFromParticularOffer = isFromMyOfferOrFromParticularOffer;
        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        final MyOffersListModel.ResultBean offerDetails = myOffersList.get(position);
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

        holder.tv_validity.setText("Valid till " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()));

        if (!offerDetails.getPromo_code().equals("")) {
            holder.tv_promo_code.setText("Promo Code - " + offerDetails.getPromo_code());
        } else {
            holder.tv_promo_code.setText("Promo code not available");
        }

        if (offerDetails.getDocuments().size() != 0) {
            String imageOne = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(0).getDocument();
            Picasso.with(context)
                    .load(imageOne)
                    .into(holder.imv_offer);
        } else {
            holder.imv_offer.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String isRecordAddedByCurrentUserId = "0";            //0 = No  1 = Yes
                if (offerDetails.getCreated_by().equals(userId)) {
                    isRecordAddedByCurrentUserId = "1";
                }

                context.startActivity(new Intent(context, ViewOfferDetails_Activity.class)
                        .putExtra("offerDetails", offerDetails)
                        .putExtra("isFromMyOfferOrFromParticularOffer", isFromMyOfferOrFromParticularOffer)
                        .putExtra("isRecordAddedByCurrentUserId", isRecordAddedByCurrentUserId));

            }
        });

    }

    @Override
    public int getItemCount() {
        return myOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout/*, cv_image*/;
        private TextView tv_title, tv_description, tv_url, tv_validity, tv_promo_code;
        private ImageView imv_offer;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
//            cv_image = view.findViewById(R.id.cv_image);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_url = view.findViewById(R.id.tv_url);
            tv_validity = view.findViewById(R.id.tv_validity);
            tv_promo_code = view.findViewById(R.id.tv_promo_code);
            imv_offer = view.findViewById(R.id.imv_offer);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
