package com.kandi.dell.nscarlauncher.ui.setting.model;

import android.support.annotation.RawRes;

import com.kandi.dell.nscarlauncher.base.model.BaseModel;

public class SetModel  extends BaseModel{
    private @RawRes int Logo;
    private String name ;
    private int item;
    public int getLogo() {
        return Logo;
    }

    public void setLogo(int logo) {
        Logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }
}
