package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.BizProfEmpDetailsList_Activity;
import in.oriange.joinsta.models.CategotyListModel;
import in.oriange.joinsta.models.SubCategotyListModel;
import in.oriange.joinsta.pojos.SubCategotyListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.Utilities;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategotyListModel> resultArrayList;
    private Context context;
    private ArrayList<SubCategotyListModel> subCategoryList;
    private ImageButton imv_arrow1;
    private String mainCategoryTypeId;
    private RecyclerView rv_sub_catrgory1;
    private SpinKitView progressBar1;
    private TextView tv_subcst_notavailable1;


    public CategoryAdapter(Context context, List<CategotyListModel> resultArrayList, String mainCategoryTypeId) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.mainCategoryTypeId = mainCategoryTypeId;
        subCategoryList = new ArrayList<>();
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

        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_builder1));

        holder.tv_categoty.setText(categotyDetails.getName());

        holder.cv_main_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setUpSubcategoryData(holder, categotyDetails);
                context.startActivity(new Intent(context, BizProfEmpDetailsList_Activity.class)
                        .putExtra("mainCategoryTypeId", mainCategoryTypeId)
                        .putExtra("categoryTypeId", categotyDetails.getId())
                        .putExtra("subCategoryTypeId", "NA"));
            }
        });

        holder.imv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSubcategoryData(holder, categotyDetails);
            }
        });


        if (!categotyDetails.getCategory_icon().trim().isEmpty()) {
            Picasso.with(context)
                    .load(categotyDetails.getCategory_icon().trim())
                    .placeholder(R.drawable.icon_preview)
                    .into(holder.imv_category, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
                        }
                    });
        } else {
            holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
        }


//        switch (mainCategoryTypeId) {
//            case "1":
//                switch (position) {
//                    case 0:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_builder1));
//                        break;
//                    case 1:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_contractor1));
//                        break;
//                    case 2:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_manufacturer1));
//                        break;
//                    case 3:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_service1));
//                        break;
//                    case 4:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_showroon));
//                        break;
//                    case 5:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_trader1));
//                        break;
//                    case 6:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_other1));
//                        break;
//                    default:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//                        break;
//
//                }
//
//                break;
//            case "2":
//                switch (position) {
//                    case 0:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_corporate));
//                        break;
//                    case 1:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_government));
//                        break;
//                    case 2:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_mnc));
//                        break;
//                    case 3:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_private));
//                        break;
//                    case 4:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_other1));
//                        break;
//                    default:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//                        break;
//
//                }
//
//                break;
//            case "3":
//                switch (position) {
//                    case 0:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_advocate));
//                        break;
//                    case 1:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_architect));
//                        break;
//                    case 2:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_ca));
//                        break;
//                    case 3:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cs));
//                        break;
//                    case 4:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cwa));
//                        break;
//                    case 5:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_doctor));
//                        break;
//                    case 6:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_engineer));
//                        break;
//                    case 7:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_interiordesign));
//                        break;
//                    case 8:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_lecture));
//                        break;
//                    case 9:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_teacher));
//                        break;
//                    case 10:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_other1));
//                        break;
//                    default:
//                        holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//                        break;
//
//                }
//
//                break;
//        }


    }

    private void setUpSubcategoryData(MyViewHolder holder, CategotyListModel categotyDetails) {
        if (holder.rv_sub_catrgory.getVisibility() == View.VISIBLE ||
                holder.progressBar.getVisibility() == View.VISIBLE ||
                holder.tv_subcst_notavailable.getVisibility() == View.VISIBLE) {
            imv_arrow1 = holder.imv_arrow;
            holder.rv_sub_catrgory.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.tv_subcst_notavailable.setVisibility(View.GONE);
            animateCollapse();
        } else {
            holder.cv_main_row.requestFocus();
            imv_arrow1 = holder.imv_arrow;
            rv_sub_catrgory1 = holder.rv_sub_catrgory;
            progressBar1 = holder.progressBar;
            tv_subcst_notavailable1 = holder.tv_subcst_notavailable;
            if (Utilities.isNetworkAvailable(context)) {
                new GetSubCategotyList().execute(categotyDetails.getId(), "1", mainCategoryTypeId);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
            animateExpand();
        }
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
        private SpinKitView progressBar;
        private TextView tv_subcst_notavailable;

        public MyViewHolder(View view) {
            super(view);
            cv_main_row = view.findViewById(R.id.cv_main_row);
            imv_category = view.findViewById(R.id.imv_category);
            imv_arrow = view.findViewById(R.id.imv_arrow);
            tv_categoty = view.findViewById(R.id.tv_categoty);
            rv_sub_catrgory = view.findViewById(R.id.rv_sub_catrgory);
            progressBar = view.findViewById(R.id.progressBar);
            tv_subcst_notavailable = view.findViewById(R.id.tv_subcst_notavailable);
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

    public class GetSubCategotyList extends AsyncTask<String, Void, String> {
        String categoryTypeId = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar1.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            categoryTypeId = params[0];
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategory");
            obj.addProperty("parent_id", params[0]);
            obj.addProperty("level", params[1]);
            obj.addProperty("category_type_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar1.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    SubCategotyListPojo pojoDetails = new Gson().fromJson(result, SubCategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            rv_sub_catrgory1.setVisibility(View.VISIBLE);
                            tv_subcst_notavailable1.setVisibility(View.GONE);
                            rv_sub_catrgory1.setLayoutManager(new LinearLayoutManager(context));
                            rv_sub_catrgory1.setAdapter(new SubCategoryAdapter(context, subCategoryList, mainCategoryTypeId, categoryTypeId));
                        }
                    } else {
                        rv_sub_catrgory1.setVisibility(View.GONE);
                        tv_subcst_notavailable1.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                rv_sub_catrgory1.setVisibility(View.GONE);
                tv_subcst_notavailable1.setVisibility(View.VISIBLE);
            }
        }
    }

}

