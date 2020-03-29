package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditRatingAndReview_Activity;
import in.oriange.joinsta.fragments.Search_Fragment;
import in.oriange.joinsta.models.RatingAndReviewModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class RatingAndReviewAdapter extends RecyclerView.Adapter<RatingAndReviewAdapter.MyViewHolder> {

    private Context context;
    private List<RatingAndReviewModel.ResultBean> reviewsList;
    private String userId, categoryTypeId;

    private PowerMenu powerMenu;
    private int itemClickedPosition = 0;
    private UserSessionManager session;

    public RatingAndReviewAdapter(Context context, List<RatingAndReviewModel.ResultBean> reviewsList, String categoryTypeId) {
        this.context = context;
        this.reviewsList = reviewsList;
        this.categoryTypeId = categoryTypeId;

        session = new UserSessionManager(context);
        try {
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
        View view = inflater.inflate(R.layout.list_row_rating_reveiw, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        RatingAndReviewModel.ResultBean resultBean = reviewsList.get(position);

        if (!resultBean.getUser_id().equals(userId)) {
            holder.imv_more.setVisibility(View.GONE);
        }

        if (!resultBean.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(resultBean.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)
                    .centerCrop()
                    .into(holder.imv_user);
        } else {
            holder.imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        holder.tv_name.setText(resultBean.getPublic_name());

        holder.tv_title.setText(resultBean.getReview_title());
        if (!resultBean.getReview_description().equals(""))
            holder.tv_review.setText(resultBean.getReview_description());
        else
            holder.tv_review.setVisibility(View.GONE);

        holder.tv_date.setText(resultBean.getFormattedDate());
        holder.rb_feedback_stars.setRating(Float.parseFloat(resultBean.getRating()));

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedPosition = position;
                powerMenu = getHamburgerPowerMenu(context, onHamburgerItemClickListener, onHamburgerMenuDismissedListener);
                powerMenu.showAsDropDown(v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imv_user;
        private TextView tv_name, tv_date, tv_title, tv_review;
        private ImageButton imv_more;
        private RatingBar rb_feedback_stars;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_user = itemView.findViewById(R.id.imv_user);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_review = itemView.findViewById(R.id.tv_review);
            tv_title = itemView.findViewById(R.id.tv_title);
            imv_more = itemView.findViewById(R.id.imv_more);
            rb_feedback_stars = itemView.findViewById(R.id.rb_feedback_stars);

            Linkify.addLinks(tv_review, Linkify.ALL);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    switch (position) {
                        case 0:
                            RatingAndReviewModel.ResultBean ratingDetails = reviewsList.get(itemClickedPosition);
                            context.startActivity(new Intent(context, EditRatingAndReview_Activity.class)
                                    .putExtra("ratingDetails", ratingDetails));

                            break;

                        case 1:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builder.setMessage("Are you sure you want to delete this review?");
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.icon_alertred);
                            builder.setCancelable(false);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Utilities.isNetworkAvailable(context)) {
                                        new DeleteReview().execute(String.valueOf(itemClickedPosition));
                                    } else {
                                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                                    }
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertD = builder.create();
                            alertD.show();
                            break;

                    }
                }
            };

    private OnDismissedListener onHamburgerMenuDismissedListener = () -> {
    };

    private static PowerMenu getHamburgerPowerMenu(
            Context context,
            OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener,
            OnDismissedListener onDismissedListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Edit"))
                .addItem(new PowerMenuItem("Delete"))
                .setAutoDismiss(true)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuEffect(MenuEffect.BODY)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(context.getResources().getColor(R.color.black))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(context.getResources().getColor(R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .setOnDismissListener(onDismissedListener)
                .setPreferenceName("HamburgerPowerMenu")
                .setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
                .build();
    }

    private class DeleteReview extends AsyncTask<String, Void, String> {
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
            String res = "[]";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "DeleteProfileRating");
            jsonObject.addProperty("profile_reviews_ratings_id", reviewsList.get(itemClickedPosition).getId());
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, jsonObject.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupMembersProfileDetails_Activity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("RatingAndReviewList_Activity"));
                        new Search_Fragment.GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));

                        switch (categoryTypeId) {
                            case "1":
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewSearchBizDetails_Activity"));
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewGroupMemberBizDetails_Activity"));
                                break;
                            case "2":
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewSearchEmpDetails_Activity"));
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewGroupMemberEmpDetails_Activity"));
                                break;
                            case "3":
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewSearchProfDetails_Activity"));
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewGroupMemberProfDetails_Activity"));
                                break;
                        }

                        Utilities.showMessage("Review deleted successfully", context, 1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
