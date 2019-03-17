package in.oriange.joinsta.adapters;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.RequirementListModel;
import in.oriange.joinsta.utilities.Utilities;

public class RequirementAdapter extends RecyclerView.Adapter<RequirementAdapter.MyViewHolder> {

    private List<RequirementListModel> resultArrayList;
    private Context context;
    PowerMenu powerMenu;

    public RequirementAdapter(Context context, List<RequirementListModel> resultArrayList) {
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
        final RequirementListModel requirementDetails = resultArrayList.get(position);

        holder.tv_requirement.setText(requirementDetails.getRequirement());
        holder.tv_name.setText(requirementDetails.getName());
        holder.tv_timelocation.setText(requirementDetails.getTime() + " | " + requirementDetails.getLocation());

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerMenu = getHamburgerPowerMenu(context,
                        onHamburgerItemClickListener, onHamburgerMenuDismissedListener);
                powerMenu.showAsDropDown(v);

            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_more;
        private TextView tv_requirement, tv_name, tv_timelocation;

        public MyViewHolder(View view) {
            super(view);
            imv_more = view.findViewById(R.id.imv_more);
            tv_requirement = view.findViewById(R.id.tv_requirement);
            tv_name = view.findViewById(R.id.tv_name);
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
                .addItem(new PowerMenuItem("EDIT", R.drawable.icon_edit))
                .addItem(new PowerMenuItem("DELETE", R.drawable.icon_delete))
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
