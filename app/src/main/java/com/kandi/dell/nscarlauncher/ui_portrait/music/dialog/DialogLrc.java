package com.kandi.dell.nscarlauncher.ui_portrait.music.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.lrcviewlib.ILrcViewSeekListener;
import com.hw.lrcviewlib.LrcDataBuilder;
import com.hw.lrcviewlib.LrcRow;
import com.hw.lrcviewlib.LrcView;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.PlayerService;

import java.io.File;
import java.util.List;

public class DialogLrc {
    Dialog dialog;
    Window window;
    Context context;
    LrcView mLrcView;
    ImageView iv_retrun;
    TextView songname,singer;
    public boolean isLrc;
    public  boolean isShow;
    public DialogLrc(final Context context) {
        dialog = new Dialog(context, R.style.nodarken_style);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        window = dialog.getWindow();
        window.setContentView(R.layout.dialog_lrc);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        this.context = context;
        mLrcView=window.findViewById(R.id.au_lrcView);
        iv_retrun=window.findViewById(R.id.iv_music_close);
        songname=window.findViewById(R.id.item_songname);
        singer=window.findViewById(R.id.item_songtime);
        iv_retrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        mLrcView.setLrcViewSeekListener(new ILrcViewSeekListener() {
            @Override
            public void onSeek(LrcRow currentLrcRow, long CurrentSelectedRowTime) {
                //在这里执行播放器控制器控制播放器跳转到指定时间
                Intent i = new Intent(context, PlayerService.class);
                i.putExtra("progress", CurrentSelectedRowTime);
                i.putExtra("MSG", FlagProperty.PROGRESS_CHANGE);

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isLrc=false;
                isShow=false;
            }
        });
    }
    public void show(){
        isShow=true;
        initLrc();
        dialog.show();
    }

    public void  cancel(){
        dialog.cancel();
        isShow=false;
    }

    private void initLrc(){
        if(App.get().getCurActivity().getDialogLocalMusicD().Playnow==null)return;
        songname.setText(App.get().getCurActivity().getDialogLocalMusicD().Playnow.displayName);
        singer.setText(App.get().getCurActivity().getDialogLocalMusicD().Playnow.artist);
        boolean isLrcExists =fileIsExists(App.get().getCurActivity().getDialogLocalMusicD().Playnow.getLrcStr());
        if(!isLrcExists){
            isLrc=false;
            return;
        }
        List<LrcRow> lrcRows = new LrcDataBuilder().BuiltFromAssets(context, App.get().getCurActivity().getDialogLocalMusicD().Playnow.getLrcStr());
        //ro  List<LrcRow> lrcRows = new LrcDataBuilder().Build(file);
        //mLrcView.setTextSizeAutomaticMode(true);//是否自动适配文字大小

        //init the lrcView
        mLrcView.getLrcSetting()
                .setTimeTextSize(40)//时间字体大小
                .setSelectLineColor(Color.parseColor("#ffffff"))//选中线颜色
                .setSelectLineTextSize(25)//选中线大小
                .setHeightRowColor(Color.parseColor("#aaffffff"))//高亮字体颜色
                .setNormalRowTextSize(24)//正常行字体大小
                .setHeightLightRowTextSize(30)//高亮行字体大小
                .setTrySelectRowTextSize(30)//尝试选中行字体大小
                .setTimeTextColor(Color.parseColor("#ffffff"))//时间字体颜色
                .setTrySelectRowColor(Color.parseColor("#55ffffff"));//尝试选中字体颜色

        mLrcView.commitLrcSettings();
        mLrcView.setLrcData(lrcRows);
        isLrc=true;
    }
    //判断文件是否存在
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
    public void refrshLrc(long time ){
        mLrcView.smoothScrollToTime(time);//传递的数据是播放器的时间格式转化为long数据
    }
}
