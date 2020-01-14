package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditGroupBanner_Activity;
import in.oriange.joinsta.models.GroupAdminBannersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class GroupBannersAdapter extends RecyclerView.Adapter<GroupBannersAdapter.MyViewHolder> {

    private Context context;
    private List<GroupAdminBannersListModel.ResultBean> resultList;

    public GroupBannersAdapter(Context context, List<GroupAdminBannersListModel.ResultBean> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_banners, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        final GroupAdminBannersListModel.ResultBean bannerDetails = resultList.get(position);

        if (!bannerDetails.getBanners_image().equals("")) {
            String url = IMAGE_LINK + "groupbanners/" + bannerDetails.getBanners_image();
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_banner_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imv_banner_image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.imv_banner_image.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.imv_banner_image.setVisibility(View.GONE);
        }

        holder.tv_title.setText(bannerDetails.getBanner_name());
        holder.tv_description.setText(bannerDetails.getBanner_description());
        holder.tv_time.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", bannerDetails.getStart_date()) + "  To  " +
                changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", bannerDetails.getEnd_date()));

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ll_buttons.getVisibility() == View.GONE) {
                    holder.ll_buttons.setVisibility(View.VISIBLE);
                    holder.view_divider.setVisibility(View.VISIBLE);
                } else if (holder.ll_buttons.getVisibility() == View.VISIBLE) {
                    holder.ll_buttons.setVisibility(View.GONE);
                    holder.view_divider.setVisibility(View.GONE);
                }

            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditGroupBanner_Activity.class)
                        .putExtra("bannerDetails", bannerDetails));
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this banner?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context))
                            new DeleteBanner().execute(bannerDetails.getId());
                        else
                            Utilities.showMessage("Please check your internet connection", context, 2);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private LinearLayout ll_buttons;
        private View view_divider;
        private ImageView imv_banner_image;
        private TextView tv_title, tv_description, tv_time;
        private Button btn_edit, btn_delete;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            view_divider = view.findViewById(R.id.view_divider);
            imv_banner_image = view.findViewById(R.id.imv_banner_image);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_time = view.findViewById(R.id.tv_time);
            btn_edit = view.findViewById(R.id.btn_edit);
            btn_delete = view.findViewById(R.id.btn_delete);
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class DeleteBanner extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "deleteGroupBannerDetails"));
            param.add(new ParamsPojo("group_banner_id", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINGRPBANNERSAPI, param);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupBanners_Activity"));
                        Utilities.showMessage("Banner deleted successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
