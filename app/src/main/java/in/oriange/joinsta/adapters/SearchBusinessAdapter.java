package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.MainDrawer_Activity;
import in.oriange.joinsta.activities.ViewSearchBizDetails_Activity;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.CalculateDistanceTime;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static in.oriange.joinsta.activities.MainDrawer_Activity.startLocationUpdates;
import static in.oriange.joinsta.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinsta.utilities.Utilities.provideLocationAccess;
import static in.oriange.joinsta.utilities.Utilities.turnOnLocation;

public class SearchBusinessAdapter extends RecyclerView.Adapter<SearchBusinessAdapter.MyViewHolder> {

    private List<SearchDetailsModel.ResultBean.BusinessesBean> resultArrayList;
    private Context context;
    private String type;            //  1 = from search  // 2 = from favorite  // 3 = from home

    public SearchBusinessAdapter(Context context, List<SearchDetailsModel.ResultBean.BusinessesBean> resultArrayList, String type) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.type = type;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final SearchDetailsModel.ResultBean.BusinessesBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getBusiness_name());

        if (searchDetails.getTag().get(0) != null || searchDetails.getTag().get(0).size() > 0) {
            StringBuilder tag = new StringBuilder();
            try {
                for (int i = 0; i < 4; i++) {
                    tag.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                }
                holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
            } catch (IndexOutOfBoundsException e) {
                holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
            }

        } else {
            if (!searchDetails.getSubtype_description().isEmpty())
                holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
            else
                holder.tv_subheading.setText(searchDetails.getType_description());

        }


        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewSearchBizDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails)
                        .putExtra("type", type));
            }
        });

        float scale = context.getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (5 * scale + 0.5f);
        if (!searchDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(searchDetails.getImage_url().trim())
                    .into(holder.imv_preview, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_preview.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_preview.setVisibility(View.VISIBLE);
                            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.imv_preview.setVisibility(View.VISIBLE);
            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
        }

        holder.tv_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchDetails.getLatitude().equals("") || searchDetails.getLongitude().equals("")) {
                    holder.tv_distance.setText(Html.fromHtml("<font color=\"#C62828\"> <b>Location of business not available</b></font>"));
                    return;
                }

                if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    provideLocationAccess(context);
                    return;
                }

                if (!isLocationEnabled(context)) {
                    turnOnLocation(context);
                    return;
                }

                if (MainDrawer_Activity.latLng == null) {
                    holder.tv_distance.setText(Html.fromHtml("<font color=\"#C62828\"> <b>Current location not available. Please try again</b></font>"));

                    return;
                }

                startLocationUpdates();

                LatLng currentLocation = new LatLng(MainDrawer_Activity.latLng.latitude, MainDrawer_Activity.latLng.longitude);
                LatLng destinationLocation = new LatLng(Double.parseDouble(searchDetails.getLatitude()), Double.parseDouble(searchDetails.getLongitude()));

                CalculateDistanceTime distance_task = new CalculateDistanceTime(context);

                distance_task.getDirectionsUrl(currentLocation, destinationLocation);

                distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                    @Override
                    public void taskCompleted(String[] time_distance) {
                        holder.tv_distance.setText(Html.fromHtml("<font color=\"#FFA000\"> <b>" + time_distance[0] + "</b></font> <font color=\"#616161\">from current location</font>"));

                    }

                });
            }
        });

        holder.imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getBusiness_name().equals("")) {
                    sb.append("Name of Business - " + searchDetails.getBusiness_name() + "\n");
                }

                if (!searchDetails.getType_description().equals("")) {
                    sb.append("Nature of Business - " + searchDetails.getType_description() + "\n");
                }

                if (!searchDetails.getSubtype_description().equals("")) {
                    sb.append("Subtype - " + searchDetails.getType_description() + "\n");
                }

                if (!searchDetails.getWebsite().equals("")) {
                    sb.append("Website - " + searchDetails.getWebsite() + "\n");
                }

                if (!searchDetails.getAddress().equals("")) {
                    sb.append("Address - " + searchDetails.getAddress() + "\n");
                }

                if (!searchDetails.getEmail().equals("")) {
                    sb.append("Email - " + searchDetails.getEmail() + "\n");
                }

                if (searchDetails.getMobiles().get(0) != null)
                    if (searchDetails.getMobiles().get(0).size() != 0) {
                        StringBuilder mobile = new StringBuilder();
                        for (int i = 0; i < searchDetails.getMobiles().get(0).size(); i++) {
                            mobile.append(searchDetails.getMobiles().get(0).get(i).getMobile_number() + ", ");
                        }

                        sb.append("Mobile - " + mobile.toString().substring(0, mobile.toString().length() - 2) + "\n");
                    }

                if (searchDetails.getLandline().get(0) != null)
                    if (searchDetails.getLandline().get(0).size() != 0) {
                        StringBuilder landline = new StringBuilder();
                        for (int i = 0; i < searchDetails.getLandline().get(0).size(); i++) {
                            landline.append(searchDetails.getLandline().get(0).get(i).getLandline_number() + ", ");
                        }

                        sb.append("Landline - " + landline.toString().substring(0, landline.toString().length() - 2) + "\n");
                    }

                if (!searchDetails.getLatitude().equals("") || !searchDetails.getLongitude().equals("")) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + searchDetails.getLatitude() + "," + searchDetails.getLongitude() + "\n");

                }

                if (searchDetails.getTag().get(0) != null)
                    if (searchDetails.getTag().get(0).size() != 0) {
                        StringBuilder tags = new StringBuilder();
                        for (int i = 0; i < searchDetails.getTag().get(0).size(); i++) {
                            tags.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                        }

                        sb.append("Tags - " + tags.toString().substring(0, tags.toString().length() - 2) + "\n");
                    }

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview;
        private CardView cv_mainlayout;
        private ProgressBar progressBar;
        private ImageButton imv_share;
        private TextView tv_heading, tv_subheading, tv_subsubheading, tv_distance;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            tv_distance = view.findViewById(R.id.tv_distance);
            imv_share = view.findViewById(R.id.imv_share);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
