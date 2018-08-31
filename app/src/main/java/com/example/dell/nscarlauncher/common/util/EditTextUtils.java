package com.example.dell.nscarlauncher.common.util;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * EditText工具类
 * Created by lenovo on 2017/8/27.
 */

public class EditTextUtils {
    /**
     * 关闭软键盘
     *
     * @param context
     * @param vs
     */
    public static void hideKeyBroad(Context context, EditText... vs) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (vs != null) {
            for (EditText v : vs) {
                v.setFocusable(false);
                v.setFocusableInTouchMode(false);
                v.requestFocus();
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    /**
     * 打开软键盘
     *
     * @param v
     */
    public static void openKeyBroad(final EditText v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                manager.showSoftInput(v, 0);
            }
        });
    }

    /**
     * 打开软键盘
     *
     * @param v
     */
    public static void openKeyBroadDelay(final EditText v) {
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                manager.showSoftInput(v, 0);
            }
        }, 300);
    }

    /**
     * EditText内容是否可见
     *
     * @param et
     * @param isShow
     */
    public static void changedEditTextVisiable(EditText et, boolean isShow) {
        if (et == null) {
            LogUtils.logFormat("EditTextUtils", "changedEditTextVisiable", "et is null");
            return;
        }
        et.setTransformationMethod(isShow ?
                HideReturnsTransformationMethod.getInstance() :
                PasswordTransformationMethod.getInstance()
        );
        int len = et.getText().length();
        et.setSelection(len);
    }

    /**
     * 更改回车键
     *
     * @param et
     * @param flag
     */
    public static void modifyEnter(EditText et, final int flag, final CallBack callBack) {
        if (et == null) {
            return;
        }
        et.setImeOptions(flag);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == flag) {
                    //按下回车做的事情
                    String keyword = v.getText().toString();
                    if (callBack != null) {
                        callBack.callBack(keyword);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 回调
     *
     * @param <T>
     */
    public interface CallBack<T> {
        void callBack(T data);
    }
}
