package com.example.dell.nscarlauncher.ui.application;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;

import java.util.List;

public class AppFragment extends BaseFragment {
    private List<ResolveInfo> mApps;
    private AppAdapter mAdapter;
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

        mApps = App.get().getPackageManager().queryIntentActivities(mainIntent, 0);
    }
    /**
     * 初始化列表
     *
     * @param data
     */
    private void initRvAdapter( List<ResolveInfo> data) {

        if (mAdapter == null) {
            mAdapter = new AppAdapter(data,App.get().getPackageManager());
            RecyclerView rv = getView(R.id.app_rv);
            rv.setAdapter(mAdapter);
            rv.setLayoutManager(new GridLayoutManager(getContext(),7));
            rv.setNestedScrollingEnabled(false);
            mAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener() {

                @Override
                public void onClick(ResolveInfo data) {


                    //该应用的包名
                    String pkg = data.activityInfo.packageName;
                    //应用的主activity类
                    String cls = data.activityInfo.name;

                    ComponentName componet = new ComponentName(pkg, cls);
                    Intent i = new Intent();
                    i.setComponent(componet);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(i);

                }
            });
        } else {
            mAdapter.notifyData(data, true);
        }
    }
}
