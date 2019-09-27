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
import in.oriange.joinsta.utilities.ApplicationConstants;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("");

        switch (position) {
            case 0:
                Picasso.with(context)
                        .load("http://joinsta.in/images/21/uplimg-20190706060849.png")
                        .into(viewHolder.imageViewBackground);
                break;
            case 1:
                Picasso.with(context)
                        .load("http://joinsta.in/images/12/uplimg-20190807075411.png")
                        .into(viewHolder.imageViewBackground);
                break;
            case 2:
                Picasso.with(context)
                        .load("http://joinsta.in/images/184/uplimg-20190810071653.png")
                        .into(viewHolder.imageViewBackground);
                break;
            case 4:
                Picasso.with(context)
                        .load("http://joinsta.in/images/535/uplimg-20190914081001.png")
                        .into(viewHolder.imageViewBackground);
                break;
            default:
                Picasso.with(context)
                        .load("http://joinsta.in/images/324/uplimg-20190822083325.png")
                        .into(viewHolder.imageViewBackground);
                break;

        }

    }

    @Override
    public int getCount() {
        return 5;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}