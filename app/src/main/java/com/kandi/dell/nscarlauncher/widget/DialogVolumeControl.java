package com.kandi.dell.nscarlauncher.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseDialogFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;


public class DialogVolumeControl extends BaseDialogFragment {
	//
	View mRootView;
	Context content ;
	static Dialog dialog;
	static SeekBar thumb_volume;
	private HomePagerActivity homePagerActivity;
	static AudioManager audiomanage;
	ImageView imagebtn_volume;
	public static boolean flag_quiet;
	int last_volume = 0;
	private int dismisstime =0;
	public void setContent(Context content ,HomePagerActivity homePagerActivity) {
		this.content = content;
		this.homePagerActivity= homePagerActivity;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_volume_control, container, false);


		Window window = getDialog().getWindow();
		window.setWindowAnimations(R.style.mystyle);
		initView(content,rootView);
		return rootView;
	}
	/**
	 *
	 *
	 * @param
	 */

	public void initView (Context context,View window) {
		
		audiomanage = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		//
		int max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL );
		int current = audiomanage.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
		Log.d("VIOCE_CALL", "max : " + max + " current : " + current);
		
		//
		max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
		current = audiomanage.getStreamVolume( AudioManager.STREAM_SYSTEM );
		Log.d("SYSTEM", "max : " + max + " current : " + current);

		//
		max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_RING );
		current = audiomanage.getStreamVolume( AudioManager.STREAM_RING );
		Log.d("RING", "max : " + max + " current : " + current);

		//
		max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		current = audiomanage.getStreamVolume( AudioManager.STREAM_MUSIC );
		FlagProperty.STREAM_MUSIC = current;
		FlagProperty.STREAM_MAX_MUSIC = max;
		VerticalSeekBar.progress = Math.round(FlagProperty.STREAM_MUSIC * 100 / FlagProperty.STREAM_MAX_MUSIC);
		Log.d("MUSIC", "max : " + max + " current : " + current);

		//
		max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_ALARM );
		current = audiomanage.getStreamVolume( AudioManager.STREAM_ALARM );
		Log.d("ALARM", "max : " + max + " current : " + current);

		
		//
		thumb_volume = (SeekBar) window.findViewById(R.id.thumb_volume);

		thumb_volume.setProgress(VerticalSeekBar.progress);
		thumb_volume.setOnSeekBarChangeListener(verticalSeekBarChangeListener);
		imagebtn_volume = (ImageView) window.findViewById(R.id.imagebt_volume_show);
		setVolumeImage(FlagProperty.STREAM_MUSIC);

		
		imagebtn_volume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!flag_quiet) {
					last_volume = FlagProperty.STREAM_MUSIC;
					FlagProperty.STREAM_MUSIC = 0;
					VerticalSeekBar.progress = Math.round(FlagProperty.STREAM_MUSIC * 100 / FlagProperty.STREAM_MAX_MUSIC);
					thumb_volume.setProgress(VerticalSeekBar.progress);
	            	setVolumeImage(FlagProperty.STREAM_MUSIC);
	            	audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, FlagProperty.STREAM_MUSIC, 0);
	            	flag_quiet = true;
//
				}else{
					FlagProperty.STREAM_MUSIC = last_volume;
					VerticalSeekBar.progress = Math.round(FlagProperty.STREAM_MUSIC * 100 / FlagProperty.STREAM_MAX_MUSIC);
					thumb_volume.setProgress(VerticalSeekBar.progress);
	            	setVolumeImage(FlagProperty.STREAM_MUSIC);
	            	audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, FlagProperty.STREAM_MUSIC, 0);
	            	flag_quiet = false;
//
				}

			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						if (dismisstime > 3) {
							dismiss();
							break;
						} else {
							dismisstime++;
						}
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public static void volumeResume(){
		if(audiomanage!=null) {
			int current = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
			FlagProperty.STREAM_MUSIC = current;
			VerticalSeekBar.progress = Math.round(FlagProperty.STREAM_MUSIC * 100 / FlagProperty.STREAM_MAX_MUSIC);
			thumb_volume.setProgress(VerticalSeekBar.progress);
		}
	}
//	 滑动监听
	private OnSeekBarChangeListener verticalSeekBarChangeListener = new OnSeekBarChangeListener()
    {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            
        }
        
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {	
        	//System.out.println("progress: " + progress);
            int volume = (int) Math.round((float)progress/100.0  * FlagProperty.STREAM_MAX_MUSIC);
            if (volume != FlagProperty.STREAM_MUSIC) {
            	audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            	FlagProperty.STREAM_MUSIC = volume;
            	setVolumeImage(volume);
			}
			dismisstime=0;
        }
    };

	@Override
	public void show(FragmentManager manager) {
		dismisstime=0;
		super.show(manager);
	}

	//
    public void setVolumeImage(int volume){
    	if (volume == 0) {
    		imagebtn_volume.setBackgroundResource(R.mipmap.volume_03_off);
		}else if (volume < 6) {
			imagebtn_volume.setBackgroundResource(R.mipmap.volume_03_off_s);
		}else if (volume > 10) {
			imagebtn_volume.setBackgroundResource(R.mipmap.volume_03_off_l);
		}else{
			imagebtn_volume.setBackgroundResource(R.mipmap.volume_03_off_m);
		}
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		homePagerActivity.initImmersionBar();
	}
}
