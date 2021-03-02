package com.kandi.dell.nscarlauncher.ui.music;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.kandi.dell.nscarlauncher.db.dao.MusicCollectionDao;
import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.ui.video.VideoFragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("DefaultLocale")
public class DialogLocalMusic  {
    @SuppressLint("SdCardPath")
    public static final String PATH_SDCARD = "/storage/emulated/0/";
    public static final String PATH_USB = "/storage/udisk/";
    public static final String PATH_MOVIE = "/storage/sdcard0/Movies/";
    public static final String PATH_MUSIC = "/storage/sdcard0/Music/";
    static int cursor_position = 0;
    ListView listview;
    public  List<Mp3Info> data = new ArrayList<Mp3Info>();
    public  Mp3Info playnow = new Mp3Info();
    public  List<Mp3Info> newdata = new ArrayList<Mp3Info>();
    public   List<Mp3Info> SDData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> USBData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> ColData = new ArrayList<Mp3Info>();
    public   List<Mp3Info> SDVideoData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> USBVideoData = new ArrayList<Mp3Info>();
    	VideoFragment videoFragment;
     MusicFragment musicFragment;
     String url;
     ContentResolver mResolver;
    public  int musicID;
    Context context;
    public  boolean usbStatus = false;
    public  boolean usedStatus = false;
    public  void Clear(){
        SDData.clear();
        USBData.clear();
        data.clear();
        playnow=new Mp3Info();

    }

    public  void setVideoFragment(VideoFragment videoFragment1) {
        this.videoFragment = videoFragment1;
    }

    public  void setMusicFragment(MusicFragment musicFragment) {
        this.musicFragment = musicFragment;
    }

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



    public  List<Mp3Info> getDataMusic(Context context,String path) {

        newdata.clear();
        ContentResolver	mResolver = context.getContentResolver();
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
    /*获取usb sd */
    private   void getSDUSBMusicData(Context context) {

        SDData.clear();
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
                if (url.toLowerCase().indexOf(PATH_SDCARD) > -1||url.toLowerCase().indexOf("/storage/sdcard0/") > -1) {
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
                i++;
                cursor.moveToNext();
            }
            cursor.close();

            musicID = 0;
            transportData();
            if(musicFragment!=null){
                musicFragment.myHandler.sendMessage(musicFragment.myHandler.obtainMessage(musicFragment.VIEWFRESH));
            }
        }
        mResolver = null;


    }


    /*获取usb sd */
    private   void getSDUSBViedoData(Context context) {
        SDVideoData.clear();
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
                i++;
                cursor.moveToNext();
            }
            cursor.close();

        }
        if(videoFragment!=null){
            videoFragment.myHandler.sendMessage(videoFragment.myHandler.obtainMessage(videoFragment.VIEWFRESH));
        }
        mResolver = null;


    }

    /*获取usb sd */
    private   void getUSBVideoMusicData(Context context,int choose) {
        switch (choose){
            case 0:
                USBVideoData.clear();
                USBData.clear();
                m = 0;
                v = 0;
                break;
            case 1:
                USBData.clear();
                m = 0;
                break;
            case 2:
                USBVideoData.clear();
                v = 0;
                break;
            default:
                break;
        }
        try {
            getMediaFile(new File(PATH_USB),choose);
        }catch (Exception e){

        }

    }

    public  void transportData(){
        data.clear();

        if (SDData != null && SDData.size() != 0) {
            for (int i = 0; i < SDData.size(); i++) {
                data.add(SDData.get(i));
            }
            return;

        }
        if (USBData != null && USBData.size() != 0) {
            for (int i = 0; i < USBData.size(); i++) {
                data.add(USBData.get(i));
            }

        }
        if (ColData != null && ColData.size() != 0) {
            for (int i = 0; i < ColData.size(); i++) {
                data.add(ColData.get(i));
            }

        }

    }
    public static void transport(List<Mp3Info> data,List<Mp3Info> newdata){
        data.clear();
        for (int i = 0; i < newdata.size(); i++) {
            data.add(newdata.get(i));
        }
    }
    public  void ScanMusic(final Context context ,final boolean isReSet){

        new Thread(){
            public void run() {
                if(isReSet){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getSDUSBMusicData(context);
                if(mThreadCallback!=null){
                    mThreadCallback.threadEndLisener();
                }
            }
        }.start();  //开启一个线程
    }

    public  void ScanVideo(final Context context ,final boolean isReSet){

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

    public  void ScanVideoMusic(final Context context ,final int choose){

        new Thread(){
            public void run() {
                if(usbStatus && !usedStatus){
                    usedStatus = true;
                    if(context!=null) {
                        getUSBVideoMusicData(context,choose);
                        if(!usbStatus){
                            USBVideoData.clear();
                            USBData.clear();
                        }
                        transportData();
                    }
                    usedStatus = false;
                }else if(!usbStatus){
                    if(context!=null) {
                        getUSBVideoMusicData(context,choose);
                        transportData();
                    }
                }
                if(videoFragment!=null){
                    videoFragment.myHandler.sendMessage(videoFragment.myHandler.obtainMessage(videoFragment.VIEWFRESH));
                }
                if(musicFragment!=null){
                    musicFragment.myHandler.sendMessage(musicFragment.myHandler.obtainMessage(musicFragment.VIEWFRESH));
                }

            }
        }.start();  //开启一个线程
    }
    public  void updateColMusic(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ColData = MusicCollectionDao.getAllFav(context);
                transportData();
                if(musicFragment!=null){
                    musicFragment.myHandler.sendMessage(musicFragment.myHandler.obtainMessage(musicFragment.VIEWFRESH));
                }
            }
        }).start();
    }
    public  void updateLocalMusic(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pathSDMusic = new File(PATH_MUSIC);
                MediaScannerConnection.scanFile(context,
                        new String[] { pathSDMusic.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                                getSDUSBMusicData(context);
                            }
                        });
            }
        }).start();
    }
    public  void updateLocalMusic(final Context context,boolean isShow){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pathSDMusic = new File(PATH_MUSIC);
                MediaScannerConnection.scanFile(context,
                        new String[] { pathSDMusic.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                getSDUSBMusicData(context);
                            }
                        });
            }
        }).start();
    }
    public  void updateLocalVideo(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {

                File pathSDMovie = new File(PATH_MOVIE);
                MediaScannerConnection.scanFile(context,
                        new String[] { pathSDMovie.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d("Video", "onScanCompleted: callback");
                                getSDUSBViedoData(context);
                            }
                        });
            }
        }).start();
    }
    public  void updateGallery(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pathSDMusic = new File("/storage/sdcard0/");
                MediaScannerConnection.scanFile(context,
                        new String[] {pathSDMusic.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//						MusicFragment.reSetMusic(false);
//						VideoFragment.dialogLocalMusic.ScanVideo(context,false);
                                getSDUSBMusicData(context);
                                getSDUSBViedoData(context);
                            }
                        });
            }
        }).start();
    }
    public  void ScanAllDaTa(final Context context ){

        new Thread(){
            public void run() {
                if(context!=null) {
                    updateGallery(context);
                    if(!usedStatus){
                        usedStatus = true;
                        getUSBVideoMusicData(context,0);
                        usedStatus = false;
                    }
                }


            }
        }.start();  //开启一个线程
    }

    private  ThreadCallback mThreadCallback;
    public DialogLocalMusic(VideoFragment videoFragment,MusicFragment musicFragment){
      this.videoFragment=videoFragment;
      this.musicFragment=musicFragment;
    }
    public interface ThreadCallback {

        void threadEndLisener();

        void videoEndListener();
    }



    static int m = 0,v = 0;
    /**
     * 获取视频文件
     *
     * @param file
     * @param choose 0:扫描音视频;1:扫描音乐;2:扫描视频
     * @return
     */
    private  void getMediaFile(File file, final int choose) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    try {
                        Log.d("1", "accept: "+name);
                        int length =name.split("\\.").length;
                        name = "." +name.split("\\.")[length-1];
                        if ((choose == 0 || choose == 1) && (name.equalsIgnoreCase(".mp3") || name.equalsIgnoreCase(".ogg") || name.equalsIgnoreCase(".wmv"))) {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            Log.d("MUSIC", "title:" + title + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = m++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            USBData.add(info);
                            return true;
                        }
                        if ((choose == 0 || choose == 2) && (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv"))
                                || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".rmvb")
                                || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v")
                                || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8")
                                || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2")
                                || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv")
                                || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v")
                                || name.equalsIgnoreCase(".rm") || name.equalsIgnoreCase(".asf")
                                || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg")
                                || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf")
                                || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
                                || name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx")
                                || name.equalsIgnoreCase(".xvid")) {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            String[] a = file.getPath().split("/");
                            String displayname = file.getPath().split("/")[a.length - 1];
                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            Log.d("VIDEO", "title:" + title + "   " + displayname + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = v++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            USBVideoData.add(info);
                            return true;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    // 判断是不是目录
                } else if (file.isDirectory()) {
                    getMediaFile(file,choose);
                }
                return false;
            }
        });
    }
}
