package com.example.dell.nscarlauncher.ui.home.model;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;

import java.util.ArrayList;

import static com.white.lib.utils.AppResUtil.getString;

public class HomeData {
    private ArrayList<HomeModel> Data =new ArrayList<>();
    public ArrayList<HomeModel> getData(){

        Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
        Data.add(new HomeModel(getString(R.string.天气),R.layout.item_weather,1));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
       Data.add(new HomeModel(getString(R.string.电台),R.layout.item_fm,0));
        return Data;
    }
}
