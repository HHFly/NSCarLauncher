package com.example.dell.nscarlauncher.common.util;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.dell.nscarlauncher.base.Activity.BaseActivity;


/**
 * fragment工具类
 * Created by lenovo on 2017/9/20.
 */

public class FragmentUtils {
    /**
     * 选择fragment
     *
     * @param activity        activity
     * @param currentFragment 当前展示的fragment
     * @param fragment        需要展示的fragment
     * @param rootId          展示的容器id
     * @return 返回当前展示的fragment
     */
    public static synchronized <T extends Fragment> T selectFragment(BaseActivity activity, Fragment currentFragment, Fragment fragment, @IdRes int rootId) {
        if (activity == null || fragment == null) {
            return null;
        }
        LogUtils.logFormat("FragmentUtils", "selectFragment", "cur:" + (currentFragment == null ? "null" : currentFragment.getClass().getSimpleName()) + "|||fragment:" + fragment.getClass().getSimpleName());
        if (currentFragment != fragment) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

            if (currentFragment == null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment).commit();
                } else {
                    transaction.add(rootId, fragment).show(fragment).commit();
                }
            } else if (fragment.isAdded()) {
                transaction.hide(currentFragment).show(fragment).commit();
            } else {
                transaction.hide(currentFragment).add(rootId, fragment).show(fragment).commit();
            }
        }
        return (T) fragment;
    }

    public static synchronized <T extends Fragment> T selectFragment(FragmentActivity activity, Fragment currentFragment, Fragment fragment, @IdRes int rootId) {
        if (activity == null || fragment == null) {
            return null;
        }
        LogUtils.logFormat("FragmentUtils", "selectFragment", "cur:" + (currentFragment == null ? "null" : currentFragment.getClass().getSimpleName()) + "|||fragment:" + fragment.getClass().getSimpleName());
        if (currentFragment != fragment) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

            if (currentFragment == null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment).commit();
                } else {
                    transaction.add(rootId, fragment).show(fragment).commit();
                }
            } else if (fragment.isAdded()) {
                transaction.hide(currentFragment).show(fragment).commit();
            } else {
                transaction.hide(currentFragment).add(rootId, fragment).show(fragment).commit();
            }
        }
        return (T) fragment;
    }
}
