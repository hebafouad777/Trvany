package com.mediaoasis.trvany.activities.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.utils.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ServiceLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_LOCATION_REQUEST_CODE = 200;
    GPSTracker gpsTracker;
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    //    LatLng PickupLocationLatLng;
//    String PickupLocationName = "", PickupLocationAddress = "";
    String[] perms = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    PlaceAutocompleteFragment autocompleteFragment;
    Button done_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        LinearLayout back_btn = (LinearLayout) findViewById(R.id.toolbarBack);
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        gpsTracker = new GPSTracker(ServiceLocationActivity.this);

        if (gpsTracker.canGetLocation()) {
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(ServiceLocationActivity.this);

            getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e("Current name: ", place.getName() + "");

                AddServiceActivity.showLocation = true;
                AddServiceActivity.LocationName = place.getName() + "";
                AddServiceActivity.LocationAddress = place.getAddress() + "";
                AddServiceActivity.LocationLatLng = place.getLatLng();

                try {
                    googleMap.clear();
                    addMyLocationMarker(place.getLatLng());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {

                Log.i("Error: ", status + "");
            }
        });

        done_btn = (Button) findViewById(R.id.buttonSelectLocation);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    void addMyLocationMarker(LatLng sydney) {
        Marker MyMarker = googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(getString(R.string.my_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_orange)));
        zoomOnMarker(sydney);
    }

    void zoomOnMarker(LatLng coordinate) {
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
        googleMap.animateCamera(yourLocation);
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(ServiceLocationActivity.this);
        if (ActivityCompat.checkSelfPermission(ServiceLocationActivity.this,
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsTracker = new GPSTracker(ServiceLocationActivity.this);
            googleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(ServiceLocationActivity.this, R.string.location_permission_error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                requestPermissions(perms, MY_LOCATION_REQUEST_CODE);
            } else {
                gpsTracker = new GPSTracker(ServiceLocationActivity.this);
                googleMap.setMyLocationEnabled(true);
            }

        }
        gpsTracker = new GPSTracker(ServiceLocationActivity.this);
        AddServiceActivity.LocationLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        addMyLocationMarker(AddServiceActivity.LocationLatLng);
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(ServiceLocationActivity.this, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 1) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                String currentAddress = obj.getSubAdminArea() + ","
                        + obj.getAdminArea();
//            double latitude = obj.getLatitude();/
//            add = add + ", " + obj.getCountryName();
//            add = add + ", " + obj.getCountryCode();
//            add = add + ", " + obj.getPostalCode();
                add = add + ", " + obj.getAdminArea();
                add = add + ", " + obj.getSubAdminArea();
                add = add + ", " + obj.getLocality();
                add = add + ", " + obj.getSubThoroughfare();

//            obj.getAddressLine(0);

//            if (obj.getAdminArea() != null)
//            if (obj.getSubAdminArea() != null)
                AddServiceActivity.LocationName = obj.getFeatureName();
                AddServiceActivity.LocationAddress = obj.getAddressLine(0) + ","
                        + obj.getAddressLine(1) + "," + obj.getAddressLine(2);

            } else {
                Toast.makeText(ServiceLocationActivity.this, R.string.cant_get_pickup, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(ServiceLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ServiceLocationActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gpsTracker = new GPSTracker(ServiceLocationActivity.this);
                googleMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(ServiceLocationActivity.this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        gpsTracker = new GPSTracker(ServiceLocationActivity.this);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
