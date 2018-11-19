package com.kandi.dell.nscarlauncher.ui.music;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.kandi.dell.nscarlauncher.R;

import java.util.HashMap;

public class CursorMusicImage {

	private static Cursor getCursor(Context context, String filePath) {
		String path = null;
		Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		// System.out.println(c.getString(c.getColumnIndex("_data")));
		if (c.moveToFirst()) {
			do {
				// 通过Cursor 获取路径，如果路径相同则break
				System.out.println("////////" + filePath);
				path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				System.out.println("?????????" + path);
				// 查找到相同的路径则返回，此时cursorPosition 便是指向路径所指向的Cursor 便可以返回了
				if (path.equals(filePath)) {
					// System.out.println("audioPath = " + path);
					// System.out.println("filePath = " + filePath);
					// cursorPosition = c.getPosition();
					break;
				}
			} while (c.moveToNext());
		}
		//
		// String audioPath = c.getString(c
		// .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
		//
		// System.out.println("audioPath = " + audioPath);
		return c;
	}

	private static String getAlbumArt(Context context, int album_id) {
		String mUriAlbums = "content://media/external/audio/albums";
		String[] projection = new String[] { "album_art" };
		Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
				projection, null, null, null);
		String album_art = null;
		if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
			cur.moveToNext(); 
			album_art = cur.getString(0);
		}
		cur.close();
		cur = null;
		return album_art;
	}
	public static Bitmap setArtwork(Context context, String url) throws Throwable{
		Bitmap bMap= null;
		MediaMetadataRetriever myRetriever = null;
		try {
			myRetriever = new MediaMetadataRetriever();
			if (Build.VERSION.SDK_INT >= 14)
				myRetriever.setDataSource(url, new HashMap<String, String>());
			else
				myRetriever.setDataSource(url);

//		myRetriever.setDataSource(context, selectedAudio); // the URI of audio file

			byte[] artwork;

			artwork = myRetriever.getEmbeddedPicture();

			if (artwork != null) {
				 bMap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);


			} else {

				bMap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.one);
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new Throwable(
					"Exception in retriveVideoFrameFromVideo(String videoPath)"
							+ e.getMessage());
		}finally
		{
			if (myRetriever != null)
			{
				myRetriever.release();
			}
		}
		return  bMap;
	}

	public static String getImage(Context context, String filePath) {
		Cursor currentCursor = getCursor(context, filePath);
		if(currentCursor.getColumnCount()!=0) {
			int album_id = currentCursor.getInt(currentCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

			String albumArt = getAlbumArt(context, album_id);
//		Bitmap bm = null;
//		if (albumArt == null) {
//			mImageView.setBackgroundResource(R.drawable.staring);
//		} else {
//			bm = BitmapFactory.decodeFile(albumArt);
//			BitmapDrawable bmpDraw = new BitmapDrawable(bm);
//			mImageView.setImageDrawable(bmpDraw);
//		}
			return albumArt;
		}
		return  null;
	}
}
