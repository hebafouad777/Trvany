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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mediaoasis.trvany.adapters.ReviewsAdapter;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.models.Rating;
import com.mediaoasis.trvany.view.CustomRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProviderInfoActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int CALL_REQUEST_CODE = 200;
    String[] permsCall = new String[]{"android.permission.CALL_PHONE"};
    Button callBroker_btn, chatBroker_btn;
    LinearLayout schedule_linear;
    CustomRatingBar ratingBar;
    TextView brokerName_tv, brokerTitle_tv;
    CircleImageView brokerImage_iv;
    FirebaseDatabase firebaseDatabase;
    Provider currentProvider;
    ArrayList<Rating> BrokerReviews;
    ReviewsAdapter mAdapter;
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_info);


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

        BrokerReviews = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        brokerName_tv = (TextView) findViewById(R.id.textDetailsBrokerName);
        brokerTitle_tv = (TextView) findViewById(R.id.textDetailsBrokerTitle);

        brokerImage_iv = (CircleImageView) findViewById(R.id.imgBrokerImg);
        ratingBar = (CustomRatingBar) findViewById(R.id.ratingBrokerRate2);
        progressBar = (ProgressBar) findViewById(R.id.progressBarReviews);
        ratingBar.setHalfStars(true);
        ratingBar.setClickable(false);
        ratingBar.setEnabled(false);
        ratingBar.setScrollToSelect(false);

        schedule_linear = (LinearLayout) findViewById(R.id.linearSchdule);
        callBroker_btn = (Button) findViewById(R.id.buttonCallBroker);
        chatBroker_btn = (Button) findViewById(R.id.buttonMsgBroker);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewBrokersReviews);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReviewsAdapter(ProviderInfoActivity.this, BrokerReviews);
        mRecyclerView.setAdapter(mAdapter);

        Intent i = getIntent();
        if (i != null && i.getParcelableExtra("provider") != null) {
            currentProvider = i.getParcelableExtra("provider");
            brokerName_tv.setText(currentProvider.getName());
//            brokerTitle_tv.setText(currentProvider.getTitle());
            Picasso.with(ProviderInfoActivity.this).load(currentProvider.getImage()).into(brokerImage_iv);
            ratingBar.setScore(currentProvider.getRate());

            DatabaseReference reviewRef = firebaseDatabase.getReference().child("").child(currentProvider.getBrokerID())
                    .child("Reviews");
            reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Rating review;
                    for (DataSnapshot child : children) {
                        review = child.getValue(Rating.class);
                        BrokerReviews.add(review);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    mAdapter = new ReviewsAdapter(ProviderInfoActivity.this, BrokerReviews);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(ProviderInfoActivity.this, getResources().getString(R.string.error_happened)
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        schedule_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProviderInfoActivity.this, PickupLocationActivity.class);
                intent.putExtra("provider", currentProvider);
                startActivity(intent);
            }
        });

        chatBroker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ConfID = "";
                boolean isConfFound = false;
                for (int i = 0; i < MainActivity.AllConversations.size(); i++)
                    if (MainActivity.AllConversations.get(i).getBrokerID().equals(currentProvider.getBrokerID())) {
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
                if (currentProvider.getPhone() != null)
                    callBroker();
            }
        });
    }


    private void createConversation() {
        final Conversation conversation = new Conversation();
        conversation.setBrokerID(currentProvider.getBrokerID());
        conversation.setUserID(MainActivity.CurrentUser.getUserID());

        conversation.setBrokerImage(currentProvider.getImage());
        conversation.setUserImage(MainActivity.CurrentUser.getPhotoURI());

        conversation.setBrokerName(currentProvider.getName());
        conversation.setUserName(MainActivity.CurrentUser.getUsername());

        conversation.setConversationID(MainActivity.CurrentUser.getUserID() + currentProvider.getBrokerID());

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("User")
                .child(MainActivity.CurrentUser.getUserID()).child("Conversations")
                .child(conversation.getConversationID());
//        conversation.setConversationID(databaseReference.push().getKey());

        databaseReference.setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference userRef = firebaseDatabase.getReference().getRoot().child("")
                            .child(currentProvider.getBrokerID()).child("Conversations")
                            .child(MainActivity.CurrentUser.getUserID() + currentProvider.getBrokerID());

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
        Intent intent = new Intent(ProviderInfoActivity.this, ChatActivity.class);
        intent.putExtra("confID", ConfID);
        intent.putExtra("isBroker", 0);
        startActivity(intent);
    }


    @SuppressLint("NewApi")
    private void callBroker() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + currentProvider.getPhone()));
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, getResources().getString(R.string.call_permission_needed),
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
                    callIntent.setData(Uri.parse("tel:" + currentProvider.getPhone()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
