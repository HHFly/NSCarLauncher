package com.kandi.dell.nscarlauncher.ui.phone;

import android.view.View;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;

public class PNumFragment extends BaseFragment {
    private  String number;
    static TextView TvNum;
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone_num;
    }
    public void setNumber(String num){
        number =  getTvText(R.id.num)+num;
        TvNum.setText(number);
    }

    public String getNumber() {
        return getTvText(R.id.num);
    }

    @Override
    public void findView() {
        TvNum= getView(R.id.num);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.num_delete);
        getView(R.id.num_delete).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                number="";
                TvNum.setText(number);
                return true;
            }
        });
    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.num_delete:
                subString();
                break;
        }
    }
    public void subString(){
        number=  getTvText(R.id.num);
        if (number!=null&&number.length()>0){
            number =number.substring(0,number.length()-1);
            TvNum.setText(number);
        }
    }
}
