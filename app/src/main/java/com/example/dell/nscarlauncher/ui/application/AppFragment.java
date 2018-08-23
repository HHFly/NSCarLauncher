package com.example.dell.nscarlauncher.ui.application;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.FilterAppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppFragment extends BaseFragment {
    private List<AppInfo> mApps;
    private AppAdapter mAdapter;
    private PackageManager pm;
    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序

    @Override
    public int getContentResId() {
        return R.layout.fragment_app;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        loadApps();
        initRvAdapter(mApps);
    }

    @Override
    public void onClick(View v) {

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
            rv.setLayoutManager(new GridLayoutManager(getContext(),7));
            rv.setNestedScrollingEnabled(false);
            mAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener() {

                @Override
                public void onClick(AppInfo data) {

                    PackageManager packageManager = getActivity().getPackageManager();
                    Intent intent=new Intent();
                    /**获得Intent*/

                    intent =packageManager.getLaunchIntentForPackage(data.getPkgName());  //com.xx.xx是我们获取到的包名
                    if(intent!=null){
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent);
                    }


                }
            });
        } else {
            mAdapter.notifyData(data, true);
        }
    }
    // 根据查询条件，查询特定的ApplicationInfo
    private List<AppInfo> queryFilterAppInfo(int filter) {
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
    private AppInfo getAppInfo(ApplicationInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setName(app.processName);
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        return appInfo;
    }
}
