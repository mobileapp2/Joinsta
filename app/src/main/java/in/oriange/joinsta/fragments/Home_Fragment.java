package in.oriange.joinsta.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import in.oriange.joinsta.R;

public class Home_Fragment extends Fragment {

    private static Context context;
    private static final String TAG = "bottom_sheet";
    private static AppCompatEditText edt_type;

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

    }

    private void setDefault() {

    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheetMenu_Fragment().show(getFragmentManager(), TAG);

            }
        });

    }

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
