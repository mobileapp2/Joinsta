package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.FullScreenImages_Activity;

import static in.oriange.joinsta.utilities.Utilities.setPaddingForView;

public class BookOrderOrderImagesAdapter extends RecyclerView.Adapter<BookOrderOrderImagesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> orderImagesList;

    public BookOrderOrderImagesAdapter(Context context, ArrayList<String> orderImagesList) {
        this.context = context;
        this.orderImagesList = orderImagesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.grid_row_book_order_images, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();

        if (!orderImagesList.get(position).isEmpty()) {
            Glide.with(context)
                    .load(orderImagesList.get(position))
                    .into(holder.imv_image);
            setPaddingForView(context, holder.imv_image, 0);
            holder.imv_image_delete.setVisibility(View.VISIBLE);
            holder.imv_image_delete.bringToFront();
        }

        holder.imv_image_delete.setVisibility(View.GONE);

        holder.imv_image.setOnClickListener(v -> {
            ArrayList<String> imageUrlList = new ArrayList<>();
            imageUrlList.add(orderImagesList.get(position));
            context.startActivity(new Intent(context, FullScreenImages_Activity.class)
                    .putExtra("imageUrlList", (Serializable) imageUrlList)
                    .putExtra("position", position));

        });
    }

    @Override
    public int getItemCount() {
        return orderImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_image, imv_image_delete;

        private MyViewHolder(View view) {
            super(view);
            imv_image = view.findViewById(R.id.imv_image);
            imv_image_delete = view.findViewById(R.id.imv_image_delete);

        }
    }
}
