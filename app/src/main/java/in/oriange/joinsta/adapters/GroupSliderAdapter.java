package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import in.oriange.joinsta.R;

public class GroupSliderAdapter extends SliderViewAdapter<GroupSliderAdapter.SliderAdapterVH> {

    private Context context;
//    private List<BannerListModel.ResultBean> bannerList;

    public GroupSliderAdapter(Context context/*, List<BannerListModel.ResultBean> bannerList*/) {
        this.context = context;
//        this.bannerList = bannerList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_banners, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, int position) {
//        BannerListModel.ResultBean bannerDetails = bannerList.get(position);

//        holder.textViewDescription.setText(bannerDetails.getBanner_description());

        switch (position) {
            case 0:
                Picasso.with(context)
//                .load(bannerDetails.getBanners_image())
                        .load("http://joinsta.in/images/184/uplimg-20190810071653.png")
                        .into(holder.imageViewBackground);
                break;
            case 1:
                Picasso.with(context)
                        .load("http://joinsta.in/images/535/uplimg-20190914081001.png")
                        .into(holder.imageViewBackground);
            case 2:
                Picasso.with(context)
                        .load("http://joinsta.in/images/12/uplimg-20190807075411.png")
                        .into(holder.imageViewBackground);
                break;
        }
    }

    @Override
    public int getCount() {
        return 3;
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