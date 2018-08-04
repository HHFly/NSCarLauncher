package com.example.dell.nscarlauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dell.nscarlauncher.base.Activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private List<ResolveInfo> mApps;
    private GridView mGrid;
    private MainAdapter mAdapter;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        loadApps();
        initRvAdapter(mApps);
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }
    /**
     * 初始化列表
     *
     * @param data
     */
    private void initRvAdapter( List<ResolveInfo> data) {

        if (mAdapter == null) {
            mAdapter = new MainAdapter(data,getPackageManager());
            RecyclerView rv = getView(R.id.recyclerView);
            rv.setAdapter(mAdapter);
            rv.setLayoutManager(new GridLayoutManager(this,5));
            rv.setNestedScrollingEnabled(false);
            mAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {

                @Override
                public void onClick(ResolveInfo data) {


                    //该应用的包名
                    String pkg = data.activityInfo.packageName;
                    //应用的主activity类
                    String cls = data.activityInfo.name;

                    ComponentName componet = new ComponentName(pkg, cls);
                    Intent i = new Intent();
                    i.setComponent(componet);
                    startActivity(i);

                }
            });
        } else {
            mAdapter.notifyData(data, true);
        }
    }

}
