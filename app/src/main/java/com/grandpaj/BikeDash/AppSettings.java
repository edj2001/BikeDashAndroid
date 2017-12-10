package com.grandpaj.BikeDash;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {
    private static final String UNIT_STRING = "MeasureUnit";
    private static final String PREF_NAME = "SpeedDemo_20100707";

    public static int getMeasureUnit(Context context){
        return getInt(context,AppSettings.UNIT_STRING);
    }

    public static void setMeasureUnit(Context context,int limit){
        putInt(context,AppSettings.UNIT_STRING,limit);
    }

    private static int getInt(Context context, String tag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);

        return pref.getInt(tag, 0);
    }

    public static void putInt(Context context, String tag, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(tag, value);
        editor.commit();
    }
    public static double getBatteryCapacity(Context context) {
        SharedPreferences pref;
        pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyString = context.getString(R.string.pref_battery_key);
        String valueString = context.getString(R.string.pref_batteryCapacityDefault);
        return Double.parseDouble(pref.getString(keyString, valueString));
    }
}