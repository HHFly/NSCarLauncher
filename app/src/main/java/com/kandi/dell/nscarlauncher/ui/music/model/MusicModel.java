package com.kandi.dell.nscarlauncher.ui.music.model;

import android.content.Context;
import android.content.Intent;

import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;


public class MusicModel {
	public static final int LIST_LOOP = 1;//列表循环
	public static final int SINGLE_LOOP = 2;         //单曲循环
	public static final int RANDOM_PLAY = 3;         //随机循环

	// 获取下首歌曲
	public static void getNextMusic(Context context, int model) {
		if(homePagerActivity.getDialogLocalMusic().data.size()>0) {
			if (model == LIST_LOOP) {
				if (homePagerActivity.getDialogLocalMusic().musicID >= (homePagerActivity.getDialogLocalMusic().data.size() - 1)) {
					homePagerActivity.getDialogLocalMusic().musicID = 0;
				} else {
					homePagerActivity.getDialogLocalMusic().musicID++;
				}
			} else if (model == SINGLE_LOOP) {

			} else if (model == RANDOM_PLAY) {
				getRandom(homePagerActivity.getDialogLocalMusic().data.size());
			}
			broadcastMusicInfo(context, FlagProperty.NEXT_MSG);
		}
	}

	//获取上首歌曲
	public static void getPrevMusic(Context context, int model) {
		if(homePagerActivity.getDialogLocalMusic().data.size()>0) {
			if (model == LIST_LOOP) {
				if (homePagerActivity.getDialogLocalMusic().musicID == 0) {
					homePagerActivity.getDialogLocalMusic().musicID = homePagerActivity.getDialogLocalMusic().data.size() - 1;
				} else {
					homePagerActivity.getDialogLocalMusic().musicID--;
				}
			} else if (model == SINGLE_LOOP) {

			} else if (model == RANDOM_PLAY) {
				getRandom(homePagerActivity.getDialogLocalMusic().data.size());
			}
			broadcastMusicInfo(context, FlagProperty.PRIVIOUS_MSG);
		}
	}

	//发送音乐变更信息
	public static void broadcastMusicInfo(Context context, int msg) {
		Intent i = new Intent(context, PlayerService.class);
		i.putExtra("MSG", msg);
		context.startService(i);
	}

	//获取随机的一首歌曲
	public static void getRandom(int sum){
		int num = 0;
		do {
			num = (int) (Math.random()*sum);
		} while (num == homePagerActivity.getDialogLocalMusic().musicID && sum > 1);
		homePagerActivity.getDialogLocalMusic().musicID = num;
	}

}
