package com.example.dell.nscarlauncher.ui.phone.model;

import com.example.dell.nscarlauncher.base.model.BaseModel;

public class MemberData extends BaseModel {
    private String name;//姓名
    private String number;//电话

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
}
