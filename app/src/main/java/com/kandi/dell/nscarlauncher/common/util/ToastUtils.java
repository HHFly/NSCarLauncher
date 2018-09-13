package com.kandi.dell.nscarlauncher.common.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

/**
 * 弹窗
 * Created by lenovo on 2017/8/24.
 */

public class ToastUtils {
    private static final String TAG = "ToastUtils";
    /**
     * 上下文
     */
    private static Context mContext;
    /**
     * Toast
     */
    private static Toast mToast;
    /**
     * 上一个显示的文本
     */
    private static CharSequence oldMsg;
    /**
     * 上一次弹窗时间
     */
    private static long oldTime;
    /**
     * 当前弹窗时间
     */
    private static long curTime;
    /**
     * 重复显示间隔
     */
    private static long REPEAT_DISPLAY_INTERVAL = 2000;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
    }

    /**
     * 弹窗
     *
     * @param resId
     */
    public static void show(@StringRes int resId) {
        show(mContext, resId);
    }

    /**
     * 弹窗
     *
     * @param context
     * @param resId
     */
    public static void show(Context context, @StringRes int resId) {
        if (context == null) {
            return;
        }
        String msg = context.getString(resId);
        show(context, msg);
    }

    /**
     * 弹窗
     *
     * @param msg
     */
    public static void show(CharSequence msg) {
        show(mContext, msg);
    }

    /**
     * 弹窗
     *
     * @param context
     * @param msg
     */
    public static void show(Context context, CharSequence msg) {
        if (context == null) {
            return;
        }

        curTime = System.currentTimeMillis();
        if (mToast == null) {
            synchronized (ToastUtils.class) {
                if (mToast == null) {
                    context.getApplicationContext();
                    mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
                show(mToast, msg);
            }
        } else {
            show(mToast, msg);
        }
    }

    /**
     * 弹窗
     *
     * @param toast
     * @param msg
     */
    private static void show(Toast toast, CharSequence msg) {
        if (toast == null) {
            Log.e(TAG, "mToast is null");
            return;
        }
        if (msg == null) {
            Log.e(TAG, "msg is null");
            return;
        }
        if (msg.equals(oldMsg)) {
            if (curTime > oldTime + REPEAT_DISPLAY_INTERVAL) {
                toast.show();
                oldTime = curTime;
            }
        } else {
            oldMsg = msg;
            toast.setText(msg);
            toast.show();
            oldTime = curTime;
        }
    }
}
