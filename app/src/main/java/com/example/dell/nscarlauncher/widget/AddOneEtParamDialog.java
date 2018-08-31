package com.example.dell.nscarlauncher.widget;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseDialogFragment;
import com.example.dell.nscarlauncher.common.util.EditTextUtils;
import com.example.dell.nscarlauncher.common.util.ToastUtils;


/**
 * 包含一个输入框的对话框
 * Created by lenovo on 2017/9/5.
 */

public class AddOneEtParamDialog extends BaseDialogFragment {
    //number
    boolean isnumber;
    //根布局
    View mRootView;

    //参数名
    @StringRes
    int mParamName;
    //空提示
    @StringRes
    int mEmptyParamHint;
    String mData ;

    // type none 0  num 1  num|numberc 2
    int type ;
    /**
     * 获取实例
     *
     * @return
     */
    public  static AddOneEtParamDialog getInstance(boolean isNumber) {
        AddOneEtParamDialog dialog = new AddOneEtParamDialog();
        dialog.isnumber =isNumber;
        if(isNumber){
            dialog.type=1;
        }
        else {
            dialog.type=0;
        }
        return dialog;
    }
    public static AddOneEtParamDialog getInstance(boolean isNumber,String text) {
        AddOneEtParamDialog dialog = new AddOneEtParamDialog();
        dialog.isnumber =isNumber;
        dialog.mData=text;
        return dialog;
    }
    public static AddOneEtParamDialog getInstance(int  type) {
        AddOneEtParamDialog dialog = new AddOneEtParamDialog();
        dialog.type=type;
        return dialog;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        switch (type){
            case 0:
                rootView = inflater.inflate(R.layout.dialog_wifi_input_password, ((ViewGroup) getDialog().getWindow().findViewById(android.R.id.content)), false);

                break;
            case 1:
                rootView = inflater.inflate(R.layout.dialog_wifi_input_password, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.dialog_wifi_input_password, container, false);
                break;
            default:  rootView = inflater.inflate(R.layout.dialog_wifi_input_password, ((ViewGroup) getDialog().getWindow().findViewById(android.R.id.content)), false);
        }


        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 初始化
     *
     * @param rootView
     */
    private void initView(View rootView) {
        mRootView = rootView;

        //设置参数名
//        setTvText(rootView, R.id.tv_title, mParamName);
        setTvText(mRootView, R.id.wifi_input_name, mData);
        //设置输入框
        EditText etValue = getView(rootView, R.id.wifi_input_password);

        EditTextUtils.openKeyBroad(etValue);
//        etValue.setText("");
        if (mEmptyParamHint != 0) {
            etValue.setHint(mEmptyParamHint);
        }
        int i =etValue.getText().length();
        etValue.setSelection(etValue.getText().length());
        View viewCancel = getView(rootView, R.id.wifi_input_cancel);
        View viewCommit = getView(rootView, R.id.wifi_input_confirm);
        setListener(viewCancel, this);
        setListener(viewCommit, this);
    }
/*
*设置输入数字
* s*/
public  AddOneEtParamDialog InputType(int inputtype){
    setETIputType(mRootView, R.id.wifi_input_password,inputtype);
    return this;
}
    /**
     * 设置参数名称
     *
     * @param data
     * @return
     */
    public AddOneEtParamDialog setTvParamName(@StringRes int data) {
        mParamName = data;
        //设置参数名
//        setTvText(mRootView, R.id.tv_title, mParamName);
        return this;
    }

    /**
     * 设置空参数提示
     *
     * @param data
     * @return
     */
    public AddOneEtParamDialog setTvEmptyParamHint(@StringRes int data) {
        mEmptyParamHint = data;
        //设置hint
        setTvHint(mRootView, R.id.wifi_input_password, mEmptyParamHint);
        return this;
    }
    /**
     * 设置空参数提示
     *
     * @param data
     * @return
     */
    public AddOneEtParamDialog setTvEmptyParamHint(String data) {

        //设置hint
        setTvHint(mRootView, R.id.wifi_input_password, data);
        return this;
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.wifi_input_cancel: {
                if (onDialogClickListener != null) {
                    onDialogClickListener.onClickCancel(this);
                }
                break;
            }
            case R.id.wifi_input_confirm: {
                String data = getCurInput();
                if (data != null) {
                    if (onDialogClickListener != null) {
                        onDialogClickListener.onClickCommit(this, data);
                    }
                }
                break;
            }
        }
    }

    private <T extends View> T getView(@IdRes int id) {
        if (mRootView != null) {
            return (T) mRootView.findViewById(id);
        }
        return null;
    }

    /**
     * 获取用户当前输入
     *
     * @return
     */
    private String getCurInput() {
        String result = null;

        EditText etValue = getView(R.id.wifi_input_password);

        String strValue = etValue.getText().toString();

        if (TextUtils.isEmpty(strValue)) {
            if (mEmptyParamHint != 0) {
                ToastUtils.show(mEmptyParamHint);
            }
        } else {
            result = strValue;
        }
        return result;
    }

    private OnDialogClickListener onDialogClickListener;

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        onDialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onClickCancel(AddOneEtParamDialog dialog);

        void onClickCommit(AddOneEtParamDialog dialog, String data);
    }

    public static class DefOnDialogClickListener implements OnDialogClickListener {
        @Override
        public void onClickCancel(AddOneEtParamDialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onClickCommit(AddOneEtParamDialog dialog, String data) {
            dialog.dismiss();
        }
    }
}
