package in.oriange.joinsta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.MapAddressListModel;
import in.oriange.joinsta.utilities.AutoCompleteLocation;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.joinsta.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinsta.utilities.Utilities.provideLocationAccess;
import static in.oriange.joinsta.utilities.Utilities.turnOnLocation;


public class PickMapLoaction_Activity extends FragmentActivity
        implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener {

    private static final int REQUEST_LOCATION_CODE = 99;
    private MapAddressListModel addressList;
    private AutoCompleteLocation autoCompleteLocation;
    private LatLng latLng1;
    private Context context;
    private GoogleMap mMap;
    private Button btn_save;
    private ImageButton btn_pick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_loaction);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = PickMapLoaction_Activity.this;
        btn_save = findViewById(R.id.btn_save);
        btn_pick = findViewById(R.id.btn_pick);

        autoCompleteLocation = findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            provideLocationAccess(context);
            return;
        }

        if (!isLocationEnabled(context)) {
            turnOnLocation(context);
            return;
        }
        startLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        init();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void init() {
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    provideLocationAccess(context);
                    return;
                }

                if (!isLocationEnabled(context)) {
                    turnOnLocation(context);
                    return;
                }

                if (MainDrawer_Activity.latLng == null) {
                    Utilities.showAlertDialog(context, "Unable to get address from this location. Please try again or search manually", false);
                    return;
                }


                addMapMarker(latLng);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (latLng1 != null) {
                    try {
                        getAllAddress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    Utilities.showAlertDialog(context, "Please search and pick a location", false);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMapMarker(latLng);
            }
        });

    }

    public void getAllAddress() throws IOException {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());
        addressList = new MapAddressListModel();
        addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        addressList.setAddress_line_one(addresses.get(0).getAddressLine(0));
        addressList.setDistrict(addresses.get(0).getLocality());
        addressList.setCountry(addresses.get(0).getCountryName());
        addressList.setState(addresses.get(0).getAdminArea());
        addressList.setPincode(addresses.get(0).getPostalCode());

//        constantData.setAddressListPojo(addressList);

        Intent intent = getIntent();
        intent.putExtra("latitude", String.format("%.6f", latLng1.latitude));
        intent.putExtra("longitude", String.format("%.6f", latLng1.longitude));
        intent.putExtra("mapAddressDetails", addressList);
        setResult(RESULT_OK, intent);

    }

    @Override
    public void onTextClear() {
        mMap.clear();
    }

    @Override
    public void onItemSelected(Place selectedPlace) {
        addMapMarker(selectedPlace.getLatLng());
    }

    private void addMapMarker(LatLng latLng) {
        latLng1 = latLng;
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            if (latLng != null) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses.size() > 0) {
                    autoCompleteLocation.setHint(addresses.get(0).getAddressLine(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 10000000;
    private long FASTEST_INTERVAL = 20000000;
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

}
