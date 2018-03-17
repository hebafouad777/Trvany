package com.mediaoasis.trvany.activities.user;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.adapters.ServiceImagesAdapter;
import com.mediaoasis.trvany.adapters.ServicesByProviderAdapter;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.view.CustomRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ServiceDetailsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int CALL_REQUEST_CODE = 200;
    public static ImageView img_iv;
    ArrayList<String> ImagesList;
    TextView description_tv, country_tv, city_tv, offer_tv, price2_tv, price_tv, area_tv, title_tv, brokerTitle_tv,
            brokerName_tv, brokerName_tv2, brokerName_tv3, brokerName_tv4, brokerAddress_tv;
    CircleImageView brokerImage_iv;
    Button callBroker_btn, chatBroker_btn;
    LinearLayout schedule_linear, brokerInfo_linear;
    CustomRatingBar ratingBar;
    //    String Country = "", City = "";
    Furniture currentFurniture;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference propertyImagesReference, propertyRef, otherServicesRef;
    Provider provider;
    String[] permsCall = new String[]{"android.permission.CALL_PHONE"};
    ServiceImagesAdapter mAdapter;
    ServicesByProviderAdapter otherServicesAdapter;
    List<Furniture> otherServicesList;
    private RecyclerView mRecyclerView, OtherServices_rv;
    private LinearLayoutManager mLayoutManager, mLayoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

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

        otherServicesList = new ArrayList<>();

        title_tv = (TextView) findViewById(R.id.textPropertyDetailsPropertyTitle);
        brokerTitle_tv = (TextView) findViewById(R.id.textPropertyDetailsBrokerTitle);
        brokerName_tv = (TextView) findViewById(R.id.textPropertyDetailsBrokerName);
        brokerName_tv2 = (TextView) findViewById(R.id.textPropertyDetailsBrokerName2);
        brokerName_tv3 = (TextView) findViewById(R.id.textPropertyDetailsBrokerName3);
        brokerName_tv4 = (TextView) findViewById(R.id.textPropertyDetailsBrokerName4);
        brokerAddress_tv = (TextView) findViewById(R.id.textPropertyDetailsBrokerAddress);
        area_tv = (TextView) findViewById(R.id.textPropertyDetailsArea);
        price_tv = (TextView) findViewById(R.id.textPropertyDetailsPrice);
        price2_tv = (TextView) findViewById(R.id.textPropertyDetailsPrice2);
        offer_tv = (TextView) findViewById(R.id.textPropertyDetailsOffer);
        city_tv = (TextView) findViewById(R.id.textPropertyDetailsCity);
        country_tv = (TextView) findViewById(R.id.textPropertyDetailsCountry);
        description_tv = (TextView) findViewById(R.id.textPropertyDetailsDesc);

        img_iv = (ImageView) findViewById(R.id.imagePropertyDetailsViewedImg);
        brokerImage_iv = (CircleImageView) findViewById(R.id.imageBrokerImage);
        ratingBar = (CustomRatingBar) findViewById(R.id.ratingBrokerRate);
        ratingBar.setHalfStars(true);
        ratingBar.setClickable(false);
        ratingBar.setEnabled(false);
        ratingBar.setScrollToSelect(false);

        schedule_linear = (LinearLayout) findViewById(R.id.linearSchdule);
        brokerInfo_linear = (LinearLayout) findViewById(R.id.linearBrokerInfo);
        callBroker_btn = (Button) findViewById(R.id.buttonCallBroker);
        chatBroker_btn = (Button) findViewById(R.id.buttonMsgBroker);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        mLayoutManager = new LinearLayoutManager(ServiceDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        OtherServices_rv = (RecyclerView) findViewById(R.id.recyclerViewOtherProperties);
        mLayoutManager2 = new LinearLayoutManager(ServiceDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        OtherServices_rv.setLayoutManager(mLayoutManager2);

        Intent i = getIntent();
        if (i != null && i.getParcelableExtra("furniture") != null) {
            currentFurniture = i.getParcelableExtra("furniture");

            propertyImagesReference = firebaseDatabase.getReference().getRoot().child("Services")
                    .child(currentFurniture.getCountry())
                    .child(currentFurniture.getCity())
                    .child(currentFurniture.getOffer())
                    .child(currentFurniture.getType())
                    .child(currentFurniture.getPropertyID())
                    .child("images");

            propertyImagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    HashMap<String, String> Images = new HashMap();
                    ImagesList = new ArrayList<>();

                    for (DataSnapshot child : children) {
//                        Images.add(child.getValue(String.class));
                        Images.put(child.getKey(), child.getValue(String.class));
                        ImagesList.add(Images.get(child.getKey()));
                    }
                    currentFurniture.setImages(Images);

                    mAdapter = new ServiceImagesAdapter(ServiceDetailsActivity.this, ImagesList, false);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(ServiceDetailsActivity.this, getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
            finish();
        }

        title_tv.setText(currentFurniture.getTitle());
        brokerName_tv.setText(currentFurniture.getBrokerID());
        area_tv.setText(currentFurniture.getArea());

        if (currentFurniture.getCountry().equals("Egypt")) {
            price_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.egp));
            price2_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.egp));
        } else if (currentFurniture.getCountry().equals("Saudi Arabia")) {
            price_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
            price2_tv.setText(currentFurniture.getPrice() + " " + getString(R.string.sar));
        }
//        else {
//            price_tv.setText(currentFurniture.getPrice());
//            price2_tv.setText(currentFurniture.getPrice());
//        }


//        if (currentFurniture.getOffer().equals("Sell"))
//            offer_tv.setText(getString(R.string.sell));
//        else if (currentFurniture.getOffer().equals("Rent"))
//            offer_tv.setText(getString(R.string.rent));

//        if (currentFurniture.getType().equals("Flat"))
//            type_tv.setText(getString(R.string.flat));
//        else if (currentFurniture.getType().equals("Studio"))
//            type_tv.setText(getString(R.string.studio));
//        else if (currentFurniture.getType().equals("Villa"))
//            type_tv.setText(getString(R.string.villa));
//        else if (currentFurniture.getType().equals("Land"))
//            type_tv.setText(getString(R.string.land));
//
//        offer_tv.setText(currentFurniture.getOffer());
        country_tv.setText(currentFurniture.getCountry());
        city_tv.setText(currentFurniture.getCity());
        description_tv.setText(currentFurniture.getDescription());
        Picasso.with(ServiceDetailsActivity.this).load(currentFurniture.getImage()).fit().into(img_iv);

        propertyRef = firebaseDatabase.getReference();
        propertyRef = propertyRef.child("Providers").child(currentFurniture.getBrokerID());
        propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                provider = dataSnapshot.getValue(Provider.class);
                provider.setBrokerID(currentFurniture.getBrokerID());
                brokerName_tv.setText(provider.getName());
                brokerName_tv2.setText(provider.getName());
                brokerName_tv3.setText(provider.getName());
                brokerName_tv4.setText(provider.getName());
                brokerAddress_tv.setText(provider.getAddress());
                brokerTitle_tv.setText(provider.getTitle());
                ratingBar.setScore(provider.getRate());

                Picasso.with(ServiceDetailsActivity.this).load(provider.getImage()).into(brokerImage_iv);

                otherServicesRef = propertyRef.child("Services");
                otherServicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        Furniture furniture;
                        for (DataSnapshot child : children) {
                            furniture = child.getValue(Furniture.class);
                            furniture.setPropertyID(child.getKey());
                            otherServicesList.add(furniture);
                        }
                        otherServicesAdapter = new ServicesByProviderAdapter(ServiceDetailsActivity.this,
                                otherServicesList);
                        OtherServices_rv.setAdapter(otherServicesAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        brokerInfo_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceDetailsActivity.this, ProviderInfoActivity.class);
                intent.putExtra("provider", provider);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        schedule_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceDetailsActivity.this, PickupLocationActivity.class);
                intent.putExtra("furniture", currentFurniture);
                intent.putExtra("provider", provider);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        chatBroker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ConfID = "";
                boolean isConfFound = false;
                for (int i = 0; i < MainActivity.AllConversations.size(); i++)
                    if (MainActivity.AllConversations.get(i).getBrokerID().equals(provider.getBrokerID())) {
                        ConfID = MainActivity.AllConversations.get(i).getConversationID();
                        isConfFound = true;
                        break;
                    }

                if (isConfFound) {
                    startChatActivity(ConfID);
                } else {
                    createConversation();
                }
            }
        });

        callBroker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provider.getPhone() != null)
                    callBroker();
            }
        });

    }

    private void createConversation() {
        final Conversation conversation = new Conversation();
        conversation.setBrokerID(provider.getBrokerID());
        conversation.setUserID(MainActivity.CurrentUser.getUserID());

        conversation.setBrokerImage(provider.getImage());
        conversation.setUserImage(MainActivity.CurrentUser.getPhotoURI());

        conversation.setBrokerName(provider.getName());
        conversation.setUserName(MainActivity.CurrentUser.getUsername());

        conversation.setConversationID(MainActivity.CurrentUser.getUserID() + currentFurniture.getBrokerID());

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("User")
                .child(MainActivity.CurrentUser.getUserID()).child("Conversations")
                .child(conversation.getConversationID());

        databaseReference.setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference userRef = firebaseDatabase.getReference().getRoot().child("Providers")
                            .child(currentFurniture.getBrokerID()).child("Conversations")
                            .child(conversation.getConversationID());
                    userRef.setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                MainActivity.AllConversations.add(conversation);
                                startChatActivity(conversation.getConversationID());
                            }
                        }
                    });

                }
            }
        });
    }

    void startChatActivity(String ConfID) {
        Intent intent = new Intent(ServiceDetailsActivity.this, ChatActivity.class);
        intent.putExtra("confID", ConfID);
        intent.putExtra("isBroker", 0);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @SuppressLint("NewApi")
    private void callBroker() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + provider.getPhone()));
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, getString(R.string.call_permission_needed),
                            Toast.LENGTH_SHORT).show();
                }
            }
            requestPermissions(permsCall, CALL_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED/* && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    return;
                }
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + provider.getPhone()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            } else {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
