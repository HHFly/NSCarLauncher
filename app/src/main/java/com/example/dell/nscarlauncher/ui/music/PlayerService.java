package com.example.dell.nscarlauncher.ui.music;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;


public class PlayerService extends Service {


	private MediaPlayer mediaPlayer; // ý�岥��������
	private int msg;				//������Ϣ
	public static boolean isPause; 		// ��ͣ״̬
	private int currentTime;		//��ǰ���Ž���
	
	public static boolean is_start_speed = true;  //�Ƿ��һ�ο������
	
	/**
	 * handler����������Ϣ�������͹㲥���²���ʱ��
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if(mediaPlayer != null) {
					currentTime = mediaPlayer.getCurrentPosition(); // ��ȡ��ǰ���ֲ��ŵ�λ��
					FragmentMusic.setMusicProgress(currentTime);
					handler.sendEmptyMessageDelayed(1, 1000);
				}
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("service", "service created");
		mediaPlayer = new MediaPlayer();
		/**
		 * �������ֲ������ʱ�ļ�����
		 */
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				FragmentMusic.bt_next.performClick();//��һ��
			}
		});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		is_start_speed = false;
		msg = intent.getIntExtra("MSG", 0);			//������Ϣ
		if (msg == FlagProperty.PLAY_MSG) {	//ֱ�Ӳ�������
			play(0);
		} else if (msg == FlagProperty.PAUSE_MSG) {	//��ͣ
			pause();	
		} else if (msg == FlagProperty.STOP_MSG) {		//ֹͣ
			stop();
		} else if (msg == FlagProperty.PRIVIOUS_MSG) {	//��һ��
			previous();
		} else if (msg == FlagProperty.NEXT_MSG) {		//��һ��
			next();
		} else if (msg == FlagProperty.PROGRESS_CHANGE) {	//���ȸ���
			currentTime = intent.getIntExtra("progress", -1);
			play(currentTime);
		} else if (msg == FlagProperty.PLAYING_MSG) {
			handler.sendEmptyMessage(1);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * ��������
	 * 
	 * @param position
	 */
	private void play(int currentTime) {
		if (isPause && currentTime== 0) {
			mediaPlayer.start();
			isPause = false;
		}else{
			try {
				if (currentTime== 0) {
					FragmentMusic.flag_first = false;
				}
				mediaPlayer.reset();// �Ѹ�������ָ�����ʼ״̬
				mediaPlayer.setDataSource(DialogLocalMusic.data.get(DialogLocalMusic.musicID).url);
				mediaPlayer.prepare(); // ���л���
				mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// ע��һ��������
				handler.sendEmptyMessage(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ͣ����
	 */
	private void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}

	/**
	 * ��һ��
	 */
	private void previous() {
		isPause = false;
		play(0);
	}

	/**
	 * ��һ��
	 */
	private void next() {
		isPause = false;
		play(0);
	}

	/**
	 * ֹͣ����
	 */
	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare(); // �ڵ���stop�������Ҫ�ٴ�ͨ��start���в���,��Ҫ֮ǰ����prepare����
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 
	 * ʵ��һ��OnPrepareLister�ӿ�,������׼���õ�ʱ��ʼ����
	 * 
	 */
	private final class PreparedListener implements OnPreparedListener {
		private int currentTime;

		public PreparedListener(int currentTime) {
			this.currentTime = currentTime;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			String musicName = ((Mp3Info)DialogLocalMusic.data.get(DialogLocalMusic.musicID)).title;
			String artist = ((Mp3Info)DialogLocalMusic.data.get(DialogLocalMusic.musicID)).artist;
			
			//���ø���ר������ͼƬ
			String albumArt = CursorMusicImage.getImage(PlayerService.this, ((Mp3Info)DialogLocalMusic.data.get(DialogLocalMusic.musicID)).url);
			if (albumArt == null) {
				FragmentMusic.circle_image.nextRoatate(R.drawable.one);
			} else {
				Bitmap bm = BitmapFactory.decodeFile(albumArt);
				BitmapDrawable bmpDraw = new BitmapDrawable(bm);
				FragmentMusic.circle_image.setImageDrawable(bmpDraw);
			}
					
			FragmentMusic.setMusicInfo(musicName, artist);
			if (!isPause) {
				mediaPlayer.start(); // ��ʼ����
			}
			if (currentTime > 0) { // ������ֲ��Ǵ�ͷ����
				mediaPlayer.seekTo(currentTime);
			}
		}
	}

}
