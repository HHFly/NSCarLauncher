package com.kandi.dell.nscarlauncher.ui.video;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FileUtil;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.db.dao.MusicCollectionDao;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;

import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;
import com.kandi.dell.nscarlauncher.widget.AddOneEtParamDialog;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity.homePagerActivity;

public class VideoFragment extends BaseFragment{
    private VideoAdapter mAdapter;
    public static List<Mp3Info> mData;

    public final static int  VIEWFRESH =1;
    public static int position = 0;
    public   Context context;
    public static int dataMode;
    public static final String PATH_SDCARDMOVIES = "/sdcard/Movies/";
    public int blockCount = 3;
    public Map<Integer, Boolean> recodeStatu = new HashMap<Integer, Boolean>();
    private TextView btn_delete,btn_copy;
    private CheckBox btn_select_all;
    private LinearLayout operate_layout;
    private List<String> operate_path;
    private int operate_total = 0;
    private int operate_count_index = 0;
    private boolean isUserCheck = false;

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
        btn_delete = getView(R.id.btn_delete);
        btn_copy = getView(R.id.btn_copy);
        operate_layout = getView(R.id.operate_layout);
        btn_select_all = getView(R.id.btn_select_all);
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
        setClickListener(R.id.btn_delete);
        setClickListener(R.id.btn_copy);
        setClickListener(R.id.btn_cancle);
        btn_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(dataMode == 1){
                        for(int i=0;i<homePagerActivity.getDialogLocalMusic().SDVideoData.size();i++){
                            recodeStatu.put(i, true);
                        }
                    }else if(dataMode == 2){
                        for(int i=0;i<homePagerActivity.getDialogLocalMusic().USBVideoData.size();i++){
                            recodeStatu.put(i, true);
                        }
                    }
                }else{
                    if(recodeStatu != null && !isUserCheck){
                        recodeStatu.clear();
                    }else{
                        isUserCheck = false;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initView() {
        context = getContext();
        operate_path = new ArrayList<String>();
        getMusicData();
//       homePagerActivity.getDialogLocalMusic().ScanVideo(getContext(),false);
//       homePagerActivity.getDialogLocalMusic().ScanVideoMusic(getContext(),2);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_local_1:
                if(dataMode != 3){
                    initOperate();
                }
                changeData(3);
                break;
            case R.id.video_local_2:
                if(dataMode != 2){
                    initOperate();
                }
                changeData(2);
                break;
            case R.id.video_local_return:
                homePagerActivity.hideFragment();
                break;
            case R.id.btn_delete:
                operate_path.clear();
                mAdapter.isShow = false;
                for (Integer entry : recodeStatu.keySet()) {
                    operate_path.add(homePagerActivity.getDialogLocalMusic().SDVideoData.get(entry).url);
                }
                if(operate_path.size() == 0){
                    return;
                }
                showLoadingDialog();
                for (int i=0;i<operate_path.size();i++){
                    FileUtil.deleteFile(new File(operate_path.get(i)));
                }
                Intent intent  =new Intent();
                intent.setAction("nscar_fresh_sdcard");
                context.sendBroadcast(intent);
                homePagerActivity.getDialogLocalMusic().updateLocalVideo(context);
                if(recodeStatu != null){
                    recodeStatu.clear();
                }
                operate_layout.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingDialog();
                    }
                },3500);
                break;
            case R.id.btn_copy:
                operate_total = 0;
                operate_count_index = 0;
                isWhile = true;
                for (Integer entry : recodeStatu.keySet()) {
                    operate_path.add(homePagerActivity.getDialogLocalMusic().USBVideoData.get(entry).url);
                }
                operate_total = operate_path.size();
                if(operate_total == 0){
                    return;
                }
                new CopyFileThread(operate_path).start();
                break;
            case R.id.btn_cancle:
                initOperate();
                break;
        }
    }

    //文件操作初始化
    public void initOperate(){
        mAdapter.isShow = false;
        if(recodeStatu != null){
            recodeStatu.clear();
        }
        if(btn_select_all.isChecked()){
            isUserCheck = false;
            btn_select_all.setChecked(false);
        }
        operate_layout.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    /*初始化本地音乐数据*/
    public   void getMusicData(){
        switch (dataMode) {
            case 3:
                dataMode=1;
                mData =homePagerActivity.getDialogLocalMusic().SDVideoData;
                selectMode(dataMode);

                break;
            case 2:
                mData =homePagerActivity.getDialogLocalMusic().USBVideoData;
                if(mData!=null||mData.size()!=0) {

                    selectMode(2);
                }
                break;
            default:
                dataMode=1;
                mData =homePagerActivity.getDialogLocalMusic().SDVideoData;
                selectMode(dataMode);
                if(mData==null||mData.size()==0){
                    mData =homePagerActivity.getDialogLocalMusic().USBVideoData;
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
            homePagerActivity.getDialogLocalMusic().updateLocalVideo(context);
        }else {
            mData =mode==1?homePagerActivity.getDialogLocalMusic().SDVideoData:homePagerActivity.getDialogLocalMusic().USBVideoData;
            selectMode(mode);
            initRvAdapter(mData);
        }

    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case VIEWFRESH:

                    if(FragmentType.VIDEO==homePagerActivity.mCurFragment.getmType()) {
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
            mAdapter.setMode(dataMode);
            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(View view,Mp3Info data, int Pos) {
                    if(mAdapter.isShow){
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                        boolean isCheck = !checkBox.isChecked();
                        if(isCheck){
                            recodeStatu.put(Pos, isCheck);
                        }else{
                            if(btn_select_all.isChecked()){
                                isUserCheck = true;
                                btn_select_all.setChecked(false);
                            }
                            recodeStatu.remove(Pos);
                        }
                        checkBox.setChecked(isCheck);
                        return;
                    }
                    JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED =true;//关闭网络播放提示
//                   int viewid =getResources().getIdentifier("fullscreen", "id", "fm.jiecao.jcvideoplayer_lib.JCVideoPlayer");
//                    ImageView imageView =(ImageView) getActivity().findViewById(getResources().getIdentifier("fullscreen", "id", "fm.jiecao.jcvideoplayer_lib.JCVideoPlayer"));
//                    ImageView imageView =  (ImageView) getActivity().findViewById(getResourceId("fm.jiecao.jcvideoplayer_lib.JCVideoPlayer", "id", "fullscreenButton"));
//                    if(imageView!=null){
//                        imageView.setVisibility(View.GONE);
//                    }
                    position = Pos;
                    homePagerActivity.getHomePagerTwoFragment().music_name.setText(getContext().getString(R.string.本地音乐));
                    JCFullScreenActivity.startActivity(getContext(),
                            data.url,
                            MyJCVideoPlayerStandard.class,
                            data.title);


                }

                @Override
                public void onLongClickMusic(Mp3Info data, int Pos) {
                    if(operate_layout != null && operate_layout.getVisibility() == View.GONE){
                        operate_layout.setVisibility(View.VISIBLE);
                    }
                    btn_delete.setVisibility(1==dataMode?View.VISIBLE:View.GONE);
                    btn_copy.setVisibility(2==dataMode?View.VISIBLE:View.GONE);
                    mAdapter.isShow = true;
                    mAdapter.notifyDataSetChanged();
                }

//                @Override
//                public void onClickDelete(Mp3Info data, int Pos) {
//                    showLoadingDialog();
//                    FileUtil.deleteFile(new File(data.url));
//                    Intent intent  =new Intent();
//                    intent.setAction("nscar_fresh_sdcard");
//                    context.sendBroadcast(intent);
//                    homePagerActivity.getDialogLocalMusic().updateLocalVideo(context);
//                    myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            hideLoadingDialog();
//                        }
//                    },3500);
//                }
//
//                @Override
//                public void onClickCopy(Mp3Info data, int Pos) {
//                    File sourse = new File(data.url);
//                    long len = sourse.length();
//                    new CopyFileThread(data.url,PATH_SDCARDMOVIES+data.displayName,0,len).start();
//                }
            });

        }else {
            mAdapter.setMode(dataMode);
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
    boolean isWhile = true;
    class CopyFileThread extends Thread{
        private List<String> strList;//源文件地址组
        private String destPath;//目标文件地址
        private long start,end;//start起始位置,end结束位置
        File sourse;

        public CopyFileThread(List<String> strList){
            this.strList = strList;
        }

        @Override
        public void run(){
            while(isWhile){
                try {
                    sourse = new File(operate_path.get(operate_count_index));
                    start = 0;
                    end = sourse.length();
                    if(operate_count_index == 0) {
                        showLoadingDialog();
                    }
                    operate_count_index++;
                    long beginTimes = System.currentTimeMillis();
                    Log.i("CopyFileThread","start:"+beginTimes);
                    //创建只读的随机访问文件
                    RandomAccessFile in = new RandomAccessFile(sourse.getAbsolutePath(),"r");
                    //创建可读写的随机访问文件
                    RandomAccessFile out = new RandomAccessFile(PATH_SDCARDMOVIES+sourse.getName(),"rw");
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
                    threadEnd();
                } catch (Exception e) {
                    e.printStackTrace();
                    FileUtil.deleteFile(new File(PATH_SDCARDMOVIES+sourse.getName()));
                    threadEnd();
                }
            }

        }

        public void threadEnd(){
            if(operate_count_index == operate_total){
                isWhile = false;
                Intent intent  =new Intent();
                intent.setAction("nscar_fresh_sdcard");
                context.sendBroadcast(intent);
                homePagerActivity.getDialogLocalMusic().updateLocalMusic(context);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.isShow = false;
                        if(recodeStatu != null){
                            recodeStatu.clear();
                        }
                        operate_layout.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                        hideLoadingDialog();
                    }
                },3500);
            }
        }
    }
}
