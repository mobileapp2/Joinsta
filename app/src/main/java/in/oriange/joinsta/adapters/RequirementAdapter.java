package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.EditRequirements_Activity;
import in.oriange.joinsta.activities.ViewRequirements_Activity;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class RequirementAdapter extends RecyclerView.Adapter<RequirementAdapter.MyViewHolder> {

    private String userId;
    private List<RequirementsListModel> resultArrayList;
    private Context context;
    private PowerMenu powerMenu;
    private UserSessionManager session;
    private int positionToDelete;

    public RequirementAdapter(Context context, List<RequirementsListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_requirement, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        final int position = holder.getAdapterPosition();
        final RequirementsListModel reqDetails = resultArrayList.get(position);
        PrettyTime p = new PrettyTime();

        holder.tv_name.setText(reqDetails.getFname() + " " + reqDetails.getMname() + " " + reqDetails.getLname());
        holder.tv_title.setText(reqDetails.getTitle());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_timelocation.setText(p.format(formatter.parse(reqDetails.getUpdated_at())) + " | " + reqDetails.getCity());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!reqDetails.getImageURL().trim().isEmpty()) {
            Picasso.with(context)
                    .load(reqDetails.getImageURL().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(250, 250)
                    .centerCrop()
                    .into(holder.imv_user, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imv_user.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.imv_user.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getCreated_by().equals(userId)) {
            holder.imv_more.setVisibility(View.VISIBLE);
        }

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionToDelete = position;
                powerMenu = getHamburgerPowerMenu(context,
                        onHamburgerItemClickListener, onHamburgerMenuDismissedListener);
                powerMenu.showAsDropDown(v);

            }
        });

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewRequirements_Activity.class)
                        .putExtra("reqDetails", reqDetails));
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imv_more;
        private CircleImageView imv_user;
        private ProgressBar progressBar;
        private CardView cv_mainlayout;
        private TextView tv_name, tv_title, tv_timelocation;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_more = view.findViewById(R.id.imv_more);
            imv_user = view.findViewById(R.id.imv_user);
            progressBar = view.findViewById(R.id.progressBar);
            tv_name = view.findViewById(R.id.tv_name);
            tv_title = view.findViewById(R.id.tv_title);
            tv_timelocation = view.findViewById(R.id.tv_timelocation);
        }
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    switch (position) {
                        case 0:
                            context.startActivity(new Intent(context, EditRequirements_Activity.class)
                                    .putExtra("reqDetails", resultArrayList.get(positionToDelete)));
                            break;

                        case 1:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builder.setMessage("Are you sure you want to delete this item?");
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.icon_alertred);
                            builder.setCancelable(false);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Utilities.isNetworkAvailable(context)) {
                                        new DeleteRequirement().execute(String.valueOf(positionToDelete));
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

    private OnDismissedListener onHamburgerMenuDismissedListener =
            new OnDismissedListener() {
                @Override
                public void onDismissed() {

                }
            };


    public static PowerMenu getHamburgerPowerMenu(
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

    public class DeleteRequirement extends AsyncTask<String, Void, String> {
        int position;
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
            position = Integer.parseInt(params[0]);
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleterequirement");
            obj.addProperty("reqId", resultArrayList.get(positionToDelete).getId());
            obj.addProperty("isdeleted", "1");
            res = APICall.JSONAPICall(ApplicationConstants.REQUIREMENTAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("Requirements_Activity"));

                        Utilities.showMessage("Requirement deleted successfully", context, 1);
                        removeItem(position);
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
