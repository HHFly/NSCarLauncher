package com.example.dell.nscarlauncher.common.util;

import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.dell.nscarlauncher.app.App;

import java.util.List;

public class InputMethodUtils {

    /***
     * 获取默认输入法
     * @return
     */
    public static String getDefaultInputMethod(){
        return  Settings.Secure.getString(App.get().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    /***
     * 设置默认输入法
     * @return
     */
    public static boolean putDefaultInputMethod(String id){
        return  Settings.Secure.putString(App.get().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,id);

    }
    /***
     * 获取系统内输入法列表
     * @return
     */
    public static List<InputMethodInfo> getInputMethodManager(){
        InputMethodManager imm = (InputMethodManager)App.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.getCurrentInputMethodSubtype();
        return imm.getInputMethodList();
    }

}
