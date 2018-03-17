package com.mediaoasis.trvany.activities.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.models.Order;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.models.Furniture;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mediaoasis.trvany.activities.user.PickupLocationActivity.PickupLocationLatLng;


public class ScheduleProviderActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    CheckBox RemindMe_cb;

    LinearLayout time_ll, date_ll, save_ll;
    TextView date_tv, time_tv;
    String DateToDisplay = "", TimeToDisplay = "";
    //    Booking CurrentBooking;
    String Status = "on request", OrderID;
    Provider provider;
    Furniture furniture;
    String PickupLocationName = "", PickupLocationAddress = "", PickupLocationSign = "";
    FirebaseDatabase firebaseDatabase;
    Order CurrentOrder;
    double PickupLocationLat, PickupLocationLng;
    double Distance, Price;
    String Currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schdule_provider);

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


        provider = new Provider();

        Intent i = getIntent();
        if (i != null && i.getParcelableExtra("provider") != null) {
            provider = i.getParcelableExtra("provider");
            furniture = i.getParcelableExtra("furniture");
        }

        Bundle b = i.getExtras();
        PickupLocationName = b.getString("pickupName");
        PickupLocationAddress = b.getString("pickupAdd");
        PickupLocationSign = b.getString("pickupSign");
        PickupLocationLat = PickupLocationLatLng.latitude;
        PickupLocationLng = PickupLocationLatLng.longitude;

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(ScheduleProviderActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        UserID = firebaseUser.getUid();

        date_tv = (TextView) findViewById(R.id.textSchduleDate);
        time_tv = (TextView) findViewById(R.id.textSchduleTime);
        RemindMe_cb = (CheckBox) findViewById(R.id.checkboxScheduleRemindMe);

        Calendar c = Calendar.getInstance();
//        year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int t = c.get(Calendar.AM_PM);
//        String time = " AM";
//        if (t == 0)
//            time = " AM";
//        else time = " PM";

        // set current date into textview
        DateToDisplay = day + "/" + month + "/" + year;
        date_tv.setText(DateToDisplay);

        TimeToDisplay = hour + ":" + minute;
        time_tv.setText(TimeToDisplay);

        time_ll = (LinearLayout) findViewById(R.id.linearSchduleTime);
        date_ll = (LinearLayout) findViewById(R.id.linearSchduleDate);
        save_ll = (LinearLayout) findViewById(R.id.linearScheduleSave);

        date_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        time_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        save_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScheduleProviderActivity.this);
        alertDialogBuilder.setTitle("Create Order");
        alertDialogBuilder
                .setMessage(R.string.create_order_question)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new GetDistanceClass().execute();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDatePickerDialog() {
        final Dialog dialog = new Dialog(ScheduleProviderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_date_picker);

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePickerDialogSchedule);

        LinearLayout done_ll = (LinearLayout) dialog.findViewById(R.id.linearScheduleDialogDone);
        done_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = datePicker.getMonth() + 1;
                DateToDisplay = datePicker.getDayOfMonth() + "/" + month + "/" + datePicker.getYear();
                date_tv.setText(DateToDisplay);
                dialog.dismiss();
//                Toast.makeText(ScheduleProviderActivity.this, ""DateToDisplay, Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    private void showTimePickerDialog() {
        final Dialog dialog = new Dialog(ScheduleProviderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_time_picker);

        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.TimePickerDialogSchedule);
        LinearLayout done_ll = (LinearLayout) dialog.findViewById(R.id.linearTimeScheduleDialogDone);
        done_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int h = timePicker.getCurrentHour();
                int m = timePicker.getCurrentMinute();
//                int t = timePicker.get
                TimeToDisplay = h + ":" + m;
                time_tv.setText(TimeToDisplay);
                dialog.dismiss();
//                Toast.makeText(ScheduleProviderActivity.this, TimeToDisplay, Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    private void createOrder() {
        CurrentOrder = new Order();

        CurrentOrder.setUserID(MainActivity.CurrentUser.getUserID());
        CurrentOrder.setUserName(MainActivity.CurrentUser.getUsername());
        CurrentOrder.setUserImage(MainActivity.CurrentUser.getPhotoURI());
        CurrentOrder.setUserPhone(MainActivity.CurrentUser.getPhone());

        CurrentOrder.setBrokerID(provider.getBrokerID());
        CurrentOrder.setBrokerName(provider.getName());
        CurrentOrder.setBrokerImage(provider.getImage());
        CurrentOrder.setBrokerTitle(provider.getTitle());
        CurrentOrder.setBrokerPhone(provider.getPhone());

        CurrentOrder.setPropertyID(furniture.getPropertyID());
        CurrentOrder.setPropertyTitle(furniture.getTitle());
//        CurrentOrder.setPropertyDescription(furniture.getDescription());
//        CurrentOrder.setPropertyImage(furniture.getImage());

        CurrentOrder.setPickupAddress(PickupLocationAddress);
        CurrentOrder.setPickupSign(PickupLocationSign);
        CurrentOrder.setPickupName(PickupLocationName);
        CurrentOrder.setPickupLatitude(PickupLocationLat);
        CurrentOrder.setPickupLongitude(PickupLocationLng);

        CurrentOrder.setTime(TimeToDisplay);
        CurrentOrder.setDate(DateToDisplay);
        CurrentOrder.setStatus(Status);

        CurrentOrder.setUserReminder(RemindMe_cb.isChecked());
        CurrentOrder.setBrokerReminder(true);
        CurrentOrder.setFees(String.valueOf(Price) + Currency);

        DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders");
        final String id = userRef.push().getKey();
        userRef = userRef.child(id);

        userRef.setValue(CurrentOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    CurrentOrder.setOrderID(id);

                    DatabaseReference brokerRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(id);

                    brokerRef.setValue(CurrentOrder)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        showWaitingDialog();
//                                        Toast.makeText(ScheduleProviderActivity.this,
//                                                "Order Created Successfully..", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(ScheduleProviderActivity.this,
                                                R.string.request_order_failed, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Toast.makeText(ScheduleProviderActivity.this,
                            getString(R.string.request_order_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

//
//                }
//            }
//        });
    }


    private void showWaitingDialog() {
        final Dialog dialog = new Dialog(ScheduleProviderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait_approval);
        dialog.setCancelable(false);

        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogWaitApproval);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        addMyLocationMarker(PickupLocationLatLng);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class GetDistanceClass extends AsyncTask<String, String, Boolean> {
        boolean isSuccess = false;
        String ResponseSTR = "", StatusDesc = "";
        String str = "";
        TextView title_tv, content_tv;
        ProgressBar progress_pb;
        Button confirm_btn, cancel_btn;
        LinearLayout buttons_ll;
        Dialog priceDialog;
        String URL = "";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            URL = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + PickupLocationLat + "," + PickupLocationLng +
                    "&destination=" + furniture.getLatitude() + "," + furniture.getLongitude() +
                    "&key=AIzaSyAdxZk7HspYTEnmmKwR85ji4Bvozg6tpbc";

            priceDialog = new Dialog(ScheduleProviderActivity.this);
            priceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            priceDialog.setContentView(R.layout.dialog_price);
            priceDialog.setCancelable(false);

            title_tv = (TextView) priceDialog.findViewById(R.id.textDialogTitle);
            content_tv = (TextView) priceDialog.findViewById(R.id.textDialogContent);
            progress_pb = (ProgressBar) priceDialog.findViewById(R.id.progressBarDialogPrice);
            confirm_btn = (Button) priceDialog.findViewById(R.id.buttonDialogPriceConfirm);
            cancel_btn = (Button) priceDialog.findViewById(R.id.buttonDialogPriceCancel);
            buttons_ll = (LinearLayout) priceDialog.findViewById(R.id.linearDialogPrice);

            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    title_tv.setText(R.string.create_order);
//                    progress_pb.setVisibility(View.VISIBLE);
                    createOrder();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    priceDialog.dismiss();
                }
            });

            priceDialog.show();

        }

        @Override
        protected Boolean doInBackground(final String... params) {
            boolean tmp = true;

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "user_id=25&lang_code=en");
            Request request = new Request.Builder()
                    .url(URL)
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "3eca8b89-8a25-abbf-c839-5250ffe1c11e")
                    .build();


            try {
                Response response = client.newCall(request).execute();
                ResponseSTR = response.body().string();
                Log.e("ResponseSTR", ResponseSTR);
            } catch (IOException e) {
                e.printStackTrace();
                priceDialog.cancel();
                Toast.makeText(ScheduleProviderActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            return tmp;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            try {
                JSONObject jsonObject = new JSONObject(ResponseSTR);

                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routesObj = routesArray.getJSONObject(0);
                JSONArray legsArray = routesObj.getJSONArray("legs");
                JSONObject legsObj = legsArray.getJSONObject(0);
                JSONObject distanceObj = legsObj.getJSONObject("distance");

                Log.e("distanceValue", distanceObj.getInt("value") + "");

                Distance = distanceObj.getInt("value") / 1000;
                Price = Math.round(Distance * 4);


                if (furniture.getCountry().equals(getString(R.string.egypt)))
                    Currency = getString(R.string.egp);
                else if (furniture.getCountry().equals(getString(R.string.ksa)))
                    Currency = getString(R.string.sar);
                else {
                    Currency = "";
                }

                content_tv.setText(getString(R.string.order_fees_will_be) + Price + Currency + '\n' +
                        getString(R.string.do_you_want_to_confirm));
                progress_pb.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
                priceDialog.cancel();
                Toast.makeText(ScheduleProviderActivity.this, getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT).show();
            }

        }


    }


}
