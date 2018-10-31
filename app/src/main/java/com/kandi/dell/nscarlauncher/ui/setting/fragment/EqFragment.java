package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.view.View;
import android.widget.LinearLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;

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
        mEqualizer = new Equalizer(0, PlayerService.mediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        short bands = mEqualizer.getNumberOfBands();

        final short minEqualizer = mEqualizer.getBandLevelRange()[0];
        final short maxEqualizer = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;

//            TextView freqTextView = new TextView(this);
//            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//
//            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//
//            freqTextView
//                    .setText((mEqualizer.getCenterFreq(band) / 1000) + "HZ");
//            mLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            minDbTextView.setText((minEqualizer / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            maxDbTextView.setText((maxEqualizer / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            SeekBar seekbar = new SeekBar(this);
            seekbar.setLayoutParams(layoutParams);
            seekbar.setMax(maxEqualizer - minEqualizer);
            seekbar.setProgress(mEqualizer.getBandLevel(band));
            seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @TargetApi(Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    mEqualizer.setBandLevel(band,
                            (short) (progress + minEqualizer));
                }
            });
            row.addView(minDbTextView);
            row.addView(seekbar);
            row.addView(maxDbTextView);
            mLayout.addView(row);
        }
    }



}
