package com.kandi.dell.nscarlauncher.common.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static final String SP_NAME = "SPUtil";
    private SharedPreferences mSp;
    private Context mContext;

    private SPUtil(Context context, String spName, int mode) {
        this.mContext = context.getApplicationContext();
        this.mSp = this.mContext.getSharedPreferences(spName, mode);
    }

    public static SPUtil getInstance(Context context) {
        return getInstance(context, "SPUtil");
    }

    public static SPUtil getInstance(Context context, String spName) {
        return getInstance(context, spName, 0);
    }

    public static SPUtil getInstance(Context context, String spName, int mode) {
        return new SPUtil(context, spName, mode);
    }
    public void putFloat(String key, float value) {
        this.mSp.edit().putFloat(key, value).commit();
    }
    public void putLong(String key, long value) {
        this.mSp.edit().putLong(key, value).commit();
    }

    public void putString(String key, String value) {
        this.mSp.edit().putString(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        this.mSp.edit().putBoolean(key, value).commit();
    }

    public void putInt(String key, int value) {
        this.mSp.edit().putInt(key, value).commit();
    }
    public float getFloat(String key, float def) {
        return this.mSp.getFloat(key, def);
    }
    public int getInt(String key, int def) {
        return this.mSp.getInt(key, def);
    }

    public long getLong(String key, long def) {
        return this.mSp.getLong(key, def);
    }

    public String getString(String key) {
        return this.mSp.getString(key, "");
    }

    public String getString(String key, String def) {
        return this.mSp.getString(key, def);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean flag) {
        return this.mSp.getBoolean(key, flag);
    }

    public void clear() {
        this.mSp.edit().clear();
    }
}
