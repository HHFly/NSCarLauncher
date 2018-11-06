package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.ScreenManager;
import com.kandi.dell.nscarlauncher.ui.setting.SetFragment;

public class DisplayFragment extends BaseFragment {
    SeekBar seekBar ;
    RelativeLayout setWallpaper;
    private BaseFragment mCurFragment;//当前页
    private static RelativeLayout fragmentShow;
    SetWallpaperFragment setWallpaperFragment;

//    Spinner spinner;
//    private ArrayAdapter adapter;
//    public String PicIndex ="picindex";
//    public int index;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_display;
    }

    @Override
    public void findView() {
        seekBar = getView(R.id.seekbar_display);
        setWallpaper = getView(R.id.setWallpaper);
        fragmentShow = getView(R.id.rl_set_wallpaper);
//        spinner = getView(R.id.spinner);

    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.setWallpaper);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int ScreenBrightness = (int) Math.round((float)progress/100.0  * 255);
                ScreenManager.setScreenBrightness(ScreenBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initView() {
        setWallpaperFragment =new SetWallpaperFragment();
        int  ScreenBrightness = ScreenManager.getScreenBrightness();
        int progress = Math.round(ScreenBrightness* 100 /255);
        seekBar.setProgress(progress);
//        //将可选内容与ArrayAdapter连接起来
//        adapter = ArrayAdapter.createFromResource(getContext(), R.array.ctype, R.layout.mspinner_item);
//        //设置下拉列表的风格
//        adapter.setDropDownViewResource(R.layout.mspinner_dropdown_item);
//        //将adapter2 添加到spinner中
//        spinner.setAdapter(adapter);
//        index = SPUtil.getInstance(getContext(),PicIndex).getInt(PicIndex,0);
//        spinner.setSelection(index);//从内存里获取数据并存入
//
//        //添加事件Spinner事件监听
//        spinner.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
//
//        //设置默认值
//        spinner.setVisibility(View.VISIBLE);
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.set_wallpaper);
        setVisibilityGone(R.id.rl_set_wallpaper,true);
    }

    public static void hideFragment(){
        fragmentShow.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                SetFragment.hideFragment();
                break;
            case R.id.setWallpaper:
                switchFragment(setWallpaperFragment);
                break;
        }
    }

//    //使用XML形式操作
//    class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {
//        public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
//                                   long arg3) {
//            TextView view = (TextView) arg1;
//            switch (position){
//                case 0:
//                    view.setBackgroundResource(R.color.dfbackground);
//                    break;
//                case 1:
//                    view.setBackgroundColor(Color.YELLOW);
//                    break;
//                case 2:
//                    view.setBackgroundColor(Color.BLUE);
//                    break;
//                case 3:
//                    view.setBackgroundColor(Color.GREEN);
//                    break;
//                default:
//                    break;
//            }
//            if(index != position){
//                SPUtil.getInstance(getContext(),PicIndex).putInt(PicIndex,position);
//                index = position;
//            }
//            //选择完成后通知到主页进行替换背景
//            Intent intent = new Intent();
//            intent.setAction("com.changeBg");
//            getActivity().sendBroadcast(intent);
//        }
//
//        public void onNothingSelected(AdapterView<?> arg0) {
//
//        }
//
//    }
}
