package com.kandi.dell.nscarlauncher.base.dialog;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseDialogFragment;


/**
 * 加载中弹窗
 * Created by lenovo on 2017/8/27.
 */

public class LoadingDialog extends BaseDialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dialog_loading, container, false);
        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.dialog_pb);
        if (Build.VERSION.SDK_INT > 22) {
            //6.0以上
            Drawable drawable = getActivity().getDrawable(R.drawable.pb_loading_after6);
            pb.setIndeterminateDrawable(drawable);
        }
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return rootView;
    }
}
