package com.kandi.dell.nscarlauncher.ui.phone.model;

import com.kandi.dell.nscarlauncher.base.model.BaseModel;

public class PhoneBookInfo extends BaseModel {
    private String name;//姓名
    private String number;//电话
    private String firstLetter;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }
}
