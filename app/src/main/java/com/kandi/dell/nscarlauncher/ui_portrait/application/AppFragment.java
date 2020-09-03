package com.kandi.dell.nscarlauncher.ui_portrait.application;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.OnRecyclerItemClickListener;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FilterAppUtils;
import com.kandi.dell.nscarlauncher.ui.application.AppInfo;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppFragment extends BaseFragment {
    public List<AppInfo> mApps;
    public  AppAdapter mAdapter;
    public PackageManager pm;
    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序
    public static final int APPREFRESH = 1000;// 刷新所有应用程序列表
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public int getContentResId() {
        return R.layout.fragment_app_por;
    }

    @Override
    public void Resume() {
        if(isSecondResume){
            ViewHandler.sendEmptyMessage(APPREFRESH);
        }
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
    }

    @Override
    public void initView() {
        setmType(FragmentType.APPLICATION);
        loadApps();
        initRvAdapter(mApps);
        initItemTouchHelper();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                App.get().getCurActivity().hideFragment();
                break;
        }
    }

    private void  initItemTouchHelper(){
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            /**
             * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
//                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mApps, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mApps, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                myAdapter.notifyItemRemoved(position);
//                datas.remove(position);
            }

            /**
             * 重写拖拽可用
             * @return
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * 长按选中Item的时候开始调用
             *
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        });

        mItemTouchHelper.attachToRecyclerView((RecyclerView) getView(R.id.app_rv));
    }
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = queryFilterAppInfo(FILTER_THIRD_APP); // 查询所有应用程序信息
    }
    /**
     * 初始化列表
     *
     * @param data
     */
    private void initRvAdapter( List<AppInfo> data) {

        if (mAdapter == null) {
            mAdapter = new AppAdapter(data,App.get().getPackageManager());
            RecyclerView rv = getView(R.id.app_rv);
            rv.setAdapter(mAdapter);
            rv.setLayoutManager(new GridLayoutManager(getContext(),4));
            rv.setNestedScrollingEnabled(false);
            rv.addOnItemTouchListener(new OnRecyclerItemClickListener(rv) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {

                }

                @Override
                public void onItemLongClick(RecyclerView.ViewHolder vh) {
                    //判断被拖拽的是否是前两个，如果不是则执行拖拽
//                    if (vh.getLayoutPosition() != 0 && vh.getLayoutPosition() != 1) {
//                        mItemTouchHelper.startDrag(vh);
//
//                        //获取系统震动服务
//                        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
//                        vib.vibrate(70);
//
//                    }
                    mItemTouchHelper.startDrag(vh);
                }
            });
            mAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener() {

                @Override
                public void onClick(AppInfo data) {

                    PackageManager packageManager = getActivity().getPackageManager();
                    Intent intent=new Intent();
                    /**获得Intent*/

                    intent =packageManager.getLaunchIntentForPackage(data.getPkgName());  //com.xx.xx是我们获取到的包名
                    if(intent!=null){
                        if("org.mozilla.firefox".equals(data.getPkgName())){
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("http://www.kandigroup.com.cn/");
                            intent.setData(content_url);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            startActivity(intent);
                        }else {
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            startActivity(intent);
                        }

                    }else {
                        App.get().getCurActivity().jumpFragment(FragmentType.VIDEO);
                    }


                }
            });
        } else {
            mAdapter.notifyData(data, true);
        }
    }
    // 根据查询条件，查询特定的ApplicationInfo
    public  List<AppInfo> queryFilterAppInfo(int filter) {
        pm = getActivity().getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfo> appInfos = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        // 根据条件来过滤
        switch (filter) {
            case FILTER_ALL_APP: // 所有应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    appInfos.add(getAppInfo(app));
                }
                return appInfos;
            case FILTER_SYSTEM_APP: // 系统程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(getAppInfo(app));
                    }
                }
                return appInfos;
            case FILTER_THIRD_APP: // 第三方应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ("com.android.providers.downloads.ui".equals(app.packageName)) {
                        appInfos.add(getAppInfo(app));
                    }
                }
                appInfos.add(new AppInfo(getString(R.string.视频),getResources().getDrawable(R.mipmap.ic_video),"",getString(R.string.视频)));
                for (ApplicationInfo app : listAppcations) {

                    //非系统程序
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        if(FilterAppUtils.filter(app.packageName)){
                            appInfos.add(getAppInfo(app));}

                    }
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                        if(FilterAppUtils.filter(app.packageName)){
                            appInfos.add(getAppInfo(app));}
                    }

                }

                for (ApplicationInfo app : listAppcations) {
//                    if("org.mozilla.firefox".equals(app.packageName)) {
                    if("com.android.browser".equals(app.packageName)) {
                        AppInfo appBo =new AppInfo();
                        appBo.setName(app.processName);
                        appBo.setPkgName(app.packageName);
                        appBo.setAppLabel(getString(R.string.更多应用));
                        appBo.setAppIcon(getResources().getDrawable(R.mipmap.ic_app_download));
                        appInfos.add(appBo);
                    }
                }
                break;
            case FILTER_SDCARD_APP: // 安装在SDCard的应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfos.add(getAppInfo(app));
                    }
                }
                return appInfos;
            default:
                return null;
        }
        return appInfos;
    }
    // 构造一个AppInfo对象 ，并赋值
    public  AppInfo getAppInfo(ApplicationInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setName(app.processName);
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        return appInfo;
    }

    public  Handler ViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case APPREFRESH:// 刷新所有应用程序列表
                    try {
                        mApps = queryFilterAppInfo(FILTER_THIRD_APP); // 查询所有应用程序信息

                        if (mAdapter != null) {
                            mAdapter.notifyData(mApps, true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    public   void refreshAppInfo(){
        ViewHandler.sendEmptyMessage(APPREFRESH);
    }
}
