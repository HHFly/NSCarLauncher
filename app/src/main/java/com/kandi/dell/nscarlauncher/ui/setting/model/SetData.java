package com.kandi.dell.nscarlauncher.ui.setting.model;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.setting.eumn.SetType;

import java.util.ArrayList;
import java.util.List;

import static com.white.lib.utils.AppResUtil.getString;

public class SetData {
    private List<SetModel> data ;
    public  List<SetModel>  getData(){
        data =new ArrayList<>();
        SetModel setModel1 =new SetModel();
        setModel1.setName(getString(R.string.显示));
        setModel1.setItem(SetType.DISPLAY);
        setModel1.setLogo(R.mipmap.ic_set_display);
        data.add(setModel1);

        SetModel setModel2 =new SetModel();
        setModel2.setName(getString(R.string.蓝牙));
        setModel2.setItem(SetType.BT);
        setModel2.setLogo(R.mipmap.ic_set_bt);
        data.add(setModel2);

        SetModel setModel3 =new SetModel();
        setModel3.setName(getString(R.string.WiFi));
        setModel3.setItem(SetType.WIFI);
        setModel3.setLogo(R.mipmap.ic_set_wifi);
        data.add(setModel3);

//        SetModel setModel4 =new SetModel();
//        setModel4.setName(getString(R.string.语言和输入法));
//        setModel4.setItem(SetType.LANGUAGE);
//        setModel4.setLogo(R.mipmap.ic_set_language);
//        data.add(setModel4);

//        SetModel setModel5 =new SetModel();
//        setModel5.setName(getString(R.string.均衡器));
//        setModel5.setItem(SetType.EQULIZER);
//        setModel5.setLogo(R.mipmap.ic_set_equlizer);
//        data.add(setModel5);

        SetModel setModel6 =new SetModel();
        setModel6.setName(getString(R.string.日期和时间));
        setModel6.setItem(SetType.DATE);
        setModel6.setLogo(R.mipmap.ic_set_date);
        data.add(setModel6);

        SetModel setModel7 =new SetModel();
        setModel7.setName(getString(R.string.车辆设置));
        setModel7.setItem(SetType.CARSET);
        setModel7.setLogo(R.mipmap.ic_set_carset);
        data.add(setModel7);

        SetModel setModel8 =new SetModel();
        setModel8.setName(getString(R.string.能量回收));
        setModel8.setItem(SetType.RECOVERY);
        setModel8.setLogo(R.mipmap.ic_set_recovery);
        data.add(setModel8);
//
//        SetModel setModel9 =new SetModel();
//        setModel9.setName(getString(R.string.升级));
//        setModel9.setItem(SetType.UPDATE);
//        setModel9.setLogo(R.mipmap.ic_set_update);
//        data.add(setModel9);

        SetModel setModel10 =new SetModel();
        setModel10.setName(getString(R.string.关于));
        setModel10.setItem(SetType.ABOUT);
        setModel10.setLogo(R.mipmap.ic_set_about);
        data.add(setModel10);

//        SetModel setModel11 =new SetModel();
//        setModel11.setName(getString(R.string.帮助));
//        setModel11.setItem(SetType.HELP);
//        setModel11.setLogo(R.mipmap.ic_set_help);
//        data.add(setModel11);
        return  data;
    }
}
