package com.example.dell.nscarlauncher.ui.setting.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.InputMethodUtils;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LanguageFragment extends BaseFragment {
    private List<InputMethodInfo> data;
    private  String[] items ={};
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_lanuage;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
            setClickListener(R.id.rl_set_inputmethod);
    }

    @Override
    public void initView() {
        data= InputMethodUtils.getInputMethodManager();
        InputMethodInfo inputMethodInfo;
        ArrayList<String> item =new ArrayList<>();
        if(data!=null){
            for(int i =0 ;i<data.size();i++){

                item.add((String) data.get(i).getServiceInfo().loadLabel(getActivity().getPackageManager()));
            }
        }

        items =(String[])item.toArray(new String[item.size()]);
        setTvText(R.id.set_inputmethod,InputMethodUtils.getDefaultInputMethod());
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_set_inputmethod:
                    showchoose();
                    break;
            }
    }

    private void  showchoose(){
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("选择输入法").setIcon(R.mipmap.ic_set_language)
                .setSingleChoiceItems(items, -1,new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodUtils.putDefaultInputMethod(data.get(which).getId());

                        dialog.dismiss();

                    }
                }).create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                App.get().getCurActivity().initImmersionBar();
            }
        });
        dialog.show();

    }
}
