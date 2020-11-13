package com.kandi.dell.nscarlauncher.ui_portrait.music.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.kandi.dell.nscarlauncher.ui.music.CursorMusicImage;
import com.kandi.dell.nscarlauncher.ui_portrait.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;

import java.io.File;
import java.io.FileInputStream;




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
						App.get().getCurActivity().getMusicFragment().setMusicProgress(currentTime);
						if(App.get().getCurActivity().getDialogLrc().isLrc){
							App.get().getCurActivity().getDialogLrc().refrshLrc(currentTime);
						}
						SPUtil.getInstance(getApplicationContext()).putInt(MusicFragment.MUSICPROGRESS,currentTime);
						handler.sendEmptyMessageDelayed(1, 1000);
					}catch (Exception e){
						App.get().getCurActivity().getMusicFragment().stopView();

						App.get().reSetMusic();
						mediaPlayer=App.get().getMediaPlayer();
						/**
						 * 设置音乐播放完成时的监听器
						 */
						if(mediaPlayer!=null){
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									App.get().getCurActivity().getMusicFragment().NextMusic();
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
					App.get().getCurActivity().getMusicFragment().NextMusic();
				}
			});
				mediaPlayer.setOnErrorListener(onErrorListener);
		}
	}
	private  MediaPlayer.OnErrorListener onErrorListener =new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {

					App.get().getCurActivity().getMusicFragment().stopView();
					App.get().reSetMusic();
					mediaPlayer=App.get().getMediaPlayer();
					mediaPlayer.setOnErrorListener(onErrorListener);
					/**
					 * 设置音乐播放完成时的监听器
					 */
					if(mediaPlayer!=null){
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								App.get().getCurActivity().getMusicFragment().NextMusic();
							}
						});
					}
					Log.d("MediaPlayer", "onError");
//					break;
//			}

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
		if(intent==null){
			return super.onStartCommand(intent, flags, startId);
		}
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
//		App.get().AllServiceControll(2,1,0,0,0);

		if (isPause && currentTime== 0) {
			mediaPlayer.start();
			isPause = false;
		}else{
			try {
				if (currentTime== 0) {
					App.get().getCurActivity().getMusicFragment().flag_first = false;
				}
				mediaPlayer.reset();//把各项参数恢复到初始状态
				App.get().getCurActivity().getDialogLocalMusicD().Playnow =new Mp3Info(App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID));
				File file = new File(App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID).url);
				SPUtil.getInstance(getApplicationContext()).putString(MusicFragment.MUSICPATH,App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID).url);
				SPUtil.getInstance(getApplicationContext()).putInt(MusicFragment.MUSICID,App.get().getCurActivity().getDialogLocalMusicD().musicDiverID);
				SPUtil.getInstance(getApplicationContext()).putInt(MusicFragment.MUSICDATAMODE, App.get().getCurActivity().getDialogLocalMusicD().dataMode);
				FileInputStream fis = new FileInputStream(file);
				mediaPlayer.setDataSource(fis.getFD());
				mediaPlayer.prepareAsync(); // 进行缓冲
				mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
				handler.sendEmptyMessage(1);
			} catch (Exception e) {
				e.printStackTrace();
//				App.get().getCurActivity().getDialogLocalMusic().data.clear();
//				App.get().getCurActivity().getDialogLocalMusic().ScanAllDaTa(this);

				App.get().getCurActivity().getMusicFragment().stopView();
//				App.get().getCurActivity().getHomePagerOneFragment().getHomePagerMusicDriverFragment().myHandler.sendEmptyMessage(HomePagerMusicDriverFragment.MUSIC_CLOSE);
				App.get().reSetMusic();
				mediaPlayer=App.get().getMediaPlayer();
				/**
				 * 设置音乐播放完成时的监听器
				 */
				if(mediaPlayer!=null){
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							App.get().getCurActivity().getMusicFragment().NextMusic();
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

			App.get().getCurActivity().getMusicFragment().stopView();
//			App.get().getCurActivity().getHomePagerOneFragment().getHomePagerMusicDriverFragment().myHandler.sendEmptyMessage(HomePagerMusicDriverFragment.MUSIC_CLOSE);
            App.get().reSetMusic();
            mediaPlayer=App.get().getMediaPlayer();
            /**
             * 设置音乐播放完成时的监听器
             */
            if(mediaPlayer!=null){
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
						App.get().getCurActivity().getMusicFragment().NextMusic();
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
		if (mediaPlayer != null&&isPause) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare(); //在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
			} catch (Exception e) {
				e.printStackTrace();

				App.get().getCurActivity().getMusicFragment().stopView();
//				App.get().getCurActivity().getHomePagerOneFragment().getHomePagerMusicDriverFragment().myHandler.sendEmptyMessage(HomePagerMusicDriverFragment.MUSIC_CLOSE);
				App.get().reSetMusic();
				mediaPlayer=App.get().getMediaPlayer();
                /**
                 * 设置音乐播放完成时的监听器
                 */
                if(mediaPlayer!=null){
                    mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            App.get().getCurActivity().getMusicFragment().NextMusic();
                        }
                    });
                }
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
			String musicName = ((Mp3Info)App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID)).title;
			String artist = ((Mp3Info)App.get().getCurActivity().getDialogLocalMusicD().data.get(App.get().getCurActivity().getDialogLocalMusicD().musicDiverID)).artist;
			
			//设置歌曲专辑内置图片
			Bitmap bm = null;
			try {
				bm = CursorMusicImage.setArtwork(PlayerService.this, App.get().getCurActivity().getDialogLocalMusicD().Playnow.url);
			} catch (Throwable throwable) {
				bm = BitmapFactory.decodeResource(PlayerService.this.getResources(), R.mipmap.one);
				throwable.printStackTrace();
			}
			App.get().getCurActivity().gramophoneView.setImageBitmap(bm);
			if( App.get().getCurActivity().getMusicFragment().gramophoneView!=null) {
				App.get().getCurActivity().getMusicFragment().gramophoneView.setImageBitmap(bm);

			}

            App.get().getCurActivity().getMusicFragment().setMusicInfo(musicName, artist);
			App.get().getCurActivity().setMusicInfo(musicName, artist);
			if (!isPause) {
				mediaPlayer.start(); // 开始播放

				App.get().getCurActivity().getMusicFragment().ViewHandler.sendEmptyMessage(MusicFragment.MUSIC_BLUETOOTH_OPEN);
			}
			if (currentTime > 0) { //  如果音乐不是从头播放
				mediaPlayer.seekTo(currentTime);
			}
		}
	}

}
