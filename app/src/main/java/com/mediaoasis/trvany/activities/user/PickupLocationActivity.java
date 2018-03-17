package com.mediaoasis.trvany.activities.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
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
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.utils.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PickupLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 200;
    public static LatLng PickupLocationLatLng;
    String PickupLocationName = "", PickupLocationAddress = "", PickupLocationSign = "";
    String[] perms = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

    GPSTracker gpsTracker;
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;

    TextView locationName_tv, locationAddress_tv, sign_tv, next_tv;
    Button changeLocation_btn, addSign_btn;
    LinearLayout next_btn, refresh_btn;

    Provider provider;
    Furniture furniture;
    boolean fromOrderDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location);

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


        Intent i = getIntent();
        if (i != null && i.getParcelableExtra("provider") != null) {
            provider = i.getParcelableExtra("provider");
            furniture = i.getParcelableExtra("furniture");
        } else if (getIntent().getExtras().getBoolean("fromOrderDetails")) {
            fromOrderDetails = true;
        }

        gpsTracker = new GPSTracker(PickupLocationActivity.this);
//        DestinationLatLng = new LatLng(0.0, 0.0);

        changeLocation_btn = (Button) findViewById(R.id.buttonChangeLocation);
        addSign_btn = (Button) findViewById(R.id.buttonAddSign);
        next_btn = (LinearLayout) findViewById(R.id.linearScheduleNext);
        refresh_btn = (LinearLayout) findViewById(R.id.linearRefreshLocation);

        locationName_tv = (TextView) findViewById(R.id.textviewPickupLocationName);
        locationAddress_tv = (TextView) findViewById(R.id.textviewPickupLocationAddress);
        sign_tv = (TextView) findViewById(R.id.textviewPickupLocationSign);
        next_tv = (TextView) findViewById(R.id.txtviewPickupNext);

        if (fromOrderDetails) {
            next_tv.setText(getString(R.string.save_changes));

            sign_tv.setText(OrderDetailsActivity.PickupSign);
            locationName_tv.setText(OrderDetailsActivity.PickupName);
            locationAddress_tv.setText(OrderDetailsActivity.PickupAddress);
            LatLng latLng = new LatLng(OrderDetailsActivity.PickupLatitude, OrderDetailsActivity.PickupLongitude);
            PickupLocationLatLng = latLng;
        }

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(PickupLocationActivity.this);

        if (gpsTracker.canGetLocation()) {
            getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }


        changeLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLocationDialog();
            }
        });

        addSign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignDialog();
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fromOrderDetails) {
                    Intent intent = new Intent(PickupLocationActivity.this, ScheduleProviderActivity.class);
                    intent.putExtra("provider", provider);
                    intent.putExtra("furniture", furniture);
                    intent.putExtra("pickupName", PickupLocationName);
                    intent.putExtra("pickupSign", PickupLocationSign);
                    intent.putExtra("pickupAdd", PickupLocationAddress);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    finish();
                } else {
                    OrderDetailsActivity.PickupAddress = PickupLocationAddress;
                    OrderDetailsActivity.PickupSign = PickupLocationSign;
                    OrderDetailsActivity.PickupName = PickupLocationName;
                    OrderDetailsActivity.PickupLatitude = PickupLocationLatLng.latitude;
                    OrderDetailsActivity.PickupLongitude = PickupLocationLatLng.longitude;
                    OrderDetailsActivity.isLocationChanged = true;
                    finish();
                }
//                showDestinationLocationDialog();
            }
        });

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsTracker.canGetLocation()) {
                    PickupLocationLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    addMyLocationMarker(PickupLocationLatLng);
                } else {
                    gpsTracker.showSettingsAlert();
                }
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


//    public void zoomOnAllMarkers() {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : AllDriversMarkers) {
//            builder.include(marker.getPosition());
//        }
//        LatLngBounds bounds = builder.build();
//        int padding = 10; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        googleMap.animateCamera(cu);
//    }

    private void getLocation() {
        gpsTracker = new GPSTracker(PickupLocationActivity.this);
        if (ActivityCompat.checkSelfPermission(PickupLocationActivity.this,
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsTracker = new GPSTracker(PickupLocationActivity.this);
            googleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(PickupLocationActivity.this, getString(R.string.location_permission_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                requestPermissions(perms, MY_LOCATION_REQUEST_CODE);
            } else {
                gpsTracker = new GPSTracker(PickupLocationActivity.this);
                googleMap.setMyLocationEnabled(true);
            }

        }
        gpsTracker = new GPSTracker(PickupLocationActivity.this);
        PickupLocationLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        addMyLocationMarker(PickupLocationLatLng);
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(PickupLocationActivity.this, Locale.getDefault());
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
                PickupLocationName = obj.getFeatureName();
                PickupLocationAddress = obj.getAddressLine(0) + "," + obj.getAddressLine(1) + "," + obj.getAddressLine(2);

                locationAddress_tv.setText(PickupLocationAddress);
                locationName_tv.setText(PickupLocationName);
            } else {
                Toast.makeText(PickupLocationActivity.this, getString(R.string.cant_get_pickup), Toast.LENGTH_SHORT).show();
                locationAddress_tv.setText(getString(R.string.latitude) + lat);
                locationName_tv.setText(getString(R.string.longitude) + lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        locationName_tv.setText(PickupLocationName);
//        locationAddress_tv.setText(PickupLocationAddress);
//        sign_tv.setText(PickupLocationSign);
//
//        if (PickupLocationLatLng != null) {
//            googleMap.clear();
//            addMyLocationMarker(PickupLocationLatLng);
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(PickupLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PickupLocationActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gpsTracker = new GPSTracker(PickupLocationActivity.this);
                googleMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(PickupLocationActivity.this, getString(R.string.permission_not_granted)
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        gpsTracker = new GPSTracker(PickupLocationActivity.this);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();
    }

    @Override
    public void onBackPressed() {
//        AllDrivers.clear();
//        AllDriversMarkers.clear();
        PickupLocationLatLng = null;
        PickupLocationAddress = "";
        PickupLocationName = "";

        super.onBackPressed();
    }

    void showSignDialog() {
        final Dialog dialog = new Dialog(PickupLocationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_sign);

        final EditText sign_et = (EditText) dialog.findViewById(R.id.edittextSign);
        Button submit_btn = (Button) dialog.findViewById(R.id.buttonSubmitSign);
        Button cancel_btn = (Button) dialog.findViewById(R.id.buttonCancelSign);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickupLocationSign = sign_et.getText().toString();
                sign_tv.setText(PickupLocationSign);
                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void showChangeLocationDialog() {
        final Dialog dialog = new Dialog(PickupLocationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_location);

        PlaceAutocompleteFragment autocompleteFragment;
        Button submit_btn = (Button) dialog.findViewById(R.id.buttonSubmitSign);
        Button cancel_btn = (Button) dialog.findViewById(R.id.buttonCancelSign);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e("Current name: ", place.getName() + "");

//                if (!isDestination) {
                PickupLocationName = place.getName() + "";
                PickupLocationAddress = place.getAddress() + "";
                PickupLocationLatLng = place.getLatLng();
//                } else {
//                    GetRideActivity.DestinationName = place.getName() + "";
//                    GetRideActivity.DestinationAddress = place.getAddress() + "";
//                    GetRideActivity.DestinationLatLng = place.getLatLng();
//                }
//                finish();
            }

            @Override
            public void onError(Status status) {

                Log.i("Error: ", status + "");
            }
        });


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PickupLocationSign = sign_et.getText().toString();
//                sign_tv.setText(PickupLocationSign);
                locationName_tv.setText(PickupLocationName);
                locationAddress_tv.setText(PickupLocationAddress);

                try {
                    googleMap.clear();
                } catch (Exception e) {
                    Toast.makeText(PickupLocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                addMyLocationMarker(PickupLocationLatLng);

                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
