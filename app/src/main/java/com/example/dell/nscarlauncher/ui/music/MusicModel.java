package com.example.dell.nscarlauncher.ui.music;

import android.content.Context;
import android.content.Intent;

import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;


public class MusicModel {
	public static final int LIST_LOOP = 1;           //�б�ѭ��
	public static final int SINGLE_LOOP = 2;         //����ѭ��
	public static final int RANDOM_PLAY = 3;         //���ѭ��
 
	// ��ȡ���׸���
	public static void getNextMusic(Context context, int model) {
		if (model == LIST_LOOP) {
			if (DialogLocalMusic.musicID == (DialogLocalMusic.data.size() - 1)) {
				DialogLocalMusic.musicID = 0;
			} else {
				DialogLocalMusic.musicID++;
			}
		} else if (model == SINGLE_LOOP) {

		} else if (model == RANDOM_PLAY) {
			getRandom(DialogLocalMusic.data.size());
		}
		broadcastMusicInfo(context, FlagProperty.NEXT_MSG);
	}

	// ��ȡ���׸���
	public static void getPrevMusic(Context context, int model) {
		if (model == LIST_LOOP) {
			if (DialogLocalMusic.musicID == 0) {
				DialogLocalMusic.musicID = DialogLocalMusic.data.size() - 1;
			}else{
				DialogLocalMusic.musicID --;
			}
		} else if (model == SINGLE_LOOP) {

		} else if (model == RANDOM_PLAY) {
			getRandom(DialogLocalMusic.data.size());
		}
		broadcastMusicInfo(context, FlagProperty.PRIVIOUS_MSG);
	}

	// �������ֱ����Ϣ
	public static void broadcastMusicInfo(Context context, int msg) {
		Intent i = new Intent(context, PlayerService.class);
		i.putExtra("MSG", msg);
		context.startService(i);
	}
	
	//��ȡ�����һ�׸���
	public static void getRandom(int sum){
		int num = 0;
		do {
			num = (int) (Math.random()*sum);
		} while (num == DialogLocalMusic.musicID);
		DialogLocalMusic.musicID = num;
	}

}
