package com.mediaoasis.trvany.activities.user;

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
import com.mediaoasis.trvany.fragments.user.HistoryFragment;
import com.mediaoasis.trvany.fragments.user.HomeFragment;
import com.mediaoasis.trvany.fragments.user.MessagesFragment;
import com.mediaoasis.trvany.fragments.user.OrdersFragment;
import com.mediaoasis.trvany.fragments.user.ProfileFragment;
import com.mediaoasis.trvany.fragments.user.SettingsFragment;
import com.mediaoasis.trvany.models.City;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Country;
import com.mediaoasis.trvany.models.Offer;
import com.mediaoasis.trvany.models.Order;
import com.mediaoasis.trvany.models.Type;
import com.mediaoasis.trvany.models.User;
import com.mediaoasis.trvany.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_MESSAGES = "messages";
    private static final String TAG_ORDERS = "orders";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_HISTORY = "history";
    public static String CURRENT_TAG = TAG_HOME;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static List<City> AllCities;
    public static List<Country> AllCountries;
    public static List<Offer> AllOffers;
    public static List<Type> AllTypes;
    public static List<String> AllCitiesNames, AllCitiesNamesAr, AllCitiesNamesTr,
            AllCountriesNames, AllCountriesNamesAr, AllCountriesNamesTr,
            AllOffersNames, AllTypesNames, AllOffersAR, AllTypesAR, AllOffersTR, AllTypesTR;
    public static User CurrentUser;
    public static List<Order> AllOrders;
    public static List<Conversation> AllConversations;
    public static boolean isOrderLoaded = false, isChatLoaded = false;
    public static Uri ProfileURL, DownloadURI;
    SharedPref sharedPref;
    Bitmap bitmap;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private CircleImageView imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    // toolbar titles respected to selected nav menu item
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
        setContentView(R.layout.activity_main);

        initializeLists();

        CurrentUser = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(Html.fromHtml("<font color='#fff'>" + getString(R.string.search_real_state) + "</font>"));
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.country);
        imgProfile = (CircleImageView) navHeader.findViewById(R.id.img_profile);

        databaseReference.child("User").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentUser = dataSnapshot.getValue(User.class);
                if (CurrentUser != null) {
                    CurrentUser.setUserID(dataSnapshot.getKey());
                    // load nav menu header data
                    loadNavHeader();
                } else {
                    Toast.makeText(MainActivity.this, "Your data is missing as a User!\nTry to login as an agent.."
                            , Toast.LENGTH_LONG);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

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
            Log.d("Main Token: ", refreshedToken);
            saveTokenToDatabase(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTokenToDatabase(String refreshedToken) {
        DatabaseReference tokenRef = firebaseDatabase.getReference().child("User")
                .child(CurrentUser.getUserID()).child("token");
        tokenRef.setValue(refreshedToken);
    }

    private void loadOrders() {
        DatabaseReference ordersRef = firebaseDatabase.getReference().child("User")
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
        DatabaseReference ordersRef = firebaseDatabase.getReference().child("User")
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

    private void initializeLists() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        AllOrders = new ArrayList<>();
        AllConversations = new ArrayList<>();

        AllCities = new ArrayList<>();
        AllCountries = new ArrayList<>();

        AllCountriesNames = new ArrayList<>();
        AllCountriesNamesAr = new ArrayList<>();
        AllCountriesNamesTr = new ArrayList<>();

        AllCitiesNames = new ArrayList<>();
        AllCitiesNamesAr = new ArrayList<>();
        AllCitiesNamesTr = new ArrayList<>();

        AllOffers = new ArrayList<>();
        AllOffersNames = new ArrayList<>();
        AllOffersAR = new ArrayList<>();
        AllOffersTR = new ArrayList<>();

        AllTypes = new ArrayList<>();
        AllTypesNames = new ArrayList<>();
        AllTypesAR = new ArrayList<>();
        AllTypesTR = new ArrayList<>();

//        AllOffers.add("Women");
//        AllOffers.add("Men");
//
//        AllOffersAR.add("سيدات");
//        AllOffersAR.add("رجال");
//
//        AllOffersTR.add("Bayan");
//        AllOffersTR.add("Erkekler");

//        AllTypes.add("Blood sampling");
//        AllTypes.add("Vaccination furniture");
//        AllTypes.add("Nursing Furniture");
//        AllTypes.add("Care for Chronic Diseases");
//        AllTypes.add("Elderly Care");

//        AllTypes.add("Services of Health SPA");
//        AllTypes.add("Beauty Salons Services");
//        AllTypes.add("Hair Care");
//        AllTypes.add("Nails Care");
//        AllTypes.add("Skin Care");

//        AllTypesAR.add("أخذ عينات دم");
//        AllTypesAR.add("خدمة التطعيم");
//        AllTypesAR.add("خدمة التمريض");
//        AllTypesAR.add("رعاية الأمراض المزمنة");
//        AllTypesAR.add("رعاية المسنين");

//        AllTypesAR.add("خدمات الحمام الصحي");
//        AllTypesAR.add("خدمات صالونات التجميل");
//        AllTypesAR.add("العناية بالشعر");
//        AllTypesAR.add("العناية بالأظافر");
//        AllTypesAR.add("العناية بالبشرة");

//        AllTypesTR.add("Kan örneklerinin alınması");
//        AllTypesTR.add("Aşılama Servisi");
//        AllTypesTR.add("Hemşirelik Hizmeti");
//        AllTypesTR.add("Kronik hastalıkların bakımı");
//        AllTypesTR.add("Yaşlı Bakım Hizmetleri");

//        AllTypesTR.add("Banyo sağlık hizmetleri");
//        AllTypesTR.add("Güzellik salonları");
//        AllTypesTR.add("Saç Bakımı");
//        AllTypesTR.add("Tırnak Bakımı");
//        AllTypesTR.add("Cilt Bakımı");
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText(CurrentUser.getUsername());
        txtWebsite.setText(CurrentUser.getEmail());

        Picasso.with(MainActivity.this).load(CurrentUser.getPhotoURI())
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
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                OrdersFragment ordersFragment = new OrdersFragment();
                return ordersFragment;
            case 2:
                MessagesFragment messagesFragment = new MessagesFragment();
                return messagesFragment;
            case 3:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 4:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 5:
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;

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
                        startActivity(new Intent(MainActivity.this, UserAccessActivity.class));
                        drawer.closeDrawers();
                        finish();
                        return true;

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
        if (!ProfileFragment.isPicFromCamera3) {
            try {
                ProfileURL = data.getData();

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ProfileURL);
                // upload image
                uploadImageToFirebase();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_happened)
                        + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
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
            StorageReference ref = storageReference.child("ProfilePhoto").child(firebaseUser.getUid());

            final Dialog progressDialog = new Dialog(MainActivity.this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_upload_image);
            final TextView textView = (TextView) progressDialog.findViewById(R.id.textDialogUpload);
            progressDialog.show();

            ref.putFile(ProfileURL).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    ProfileFragment.img.setImageBitmap(bitmap);

                    DownloadURI = taskSnapshot.getDownloadUrl();
                    CurrentUser.setPhotoURI(DownloadURI.toString());
//                    Picasso.with(getActivity()).load(DownloadURI).into(img);
                    databaseReference.child("User").child(firebaseUser.getUid()).child("photoURI")
                            .setValue(DownloadURI.toString());
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.uploaded_success)
                            , Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.upload_failed)
                            , Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    textView.setText(progress + getResources().getString(R.string.percent_uploaded));
                }
            });
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
