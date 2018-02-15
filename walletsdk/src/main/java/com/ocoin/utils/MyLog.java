package com.ocoin.utils;

import android.util.Log;

/**
 * Created by y on 2018/2/9.
 */

public class MyLog {

    public static boolean DEBUG = true;
    static String TAG = "WALLET_SDK";

    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }


    public static void d(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

}
