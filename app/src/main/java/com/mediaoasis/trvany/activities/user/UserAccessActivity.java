package com.mediaoasis.trvany.activities.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.ProviderAccessActivity;
import com.mediaoasis.trvany.fragments.provider.SignInBrokerFragment;
import com.mediaoasis.trvany.fragments.provider.SignUpBrokerFragment;
import com.mediaoasis.trvany.fragments.user.SignInFragment;
import com.mediaoasis.trvany.fragments.user.SignUpFragment;
import com.mediaoasis.trvany.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class UserAccessActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button agent_btn;
    SharedPref sharedPref;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedPref = new SharedPref(UserAccessActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent;
            if (!sharedPref.getBoolean("isBroker")) {
                intent = new Intent(UserAccessActivity.this, MainActivity.class);
            } else {
                intent = new Intent(UserAccessActivity.this, ProviderAccessActivity.class);
            }
            startActivity(intent);
            finish();
        } else {

            setContentView(R.layout.activity_user_access);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            agent_btn = (Button) findViewById(R.id.buttonAgent);

            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                int count = 0;

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());

                    if (tabLayout.getTabAt(0) == tab) {
                        tabLayout.getTabAt(0).setText(R.string.sign_up);
//                        tabLayout.getTabAt(0).setCustomView(R.layout.tab_sign_up_selected);
//                        tabLayout.getTabAt(1).setCustomView(R.layout.tab_sign_in_unselected);
                    } else if (tabLayout.getTabAt(1) == tab) {
                        tabLayout.getTabAt(1).setText(R.string.sign_in);
//                        tabLayout.getTabAt(1).setCustomView(R.layout.tab_sign_in_selected);
//                        tabLayout.getTabAt(0).setCustomView(R.layout.tab_sign_up_unselected);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }


                @Override
                public void onTabReselected(TabLayout.Tab tab) {


                }
            });

            agent_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserAccessActivity.this, ProviderAccessActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

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
                    return new SignUpFragment();
                case 1:
                    return new SignInFragment();
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
