package com.example.dell.nscarlauncher.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dell.nscarlauncher.MainActivity;
import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.ui.home.HomeData;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private CircleIndicator indicator;//指示器
    private  int PageCount;
    private ArrayList<HomeModel> mData;
    private ImageView imageViews;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_homepager;
    }

    @Override
    public void initView() {
        initDa();
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
//        viewPager.setOffscreenPageLimit(PageCount-1);
        viewPager.setOffscreenPageLimit(mFragments.size());
        indicator.setViewPager(viewPager);
    }

    @Override
    public void findView() {
        super.findView();
        mFragments = new ArrayList<>();
        viewPager=getView(R.id.viewPager);
        indicator =getView(R.id.indicator);

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
    private  void initDa(){
        HomePagerOneFragment homePagerOneFragment =new HomePagerOneFragment();
        HomePagerTwoFragment homePagerTwoFragment =new HomePagerTwoFragment();
        mFragments.add(homePagerOneFragment);
        mFragments.add(homePagerTwoFragment);
    }
//    private void  initData(){
//        HomeData data =new HomeData();
//        mData= data.getData();
//        ArrayList<HomeModel> page =new ArrayList<>();
//        while (mData.size()>0){
//            HomeModel homeModel =new HomeModel(mData.get(0));
//            page.add(homeModel);
//            mData.remove(0);
//            if(page.size()==4){
//                HomePagerFragment homePagerFragment =new HomePagerFragment();
//                homePagerFragment.setmData(page);
//                page.clear();
//                mFragments.add(homePagerFragment);
//                PageCount++;
//            }
//        }
//        if(page.size()>0) {
//            HomePagerFragment homePagerFragment = new HomePagerFragment();
//            homePagerFragment.setmData(page);
//            page.clear();
//            mFragments.add(homePagerFragment);
//            PageCount++;
//        }
//
//    }


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
