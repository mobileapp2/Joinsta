package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.SelectLocation_Activity;
import in.oriange.joinsta.adapters.CategoryAdapter;
import in.oriange.joinsta.models.CategotyListModel;

import static android.app.Activity.RESULT_OK;

public class Home_Fragment extends Fragment {

    private Context context;
    private final String TAG = "bottom_sheet";
    private AppCompatEditText edt_type, edt_location;
    private RecyclerView rv_category;
    private View ll_mainlayout;
    private PowerMenu iconMenu;

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
        ll_mainlayout = rootView.findViewById(R.id.ll_mainlayout);

        edt_type = rootView.findViewById(R.id.edt_type);
        edt_location = rootView.findViewById(R.id.edt_location);

        rv_category = rootView.findViewById(R.id.rv_category);
        rv_category.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {
        ArrayList<CategotyListModel> categotyList = new ArrayList<>();

        categotyList.add(new CategotyListModel(R.drawable.icon_builder1, "Builder"));
        categotyList.add(new CategotyListModel(R.drawable.icon_contractor1, "Contractor"));
        categotyList.add(new CategotyListModel(R.drawable.icon_manufacturer1, "Manufacturer"));
        categotyList.add(new CategotyListModel(R.drawable.icon_service1, "Service Provider"));
        categotyList.add(new CategotyListModel(R.drawable.icon_showroon, "Showroom"));
        categotyList.add(new CategotyListModel(R.drawable.icon_trader1, "Trader"));
        categotyList.add(new CategotyListModel(R.drawable.icon_other1, "Other"));

        rv_category.setAdapter(new CategoryAdapter(context, categotyList));


    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new BottomSheetMenu_Fragment().show(getFragmentManager(), TAG);

                iconMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("Business", R.drawable.icon_trader1))
                        .addItem(new PowerMenuItem("Profession", R.drawable.icon_profession))
                        .addItem(new PowerMenuItem("Empolyee", R.drawable.icon_employee))
                        .addItem(new PowerMenuItem("Student", R.drawable.icon_student))
                        .setOnMenuItemClickListener(onIconMenuItemClickListener)
                        .setAnimation(MenuAnimation.FADE)
                        .setMenuEffect(MenuEffect.BODY)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                iconMenu.showAsDropDown(v);


            }
        });

        edt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SelectLocation_Activity.class), 10000);
            }
        });

    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {
                    edt_type.setText(item.getTitle());
                    iconMenu.dismiss();
                }
            };

    @SuppressLint("ValidFragment")
    public class BottomSheetMenu_Fragment extends BottomSheetDialogFragment {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10000 && resultCode == RESULT_OK) {
            String requiredValue = data.getStringExtra("locationname");
            edt_location.setText(requiredValue);
        }

    }
}
