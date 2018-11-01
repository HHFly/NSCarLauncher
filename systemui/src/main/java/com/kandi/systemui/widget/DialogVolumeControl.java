package com.kandi.systemui.widget;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kandi.systemui.R;
import com.kandi.systemui.service.BluetoothController;
import com.kandi.systemui.service.KandiSystemUiService;


public class DialogVolumeControl {
	//
	View mRootView;
	Context content ;
	static Dialog alertDialog;
	static SeekBar thumb_volume;
int STREAM_MUSIC,STREAM_MAX_MUSIC,progress;
	static AudioManager audiomanage;
	ImageView imagebtn_volume;
	public static boolean flag_quiet;
	int last_volume = 0;
	private int dismisstime =0;


	public void setContent(Context content){
		this.content = content;

	}
	// 来电显示弹出框
	public void incomingShow() {

			alertDialog = new MyDialog(content);
			alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
			Window window = alertDialog.getWindow();
			window.requestFeature(Window.FEATURE_NO_TITLE);
			window.setContentView(R.layout.dialog_volume_control);

// 设置具体参数
			WindowManager.LayoutParams lp = window.getAttributes();
//		lp.x = 0;
//		lp.y = -285;
		lp.gravity = Gravity.CENTER|Gravity.TOP;
			window.setAttributes(lp);
			window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			initView(content,window);

	}
	class MyDialog extends Dialog{
		Context context;
		public MyDialog(Context context) {
			super(context, R.style.tranDialog);
			this.context = context;
		}
	}
public void  show(){
	   dismisstime =0;
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    if (dismisstime > 3) {
                        alertDialog.dismiss();
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
		alertDialog.show();
}


	/**
	 *
	 *
	 * @param
	 */

	public void initView (Context context,Window window) {
		
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
		STREAM_MUSIC = current;
		STREAM_MAX_MUSIC = max;
		progress = Math.round(STREAM_MUSIC * 100 / STREAM_MAX_MUSIC);
		Log.d("MUSIC", "max : " + max + " current : " + current);

		//
		max = audiomanage.getStreamMaxVolume( AudioManager.STREAM_ALARM );
		current = audiomanage.getStreamVolume( AudioManager.STREAM_ALARM );
		Log.d("ALARM", "max : " + max + " current : " + current);

		
		//
		thumb_volume = (SeekBar) window.findViewById(R.id.thumb_volume);

		thumb_volume.setProgress(progress);
		thumb_volume.setOnSeekBarChangeListener(verticalSeekBarChangeListener);
		imagebtn_volume = (ImageView) window.findViewById(R.id.imagebt_volume_show);
		setVolumeImage(STREAM_MUSIC);

		imagebtn_volume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!flag_quiet) {
					last_volume = STREAM_MUSIC;
					STREAM_MUSIC = 0;
					progress = Math.round(STREAM_MUSIC * 100 / STREAM_MAX_MUSIC);
					thumb_volume.setProgress(progress);
					setVolumeImage(STREAM_MUSIC);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, STREAM_MUSIC, 0);
					flag_quiet = true;
//
				}else{
					STREAM_MUSIC = last_volume;
					progress = Math.round(STREAM_MUSIC * 100 / STREAM_MAX_MUSIC);
					thumb_volume.setProgress(progress);
					setVolumeImage(STREAM_MUSIC);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, STREAM_MUSIC, 0);
					flag_quiet = false;
//
				}
			}
		});



	}

	public void setVolumeProgress(int progress){
		thumb_volume.setProgress(progress);
	}

	public int getVolumeProgress(){
		return thumb_volume.getProgress();
	}

	public void setVolumeMute(){
		imagebtn_volume.performClick();
	}

	public  void volumeResume(){
		if(audiomanage!=null) {
			int current = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
			STREAM_MUSIC = current;
			progress = Math.round(STREAM_MUSIC * 100 / STREAM_MAX_MUSIC);
			thumb_volume.setProgress(progress);
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
            int volume = (int) Math.round((float)progress/100.0  * STREAM_MAX_MUSIC);

            if (volume != STREAM_MUSIC) {
            	audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            	STREAM_MUSIC = volume;
            	setVolumeImage(volume);
			}
			int blueVolume =  (int) Math.round((float)progress/100.0  * 21.0);
            if(BluetoothController.isRingCall) {
				if (KandiSystemUiService.btservice != null) {
					try {
						KandiSystemUiService.btservice.btSetVol(String.valueOf(blueVolume));
					} catch (RemoteException e) {
						e.printStackTrace();
						Log.d("BT", "blueVolume set error");
					}
				}
			}
			dismisstime=0;
        }
    };



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


}
