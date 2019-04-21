package in.oriange.joinsta.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import es.dmoral.toasty.Toasty;
import in.oriange.joinsta.R;

public class Utilities {

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 200;
    public static final int VIDEO_REQUEST = 300;

    public static SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dfDate2 = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat dfDate3 = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat dfDate4 = new SimpleDateFormat("yyyy/MM/dd");

    public static boolean isMobileNo(EditText edt) {
        edt.setError(null);
        if ((edt.getText().toString().trim().length() == 10)
                && (isValidMobileno(edt.getText().toString().trim())))
            return true;

        else {
            edt.setError("Enter valid mobile number");
            edt.requestFocus();
            return false;
        }
    }

    public static String ConvertDateFormat(DateFormat dateFormat, int day, int month, int year) {
        String startDateString = String.valueOf(day) + "/"
                + String.valueOf(month) + "/"
                + String.valueOf(year);
        Date startDate;
        String newDateString = "";
        try {
            startDate = dfDate2.parse(startDateString);
            newDateString = dateFormat.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    private static boolean isValidMobileno(String mobileno) {
        String Mobile_PATTERN = "^[6-9]{1}[0-9]{9}$";                                               //^[+]?[0-9]{10,13}$
        Pattern pattern = Pattern.compile(Mobile_PATTERN);
        Matcher matcher = pattern.matcher(mobileno);
        return matcher.matches();
    }

    private static boolean isValidPincode(String pincode) {
        String Pincode_PATTERN = "[4]{1}[0-9]{5}";
        Pattern pattern = Pattern.compile(Pincode_PATTERN);
        Matcher matcher = pattern.matcher(pincode);
        return matcher.matches();
    }

    private static boolean isValidPanNum(String pannum) {
        String Pannum_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern pattern = Pattern.compile(Pannum_PATTERN);
        Matcher matcher = pattern.matcher(pannum);
        return matcher.matches();
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    //******************************* Massages Methods *********************************************

    /* show message int*/
    public static void showMessage(int msg, Context context, int type) {    //1=success, 2=warning, 3=erroe
        if (type == 1) {
            Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
        } else if (type == 2) {
            Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
        } else if (type == 3) {
            Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show();
        }
    }

    public static void showMessage(String msg, Context context, int type) {
        if (type == 1) {
            Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
        } else if (type == 2) {
            Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
        } else if (type == 3) {
            Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show();
        }
    }

    static android.app.AlertDialog alertDialog;

    @SuppressWarnings("deprecation")
    public static void showAlertDialog(Context context, String title,
                                       String message, Boolean status) {
        alertDialog = new android.app.AlertDialog.Builder(context, R.style.CustomDialogTheme).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        if (status != null)
            alertDialog.setIcon((status) ? R.drawable.icon_success : R.drawable.icon_alertred);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null;

        } catch (Exception e) {
            return false;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void provideCameraAndStorageAccess(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialog.setTitle("Permission");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.icon_alertred);
        alertDialog.setMessage("Please grant permission for camera and storage");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null)));
            }
        });
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialog.create();
        alertDialog.show();

    }

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void turnOnLocation(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.icon_alertred);
        alertDialog.setMessage("GPS is not enabled. Please turn on the location from settings.");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }

    public static void provideLocationAccess(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialog.setTitle("Permission");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.icon_alertred);
        alertDialog.setMessage("Please grant permission for location access");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null)));
            }
        });
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialog.create();
        alertDialog.show();

    }
}
