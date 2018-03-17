package com.mediaoasis.trvany.activities.provider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.UserAccessActivity;
import com.mediaoasis.trvany.fragments.provider.AppointmentsFragment;
import com.mediaoasis.trvany.fragments.provider.ChatFragment;
import com.mediaoasis.trvany.fragments.provider.History2Fragment;
import com.mediaoasis.trvany.fragments.provider.Profile2Fragment;
import com.mediaoasis.trvany.fragments.provider.FurnitureFragment;
import com.mediaoasis.trvany.fragments.provider.Settings2Fragment;
import com.mediaoasis.trvany.fragments.user.HomeFragment;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Order;
import com.mediaoasis.trvany.models.Provider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG_HOME = "home";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_ORDERS = "orders";
    private static final String TAG_PROFILE = "profile";
    //    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_MESSAGES = "messages";
    public static String CURRENT_TAG = TAG_HOME;

    public static List<Order> AllOrders;
    public static List<Conversation> AllConversations;

    public static int navItemIndex = 0;
    public static Provider currentProvider;
    public static boolean isOrderLoaded = false, isChatLoaded = false;
    public static Uri ProfileURL, DownloadURI;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Bitmap bitmap;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private CircleImageView imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = firebaseDatabase.getReference();

        AllOrders = new ArrayList<>();
        AllConversations = new ArrayList<>();

        Intent i = getIntent();
        currentProvider = i.getParcelableExtra("broker");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(Html.fromHtml("<font color='#fff'>" + getString(R.string.my_properties) + "</font>"));
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.country);
        imgProfile = (CircleImageView) navHeader.findViewById(R.id.img_profile);

        // load nav menu header data
        loadNavHeader();
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity2_titles);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadConversations();
        loadOrders();
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Main2 Token: ", refreshedToken);
            saveTokenToDatabase(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTokenToDatabase(String refreshedToken) {
        DatabaseReference tokenRef = firebaseDatabase.getReference().child("Providers")
                .child(firebaseAuth.getCurrentUser().getUid()).child("token");
        tokenRef.setValue(refreshedToken);
    }

    private void loadOrders() {
        DatabaseReference ordersRef = firebaseDatabase.getReference().child("Providers")
                .child(firebaseAuth.getCurrentUser().getUid()).child("Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllOrders.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Order order;
                for (DataSnapshot child : children) {
                    order = child.getValue(Order.class);
                    order.setOrderID(child.getKey());
                    AllOrders.add(order);
                }
                isOrderLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadConversations() {
        DatabaseReference ordersRef = firebaseDatabase.getReference().child("Providers")
                .child(firebaseAuth.getCurrentUser().getUid()).child("Conversations");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllConversations.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Conversation conversation;
                for (DataSnapshot child : children) {
                    conversation = child.getValue(Conversation.class);
                    conversation.setConversationID(child.getKey());
                    AllConversations.add(conversation);
                }
                isChatLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText(currentProvider.getName());
        txtWebsite.setText(currentProvider.getTitle());

        Picasso.with(Main2Activity.this).load(currentProvider.getImage())
                .placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(imgProfile);

        // showing dot next to notifications label
//        navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
//            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
//        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setToolbarTitle() {
//        getSupportActionBar().setTitle(activityTitles[navItemIndex]);

        toolbar.setTitle(Html.fromHtml("<font color='#000'>" + activityTitles[navItemIndex] + "</font>"));
    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                FurnitureFragment homeFragment = new FurnitureFragment();
                return homeFragment;
            case 1:
                AppointmentsFragment appointmentsFragment = new AppointmentsFragment();
                return appointmentsFragment;
//            case 2:
//                NotificationFragment notificationFragment = new NotificationFragment();
//                return notificationFragment;
            case 2:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 3:
                Profile2Fragment profile2Fragment = new Profile2Fragment();
                return profile2Fragment;
            case 4:
                Settings2Fragment settings2Fragment = new Settings2Fragment();
                return settings2Fragment;

            case 5:
                History2Fragment historyFragment = new History2Fragment();
                return historyFragment;

//            case 6:
////                HomeFragment settingsFragment = new HomeFragment();
////                return settingsFragment;
//                return new HomeFragment();
            default:
                return new HomeFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_track_orders:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ORDERS;
                        break;
//                    case R.id.nav_notifications:
//                        CURRENT_TAG = TAG_NOTIFICATIONS;
//                        navItemIndex = 2;
//                        break;
                    case R.id.nav_messages:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MESSAGES;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_history:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_HISTORY;
                        break;
                    case R.id.nav_signout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(Main2Activity.this, UserAccessActivity.class));
                        drawer.closeDrawers();
                        finish();
                        return true;
//                    case R.id.nav_privacy_policy:
                    // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!Profile2Fragment.isPicFromCamera2) {
            try {
                ProfileURL = data.getData();

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ProfileURL);
                // upload image
                uploadImageToFirebase();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Main2Activity.this, getString(R.string.error_happened)
                        + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            try{
                bitmap = (Bitmap) data.getExtras().get("data");
                ProfileURL = createImageFile(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Uri createImageFile(Bitmap bitmap) throws IOException {
        // Create an image file name
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        long curMills = System.currentTimeMillis();
        File file = new File(path, "ossul-" + curMills + ".jpg");
        fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream

        String path2 = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ossul-" + curMills, null);
        return Uri.parse(path2);
    }

    private void uploadImageToFirebase() {
        if (ProfileURL != null) {
            StorageReference ref = storageReference.child("ProvidersImages").child(firebaseUser.getUid());

            final Dialog progressDialog = new Dialog(Main2Activity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_upload_image);
            final TextView textView = (TextView) progressDialog.findViewById(R.id.textDialogUpload);
            progressDialog.show();

            ref.putFile(ProfileURL).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Profile2Fragment.img.setImageBitmap(bitmap);

                    DownloadURI = taskSnapshot.getDownloadUrl();
                    currentProvider.setImage(DownloadURI.toString());
//                    Picasso.with(getActivity()).load(DownloadURI).into(img);
                    databaseReference.child("Providers").child(currentProvider.getBrokerID()).child("image")
                            .setValue(DownloadURI.toString());
                    Toast.makeText(Main2Activity.this, getString(R.string.uploaded_success), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(Main2Activity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    textView.setText(progress + getString(R.string.percent_uploaded));
                }
            });
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
