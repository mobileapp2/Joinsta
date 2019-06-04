package in.oriange.joinsta.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import java.util.HashMap;

import androidx.core.app.ActivityCompat;

import in.oriange.joinsta.activities.Login_Activity;


public class UserSessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String login) {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(ApplicationConstants.IS_USER_LOGIN, true);
        editor.putString(ApplicationConstants.KEY_LOGIN_INFO, login);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                Context.MODE_PRIVATE);
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(ApplicationConstants.KEY_LOGIN_INFO,
                pref.getString(ApplicationConstants.KEY_LOGIN_INFO, null));
        return user;
    }

    public void logoutUser() {
        cleanLoginInfo();
        Intent i = new Intent(_context, Login_Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);

        if (Build.VERSION.SDK_INT >= 16)
            ((Activity) _context).finishAffinity();
        else
            ActivityCompat.finishAffinity((Activity) _context);
    }

    public void cleanLoginInfo() {
        editor = pref.edit();
        editor.remove(ApplicationConstants.KEY_LOGIN_INFO);
        editor.remove(ApplicationConstants.IS_USER_LOGIN);
        editor.apply();
        editor.commit();
    }

    public void updateSession(String loginInfo) {
        editor = pref.edit();
        editor.remove(ApplicationConstants.KEY_LOGIN_INFO);
        editor.remove(ApplicationConstants.IS_USER_LOGIN);
        editor.apply();
        editor.commit();

        createUserLoginSession(loginInfo);
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(ApplicationConstants.IS_USER_LOGIN, false);
    }

    public void setLocation(String pincodeInfo) {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(ApplicationConstants.IS_LOCATION_SET, true);
        editor.putString(ApplicationConstants.KEY_LOCATION_INFO, pincodeInfo);
        editor.commit();
    }

    public HashMap<String, String> getLocation() {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                Context.MODE_PRIVATE);
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(ApplicationConstants.KEY_LOCATION_INFO,
                pref.getString(ApplicationConstants.KEY_LOCATION_INFO, null));
        return user;
    }

    public boolean isLocationSet() {
        return pref.getBoolean(ApplicationConstants.IS_LOCATION_SET, false);
    }



    public void createAndroidToken(String tokenID) {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(ApplicationConstants.KEY_ANDROIDTOKETID, tokenID);
        editor.commit();
    }

    public HashMap<String, String> getAndroidToken() {
        pref = _context.getSharedPreferences(ApplicationConstants.PREFER_NAME,
                Context.MODE_PRIVATE);
        HashMap<String, String> androidTokenID = new HashMap<String, String>();
        androidTokenID.put(ApplicationConstants.KEY_ANDROIDTOKETID,
                pref.getString(ApplicationConstants.KEY_ANDROIDTOKETID, ""));
        return androidTokenID;
    }

}