package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.BookOrderBusinessOwnerModel;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class BookOrderProductsAdapter extends RecyclerView.Adapter<BookOrderProductsAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean> productDetailsList;

    public BookOrderProductsAdapter(Context context, List<BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean> productDetailsList) {
        this.context = context;
        this.productDetailsList = productDetailsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_book_order_owner_products, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean productDetails = productDetailsList.get(position);

        if (productDetails.getProduct_images().size() != 0) {
            String url = IMAGE_LINK + "product/" + productDetails.getProduct_images().get(0);
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_productimage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imv_image_not_available.setVisibility(View.GONE);
                            holder.imv_productimage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.imv_image_not_available.setVisibility(View.VISIBLE);
                            holder.imv_productimage.setVisibility(View.GONE);
                        }
                    });

        } else {
            holder.imv_image_not_available.setVisibility(View.VISIBLE);
            holder.imv_productimage.setVisibility(View.GONE);
        }

        holder.tv_productname.setText(productDetails.getName());
        holder.tv_productinfo.setText(productDetails.getDescription());
        holder.tv_quantity.setText("Quantity - " + productDetails.getQuantity());

        if (productDetails.getAmount().equals("0")) {
            holder.tv_price.setVisibility(View.GONE);
            holder.tv_no_price_available.setVisibility(View.VISIBLE);
        } else {
            holder.tv_no_price_available.setVisibility(View.GONE);
            holder.tv_price.setVisibility(View.VISIBLE);
            holder.tv_price.setText("₹ " + Integer.parseInt(productDetails.getAmount()) + "/" + productDetails.getUnit_of_measure());
        }

        if (holder.tv_productinfo.getText().toString().trim().equals(""))
            holder.tv_productinfo.setVisibility(View.GONE);

        if (position == productDetailsList.size() - 1)
            holder.view_divider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_mainlayout;
        private TextView tv_productname, tv_productinfo, tv_quantity, tv_price, tv_no_price_available;
        private ImageView imv_productimage, imv_image_not_available;
        private View view_divider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_mainlayout = itemView.findViewById(R.id.ll_mainlayout);
            tv_productname = itemView.findViewById(R.id.tv_productname);
            tv_productinfo = itemView.findViewById(R.id.tv_productinfo);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_no_price_available = itemView.findViewById(R.id.tv_no_price_available);
            imv_productimage = itemView.findViewById(R.id.imv_productimage);
            imv_image_not_available = itemView.findViewById(R.id.imv_image_not_available);
            view_divider = itemView.findViewById(R.id.view_divider);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
