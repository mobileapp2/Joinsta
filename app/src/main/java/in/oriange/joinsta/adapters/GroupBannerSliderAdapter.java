package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.GroupBannerListModel;

public class GroupBannerSliderAdapter extends SliderViewAdapter<GroupBannerSliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<GroupBannerListModel.ResultBean> bannerList;

    public GroupBannerSliderAdapter(Context context, List<GroupBannerListModel.ResultBean> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_banners, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, int position) {

        GroupBannerListModel.ResultBean bannerDetails = bannerList.get(position);

        holder.textViewDescription.setText(bannerDetails.getBanner_description());

        Picasso.with(context)
                .load(bannerDetails.getBanners_image())
                .into(holder.imageViewBackground);

    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private ImageView imageViewBackground;
        private TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
        }
    }
}