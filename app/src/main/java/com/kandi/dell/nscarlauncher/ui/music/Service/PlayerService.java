package com.kandi.dell.nscarlauncher.ui.music.Service;
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

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.kandi.dell.nscarlauncher.ui.music.CursorMusicImage;

import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.io.File;
import java.io.FileInputStream;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;


public class PlayerService extends Service {


	  MediaPlayer mediaPlayer; // 媒体播放器对象
	private int msg;				//播放信息
	public static boolean isPause; 		//  暂停状态
	private int currentTime;		//当前播放进度
	
	public static boolean is_start_speed = true;  //是否第一次快进播放



	/**
	 * handler用来接收消息，来发送广播更新播放时间
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if(mediaPlayer != null) {
					try {
						currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
						homePagerActivity.getMusicFragment().setMusicProgress(currentTime);
						SPUtil.getInstance(getApplicationContext(),MusicFragment.MUSICPROGRESS).putInt(MusicFragment.MUSICPROGRESS,currentTime);
						handler.sendEmptyMessageDelayed(1, 1000);
					}catch (Exception e){
						homePagerActivity.getMusicFragment().stopView();
						homePagerActivity.getHomePagerTwoFragment().myHandler.sendEmptyMessage(HomePagerTwoFragment.MUSIC_CLOSE);
						App.get().reSetMusic();
						mediaPlayer=App.get().getMediaPlayer();
						/**
						 * 设置音乐播放完成时的监听器
						 */
						if(mediaPlayer!=null){
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									homePagerActivity.getMusicFragment().bt_next.performClick();
								}
							});
						}
					}

				}
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("service", "service created");
		mediaPlayer = App.get().getMediaPlayer();
		/**
		 * 设置音乐播放完成时的监听器
		 */
		if(mediaPlayer!=null){
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					homePagerActivity.getMusicFragment().bt_next.performClick();
				}
			});
				mediaPlayer.setOnErrorListener(onErrorListener);
		}
	}
	private  MediaPlayer.OnErrorListener onErrorListener =new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what) {
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					homePagerActivity.getMusicFragment().stopView();
					homePagerActivity.getHomePagerTwoFragment().myHandler.sendEmptyMessage(HomePagerTwoFragment.MUSIC_CLOSE);
					App.get().reSetMusic();
					mediaPlayer=App.get().getMediaPlayer();
					mediaPlayer.setOnErrorListener(onErrorListener);
					Log.d("MediaPlayer", "onError");
					break;
			}

			return false;
		}
	};
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		is_start_speed = false;
		msg = intent.getIntExtra("MSG", 0);			//播放信息
		if (msg == FlagProperty.PLAY_MSG) {	//直接播放音乐
			play(0);
		} else if (msg == FlagProperty.PAUSE_MSG) {	//暂停
			pause();	
		} else if (msg == FlagProperty.STOP_MSG) {		//停止
			stop();
		} else if (msg == FlagProperty.PRIVIOUS_MSG) {	//上一首
			previous();
		} else if (msg == FlagProperty.NEXT_MSG) {		//下一首
			next();
		} else if (msg == FlagProperty.PROGRESS_CHANGE) {	//进度更新
			currentTime = intent.getIntExtra("progress", -1);
			play(currentTime);
		} else if (msg == FlagProperty.PLAYING_MSG) {
			handler.sendEmptyMessage(1);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 *  播放音乐
	 * 
	 * @param
	 */
	private void play(int currentTime) {
		if (isPause && currentTime== 0) {
			mediaPlayer.start();
			isPause = false;
		}else{
			try {
				if (currentTime== 0) {
					homePagerActivity.getMusicFragment().flag_first = false;
				}
				mediaPlayer.reset();//把各项参数恢复到初始状态
				homePagerActivity.getDialogLocalMusic().playnow =new Mp3Info(homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID));
				File file = new File(homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID).url);
				SPUtil.getInstance(getApplicationContext(),MusicFragment.MUSICPATH).putString(MusicFragment.MUSICPATH,homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID).url);
				SPUtil.getInstance(getApplicationContext(),MusicFragment.MUSICID).putInt(MusicFragment.MUSICID,homePagerActivity.getDialogLocalMusic().musicID);
				SPUtil.getInstance(getApplicationContext(),MusicFragment.MUSICDATAMODE).putInt(MusicFragment.MUSICDATAMODE, homePagerActivity.getMusicFragment().dataMode);
				FileInputStream fis = new FileInputStream(file);
				mediaPlayer.setDataSource(fis.getFD());
				mediaPlayer.prepareAsync(); // 进行缓冲
				mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
				handler.sendEmptyMessage(1);
			} catch (Exception e) {
				e.printStackTrace();
//				homePagerActivity.getDialogLocalMusic().data.clear();
//				homePagerActivity.getDialogLocalMusic().ScanAllDaTa(this);
				homePagerActivity.getMusicFragment().stopView();
				homePagerActivity.getHomePagerTwoFragment().myHandler.sendEmptyMessage(HomePagerTwoFragment.MUSIC_CLOSE);
				App.get().reSetMusic();
				mediaPlayer=App.get().getMediaPlayer();
				/**
				 * 设置音乐播放完成时的监听器
				 */
				if(mediaPlayer!=null){
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							homePagerActivity.getMusicFragment().bt_next.performClick();
						}
					});
				}
			}
		}
	}

	/**
	 * 暂停音乐
	 */
	private void pause() {
	    try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPause = true;
            }
        }catch (Exception e){
			homePagerActivity.getMusicFragment().stopView();
			homePagerActivity.getHomePagerTwoFragment().myHandler.sendEmptyMessage(HomePagerTwoFragment.MUSIC_CLOSE);
            App.get().reSetMusic();
            mediaPlayer=App.get().getMediaPlayer();
            /**
             * 设置音乐播放完成时的监听器
             */
            if(mediaPlayer!=null){
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
						homePagerActivity.getMusicFragment().bt_next.performClick();
                    }
                });
            }
        }

	}

	/**
	 * 上一首
	 */
	private void previous() {
		isPause = false;
		play(0);
	}

	/**
	 *  下一首
	 */
	private void next() {
		isPause = false;
		play(0);
	}

	/**
	 *停止音乐
	 */
	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare(); //在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
			} catch (Exception e) {
				e.printStackTrace();
				homePagerActivity.getMusicFragment().stopView();
				homePagerActivity.getHomePagerTwoFragment().myHandler.sendEmptyMessage(HomePagerTwoFragment.MUSIC_CLOSE);
				App.get().reSetMusic();
				mediaPlayer=App.get().getMediaPlayer();

			}
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 
	 * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
	 * 
	 */
	private final class PreparedListener implements OnPreparedListener {
		private int currentTime;

		public PreparedListener(int currentTime) {
			this.currentTime = currentTime;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			String musicName = ((Mp3Info)homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID)).title;
			String artist = ((Mp3Info)homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID)).artist;
			
			//设置歌曲专辑内置图片
			Bitmap bm = null;
			try {
				bm = CursorMusicImage.setArtwork(PlayerService.this, homePagerActivity.getDialogLocalMusic().playnow.url);
			} catch (Throwable throwable) {
				bm = BitmapFactory.decodeResource(PlayerService.this.getResources(), R.mipmap.one);
				throwable.printStackTrace();
			}
			BitmapDrawable bmpDraw = new BitmapDrawable(bm);
			if( homePagerActivity.getMusicFragment().circle_image!=null) {
				homePagerActivity.getMusicFragment().circle_image.setImageDrawable(bmpDraw);

			}
//			String albumArt = CursorMusicImage.getImage(PlayerService.this, ((Mp3Info)homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID)).url);
//			if (albumArt == null) {
//				if(MusicFragment.circle_image!=null) {
//					MusicFragment.circle_image.setImageResource(R.mipmap.one);
//				}
//			} else {
//
////				Bitmap bm = BitmapFactory.decodeFile(albumArt);

//			}

			homePagerActivity.getMusicFragment().setMusicInfo(musicName, artist);
			homePagerActivity.getMusicFragment().setMusicCol(homePagerActivity.getDialogLocalMusic().playnow.url);
			if (!isPause) {
				mediaPlayer.start(); // 开始播放
			}
			if (currentTime > 0) { //  如果音乐不是从头播放
				mediaPlayer.seekTo(currentTime);
			}
		}
	}

}
