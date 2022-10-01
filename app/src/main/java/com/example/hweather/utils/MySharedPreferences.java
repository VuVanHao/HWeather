package com.example.hweather.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static SharedPreferences sharedPreferences;
    private static final String SHARED_NAME = "WEATHER";
    private static final String SHARED_LANG = "LANG";
    private static final String SHARED_TEMP = "TEMP";
    private static final String SHARED_PER = "PERM";

    public static SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static String getLanguage(Context context) {
        return getInstance(context).getString(SHARED_LANG, "vi");
    }

    public static void setLanguage(Context context, String lang) {
        getInstance(context).edit().putString(SHARED_LANG, lang).commit();
    }

    public static void setTempUnit(Context context, int unit) {
        getInstance(context).edit().putInt(SHARED_TEMP, unit).commit();
    }

    public static int getTempUnit(Context context) {
        return getInstance(context).getInt(SHARED_TEMP, 0);
    }

    public static String getPermission(Context context) {
        return getInstance(context).getString(SHARED_PER, "grant");
    }

    public static void setPermission(Context context, String lang) {
        getInstance(context).edit().putString(SHARED_PER, lang).commit();
    }
}
