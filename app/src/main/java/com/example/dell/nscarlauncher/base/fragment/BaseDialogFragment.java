package com.example.dell.nscarlauncher.base.fragment;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;


/**
 * 对话框基类
 * Created by lenovo on 2017/8/27.
 */

public abstract class BaseDialogFragment extends DialogFragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    protected ImmersionBar mImmersionBar;
    private boolean isCanceledOnTouchOutside = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //隐藏对话框默认标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //对话框背景透明
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置弹窗动画
        dialog.getWindow().getAttributes().windowAnimations = R.style.BaseDialogAnim;
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        return dialog;
    }
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.5),(int) (dm.heightPixels * 0.5));

        }
        if(this.getStartInBottom()) {
            this.startInBottom();
        }
        if(this.getStartInTop()) {
            this.startInTop();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isImmersionBarEnabled())
            initImmersionBar();
    }
    /**
     * 是否在Fragment使用沉浸式
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this, getDialog());
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR).init();
    }
    protected boolean getStartInBottom() {
        return false;
    }
    protected boolean getStartInTop() {
        return false;
    }
    protected void startInBottom() {
        Window win = this.getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(16777215));
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = 80;
        params.width = -1;
        params.height = -2;
        win.setAttributes(params);
    }
    protected void startInTop() {
        Window win = this.getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(16777215));
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.TOP;
        params.width = -1;
        params.height = -2;
        win.setAttributes(params);
    }
    /**
     * 设置点击外面对话框会不会消失
     *
     * @param canceledOnTouchOutside
     */
    public BaseDialogFragment setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    /**
     * 显示弹窗
     *
     * @param manager
     */
    public void show(android.support.v4.app.FragmentManager manager) {
        try {

            show(manager, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取实例
     *
     * @param rootView
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T getView(View rootView, @IdRes int id) {
        return (T) rootView.findViewById(id);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 资源为空
     */
    protected final int RES_ID_NULL = -1;

    /**
     * 设置文本
     *
     * @param view
     * @param resId
     */
    public void setText(View view, @StringRes int resId) {
        if (view != null && view instanceof TextView && resId != RES_ID_NULL) {
            ((TextView) view).setText(resId);
        }
    }

    /**
     * 设置文本
     *
     * @param view
     * @param data
     */
    public void setText(View view, String data) {
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(data);
        }
    }

    /**
     * 资源id是否为空
     *
     * @param resId
     * @return
     */
    public boolean isResIdNull(int resId) {
        return resId == RES_ID_NULL;
    }

    /**
     * 设置是否显示
     *
     * @param view
     * @param isVisibility
     */
    public void setVisibility(View view, boolean isVisibility) {
        if (view != null) {
            view.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置监听
     *
     * @param view
     * @param listener
     */
    public void setListener(View view, View.OnClickListener listener) {
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @return
     */
    public String getStringApp(@StringRes int resId) {
        return App.get().getString(resId);
    }

    /**
     * 设置文案
     *
     * @param rootView
     * @param id
     * @param data
     */
    public void setTvText(View rootView, @IdRes int id, String data) {
        if (rootView != null) {
            View view = getView(rootView, id);
            if (view != null && view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setText(data);
            }
        }
    }
    /**
     * 设置文案
     *
     * @param rootView
     * @param id
     * @param data
     */
    public void setTvHint(View rootView, @IdRes int id, @StringRes int data) {
        if (rootView != null) {
            View view = getView(rootView, id);
            if (view != null && view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setHint(data);
            }
        }
    }

    /**
         * 设置文案
         *
         * @param rootView
         * @param id
         * @param data
         */
    public void setTvHint(View rootView, @IdRes int id, String data) {
        if (rootView != null) {
            View view = getView(rootView, id);
            if (view != null && view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setHint(data);
            }
        }
    }
    /**
     * 设置Edit
     *
     * @param rootView
     * @param id
     * @param
     */
    public void setETIputType(View rootView, @IdRes int id, @StringRes int type) {
        if (rootView != null) {
            View view = getView(rootView, id);
            if (view != null && view instanceof EditText) {
                EditText tv = (EditText) view;
                tv.setInputType(type);
            }
        }
    }
}
