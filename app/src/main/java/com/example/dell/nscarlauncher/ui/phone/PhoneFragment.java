package com.example.dell.nscarlauncher.ui.phone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;

import java.util.ArrayList;

public class PhoneFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private Fragment mCurFragment;//当前页
    private ArrayList<Fragment> mFragments;
    private  PNumFragment pNumFragment;
    private  PMemberFragment pMemberFragment;
    private  PTalklogFragment pTalklogFragment;
    @Override
    public int getContentResId() {
        return R.layout.fragment_phone;
    }

    @Override
    public void findView() {
        viewPager=getView(R.id.viewPager);
        mFragments = new ArrayList<>();
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_call);
        setClickListener(R.id.rl_1);
        setClickListener(R.id.rl_2);
        setClickListener(R.id.rl_3);
        setClickListener(R.id.ll_1);
        setClickListener(R.id.ll_2);
        setClickListener(R.id.ll_3);
        setClickListener(R.id.ll_4);
        setClickListener(R.id.ll_5);
        setClickListener(R.id.ll_6);
        setClickListener(R.id.ll_7);
        setClickListener(R.id.ll_8);
        setClickListener(R.id.ll_9);
        setClickListener(R.id.ll_10);
        setClickListener(R.id.ll_11);
        setClickListener(R.id.ll_12);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void initView() {
        initData();
        viewPager.setAdapter(new MyAdapter(getFragmentManager()));
//        viewPager.setOffscreenPageLimit(PageCount-1);
        viewPager.setOffscreenPageLimit(2);
        setViewSelected(R.id.rl_1,true);

    }
    /*电话*/
   private void callphone(){

   }
    /*添加电话号码*/
    private void addphone(String num){
        pNumFragment.setNumber(num);
    }
    /*选择tab*/
    private void tabSelected(int i){
        setViewSelected(R.id.rl_1,false);
        setViewSelected(R.id.rl_2,false);
        setViewSelected(R.id.rl_3,false);
        switch (i){
            case 1 :
                setViewSelected(R.id.rl_1,true);
                break;
            case 2 :
                setViewSelected(R.id.rl_2,true);
                break;
            case 3 :
                setViewSelected(R.id.rl_3,true);
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                tabSelected(1);
                break;
            case 1:
                tabSelected(2);
                break;
            case 2:
                tabSelected(3);
                break;

        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
    private void   initData() {
         pNumFragment =new PNumFragment();
         pMemberFragment =new PMemberFragment();
         pTalklogFragment =new PTalklogFragment();
        mFragments.add(pNumFragment);
        mFragments.add(pMemberFragment);
        mFragments.add(pTalklogFragment);
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_1:
                tabSelected(1);
                viewPager.setCurrentItem(0);
                break;
            case R.id.rl_2:
                tabSelected(2);
                viewPager.setCurrentItem(1);
                break;
            case R.id.rl_3:
                tabSelected(3);
                viewPager.setCurrentItem(2);
                break;
            case R.id.iv_call:
                callphone();
                break;
            case R.id.ll_1:
                addphone("1");
                break;
            case R.id.ll_2:
                addphone("2");
                break;
            case R.id.ll_3:
                addphone("3");
                break;
            case R.id.ll_4:
                addphone("4");
                break;
            case R.id.ll_5:
                addphone("5");
                break;
            case R.id.ll_6:
                addphone("6");
                break;
            case R.id.ll_7:
                addphone("7");
                break;
            case R.id.ll_8:
                addphone("8");
                break;
            case R.id.ll_9:
                addphone("9");
                break;
            case R.id.ll_10:
                addphone("*");
                break;
            case R.id.ll_11:
                addphone("0");
                break;
            case R.id.ll_12:
                addphone("#");
                break;
        }
    }
}
