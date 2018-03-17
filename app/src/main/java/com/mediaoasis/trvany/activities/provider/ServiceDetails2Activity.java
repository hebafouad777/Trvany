package com.mediaoasis.trvany.activities.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.adapters.RequestsAdapter;
import com.mediaoasis.trvany.adapters.ServiceImagesAdapter;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.models.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ServiceDetails2Activity extends AppCompatActivity {

    public static ArrayList<String> ImagesList, ImagesKeys;
    public static ImageView img_iv;
    public static List<Order> PropertyOrders;
    HashMap<String, String> Images;
    TextView description_tv, country_tv, city_tv, location_tv, price_tv, price2_tv, area_tv,
            title_tv, ordersNum_tv, offer_tv, type_tv;
    Button edit_btn;
    Furniture currentFurniture;
    ProgressBar progressBarApp, progressBarImgs;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference propertyImagesReference;
    ServiceImagesAdapter mAdapter;
    RequestsAdapter requestsAdapter;
    private RecyclerView mRecyclerView, requests_rv;
    private LinearLayoutManager mLayoutManager, mLayoutManager2;
    private boolean isAdapterCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details2);

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


        firebaseDatabase = FirebaseDatabase.getInstance();
        PropertyOrders = new ArrayList<>();

        title_tv = (TextView) findViewById(R.id.textViewPropertyTitle);
        area_tv = (TextView) findViewById(R.id.textPropertyDetailsArea);
        price_tv = (TextView) findViewById(R.id.textPropertyDetailsPrice);
        price2_tv = (TextView) findViewById(R.id.textPropertyDetailsPrice2);
        location_tv = (TextView) findViewById(R.id.textPropertyDetailsLocation);
        city_tv = (TextView) findViewById(R.id.textPropertyDetailsCity);
        country_tv = (TextView) findViewById(R.id.textPropertyDetailsCountry);
        description_tv = (TextView) findViewById(R.id.textPropertyDesc);
        type_tv = (TextView) findViewById(R.id.textPropertyDetailsType);
        offer_tv = (TextView) findViewById(R.id.textPropertyDetailsOffer);
        ordersNum_tv = (TextView) findViewById(R.id.textViewPropertyReqNum);

        img_iv = (ImageView) findViewById(R.id.imagePropertyDetailsViewedImg);
        edit_btn = (Button) findViewById(R.id.buttonEditProperty);

        progressBarApp = (ProgressBar) findViewById(R.id.progBarAppointments);
        progressBarImgs = (ProgressBar) findViewById(R.id.progBarImgs);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        mLayoutManager = new LinearLayoutManager(ServiceDetails2Activity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        requests_rv = (RecyclerView) findViewById(R.id.recyclerViewAppointments);
        mLayoutManager2 = new LinearLayoutManager(ServiceDetails2Activity.this, LinearLayoutManager.VERTICAL, false);
        requests_rv.setLayoutManager(mLayoutManager2);

        Intent i = getIntent();
        if (i != null && i.getParcelableExtra("furniture") != null) {
            currentFurniture = i.getParcelableExtra("furniture");
            getImagesList();
            getActiveAppointments();

            title_tv.setText(currentFurniture.getTitle());
            location_tv.setText(currentFurniture.getAddress());
            description_tv.setText(currentFurniture.getDescription());

            country_tv.setText(currentFurniture.getCountry());
            city_tv.setText(currentFurniture.getCity());
            area_tv.setText(currentFurniture.getArea());

//            if (currentFurniture.getOffer().equals("Sell"))
//                offer_tv.setText(getString(R.string.sell));
//            else if (currentFurniture.getOffer().equals("Rent"))
//                offer_tv.setText(getString(R.string.rent));
//
//            if (currentFurniture.getType().equals("Flat"))
//                type_tv.setText(getString(R.string.flat));
//            else if (currentFurniture.getType().equals("Studio"))
//                type_tv.setText(getString(R.string.studio));
//            else if (currentFurniture.getType().equals("Villa"))
//                type_tv.setText(getString(R.string.villa));
//            else if (currentFurniture.getType().equals("Land"))
//                type_tv.setText(getString(R.string.land));


            Picasso.with(ServiceDetails2Activity.this).load(currentFurniture.getImage()).fit().into(img_iv);

            if (currentFurniture.getCountry().equals("Egypt")) {
                price_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.egp));
                price2_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.egp));
            } else if (currentFurniture.getCountry().equals("Saudi Arabia")) {
                price_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
                price2_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
            } else if (currentFurniture.getCountry().equals("Turkey")) {
                price_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
                price2_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
            } else {
                price_tv.setText(currentFurniture.getPrice() + "");
                price2_tv.setText(currentFurniture.getPrice() + "");
            }

            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ServiceDetails2Activity.this, UpdateServiceActivity.class);
                    intent.putExtra("property", currentFurniture);
                    startActivity(intent);
                }
            });

        } else {
            Toast.makeText(ServiceDetails2Activity.this, getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void getActiveAppointments() {
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (Main2Activity.isOrderLoaded) {
                    PropertyOrders.clear();

                    for (int i = 0; i < Main2Activity.AllOrders.size(); i++) {
                        if (!Main2Activity.AllOrders.get(i).getStatus().startsWith("canceled")
                                || !Main2Activity.AllOrders.get(i).getStatus().equals("done")) {
                            if (Main2Activity.AllOrders.get(i).getPropertyID().equals(currentFurniture.getPropertyID()))
                                PropertyOrders.add(Main2Activity.AllOrders.get(i));
                        }
                    }

                    progressBarApp.setVisibility(View.INVISIBLE);
                    requestsAdapter = new RequestsAdapter(ServiceDetails2Activity.this, PropertyOrders);
                    requests_rv.setAdapter(requestsAdapter);
                    if (PropertyOrders.size() == 0)
                        ordersNum_tv.setText(R.string.property_no_requests);
                    else if (PropertyOrders.size() == 1)
                        ordersNum_tv.setText(getString(R.string.this_property_has) + PropertyOrders.size()
                                + getString(R.string.request));
                    else
                        ordersNum_tv.setText(getString(R.string.this_property_have) + PropertyOrders.size()
                                + getString(R.string.requests));
                } else {
                    this.start();
                }
            }
        }.start();
    }

//    public void selectImageFromGallery(Context context, int pos) {
//        String SelectedImageURL = ImagesList.get(pos);
//        Picasso.with(context).load(SelectedImageURL).into(img_iv);
//    }


    public void getImagesList() {
        final DatabaseReference propertyImagesReference = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(currentFurniture.getBrokerID())
                .child("Furniture")
                .child(currentFurniture.getPropertyID())
                .child("images");

        propertyImagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Images = new HashMap();
                ImagesList = new ArrayList<>();
                ImagesKeys = new ArrayList<>();

                for (DataSnapshot child : children) {
//                        Images.add(child.getValue(String.class));
                    Images.put(child.getKey(), child.getValue(String.class));
                    ImagesList.add(Images.get(child.getKey()));
                    ImagesKeys.add(child.getKey());
                }
                progressBarImgs.setVisibility(View.INVISIBLE);
                currentFurniture.setImages(Images);
                mAdapter = new ServiceImagesAdapter(ServiceDetails2Activity.this, ImagesList, true);
                mRecyclerView.setAdapter(mAdapter);
                isAdapterCreated = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAdapterCreated)
            mAdapter.notifyDataSetChanged();
    }
}
