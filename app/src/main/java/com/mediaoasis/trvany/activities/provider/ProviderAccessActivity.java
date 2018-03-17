package com.mediaoasis.trvany.activities.provider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.fragments.provider.SignInBrokerFragment;
import com.mediaoasis.trvany.fragments.provider.SignUpBrokerFragment;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.utils.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProviderAccessActivity extends AppCompatActivity {
    private static final int CAM_REQUREST = 2;
    private static final int GALLERY_REQUEST_CODE = 1;
    public static Uri ProfileURL;
    public static Bitmap bitmap;
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    Provider currentProvider;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    SharedPref sharedPref;
    StorageReference storageReference;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        storageReference = FirebaseStorage.getInstance().getReference();

        sharedPref = new SharedPref(ProviderAccessActivity.this);
        currentProvider = new Provider();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
//            goToMainActivity();
            checkIfActivated(firebaseAuth.getCurrentUser().getUid());
        } else {
            createBrokerAccessViews();
        }


    }

    private void createBrokerAccessViews() {

        setContentView(R.layout.activity_provider_access);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
//            Skip = (Button) findViewById(R.id.buttonSkip);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            int count = 0;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tabLayout.getTabAt(0) == tab)
                    tabLayout.getTabAt(0).setText(R.string.sign_up);

                else if (tabLayout.getTabAt(1) == tab)
                    tabLayout.getTabAt(1).setText(R.string.sign_in);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }


            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
    }

    private void checkIfActivated(String uID) {

        progressDialog = new ProgressDialog(ProviderAccessActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot();
        databaseReference.child("Providers").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentProvider = dataSnapshot.getValue(Provider.class);
                if (currentProvider != null) {
                    currentProvider.setBrokerID(dataSnapshot.getKey());
                    progressDialog.dismiss();

                    goToMainActivity();
//                    if (currentProvider.getActivated() == 1) {
//                        sharedPref.setBoolean("isActivated", true);
//
//                    } else {
//                        Toast.makeText(ProviderAccessActivity.this, R.string.account_activation_error,
//                                Toast.LENGTH_LONG).show();
//                        createBrokerAccessViews();
//                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProviderAccessActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void goToMainActivity() {
        Toast.makeText(ProviderAccessActivity.this, getString(R.string.welcome) + currentProvider.getName(),
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProviderAccessActivity.this, Main2Activity.class);
        intent.putExtra("broker", currentProvider);
        startActivity(intent);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        ProviderAccessActivity.ViewPagerAdapter adapter = new ProviderAccessActivity.ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new SignUpBrokerFragment());
        adapter.addFragment(new SignInBrokerFragment());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setText(R.string.sign_up);
        tabLayout.getTabAt(1).setText(R.string.sign_in);

        tabLayout.setTabTextColors(Color.parseColor("#a4a4a4"), Color.parseColor("#000000"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!SignUpBrokerFragment.isPicFromCamera) {
            Log.e("Request from camera", "false");
            try {
                ProfileURL = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ProfileURL);
                SignUpBrokerFragment.img.setImageBitmap(bitmap);
                // upload image
//            uploadImageToFirebase();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ProviderAccessActivity.this, getString(R.string.error_happened) + " "
                        + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Request from camera", "true");
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                ProfileURL = createImageFile(bitmap);
                SignUpBrokerFragment.img.setImageBitmap(bitmap);
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

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SignUpBrokerFragment();
                case 1:
                    return new SignInBrokerFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

}
