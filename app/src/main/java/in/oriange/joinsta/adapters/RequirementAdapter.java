package in.oriange.joinsta.adapters;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ViewRequirements_Activity;
import in.oriange.joinsta.models.RequirementsListModel;

public class RequirementAdapter extends RecyclerView.Adapter<RequirementAdapter.MyViewHolder> {

    private List<RequirementsListModel> resultArrayList;
    private Context context;
    PowerMenu powerMenu;

    public RequirementAdapter(Context context, List<RequirementsListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_requirement, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final RequirementsListModel reqDetails = resultArrayList.get(position);
        PrettyTime p = new PrettyTime();

        holder.tv_name.setText(reqDetails.getFname() + " " + reqDetails.getMname() + " " + reqDetails.getLname());
        holder.tv_title.setText(reqDetails.getTitle());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            holder.tv_timelocation.setText(p.format(formatter.parse(reqDetails.getUpdated_at())) + " | " + reqDetails.getCity());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        private ImageView imv_more;
        private CardView cv_mainlayout;
        private TextView tv_name, tv_title, tv_timelocation;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_more = view.findViewById(R.id.imv_more);
            tv_name = view.findViewById(R.id.tv_name);
            tv_title = view.findViewById(R.id.tv_title);
            tv_timelocation = view.findViewById(R.id.tv_timelocation);
        }
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();
                    powerMenu.setSelectedPosition(position);
                }
            };
    private OnDismissedListener onHamburgerMenuDismissedListener =
            new OnDismissedListener() {
                @Override
                public void onDismissed() {
                    Log.d("Test", "onDismissed hamburger menu");
                }
            };


    public static PowerMenu getHamburgerPowerMenu(
            Context context,
            OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener,
            OnDismissedListener onDismissedListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("EDIT"))
                .addItem(new PowerMenuItem("DELETE"))
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
}
