package com.kandi.dell.nscarlauncher.widget;

import android.content.Context;
import android.media.audiofx.Equalizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.common.util.JsonUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.setting.fragment.EqFragment;

public class EqSeekBarView extends LinearLayout {
    private TextView  tv_eq_hz,tv_eq_band;
    private VerticalSeekBarII verticalSeekBar;
     short band ;
    private Equalizer mEqualizer;
    private int position;

    public EqSeekBarView(final Context context , final short band, final Equalizer mEqualizer) {
        super(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_eq_seekbar, this);
        this.band =band;
        this.mEqualizer =mEqualizer;
        tv_eq_hz=findViewById(R.id.tv_eq_hz);
        tv_eq_band=findViewById(R.id.tv_eq_band);
        verticalSeekBar =findViewById(R.id.eq_seekbar);
        final short minEqualizer = mEqualizer.getBandLevelRange()[0];
        final short maxEqualizer = mEqualizer.getBandLevelRange()[1];
        tv_eq_band.setText((mEqualizer.getCenterFreq(band) / 1000) + "HZ");
        tv_eq_hz.setText( (mEqualizer.getBandLevel(band)/100)+"dB");
        verticalSeekBar.setMax(maxEqualizer -minEqualizer);

        verticalSeekBar.setProgress(mEqualizer.getBandLevel(band)-minEqualizer);
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEqualizer.setBandLevel(band, (short) (progress + minEqualizer));
                tv_eq_hz.setText( (mEqualizer.getBandLevel(band)/100)+"dB");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("onStartTrackingTouch", "onStartTrackingTouch:  ");
                if (onItemClickListener != null) {
                    onItemClickListener.onClickMode();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("onStopTrackingTouch", "onStopTrackingTouch:  ");
                Equalizer.Settings settings =mEqualizer.getProperties();
                SPUtil.getInstance(context,"EQ").putString("EQSet", JsonUtils.toJson(mEqualizer.getProperties()));
                SPUtil.getInstance(context,"EQ").putInt("EQPosition", EqFragment.postion );
            }
        });
//        verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress, boolean fromUser) {
//                mEqualizer.setBandLevel(band,
//                        (short) (progress + minEqualizer));
//                tv_eq_hz.setText( (mEqualizer.getBandLevel(band)/100)+"dB");
//            }
//
//            @Override
//            public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//
//            }
//        });
    }


    public void  refreshSeekbar(final short band, final Equalizer mEqualizer){
        final short minEqualizer = mEqualizer.getBandLevelRange()[0];
        final short maxEqualizer = mEqualizer.getBandLevelRange()[1];
        verticalSeekBar.setMax(maxEqualizer -minEqualizer);
        verticalSeekBar.setProgress(mEqualizer.getBandLevel(band)-minEqualizer);
        tv_eq_hz.setText( (mEqualizer.getBandLevel(band)/100)+"dB");
    }
    public void  setTv_eq_hz(String hz){
        tv_eq_hz.setText(hz);
    }

    public void  setTv_eq_band(String band){
        tv_eq_band.setText(band);
    }
    public void  setProgress(int progress){
        verticalSeekBar.setProgress(progress);
    }
    public void  setMax(int max){
        verticalSeekBar.setMax(max);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        /**
         * 点击
         *
         *
         */
        void onClickMode();

    }
}
