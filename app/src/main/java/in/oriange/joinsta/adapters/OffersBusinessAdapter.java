package in.oriange.joinsta.adapters;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import java.util.Calendar;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewOfferDetails_Activity;
import in.oriange.joinsta.models.BannerListModel;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.models.OfferDetailsModel;
import in.oriange.joinsta.utilities.Utilities;
import jp.shts.android.library.TriangleLabelView;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.diffBetweenTwoDates;

public class OffersBusinessAdapter extends RecyclerView.Adapter<OffersBusinessAdapter.MyViewHolder> {

    private Context context;
    private List<OfferDetailsModel.ResultBean.BusinessesBean> businessOffersList;
    private int mYear, mMonth, mDay;
    private String startDate;

    public OffersBusinessAdapter(Context context, List<OfferDetailsModel.ResultBean.BusinessesBean> businessOffersList) {
        this.context = context;
        this.businessOffersList = businessOffersList;

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        startDate = mYear + "-" + mMonth + "-" + mDay;
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

        if (diffBetweenTwoDates(startDate, offerDetails.getCreated_at()) <= 2) {
            holder.tv_new.setVisibility(View.VISIBLE);
        }

        if (!offerDetails.getBusiness_name().isEmpty() && !offerDetails.getCategory_name().isEmpty()) {
            holder.tv_business_name.setText(Html.fromHtml("<font color=\"#EF6C00\"> <b>" + offerDetails.getBusiness_name() + "</b> </font>" + " (" + offerDetails.getCategory_name() + ")"));
        } else if (offerDetails.getBusiness_name().isEmpty() && offerDetails.getCategory_name().isEmpty()) {
            holder.tv_business_name.setVisibility(View.GONE);
        } else if (!offerDetails.getBusiness_name().isEmpty()) {
            holder.tv_business_name.setText(Html.fromHtml("<font color=\"#EF6C00\"> <b>" + offerDetails.getBusiness_name() + "</b> </font>"));
        }

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

        holder.tv_validity.setText(Html.fromHtml("Valid upto " + "<font color=\"#EF6C00\"> <b>" + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()) + "</b> </font>"));

        if (!offerDetails.getPromo_code().equals("")) {
            holder.tv_promo_code.setText(offerDetails.getPromo_code());
        } else {
            holder.cv_promo_code.setVisibility(View.GONE);
        }

        holder.cv_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(offerDetails.getPromo_code());
                Utilities.showMessage("Promo code copied to clipboard", context, 1);
            }
        });


        List<MyOffersListModel.ResultBean.DocumentsBean> documentsList = new ArrayList<>();

        for (int i = 0; i < offerDetails.getOffer_documents().size(); i++) {
            documentsList.add(new MyOffersListModel.ResultBean.DocumentsBean(offerDetails.getOffer_documents().get(i).getDocument()));
        }

        if (documentsList.size() != 0) {

            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
            for (int i = 0; i < documentsList.size(); i++) {
                bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "offerdoc/business/" + documentsList.get(i).getDocument()));
            }

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


        final MyOffersListModel.ResultBean resultBean = new MyOffersListModel.ResultBean(
                offerDetails.getOffer_id(), "1", offerDetails.getId(),
                offerDetails.getTitle(), offerDetails.getDescription(), offerDetails.getStart_date(),
                offerDetails.getEnd_date(), offerDetails.getUrl(), offerDetails.getPromo_code(),
                offerDetails.getCreated_by(), offerDetails.getUpdated_by(), offerDetails.getCreated_at(),
                offerDetails.getUpdated_at(), offerDetails.getCategory_name(), offerDetails.getBusiness_name(), documentsList
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

        private CardView cv_mainlayout, cv_promo_code;
        private SliderView imageSlider;
        private TriangleLabelView tv_new;
        private TextView tv_business_name, tv_title, tv_description, tv_url, tv_validity, tv_promo_code;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            cv_promo_code = view.findViewById(R.id.cv_promo_code);
            imageSlider = view.findViewById(R.id.imageSlider);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_url = view.findViewById(R.id.tv_url);
            tv_validity = view.findViewById(R.id.tv_validity);
            tv_promo_code = view.findViewById(R.id.tv_promo_code);
            tv_new = view.findViewById(R.id.tv_new);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
