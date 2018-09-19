package com.kandi.dell.nscarlauncher.ui.music;

import android.annotation.SdkConstant;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.ui.video.VideoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("DefaultLocale")
public class DialogLocalMusic  {
	@SuppressLint("SdCardPath")
	public static final String PATH_SDCARD = "/storage/emulated/0/";
	public static final String PATH_USB = "/storage/udisk/";
	static Dialog dialog;
	static ImageView cursor;
	ImageButton btn_locality, btn_usb, btn_refresh;
	static int cursor_position = 0;
	ListView listview;
	public static List<Mp3Info> data = new ArrayList<Mp3Info>();
	public static Mp3Info playnow = new Mp3Info();
	public static List<Mp3Info> newdata = new ArrayList<Mp3Info>();
	public static  List<Mp3Info> SDData = new ArrayList<Mp3Info>();
	public static List<Mp3Info> USBData = new ArrayList<Mp3Info>();
	public static  List<Mp3Info> SDVideoData = new ArrayList<Mp3Info>();
	public static List<Mp3Info> USBVideoData = new ArrayList<Mp3Info>();

	public static MusicListAdapter adapter;
	static String url;
	static ContentResolver mResolver;
	public static int musicID;
	Context context;
	public static void Clear(){
		SDData.clear();
		USBData.clear();
		data.clear();
		playnow=new Mp3Info();

	}

//	public DialogLocalMusic(final Context context) {
//		dialog = new Dialog(context, R.style.nodarken_style);
////		dialog.show();
//		Window window = dialog.getWindow();
//		window.setContentView(R.layout.dialog_local_music);
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.y = 34; //
//		window.setAttributes(lp);
//		window.setWindowAnimations(R.style.dialog_localmusic_style);
//		this.context = context;
//
//		cursor = (ImageView) window.findViewById(R.id.local_music_cursor);
//		btn_locality = (ImageButton) window.findViewById(R.id.local_music_locality);
//		btn_locality.setOnClickListener(this);
//		btn_usb = (ImageButton) window.findViewById(R.id.local_music_usb);
//		btn_usb.setOnClickListener(this);
//		btn_refresh = (ImageButton) window.findViewById(R.id.local_music_refresh);
//		btn_refresh.setOnClickListener(this);
//
//		listview = (ListView) window.findViewById(R.id.local_music_listview);
//
////		dialog.hide();
//
//		getFile(PATH_SDCARD);
//		adapter = new MusicListAdapter(context);
//		listview.setAdapter(adapter);
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				if (newdata.size() > 0) {
//					transportData();
//				}
//
//				musicID = arg2;
//				PlayerService.isPause = false;
//				Intent i = new Intent(context, PlayerService.class);
//				i.putExtra("MSG", FlagProperty.PLAY_MSG);
//				context.startService(i);
//				MusicFragment.listStartPlayMusic();
//			}
//		});
//
////		MediaScannerConnection.scanFile(context, new String[] { Environment
////				.getExternalStorageDirectory().getAbsolutePath() }, null, null);
//
//	}
	
	private void scanSdCard(){
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
        ScanSdReceiver scanSdReceiver = new ScanSdReceiver();
        context.registerReceiver(scanSdReceiver, intentfilter);
        
        MediaScannerConnection.scanFile(context, new String[] { Environment
                .getExternalStorageDirectory().getAbsolutePath() }, null, null);
        
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Video.Media.DATA, PATH_SDCARD);
        
        
        final Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://"+ Environment.getExternalStorageDirectory().getAbsolutePath())));
        
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(PATH_SDCARD)); //out is your output file
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
    }

//	public static void showLocalMusicDialog() {
//		dialog.show();
//		changeCursorPosition(cursor_position);
//	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.local_music_locality:
//			getFile(PATH_SDCARD);
//			changeCursorPosition(0);
//			break;
//		case R.id.local_music_usb:
//			getFile(PATH_USB);
//			changeCursorPosition(155);
//			break;
//		case R.id.local_music_refresh:
//			if (cursor_position == 0) {
//				btn_locality.performClick();
//			} else if (cursor_position == 155) {
//				btn_usb.performClick();
//			}
//			break;
//		}
//		adapter.notifyDataSetChanged();
//
//	}
	public static List<Mp3Info> getData(Context context,String path) {

//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        Uri uri = Uri.fromFile(new File(path));
//        intent.setData(uri);
//        context.sendBroadcast(intent);
		//scanSdCard();

		System.out.println("get file");

		newdata.clear();

		ContentResolver	mResolver = context.getContentResolver();
		System.out.println("mResolver:" + mResolver);
		Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		int i = 0, j = 0;
		int cursorCount = cursor.getCount();
		System.out.println("cursorCount" + cursorCount);
		if (cursorCount > 0) {
			cursor.moveToFirst();
			while (i < cursorCount) {
				// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
				url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				if (url.toLowerCase().indexOf(path) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					info.url = url;
					newdata.add(info);
				}
				i++;
				cursor.moveToNext();
			}
			cursor.close();
		}

		if (data.size() == 0) {
			musicID = 0;
			transportData();
		}
		System.out.println("newdata.size():" + newdata.size());
		mResolver = null;
		return data;
	}
	private void getFile(String path) {

//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        Uri uri = Uri.fromFile(new File(path));
//        intent.setData(uri);
//        context.sendBroadcast(intent);
		//scanSdCard();

        System.out.println("get file");

		newdata.clear();

		ContentResolver mResolver = context.getContentResolver();
		System.out.println("mResolver:" + mResolver);
		Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		int i = 0, j = 0;
		int cursorCount = cursor.getCount();
		System.out.println("cursorCount" + cursorCount);
		if (cursorCount > 0) {
			cursor.moveToFirst();
			while (i < cursorCount) {
				// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
				url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				if (url.toLowerCase().indexOf(path) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					info.url = url;
					newdata.add(info);
				}
				i++;
				cursor.moveToNext();
			}
			cursor.close();
		}
		if (data.size() == 0) {
			musicID = 0;
			transportData();
		}
		System.out.println("newdata.size():" + newdata.size());
		mResolver = null;
	}
	/*获取usb sd */
	private static  void getSDUSBData(Context context) {


		SDData.clear();
		USBData.clear();
		ContentResolver mResolver = context.getContentResolver();
		System.out.println("mResolver:" + mResolver);
		Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		int i = 0, j = 0;
		int cursorCount = cursor.getCount();
		System.out.println("cursorCount" + cursorCount);
		if (cursorCount > 0) {
			cursor.moveToFirst();
			while (i < cursorCount) {
				// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
				url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				if (url.toLowerCase().indexOf(PATH_SDCARD) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					info.url = url;
					SDData.add(info);
				}
				if (url.toLowerCase().indexOf(PATH_USB) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					info.url = url;
					USBData.add(info);
				}
				i++;
				cursor.moveToNext();
			}
			cursor.close();

				musicID = 0;
				transportData();

		}
		mResolver = null;
	}
	public static void updateGallery(final Context context){
		File pathSD = new File(PATH_SDCARD);
		File pathUSB = new File(PATH_USB);
		MediaScannerConnection.scanFile(context,
				new String[] { pathSD.getAbsolutePath(),pathUSB.getAbsolutePath() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						MusicFragment.reSetMusic(false);
						VideoFragment.dialogLocalMusic.ScanVideo(context,false);
					}
				});

	}

	/*获取usb sd */
	private static  void getSDUSBViedoData(Context context) {


		SDVideoData.clear();
		USBVideoData.clear();

		ContentResolver mResolver = context.getContentResolver();
		System.out.println("mResolver:" + mResolver);
		Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		int i = 0, j = 0;
		int cursorCount = cursor.getCount();
		String url;
		System.out.println("cursorCount" + cursorCount);
		if (cursorCount > 0) {
			cursor.moveToFirst();
			while (i < cursorCount) {
				// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
				 url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				if (url.toLowerCase().indexOf(PATH_SDCARD) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
					info.url = url;
					SDVideoData.add(info);
				}
				if (url.toLowerCase().indexOf(PATH_USB) > -1) {
					Mp3Info info = new Mp3Info();
					info.id = j++;
					info.displayName = cursor
							.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
					System.out.println("歌曲名:" + info.displayName);
					info.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					info.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					info.url = url;
					USBVideoData.add(info);
				}
				i++;
				cursor.moveToNext();
			}
			cursor.close();

		}
		mResolver = null;
	}
	public static void transportData(){
		data.clear();
		if(SDData!=null&&SDData.size()!=0) {
			for (int i = 0; i < SDData.size(); i++) {
				data.add(SDData.get(i));
			}
			return;
		}
		if(USBData!=null&&USBData.size()!=0) {
			for (int i = 0; i < USBData.size(); i++) {
				data.add(USBData.get(i));
			}

		}
	}
	public static void transport(List<Mp3Info> data,List<Mp3Info> newdata){
		data.clear();
		for (int i = 0; i < newdata.size(); i++) {
			data.add(newdata.get(i));
		}
	}
	public static void ScanMusic(final Context context ,final boolean isReSet){

		new Thread(){
			public void run() {
				if(isReSet){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				getSDUSBData(context);
				if(mThreadCallback!=null){
					mThreadCallback.threadEndLisener();
				}
			}
		}.start();  //开启一个线程
	}

	public static void ScanVideo(final Context context ,final boolean isReSet){

		new Thread(){
			public void run() {
				if(isReSet){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(context!=null) {
					getSDUSBViedoData(context);
				}
				if(mThreadCallback!=null){
					mThreadCallback.videoEndListener();
				}

			}
		}.start();  //开启一个线程
	}
	// private void getFile(String path){
	// map.clear();
	// data.clear();
	// File file = new File(path);
	// if (file.exists()) {
	// File[] files = file.listFiles();// ��ȡ
	// getFileName(files);
	// }
	// }
	//
	// //��Ŀ¼�л�ȡ�����ļ�
	// private void getFileName(File[] files) {
	// if (files != null) {// ���ж�Ŀ¼�Ƿ�Ϊ�գ�����ᱨ��ָ��
	// for (File file : files) {
	// if (file.isDirectory()) {
	// getFileName(file.listFiles());
	// } else {
	// String fileName = file.getName();
	// if (fileName.endsWith(".mp3")) {
	// map.put(fileName, file.getPath());
	// data.add(fileName);
	// }
	// }
	// }
	// }
	// }

	//光亮游标随着点击的不同而不同
	public static void changeCursorPosition(int nextPosition) {
		Animation animation = new TranslateAnimation(cursor_position, nextPosition, 0, 0);
		animation.setFillAfter(true);//  True:图片停在动画结束位置
		animation.setDuration(300);
		cursor.startAnimation(animation);
		cursor_position = nextPosition;
	}

	public class MusicListAdapter extends BaseAdapter {
		Context context;

		public MusicListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return newdata.size();
		}

		@Override
		public Object getItem(int position) {
			return newdata.get(position);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			convertView = View.inflate(context, R.layout.music_list_cell, null);
//			TextView tv = (TextView) convertView.findViewById(R.id.music_list_textview);
//			tv.setTextColor(Color.WHITE);
//			tv.setText(newdata.get(position).displayName);
			return convertView;
		}

	}
	private static ThreadCallback mThreadCallback;
	public DialogLocalMusic(ThreadCallback threadCallback){
		this.mThreadCallback = threadCallback;
	}
	public interface ThreadCallback {

		void threadEndLisener();

		void videoEndListener();
	}
}
