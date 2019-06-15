package in.oriange.joinsta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.utilities.AutoCompleteLocation;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinsta.utilities.Utilities.provideLocationAccess;
import static in.oriange.joinsta.utilities.Utilities.turnOnLocation;

public class SelectLocation_Activity extends AppCompatActivity
        implements AutoCompleteLocation.AutoCompleteLocationListener {

    private Context context;
    private AutoCompleteLocation autoCompleteLocation;
    private CardView cv_locationdetails;
    private TextView tv_locality, tv_city, tv_state, tv_pincode;
    private Button btn_current_location, btn_select;
    private LottieAnimationView animationView;

    private int startOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        init();
        setDefault();
        getSessionDetails();
        setEventHandlers();
        setUpToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            return;
        }
        if (isLocationEnabled(context)) {
            startLocationUpdates();
        }

    }

    private void init() {
        context = SelectLocation_Activity.this;

        autoCompleteLocation = findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);


        animationView = findViewById(R.id.animation_view);

        cv_locationdetails = findViewById(R.id.cv_locationdetails);
        tv_locality = findViewById(R.id.tv_locality);
        tv_city = findViewById(R.id.tv_city);
        tv_state = findViewById(R.id.tv_state);
        tv_pincode = findViewById(R.id.tv_pincode);

        btn_current_location = findViewById(R.id.btn_current_location);
        btn_select = findViewById(R.id.btn_select);
    }

    private void setDefault() {
        startOrigin = getIntent().getIntExtra("startOrigin", 0);
    }

    private void getSessionDetails() {

    }

    private void setEventHandlers() {
        btn_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    provideLocationAccess(context);
                } else {
                    if (!isLocationEnabled(context)) {
                        turnOnLocation(context);
                    } else {
                        startLocationUpdates();
                        if (latLng != null) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new GetAddress().execute(
                                        String.valueOf(latLng.latitude),
                                        String.valueOf(latLng.longitude));
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        } else {
                            Utilities.showAlertDialog(context, "Unable to get address from this location. Please try again or search manually", false);
                        }
                    }
                }
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_city.getText().toString().trim().equals("")) {
                    return;
                }

                UserSessionManager session = new UserSessionManager(context);
                session.setLocation(tv_city.getText().toString());
                finishAffinity();
                startActivity(new Intent(context, MainDrawer_Activity.class)
                        .putExtra("startOrigin", startOrigin));
            }
        });
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onTextClear() {

    }

    @Override
    public void onItemSelected(Place selectedPlace) {
        autoCompleteLocation.setText("");
        if (Utilities.isNetworkAvailable(context)) {
            LatLng latLng = selectedPlace.getLatLng();

            new GetAddress().execute(
                    String.valueOf(latLng.latitude),
                    String.valueOf(latLng.longitude));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class GetAddress extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            animationView.playAnimation();
            animationView.setVisibility(View.VISIBLE);
            cv_locationdetails.setVisibility(View.GONE);
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            Geocoder geocoder;
            List<Address> addresses = null;

            try {
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1); // Here 1 represent max icon_location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);

            animationView.pauseAnimation();
            animationView.setVisibility(View.GONE);
            if (addresses != null && !addresses.isEmpty()) {

                cv_locationdetails.setVisibility(View.VISIBLE);
                tv_state.setText(addresses.get(0).getAdminArea());
                tv_city.setText(addresses.get(0).getLocality());
                tv_locality.setText(addresses.get(0).getSubLocality());
                tv_pincode.setText(addresses.get(0).getPostalCode());
            } else {
                Utilities.showAlertDialog(context, "Unable to get address from this location. Please try again or search manually", false);
                cv_locationdetails.setVisibility(View.GONE);
            }

        }
    }

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 10000000;  /* 10 secs */
    private long FASTEST_INTERVAL = 20000; /* 2 sec */
    private LatLng latLng;

    @SuppressLint("RestrictedApi")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(SelectLocation_Activity.this);
    }

}
