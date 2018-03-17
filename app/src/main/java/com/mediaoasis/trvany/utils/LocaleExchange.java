package com.mediaoasis.trvany.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mediaoasis.trvany.SplashActivity;

import java.util.Locale;

/**
 * Created by Nasr on 7/30/2016.
 */
public class LocaleExchange {

    public static void exchangeResources(Context activity, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(Locale.getDefault());
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
//        updateResources(activity, lang);
    }

    public static void updateResources(Context activity, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(Locale.getDefault());
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        ((Activity) activity).finish();
//        this.setLang(KEY_LANG, lang);
    }

//    public static void updateResources(Context context, String language) {
//        Locale myLocale = new Locale(language);
//        Resources res = context.getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//
//        Intent intent = new Intent(context, SplashActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
//        ((Activity) context).finish();
//    }
}
