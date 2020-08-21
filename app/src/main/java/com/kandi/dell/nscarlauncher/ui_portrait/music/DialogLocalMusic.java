package com.kandi.dell.nscarlauncher.ui_portrait.music;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.FileUtil;
import com.kandi.dell.nscarlauncher.common.util.MemoryUtil;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.ui_portrait.music.adapter.MusicAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.ScanService;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;



public class DialogLocalMusic implements Observer {


    public static final String PATH_MUSIC = "/storage/emulated/0/Music/";
    public  List<Mp3Info> data = new ArrayList<Mp3Info>();
    public  Mp3Info Playnow;
    private MusicAdapter mAdapter;

    public  boolean isShow;
     Dialog dialog;
    Window window;
    TextView tv_songamount,tv_local_music,tv_usb_music,tv_sd_music,btn_delete,btn_copy,btn_cancel;
    ImageView iv_music_close;
    private LinearLayout operate_layout,ll_tab;
    private CheckBox btn_select_all;
    public  int musicDiverID,dataMode;
    public Map<Integer, Mp3Info> recodeStatu = new HashMap<>();
    private List<String> operate_path=new ArrayList<>();
    private boolean isUserCheck ,isWhile;
    private  int operate_total,operate_count_index,lastDataMode=-1;
    public DialogLocalMusic(final Context context) {
        dialog = new Dialog(context, R.style.nodarken_style);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
         window = dialog.getWindow();
        window.setContentView(R.layout.dialog_music_list);
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.gravity= Gravity.TOP;
//        lp.gravity=Gravity.TOP;
//        lp.y=60;
        window.setAttributes(lp);
//        window.setWindowAnimations(R.style.dialog_localmusic_style);
        this.context = context;
        iv_music_close =window.findViewById(R.id.iv_music_close);

        tv_local_music =window.findViewById(R.id.tv_local_music);


         tv_usb_music =window.findViewById(R.id.tv_usb_music);

        tv_sd_music =window.findViewById(R.id.tv_sd_music);


        operate_layout = window.findViewById(R.id.operate_layout);
        ll_tab = window.findViewById(R.id.ll_tab);
        btn_select_all = window.findViewById(R.id.btn_select_all);
        btn_cancel  = window.findViewById(R.id.btn_cancle);
        btn_delete = window.findViewById(R.id.btn_delete);
        btn_copy = window.findViewById(R.id.btn_copy);
        setListen();

        dataMode = SPUtil.getInstance(context).getInt("dd",-1);


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isShow=false;
            }
        });
    }
    private void setListen(){
        iv_music_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        tv_sd_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reFresh(2);

            }
        });
        tv_usb_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             reFresh(1);
            }
        });
        tv_local_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             reFresh(0);
            }
        });
        btn_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //存入Map
                    if(dataMode == 0){
                        for(int i=0;i<App.get().getCurActivity().getScanService().SDData.size();i++){
                            recodeStatu.put(i,App.get().getCurActivity().getScanService().SDData.get(i));
                        }
                    }else if(dataMode == 1){
                        for(int i=0;i<App.get().getCurActivity().getScanService().USBData.size();i++){

                            recodeStatu.put(i, App.get().getCurActivity().getScanService().USBData.get(i));
                        }
                    }else if(dataMode==2){
                        for(int i=0;i<App.get().getCurActivity().getScanService().SDCardData.size();i++){
                            recodeStatu.put(i, App.get().getCurActivity().getScanService().SDCardData.get(i));
                        }
                    }
                }else{
                    if(recodeStatu != null && !isUserCheck){

                        recodeStatu.clear();
                    }else{
                        isUserCheck = false;
                    }
                }
                //清空数据选项

                    for(int i=0;i<App.get().getCurActivity().getScanService().SDData.size();i++){
                        App.get().getCurActivity().getScanService().SDData.get(i).setCheck(isChecked);

                    }

                    for(int i=0;i<App.get().getCurActivity().getScanService().USBData.size();i++){
                        App.get().getCurActivity().getScanService().USBData.get(i).setCheck(isChecked);

                    }

                    for(int i=0;i<App.get().getCurActivity().getScanService().SDCardData.size();i++){
                        App.get().getCurActivity().getScanService().SDCardData.get(i).setCheck(isChecked);
                    }

                mAdapter.notifyDataSetChanged();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOperate();
            }
        });
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operate_total = 0;
                operate_count_index = 0;
                operate_path.clear();
                isWhile = true;

                for (Mp3Info entry : recodeStatu.values()) {
                    operate_path.add(entry.getUrl());
                }
                operate_total = operate_path.size();
                if(operate_path.size() == 0){
                    Toast.makeText(context, R.string.未选择文件, Toast.LENGTH_SHORT).show();

                    return;
                }
                new CopyFileThread(operate_path).start();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operate_path.clear();

                mAdapter.isShow = false;
                for (Mp3Info entry : recodeStatu.values()) {
                    operate_path.add(entry.getUrl());
                }
                if(operate_path.size() == 0){
                    Toast.makeText(context, R.string.未选择文件, Toast.LENGTH_SHORT).show();
                    return;
                }
                App.get().getCurActivity().showLoadingDialog();
                for (int i=0;i<operate_path.size();i++){
                    FileUtil.deleteFile(new File(operate_path.get(i)));
//                    MusicCollectionDao.deleteFavByUrl(getContext(),operate_path.get(i));
                }

                String [] paths =new String[operate_path.size()];
                for (int i =0;i<operate_path.size();i++){
                    paths[i]=operate_path.get(i);
                }
                updateLocalMusic(context,paths);
//                App.get().getCurActivity().getDialogLocalMusic().updateLocalMusic(context);

            }
        });
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
                                App.get().getCurActivity().getScanService().getSDUSBMusicData(context);

                                myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initOperate();
                                        App.get().getCurActivity().hideLoadingDialog();
//                                        reFresh(0);
                                    }
                                }, 1000);
                            }
                        });
            }
        }).start();
    }
    //文件操作初始化
    public void initOperate(){
        mAdapter.isShow = false;
        if(recodeStatu != null){
            recodeStatu.clear();
        }
        //清空数据选项

        for(int i=0;i<App.get().getCurActivity().getScanService().SDData.size();i++){
            App.get().getCurActivity().getScanService().SDData.get(i).setCheck(false);

        }

        for(int i=0;i<App.get().getCurActivity().getScanService().USBData.size();i++){
            App.get().getCurActivity().getScanService().USBData.get(i).setCheck(false);

        }

        for(int i=0;i<App.get().getCurActivity().getScanService().SDCardData.size();i++){
            App.get().getCurActivity().getScanService().SDCardData.get(i).setCheck(false);
        }
        if(btn_select_all.isChecked()){
            isUserCheck = false;
            btn_select_all.setChecked(false);
        }
        operate_layout.setVisibility(View.GONE);
        iv_music_close.setVisibility(View.VISIBLE);
        ll_tab.setVisibility(View.VISIBLE);

        mAdapter.notifyDataSetChanged();
    }

    private void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = window.findViewById(R.id.recyclerView_music);
            mAdapter =new MusicAdapter(data);
            mAdapter.setRecodeStatu(recodeStatu);
            if (rv != null) {

                rv.setLayoutManager(new GridLayoutManager(context,2));

                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
                    if(lastDataMode==dataMode){

                    }else {
                        lastDataMode=dataMode;
                        tranportData();
                    }


//                    App.get().getCurActivity().getMusicFragment().getDriverFragment().recoveryLast = false;
                    musicDiverID =Pos;
                    PlayerService.isPause = false;
                    Intent i = new Intent(context, PlayerService.class);
                    i.putExtra("MSG", FlagProperty.PLAY_MSG);
                    context.startService(i);
//                    App.get().getCurActivity().getMusicFragment().getDriverFragment().listStartPlayMusic();
                    }


                @Override
                public void onLongClickMusic(Mp3Info data, int Pos) {
                        if(!mAdapter.isShow) {
//                            Intent i = new Intent(context, PlayerService.class);
//                            i.putExtra("MSG", FlagProperty.PAUSE_MSG);
//                            App.get().getCurActivity().getMusicFragment().getDriverFragment().ViewHandler.sendEmptyMessage(MUSIC_BLUETOOTH_CLOSE);
//                            context.startService(i);
//                            Intent ii = new Intent(context, PlayerPassengerService.class);
//                            ii.putExtra("MSG", FlagProperty.PAUSE_MSG);
//                            context.startService(ii);
//                            App.get().getCurActivity().getMusicFragment().getPassengerFragment().ViewHandler.sendEmptyMessage(MUSIC_BLUETOOTH_CLOSE);
                            if (operate_layout != null && operate_layout.getVisibility() == View.GONE) {
                                operate_layout.setVisibility(View.VISIBLE);
                                ll_tab.setVisibility(View.GONE);
                                iv_music_close.setVisibility(View.GONE);
                            }
                            btn_delete.setVisibility(0 == dataMode ? View.VISIBLE : View.GONE);
                            btn_copy.setVisibility(0 == dataMode ? View.GONE : View.VISIBLE);
                            btn_select_all.setChecked(false);
                            if(recodeStatu != null){
                                recodeStatu.clear();
                            }
                            mAdapter.isShow = true;
                            mAdapter.notifyDataSetChanged();
                        }
                }

                @Override
                public void onDelete(Mp3Info data, int Pos) {
                    operate_path.clear();
                    recodeStatu.put(Pos,data);
                    mAdapter.isShow = false;
                    for (Mp3Info entry : recodeStatu.values()) {
                        operate_path.add(entry.getUrl());
                    }
                    if(operate_path.size() == 0){
                        Toast.makeText(context, R.string.未选择文件, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    App.get().getCurActivity().showLoadingDialog();
                    for (int i=0;i<operate_path.size();i++){
                        FileUtil.deleteFile(new File(operate_path.get(i)));
//                    MusicCollectionDao.deleteFavByUrl(getContext(),operate_path.get(i));
                    }

                    String [] paths =new String[operate_path.size()];
                    for (int i =0;i<operate_path.size();i++){
                        paths[i]=operate_path.get(i);
                    }
                    updateLocalMusic(context,paths);
                }


            });

        }else {
            mAdapter.notifyData(data,true);
        }

//        setViewVisibilityGone(R.id.item_music_null,data==null||data.size()==0);
//        setViewVisibilityGone(R.id.item_music_null,App.get().getCurActivity().getDialogLocalMusic().SDData.size()==0&&App.get().getCurActivity().getDialogLocalMusic().USBData.size()==0);
//        setViewVisibilityGone(R.id.rl_music_nodata,data==null||data.size()==0);
    }
    private void tranportData(){


        switch (dataMode){
            case 0:
                ScanService.transport(data,App.get().getCurActivity().getScanService().SDData);
                break;

            case 1:

                ScanService.transport(data,App.get().getCurActivity().getScanService().USBData);

                break;
            case 2:


                ScanService.transport(data,App.get().getCurActivity().getScanService().SDCardData);

                break;
        }
        Log.d("Media", "tranportData: " +dataMode +"     "+data.size());
    }
    private void reFresh(int Role){
        if(dialog!=null){

           switch (Role){
               case 0:
                   initRvAdapter(App.get().getCurActivity().getScanService().SDData);
                   setSelect(0);
                   break;

               case 1:
                   initRvAdapter(App.get().getCurActivity().getScanService().USBData);
                   setSelect(1);
                   break;
               case 2:
                   initRvAdapter(App.get().getCurActivity().getScanService().SDCardData);
                   setSelect(2);
                   break;
           }
        }
    }
    private  void setSelect(int Role){
        if(dialog!=null){
            dataMode =Role;
            switch (Role){
                case 0:
                    if(tv_local_music!=null) {
                        tv_local_music.setSelected(true);
                        tv_usb_music.setSelected(false);
                        tv_sd_music.setSelected(false);

//                       ScanService.transport(data,App.get().getCurActivity().getScanService().SDData);
                    }
                    break;

                case 1:
                    if(tv_usb_music!=null) {
                        tv_usb_music.setSelected(true);
                        tv_local_music.setSelected(false);
                        tv_sd_music.setSelected(false);

//                        ScanService.transport(data,App.get().getCurActivity().getScanService().USBData);
                    }
                    break;
                case 2:
                    if(tv_sd_music!=null) {
                        tv_usb_music.setSelected(false);
                        tv_local_music.setSelected(false);
                        tv_sd_music.setSelected(true);
//                        tv_songamount.setText(String.format(App.get().getString(R.string.共首), SDCardData.size()));
//                        ScanService.transport(data,App.get().getCurActivity().getScanService().SDCardData);
                    }
                    break;
            }
        }
    }
    public  void  show(){
        dialog.show();
//        fresh();
        isShow=true;
    }
    public void  cancel(){
        dialog.cancel();
        isShow=false;
    }
    Context context;




    public Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    reFresh(0);
                    break;

                case 1:
                   reFresh(1);
                    break;
                case 2:
                    reFresh(2);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void update(Observable o, Object arg) {
      switch ((int)arg){
          case 1:
              if(dataMode==-1){
                  fresh();
                  tranportData();
              }else {
                  fresh(dataMode);
                  tranportData();
              }
              break;
      }



    }
    private void fresh(){
        if(App.get().getCurActivity().getScanService().SDData.size()>0) {
            myHandler.sendMessage(myHandler.obtainMessage(0));
//            if(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler!=null){
//                App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.sendMessage(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.obtainMessage(2));
//            }
        }else {
            if(App.get().getCurActivity().getScanService().USBData.size()>0){
                myHandler.sendMessage(myHandler.obtainMessage(1));
//                if(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler!=null){
//                    App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.sendMessage(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.obtainMessage(1));
//                }
            }else {
                myHandler.sendMessage(myHandler.obtainMessage(2));
//                if(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment()!=null){
//                    App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.sendMessage(App.get().getCurActivity().getMusicFragment().getLocalMusicFragment().myHandler.obtainMessage(0));
//                }
            }

        }
    }
    private void fresh(int role){

        switch (role){
            case 0:
                myHandler.sendMessage(myHandler.obtainMessage(0));
                break;

            case 1:
                myHandler.sendMessage(myHandler.obtainMessage(1));
                break;
            case 2:
                myHandler.sendMessage(myHandler.obtainMessage(2));
                break;
        }

    }
    class CopyFileThread extends Thread{
        private List<String> strList;//源文件地址组
        private String destPath;//目标文件地址
        private long start,end;//start起始位置,end结束位置
        File sourse;
        String[] paths;
        public CopyFileThread(List<String> strList){
            this.strList = strList;
        }

        @Override
        public void run(){
            paths =new String[operate_total];
            while(isWhile){
                try {
                    sourse = new File(operate_path.get(operate_count_index));
                    start = 0;
                    end = sourse.length();

                    long available= MemoryUtil.getAvailableMemory(context);
                    if(end>available){
                        Toast.makeText(context,sourse.getName()+context.getString(R.string.文件tip),Toast.LENGTH_LONG);
                        operate_count_index++;
                        continue;
                    }
                    if(operate_count_index == 0) {
                        App.get().getCurActivity().showLoadingDialog();
                    }

                    long beginTimes = System.currentTimeMillis();
                    Log.i("CopyFileThread","start:"+sourse.getAbsolutePath());
                    //创建只读的随机访问文件
                    RandomAccessFile in = new RandomAccessFile(sourse.getAbsolutePath(),"r");
                    //创建可读写的随机访问文件
                    paths[operate_count_index] = PATH_MUSIC+sourse.getName();
                    RandomAccessFile out = new RandomAccessFile(PATH_MUSIC+sourse.getName(),"rw");
                    //将输入跳转到指定位置
                    in.seek(start);
                    //从指定位置开始写
                    out.seek(start);
                    //文件输入通道
                    FileChannel inChannel = in.getChannel();
                    //文件输出通道
                    FileChannel outChannel = out.getChannel();
                    //锁住需要操作的区域,false代表锁住
                    FileLock lock = outChannel.lock(start,(end-start),false);
                    //将字节从此通道的文件传输到给定的可写入字节的输出通道
                    inChannel.transferTo(start,(end-start),outChannel);
                    lock.release();
                    out.close();
                    in.close();
                    long endTimes = System.currentTimeMillis();
                    Log.i("CopyFileThread",""+Thread.currentThread().getName()+"-alltime:"+(endTimes-beginTimes));
                    operate_count_index++;

                    threadEnd();
                    if(operate_count_index>operate_total){
                        isWhile = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    FileUtil.deleteFile(new File(PATH_MUSIC+sourse.getName()));
                    threadEnd();
                }
            }

        }

        public void threadEnd(){
            if(operate_count_index == operate_total){
                isWhile = false;
                updateLocalMusic(context,paths);
//                App.get().getCurActivity().getScanService().updateLocalMusic(context,paths);
//                myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                   initOperate();
//                        App.get().getCurActivity().hideLoadingDialog();
//                    }
//                },3500);
            }
        }
    }
}
