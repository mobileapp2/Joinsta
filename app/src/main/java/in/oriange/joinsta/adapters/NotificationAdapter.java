package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.NotificationListModel;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;
    private PrettyTime p;

    public NotificationAdapter(Context context, List<NotificationListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        session = new UserSessionManager(context);
        p = new PrettyTime();

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_notification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final NotificationListModel.ResultBean notificationDetails = resultArrayList.get(position);


        holder.tv_title.setText(notificationDetails.getTitle().trim());
        holder.tv_message.setText(notificationDetails.getDescription().trim());
        holder.view_foreground.bringToFront();

//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//            holder.tv_time.setText(p.format(formatter.parse(notificationDetails.getCreated_at())));
        holder.tv_time.setText(notificationDetails.getCreated_at());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        if (!notificationDetails.getImage().equals("")) {
            String url = IMAGE_LINK + "notifications/" + notificationDetails.getImage();
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_notificationimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imv_notificationimg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.imv_notificationimg.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.imv_notificationimg.setVisibility(View.GONE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification(notificationDetails);
            }
        });

    }


    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private ImageView imv_notificationimg;
        private TextView tv_title, tv_message, tv_time;
        public LinearLayout view_foreground;
        private RelativeLayout view_background;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_notificationimg = view.findViewById(R.id.imv_notificationimg);
            tv_title = view.findViewById(R.id.tv_title);
            tv_message = view.findViewById(R.id.tv_message);
            tv_time = view.findViewById(R.id.tv_time);
            view_foreground = view.findViewById(R.id.view_foreground);
            view_background = view.findViewById(R.id.view_background);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void showNotification(NotificationListModel.ResultBean notificationDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_notification, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_notificationimg = promptView.findViewById(R.id.imv_notificationimg);
        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final TextView tv_message = promptView.findViewById(R.id.tv_message);
        final TextView tv_time = promptView.findViewById(R.id.tv_time);

        if (!notificationDetails.getImage().equals("")) {
            String url = IMAGE_LINK + "notifications/" + notificationDetails.getImage();
            Picasso.with(context)
                    .load(url)
                    .into(imv_notificationimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            imv_notificationimg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            imv_notificationimg.setVisibility(View.GONE);
                        }
                    });
        } else {
            imv_notificationimg.setVisibility(View.GONE);
        }

        tv_title.setText(notificationDetails.getTitle().trim());
        tv_message.setText(notificationDetails.getDescription().trim());
        tv_time.setText(notificationDetails.getCreated_at());

        final AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }


}
