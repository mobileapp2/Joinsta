package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenuItem;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.oriange.joinsta.R;

public class Home_Fragment extends Fragment {

    private static Context context;
    private static final String TAG = "bottom_sheet";
    private static AppCompatEditText edt_type;
    private CustomPowerMenu writeMenu;
    private View ll_mainlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        edt_type = rootView.findViewById(R.id.edt_type);
        ll_mainlayout = rootView.findViewById(R.id.ll_mainlayout);

    }

    private void setDefault() {

    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new BottomSheetMenu_Fragment().show(getFragmentManager(), TAG);

                ColorDrawable drawable =
                        new ColorDrawable(context.getResources().getColor(R.color.mediumGray));
                writeMenu = new CustomPowerMenu.Builder<>(context, new CenterMenuAdapter())
                        .addItem("Business")
                        .addItem("Profession")
                        .addItem("Empolyee")
                        .setAnimation(MenuAnimation.FADE)
                        .setMenuEffect(MenuEffect.BODY)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setDivider(drawable)
                        .setDividerHeight(1)
                        .setOnMenuItemClickListener(onWriteItemClickListener)
                        .build();

                writeMenu.showAtCenter(ll_mainlayout);

            }
        });

    }

    public class CenterMenuAdapter extends MenuBaseAdapter<String> {

        public CenterMenuAdapter() {
            super();
        }

        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();

            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_title_menu, viewGroup, false);
            }

            String item = (String) getItem(index);
            final TextView title = view.findViewById(R.id.item_title);
            title.setText(item);
            title.setTextColor(context.getResources().getColor(R.color.mediumGray));
            return super.getView(index, view, viewGroup);
        }

        @Override
        public void setSelectedPosition(int position) {
            notifyDataSetChanged();
        }
    }

    private OnMenuItemClickListener<String> onWriteItemClickListener =
            new OnMenuItemClickListener<String>() {
                @Override
                public void onItemClick(int position, String title) {
                    edt_type.setText(title);
                    writeMenu.dismiss();
                }
            };

    @SuppressLint("ValidFragment")
    public static class BottomSheetMenu_Fragment extends BottomSheetDialogFragment {

        private LinearLayout ll_business, ll_profession, ll_employee;
        private BottomSheetDialog bottomDialog;

        @Override
        public int getTheme() {
            return R.style.BottomSheetDialogTheme;
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            bottomDialog = new BottomSheetDialog(context);
            bottomDialog = (BottomSheetDialog) dialog;
            View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom_sheet_dialog, null);
            dialog.setContentView(view);

            init(view);
            setDefaults();
            setEventListner();
        }

        private void init(View view) {
            context = getActivity();
            ll_business = view.findViewById(R.id.ll_business);
            ll_profession = view.findViewById(R.id.ll_profession);
            ll_employee = view.findViewById(R.id.ll_employee);

        }

        private void setDefaults() {

        }

        private void setEventListner() {
            ll_business.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Business");
                    bottomDialog.dismiss();
                }
            });
            ll_profession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Profession");
                    bottomDialog.dismiss();
                }
            });
            ll_employee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_type.setText("Employee");
                    bottomDialog.dismiss();
                }
            });

        }
    }


}
