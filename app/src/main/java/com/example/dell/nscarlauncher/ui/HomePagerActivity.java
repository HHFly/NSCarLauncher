package com.example.dell.nscarlauncher.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.common.util.FragmentUtils;
import com.example.dell.nscarlauncher.ui.bluetooth.BTMusicFragment;
import com.example.dell.nscarlauncher.ui.fm.FMFragment;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;
import com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.example.dell.nscarlauncher.ui.phone.PhoneFragment;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private CircleIndicator indicator;//viewpager指示器
    private Fragment mCurFragment;//当前页
    private FMFragment fmFragment ;//收音机
    private BTMusicFragment btMusicFragment;//蓝牙音乐
    private MusicFragment musicFragment;//本地音乐
    private PhoneFragment phoneFragment;//电话
    private ArrayList<HomeModel> mData;
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
        setClickListener(R.id.center_img);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_iv_sound:

                break;
            case R.id.center_img:
                hideFragment();
                break;
        }
    }
    //初始化viewpager 数据
    private  void initDa(){
        HomePagerOneFragment homePagerOneFragment =new HomePagerOneFragment();
        homePagerOneFragment.setHomePagerActivity(this);
        HomePagerTwoFragment homePagerTwoFragment =new HomePagerTwoFragment();
        homePagerOneFragment.setHomePagerActivity(this);
        mFragments.add(homePagerOneFragment);
        mFragments.add(homePagerTwoFragment);
        createFragment();
    }

    /**
     * 初始化fragment
     */
    private void createFragment() {
      fmFragment =new FMFragment();
      fmFragment.setHomePagerActivity(this);
      btMusicFragment =new BTMusicFragment();
        musicFragment= new MusicFragment();
        phoneFragment= new PhoneFragment();
    }
    /*隐藏fragemt*/
    public  void  hideFragment(){
        setViewVisibility(R.id.frame_main,false);
    }
    /*显示fragment*/

    public void showFragemnt(){
        setViewVisibility(R.id.frame_main,true);
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
    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        showFragemnt();
        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.frame_main);
    }
    public void  jumpFragment(@FragmentType int type ){
        switch (type){
            case  FragmentType.FM:
                switchFragment(fmFragment);
                break;
            case  FragmentType.BTMUSIC:
                switchFragment(btMusicFragment);
                break;
            case  FragmentType.MUSIC:
                switchFragment(musicFragment);
                break;
             case  FragmentType.PHONE:
                 switchFragment(phoneFragment);
                 break;
        }
    }
    /*viewpager适配器*/
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
}
