package com.kandi.dell.nscarlauncher.ui.video;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.widget.AddOneEtParamDialog;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class VideoFragment extends BaseFragment{
    private VideoAdapter mAdapter;
    public static List<Mp3Info> mData;
    public static DialogLocalMusic dialogLocalMusic;
    public final static int  VIEWFRESH =1;
    public static int position = 0;
    public static Context context;
    public static int dataMode;
    public static final String PATH_SDCARDMOVIES = "/sdcard/Movies/";
    public int blockCount = 3;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.VIDEO);
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_video;
    }

    @Override
    public void findView() {

    }

    @Override
    public void Resume() {
        super.Resume();
        if(mData!=null&&isSecondResume){
            getMusicData();
        }
    }

    @Override
    public void setListener() {
        setClickListener(R.id.video_local_1);
        setClickListener(R.id.video_local_2);
        setClickListener(R.id.video_local_return);
    }

    @Override
    public void initView() {
        context = getContext();
        DialogLocalMusic.setVideoFragment(this);
        getMusicData();
//       dialogLocalMusic.ScanVideo(getContext(),false);
//       dialogLocalMusic.ScanVideoMusic(getContext(),2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_local_1:
                changeData(3);
                break;
            case R.id.video_local_2:
                changeData(2);
                break;
            case R.id.video_local_return:
                HomePagerActivity.hideFragment();
                break;
        }
    }

    /*初始化本地音乐数据*/
    public   void getMusicData(){
        switch (dataMode) {
            case 3:
                dataMode=1;
                mData =DialogLocalMusic.SDVideoData;
                selectMode(dataMode);

                break;
            case 2:
                mData =DialogLocalMusic.USBVideoData;
                if(mData!=null||mData.size()!=0) {

                    selectMode(2);
                }
                break;
            default:
                dataMode=1;
                mData =DialogLocalMusic.SDVideoData;
                selectMode(dataMode);
                if(mData==null||mData.size()==0){
                    mData =DialogLocalMusic.USBVideoData;
                    if(mData!=null||mData.size()!=0) {
                        dataMode=2;
                        selectMode(dataMode);
                    }
                }
                break;
        }
        Log.d("Video ", "getMusicData: " +String.valueOf(mData.size()));
        initRvAdapter(mData);

    }
    private void  changeData(int mode){
        dataMode = mode;

        if(3==dataMode){
            DialogLocalMusic.updateLocalVideo(context);
        }else {
            mData =mode==1?DialogLocalMusic.SDVideoData:DialogLocalMusic.USBVideoData;
            selectMode(mode);
            initRvAdapter(mData);
        }

    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case VIEWFRESH:

                    if(FragmentType.VIDEO==HomePagerActivity.mCurFragment.getmType()) {
                        getMusicData();
                    }
                    break;
                default:
                    break;
            }
        };
    };
    private void  selectMode(int type){
        setViewSelected(R.id.video_local_1,false);
        setViewSelected(R.id.video_local_2,false);
        switch (type){
            case 1:
                setViewSelected(R.id.video_local_1,true);
                break;
            case 2 :
                setViewSelected(R.id.video_local_2,true);
                break;
        }
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_videolocoal);
            mAdapter =new VideoAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
                    JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
//                   int viewid =getResources().getIdentifier("fullscreen", "id", "fm.jiecao.jcvideoplayer_lib.JCVideoPlayer");
//                    ImageView imageView =(ImageView) getActivity().findViewById(getResources().getIdentifier("fullscreen", "id", "fm.jiecao.jcvideoplayer_lib.JCVideoPlayer"));
//                    ImageView imageView =  (ImageView) getActivity().findViewById(getResourceId("fm.jiecao.jcvideoplayer_lib.JCVideoPlayer", "id", "fullscreenButton"));
//                    if(imageView!=null){
//                        imageView.setVisibility(View.GONE);
//                    }
                    position = Pos;
                    JCFullScreenActivity.startActivity(getContext(),
                            data.url,
                            MyJCVideoPlayerStandard.class,
                            data.title);


                }

                @Override
                public void onLongClickMusic(Mp3Info data, int Pos) {
                    if(dataMode==2){
                        ShowDialog(data);
                    }

                }


            });

        }else {
            mAdapter.notifyData(data,true);
        }
//        setViewVisibilityGone(R.id.item_music_null,data==null||data.size()==0);

        setViewVisibilityGone(R.id.rl_video_local_nodata,data==null||data.size()==0);
    }
    /**
     * 得到资源文件.
     *
     * @param packageName
     *            包名
     * @param typeName
     *            资源类型
     * @param instenceName
     *            资源名
     * @return int
     */
    public static int getResourceId(String packageName, String typeName, String instenceName) {
        if (packageName != null && typeName != null && instenceName != null) {
            try {
//                Class<?> cl = Class.forName(packageName + "$" + typeName);
                Class<?> cl = Class.forName(packageName);

                Field field = cl.getField(instenceName);
                field.setAccessible(true);//开放权限
                Object obj = field.get(cl.newInstance());
                return Integer.parseInt(obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    //    填写信息dialog
    private  void  ShowDialog( final Mp3Info info){
        AddOneEtParamDialog mAddOneEtParamDialog = AddOneEtParamDialog.getInstance(false,"",2);

        mAddOneEtParamDialog.setOnDialogClickListener(new AddOneEtParamDialog.DefOnDialogClickListener() {
            @Override
            public void onClickCommit(AddOneEtParamDialog dialog, String data) {
                File sourse = new File(info.url);
                long len = sourse.length();
//                long oneNum = len/blockCount;
//                for(int i=0;i<blockCount-1;i++){
//                    new CopyFileThread(info.url,PATH_SDCARDMOVIES+info.displayName,oneNum*i,oneNum*(i+1)).start();
//                }
//                new CopyFileThread(info.url,PATH_SDCARDMOVIES+info.displayName,oneNum*(blockCount-1),len).start();
                new CopyFileThread(info.url,PATH_SDCARDMOVIES+info.displayName,0,len).start();
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();

            }

            @Override
            public void onClickCancel(AddOneEtParamDialog dialog) {
                App.get().getCurActivity().initImmersionBar();
                dialog.dismiss();
            }
        });

        mAddOneEtParamDialog.show(this.getFragmentManager());
    }
    class CopyFileThread extends Thread{
        private String srcPath;//源文件
        private String destPath;//目标文件地址
        private long start,end;//start起始位置,end结束位置

        public CopyFileThread(String srcPath,String destPath,long start,long end){
            this.srcPath = srcPath;
            this.destPath = destPath;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run(){
            try {
                showLoadingDialog();
                long beginTimes = System.currentTimeMillis();
                Log.i("CopyFileThread","start:"+beginTimes);
                //创建只读的随机访问文件
                RandomAccessFile in = new RandomAccessFile(srcPath,"r");
                //创建可读写的随机访问文件
                RandomAccessFile out = new RandomAccessFile(destPath,"rw");
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
                Intent intent  =new Intent();
                intent.setAction("nscar_fresh_sdcard");
                context.sendBroadcast(intent);
                DialogLocalMusic.updateLocalVideo(context);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingDialog();
                    }
                },3500);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
