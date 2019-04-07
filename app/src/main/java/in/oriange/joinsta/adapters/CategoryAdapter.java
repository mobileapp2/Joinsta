package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.CategotyListModel;
import in.oriange.joinsta.models.SubCategotyListModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategotyListModel> resultArrayList;
    private Context context;
    private ArrayList<SubCategotyListModel> subCategoryList;
    private ImageButton imv_arrow1;

    public CategoryAdapter(Context context, List<CategotyListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

        subCategoryList = new ArrayList<>();
        subCategoryList.add(new SubCategotyListModel(R.drawable.icon_builder1, "Builder"));
        subCategoryList.add(new SubCategotyListModel(R.drawable.icon_contractor1, "Contractor"));
        subCategoryList.add(new SubCategotyListModel(R.drawable.icon_manufacturer1, "Manufacturer"));
        subCategoryList.add(new SubCategotyListModel(R.drawable.icon_service1, "Service Provider"));
        subCategoryList.add(new SubCategotyListModel(R.drawable.icon_showroon, "Showroom"));
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_category, parent, false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final CategotyListModel categotyDetails = resultArrayList.get(position);

        holder.imv_category.setImageDrawable(context.getResources().getDrawable(categotyDetails.getImageId()));
        holder.tv_categoty.setText(categotyDetails.getCategoryName());

        holder.rv_sub_catrgory.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_sub_catrgory.setAdapter(new SubCategoryAdapter(context, subCategoryList));

        holder.cv_main_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.rv_sub_catrgory.getVisibility() == View.VISIBLE) {
                    imv_arrow1 = holder.imv_arrow;
                    holder.rv_sub_catrgory.setVisibility(View.GONE);
                    animateCollapse();
                } else {
                    holder.cv_main_row.requestFocus();
                    imv_arrow1 = holder.imv_arrow;
                    holder.rv_sub_catrgory.setVisibility(View.VISIBLE);
                    animateExpand();
                }
            }
        });

        holder.imv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.rv_sub_catrgory.getVisibility() == View.VISIBLE) {
                    imv_arrow1 = holder.imv_arrow;
                    holder.rv_sub_catrgory.setVisibility(View.GONE);
                    animateCollapse();
                } else {
                    holder.cv_main_row.requestFocus();
                    imv_arrow1 = holder.imv_arrow;
                    holder.rv_sub_catrgory.setVisibility(View.VISIBLE);
                    animateExpand();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_main_row;
        private ImageView imv_category;
        private ImageButton imv_arrow;
        private TextView tv_categoty;
        private RecyclerView rv_sub_catrgory;

        public MyViewHolder(View view) {
            super(view);
            cv_main_row = view.findViewById(R.id.cv_main_row);
            imv_category = view.findViewById(R.id.imv_category);
            imv_arrow = view.findViewById(R.id.imv_arrow);
            tv_categoty = view.findViewById(R.id.tv_categoty);
            rv_sub_catrgory = view.findViewById(R.id.rv_sub_catrgory);
        }
    }

    private void animateExpand() {
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        imv_arrow1.startAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        imv_arrow1.startAnimation(rotate);
    }
}

