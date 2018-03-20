package com.mediaoasis.trvany.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nasr on 1/12/2017.
 */

public class SharedPref {

    public static final String MyPREFERENCES = "MyPrefs";

    SharedPreferences sharedpreferences;
    Context context;
    SharedPreferences.Editor editor;

    public SharedPref(Context con) {
        context = con;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedpreferences.getString(key, "");
    }

    public String getStringUserID(String key) {
        return sharedpreferences.getString(key, "-1");
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int getInteger(String key) {
        return sharedpreferences.getInt(key, 0);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

   public String getLanguage(){
       return sharedpreferences.getString("lang", "ar");
   }

   public void setLanguage(Context context, String curLang){
       setString("lang", curLang);
       LocaleExchange.updateResources(context, curLang);
   }

}
