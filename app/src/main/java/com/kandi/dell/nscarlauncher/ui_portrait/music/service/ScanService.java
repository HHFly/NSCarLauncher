package com.kandi.dell.nscarlauncher.ui_portrait.music.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;



public class ScanService extends  Observable {
    public static final String PATH_SDCARD2 = "/storage/emulated/0/";
    public static final String PATH_SDCARD1 = "/storage/sdcard/";
    //    public static final String PATH_USB = "/storage/emulated/0/udisk/";
    public static final String PATH_SD = "/mnt/media_rw/extsd/";
    public static final String PATH_USB = "/mnt/media_rw/udisk/";
    public static final String PATH_MOVIE = "/storage/sdcard/Movies/";
    public static final String PATH_MUSIC = "/storage/emulated/0/Music/";


    public  List<Mp3Info> newdata = new ArrayList<Mp3Info>();
    public   List<Mp3Info> SDData = new ArrayList<Mp3Info>();
    public   List<Mp3Info> SDCardData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> USBData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> ColData = new ArrayList<Mp3Info>();
    public   List<Mp3Info> SDVideoData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> USBVideoData = new ArrayList<Mp3Info>();
    public  List<Mp3Info> SDCardVideoData = new ArrayList<Mp3Info>();
    String url;


    public ScanService() {
    }

    public    void getSDUSBMusicData(Context context) {

        SDData.clear();
        ContentResolver mResolver = context.getContentResolver();
        System.out.println("mResolver:" + mResolver);
        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int i = 0, j = 0;
        int cursorCount = cursor.getCount();
        System.out.println("cursorCountSDMusic" + cursorCount);
        if (cursorCount > 0) {
            cursor.moveToFirst();
            while (i < cursorCount) {
                // 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Log.d("cursorCountSDMusic", "getSDUSBMusicData: "+url);
                if (url.toLowerCase().indexOf(PATH_SDCARD2) > -1||url.toLowerCase().indexOf(PATH_SDCARD1) > -1) {
                    Mp3Info info = new Mp3Info();
                    info.id = j++;
                    info.displayName = cursor
                            .getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
//                    System.out.println("歌曲名:" + info.displayName);
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


        }
        setChanged();
        notifyObservers(1);



    }


    /*获取usb sd */
    public    void getSDUSBViedoData(Context context) {
        SDVideoData.clear();
        ContentResolver mResolver = context.getContentResolver();
        System.out.println("mResolver:" + mResolver);
        Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int i = 0, j = 0;
        int cursorCount = cursor.getCount();
        String url;
        System.out.println("cursorCountSDVIDEO" + cursorCount);
        if (cursorCount > 0) {
            cursor.moveToFirst();
            while (i < cursorCount) {
                // 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                if (url.toLowerCase().indexOf(PATH_SDCARD2) > -1) {
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
        setChanged();
        notifyObservers(2);
        mResolver = null;


    }

    /*获取usb sd */
    synchronized private   void getUSBVideoMusicData(Context context,int choose) {
        switch (choose){
            case 0:
                USBVideoData.clear();
                USBData.clear();
                SDCardData.clear();
                SDCardVideoData.clear();
                m = 0;
                v = 0;
                break;
            case 1:
                USBData.clear();
                SDCardData.clear();
                m = 0;
                break;
            case 2:
                USBVideoData.clear();
                SDCardVideoData.clear();
                v = 0;
                break;
            default:
                break;
        }
        try {
            getUSBFile(new File(PATH_USB),choose);
            getSDFile(new File(PATH_SD),choose);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void transport(List<Mp3Info> data,List<Mp3Info> newdata){
        data.clear();
        for (int i = 0; i < newdata.size(); i++) {
            data.add(newdata.get(i));
        }
    }


    public  void updateLocalMusic(final Context context,final String[] paths){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaScannerConnection.scanFile(context,
                        paths, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d("MediaScannerConnection","onScanCompleted");
                                getSDUSBMusicData(context);


                            }
                        });
            }
        }).start();
    }

    public  void updateLocalVideo(final Context context,final String[] paths){
        new Thread(new Runnable() {
            @Override
            public void run() {

                MediaScannerConnection.scanFile(context,
                        paths, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d("Video", "onScanCompleted: callback");
                                getSDUSBViedoData(context);

                            }
                        });
            }
        }).start();
    }

    public  void ScanVideoMusic(final Context context ,final int choose){

        new Thread(){
            public void run() {

                    if(context!=null) {
                        getUSBVideoMusicData(context,choose);

                    }


//                Looper.prepare();
                setChanged();
                notifyObservers(1);
                setChanged();
                notifyObservers(2);
//                if(homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler!=null){
//                    homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler.sendMessage(homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler.obtainMessage(1));
//                }
//                if(homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler!=null){
//                    homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler.sendMessage(homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler.obtainMessage(1));
//                }
//                Looper.loop();// 进入loop中的循环，查看消息队列

            }
        }.start();  //开启一个线程
    }
    public  void updateGallery(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pathSDMusic = new File(PATH_SDCARD1);
                MediaScannerConnection.scanFile(context,
                        new String[] {pathSDMusic.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//						MusicFragment.reSetMusic(false);
//						VideoFragment.dialogLocalMusic.ScanVideo(context,false);
                                getSDUSBMusicData(context);
                                getSDUSBViedoData(context);
                                setChanged();
                                notifyObservers(1);
                                setChanged();
                                notifyObservers(2);
//                                if(homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler!=null){
//                                    homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler.sendMessage(homePagerActivity.getVideoFragment().getPassengerVideoFragment().myHandler.obtainMessage(1));
//                                }
//                                if(homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler!=null){
//                                    homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler.sendMessage(homePagerActivity.getVideoFragment().getDriverVideoFragment().myHandler.obtainMessage(1));
//                                }
                            }
                        });
            }
        }).start();
    }
    public  void ScanAllDaTa(final Context context ){

        new Thread(){
            public void run() {
                if(context!=null) {
//                    updateGallery(context);

                    getUSBVideoMusicData(context, 0);

                }
            }
        }.start();  //开启一个线程
    }



    static int m = 0,v = 0;
    /**
     * 获取视频文件
     *
     * @param file
     * @param choose 0:扫描音视频;1:扫描音乐;2:扫描视频
     * @return
     */
    private  void getUSBFile(File file, final int choose) {


        File[] files =file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    try {
//                        Log.d("1", "accept: "+name);
                        int length =name.split("\\.").length;
                        name = "."+name.split("\\.")[length-1];

//                        if ((choose == 0 || choose == 1) && (name.equalsIgnoreCase(".mp3") || name.equalsIgnoreCase(".ogg") || name.equalsIgnoreCase(".wav")|| name.equalsIgnoreCase(".wmv")|| name.equalsIgnoreCase(".wma"))) {
                        if ((choose == 0 || choose == 1) && (name.equalsIgnoreCase(".mp3")
                                || name.equalsIgnoreCase(".ogg") || name.equalsIgnoreCase(".wav")||
                                name.equalsIgnoreCase(".wmv")|| name.equalsIgnoreCase(".wma")
                                || name.equalsIgnoreCase(".aac") || name.equalsIgnoreCase(".flac")
                                || name.equalsIgnoreCase(".gsm") || name.equalsIgnoreCase(".mid")
                                || name.equalsIgnoreCase(".xmf") || name.equalsIgnoreCase(".mxmf")
                                || name.equalsIgnoreCase(".rtttl") || name.equalsIgnoreCase(".rtx")
                                || name.equalsIgnoreCase(".ota") || name.equalsIgnoreCase(".imy"))) {

                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
//                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            Log.d("MUSIC", "title:" + title + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = m++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            USBData.add(info);

                        }
                        if ((choose == 0 || choose == 2) && (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv"))
                                || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".asf")
                                || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v")
                                || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8")
                                || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2")
                                || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv")
                                || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v")

                                || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg")
                                || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf")
                                || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
                                || name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx")
                                || name.equalsIgnoreCase(".xvid")) {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
//                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            String[] a = file.getPath().split("/");
//                            String displayname = file.getPath().split("/")[a.length - 1];
//                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            Log.d("VIDEO", "title:" + title + "   " + displayname + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = v++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            try {
                                info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            }catch (Exception e){
                            }
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            USBVideoData.add(info);


                        }
                        return true;
                    }catch (Exception e){
                        return false;
                    }
                    // 判断是不是目录
                } else if (file.isDirectory()) {
                    getUSBFile(file,choose);
                }
                return false;
            }
        });
    }


    private  void getSDFile(File file, final int choose) {


        File[] files =file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    try {
//                        Log.d("1", "accept: "+name);
                        int length =name.split("\\.").length;
                        name = "."+name.split("\\.")[length-1];
//                        if ((choose == 0 || choose == 1) && (name.equalsIgnoreCase(".mp3") || name.equalsIgnoreCase(".ogg") || name.equalsIgnoreCase(".wav")|| name.equalsIgnoreCase(".wmv")|| name.equalsIgnoreCase(".wma"))) {
                        if ((choose == 0 || choose == 1) && (name.equalsIgnoreCase(".mp3")
                                || name.equalsIgnoreCase(".ogg") || name.equalsIgnoreCase(".wav")||
                                name.equalsIgnoreCase(".wmv")|| name.equalsIgnoreCase(".wma")
                                || name.equalsIgnoreCase(".aac") || name.equalsIgnoreCase(".flac")
                                || name.equalsIgnoreCase(".gsm") || name.equalsIgnoreCase(".mid")
                                || name.equalsIgnoreCase(".xmf") || name.equalsIgnoreCase(".mxmf")
                                || name.equalsIgnoreCase(".rtttl") || name.equalsIgnoreCase(".rtx")
                                || name.equalsIgnoreCase(".ota") || name.equalsIgnoreCase(".imy"))) {

                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
//                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            Log.d("MUSIC", "title:" + title + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = m++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            SDCardData.add(info);

                        }
                        if ((choose == 0 || choose == 2) && (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv"))
                                || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".asf")
                                || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v")
                                || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8")
                                || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2")
                                || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv")
                                || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v")

                                || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg")
                                || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf")
                                || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
                                || name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx")
                                || name.equalsIgnoreCase(".xvid")) {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(file.getPath());
//                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            String[] a = file.getPath().split("/");
//                            String displayname = file.getPath().split("/")[a.length - 1];
//                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            Log.d("VIDEO", "title:" + title + "   " + displayname + "   " + album + "   " + artist + "   " + duration);
                            Mp3Info info = new Mp3Info();
                            file.getUsableSpace();
                            info.id = v++;
                            info.displayName = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                            try {
                                info.duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            }catch (Exception e){
                            }
                            info.title = file.getName();//mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                            info.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            info.url = file.getPath();
                            SDCardVideoData.add(info);


                        }
                        return true;
                    }catch (Exception e){
                        return false;
                    }
                    // 判断是不是目录
                } else if (file.isDirectory()) {
                    getSDFile(file,choose);
                }
                return false;
            }
        });
    }
    public   List<Mp3Info>getDatabyMode(int mode){
        switch (mode){
            case 0:
                return SDData;
            case 1:

                return USBData;
            case 2:

                return SDCardData;

        }
        return null;
    }

}
