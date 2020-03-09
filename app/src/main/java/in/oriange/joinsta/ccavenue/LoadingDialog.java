package in.oriange.joinsta.ccavenue;

import android.app.ProgressDialog;
import android.content.Context;

import in.oriange.joinsta.R;


public class LoadingDialog {

    static ProgressDialog progressDialog;


    public static void showLoadingDialog(Context context, String message) {

        if (!(progressDialog != null && progressDialog.isShowing())) {
            progressDialog = new ProgressDialog(context, R.style.CustomDialogTheme);
            progressDialog.setMessage(message);

            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();
        }
    }

    public static void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();

    }


}
