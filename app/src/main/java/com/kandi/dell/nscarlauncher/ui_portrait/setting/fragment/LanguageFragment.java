package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.InputMethodUtils;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.InputAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.LanguageAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.LanguageBean;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class LanguageFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private List<InputMethodInfo> data;
    private RecyclerView la_recyclerView,input_recyclerView;
    private  String[] items ={};
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_lanuage_portrait;
    }

    @Override
    public void findView() {
        la_recyclerView = getView(R.id.la_recyclerView);
        input_recyclerView = getView(R.id.input_recyclerView);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.language_layout);
        setClickListener(R.id.keyinput_layout);
    }

    @Override
    public void initView() {
        LanguageAdapter languageAdapter = new LanguageAdapter(homePagerActivity.getActivity().getApplicationContext());
        if (la_recyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            la_recyclerView.setLayoutManager(linearLayoutManager);
            la_recyclerView.setAdapter(languageAdapter);
        }

        InputAdapter inputAdapter = new InputAdapter(homePagerActivity.getActivity().getApplicationContext());
        if (input_recyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            input_recyclerView.setLayoutManager(linearLayoutManager);
            input_recyclerView.setAdapter(inputAdapter);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_back:
                    homePagerActivity.getSetFragment().hideFragment();
                    break;
                case R.id.language_layout:
                    getView(R.id.language_choose_layout).setVisibility(getView(R.id.language_choose_layout).getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);
                    break;
                case R.id.keyinput_layout:
                    getView(R.id.input_choose_layout).setVisibility(getView(R.id.input_choose_layout).getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);
                    break;
            }
    }

}
