package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.view.View;
import android.widget.LinearLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.widget.EqSeekBarView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EqFragment extends BaseFragment {
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;
    private LinearLayout mLayout;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_eq;
    }

    @Override
    public void findView() {
        mLayout =getView(R.id.ll_seekbar);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        setEqualize();
    }

    @Override
    public void onClick(View v) {

    }

    private void setEqualize() {
        mEqualizer = new Equalizer(0, App.get().getMediaPlayer().getAudioSessionId());
        mEqualizer.setEnabled(true);

        short bands = mEqualizer.getNumberOfBands();


        for (short i = 0; i < bands; i++) {
            final short band = i;

            EqSeekBarView eqSeekBarView =new EqSeekBarView(getContext(),band,mEqualizer);


            mLayout.addView(eqSeekBarView);
        }
    }



}
