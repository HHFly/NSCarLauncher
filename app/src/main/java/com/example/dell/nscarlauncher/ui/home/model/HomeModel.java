package com.example.dell.nscarlauncher.ui.home.model;

import android.support.annotation.LayoutRes;

import com.example.dell.nscarlauncher.ui.home.androideunm.ClassType;

public class HomeModel {
    private String name ;
    private @LayoutRes int layout;
    private @ClassType int type;
    public HomeModel(String name, int layout,int type) {
        this.name = name;
        this.layout = layout;
        this.type =type;
    }
    public HomeModel(HomeModel homeModel) {
        this.name = homeModel.getName();
        this.layout = homeModel.getLayout();
        this.type=homeModel.getType();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
