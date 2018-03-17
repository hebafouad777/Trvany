package com.mediaoasis.trvany;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mediaoasis.trvany.activities.user.UserAccessActivity;
import com.mediaoasis.trvany.utils.LocaleExchange;
import com.mediaoasis.trvany.utils.SharedPref;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SplashActivity extends AppCompatActivity {
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = new SharedPref(SplashActivity.this);
        LocaleExchange.exchangeResources(SplashActivity.this, sharedPref.getLanguage());

        Log.e("curLang", sharedPref.getLanguage());

        if(sharedPref.getLanguage().equals("ar")){
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/GE_SS_Text_Light.otf")
                    .setFontAttrId(R.attr.fontPath)
                    .build());
        }else{
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/Lato-Regular.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build());
        }

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (!sharedPref.getBoolean("isFirstTime")) {
                    sharedPref.setBoolean("isFirstTime", true);
                    startActivity(new Intent(SplashActivity.this, WalkThroughActivity.class));
                } else {
                    Intent intent = new Intent(SplashActivity.this, UserAccessActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }.start();

    }
}
