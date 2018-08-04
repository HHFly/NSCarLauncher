package com.example.dell.nscarlauncher.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.ui.home.HomeData;
import com.example.dell.nscarlauncher.ui.home.HomePagerOneFragment;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;
import com.example.dell.nscarlauncher.widget.CustomViewPager;
import com.gyf.barlibrary.BarHide;

import java.util.ArrayList;

public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private ArrayList<Fragment> mFragments;
    private CustomViewPager viewPager;
    private  int PageCount;
    private ArrayList<HomeModel> mData;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_homepager;
    }

    @Override
    public void initView() {
        initData();
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(PageCount-1);

    }

    @Override
    public void findView() {
        super.findView();
        mFragments = new ArrayList<>();
        viewPager=getView(R.id.viewPager);
    }

    @Override
    public void setListener() {
        super.setListener();
        setClickListener(R.id.title_iv_sound);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_iv_sound:
                break;
        }
    }
    private void  initData(){
        HomeData data =new HomeData();
        mData= data.getData();
        ArrayList<HomeModel> page =new ArrayList<>();
        while (mData.size()>0){
            HomeModel homeModel =new HomeModel(mData.get(0));
            page.add(homeModel);
            mData.remove(0);
            if(page.size()==4){
                HomePagerOneFragment homePagerOneFragment =new HomePagerOneFragment();
                homePagerOneFragment.setmData(page);
                page.clear();
                mFragments.add(homePagerOneFragment);
                PageCount++;
            }
        }
        if(page.size()>0) {
            HomePagerOneFragment homePagerOneFragment = new HomePagerOneFragment();
            homePagerOneFragment.setmData(page);
            page.clear();
            mFragments.add(homePagerOneFragment);
            PageCount++;
        }

    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class MyAdapter extends FragmentPagerAdapter {
        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
